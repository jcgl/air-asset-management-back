package com.air.assetmanagement.controller;

import com.air.assetmanagement.model.User;
import com.air.assetmanagement.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Integer>> importFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(userService.importFromExcel(file));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
