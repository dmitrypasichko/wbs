package io.github.dmitrypasichko.wbs.wbs.auth;

import io.github.dmitrypasichko.wbs.wbs.security.JwtService;
import io.github.dmitrypasichko.wbs.wbs.user.Role;
import io.github.dmitrypasichko.wbs.wbs.user.UserEntity;
import io.github.dmitrypasichko.wbs.wbs.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final RefreshTokenService refreshTokens;

    public AuthController(UserService users, PasswordEncoder encoder, JwtService jwt, RefreshTokenService refreshTokens) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
        this.refreshTokens = refreshTokens;
    }

    public record RegisterRequest(@NotBlank String username, @Email String email, @NotBlank String password) {}
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record TokenResponse(String accessToken, String refreshToken, String username, Set<String> roles) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (users.existsByUsername(req.username()) || users.existsByEmail(req.email())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username or email already taken"));
        }
        UserEntity u = users.register(req.username(), req.email(), req.password(), Set.of(Role.USER));
        var roles = u.getRoles().stream().map(Role::name).collect(Collectors.toSet());
        String access = jwt.generateAccess(u.getUsername(), Map.of("roles", roles));
        var refresh = refreshTokens.issue(u);
        return ResponseEntity.ok(new TokenResponse(access, refresh.getToken(), u.getUsername(), roles));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        var opt = users.findByUsername(req.username());
        if (opt.isEmpty()) return ResponseEntity.status(401).body(Map.of("message","Invalid credentials"));
        UserEntity u = opt.get();
        if (!encoder.matches(req.password(), u.getPassword()))
            return ResponseEntity.status(401).body(Map.of("message","Invalid credentials"));

        var roles = u.getRoles().stream().map(Role::name).collect(Collectors.toSet());
        String access = jwt.generateAccess(u.getUsername(), Map.of("roles", roles));
        var refresh = refreshTokens.issue(u);
        return ResponseEntity.ok(new TokenResponse(access, refresh.getToken(), u.getUsername(), roles));
    }

    public record RefreshRequest(@NotBlank String refreshToken) {}

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshRequest req) {
        var opt = refreshTokens.find(req.refreshToken());
        if (opt.isEmpty()) return ResponseEntity.status(401).body(Map.of("message","Invalid refresh token"));
        var t = opt.get();
        if (t.isRevoked() || t.getExpiresAt().isBefore(java.time.Instant.now())) {
            return ResponseEntity.status(401).body(Map.of("message","Refresh token expired or revoked"));
        }
        var u = t.getUser();
        var roles = u.getRoles().stream().map(Role::name).collect(Collectors.toSet());
        String newAccess = jwt.generateAccess(u.getUsername(), Map.of("roles", roles));
        var newRefresh = refreshTokens.rotate(t);
        return ResponseEntity.ok(new TokenResponse(newAccess, newRefresh.getToken(), u.getUsername(), roles));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody RefreshRequest req) {
        var opt = refreshTokens.find(req.refreshToken());
        if (opt.isPresent()) {
            refreshTokens.revoke(opt.get());
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String token = auth.substring(7);
        var claims = jwt.getAllClaims(token);
        return ResponseEntity.ok(Map.of(
                "username", claims.getSubject(),
                "roles", claims.get("roles")
        ));
    }
}
