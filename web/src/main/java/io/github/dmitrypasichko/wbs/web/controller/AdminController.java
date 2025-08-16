package io.github.dmitrypasichko.wbs.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/panel")
    public ResponseEntity<?> panel() {
        return ResponseEntity.ok(Map.of("message", "Welcome, admin!"));
    }
}
