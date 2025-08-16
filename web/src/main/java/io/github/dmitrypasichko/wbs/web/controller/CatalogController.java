package io.github.dmitrypasichko.wbs.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    @GetMapping
    public ResponseEntity<?> list() {
        var items = List.of(
                Map.of("id", 1, "name", "Desk A1", "zone", "Open Space"),
                Map.of("id", 2, "name", "Desk B4", "zone", "Silent Zone"),
                Map.of("id", 3, "name", "Meeting Room 2", "zone", "Meetings")
        );
        return ResponseEntity.ok(items);
    }
}
