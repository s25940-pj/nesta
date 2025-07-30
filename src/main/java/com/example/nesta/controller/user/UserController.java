package com.example.nesta.controller.user;

import com.example.nesta.dto.user.UserRegisterRequest;
import com.example.nesta.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("User successfully created.");
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok(userService.loginUser(username, password).getBody());
    }
}

