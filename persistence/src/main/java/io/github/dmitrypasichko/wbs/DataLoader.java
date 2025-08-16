package io.github.dmitrypasichko.wbs;

import io.github.dmitrypasichko.wbs.model.Role;
import io.github.dmitrypasichko.wbs.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seedUsers(UserService users) {
        return args -> {
            if (!users.existsByUsername("admin")) {
                users.register("admin", "admin@example.com", "admin123", Set.of(Role.ADMIN, Role.USER));
            }
            if (!users.existsByUsername("manager")) {
                users.register("manager", "manager@example.com", "manager123", Set.of(Role.MANAGER, Role.USER));
            }
            if (!users.existsByUsername("user")) {
                users.register("user", "user@example.com", "user123", Set.of(Role.USER));
            }
        };
    }
}
