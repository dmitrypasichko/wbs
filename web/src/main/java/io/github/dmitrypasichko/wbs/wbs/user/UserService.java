package io.github.dmitrypasichko.wbs.wbs.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public Optional<UserEntity> findByUsername(String username) {
        return repo.findByUsername(username);
    }

    public boolean existsByUsername(String username) { return repo.existsByUsername(username); }
    public boolean existsByEmail(String email) { return repo.existsByEmail(email); }

    @Transactional
    public UserEntity register(String username, String email, String rawPassword, Set<Role> roles) {
        UserEntity u = new UserEntity();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(encoder.encode(rawPassword));
        u.setRoles(roles == null || roles.isEmpty() ? Set.of(Role.USER) : roles);
        return repo.save(u);
    }
}
