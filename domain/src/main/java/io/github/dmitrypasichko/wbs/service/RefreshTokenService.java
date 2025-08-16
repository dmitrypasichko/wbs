package io.github.dmitrypasichko.wbs.service;

import io.github.dmitrypasichko.wbs.model.RefreshToken;
import io.github.dmitrypasichko.wbs.repository.RefreshTokenRepository;
import io.github.dmitrypasichko.wbs.model.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public RefreshToken issue(UserEntity user) {
        RefreshToken t = new RefreshToken();
        t.setUser(user);
        t.setToken(UUID.randomUUID().toString());
        t.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));
        t.setRevoked(false);
        return repo.save(t);
    }

    public Optional<RefreshToken> find(String token) {
        return repo.findByToken(token);
    }

    @Transactional
    public RefreshToken rotate(RefreshToken current) {
        current.setRevoked(true);
        repo.save(current);
        return issue(current.getUser());
    }

    @Transactional
    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        repo.save(token);
    }
}
