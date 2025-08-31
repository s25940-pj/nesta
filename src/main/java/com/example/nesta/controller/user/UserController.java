package com.example.nesta.controller.user;

import com.example.nesta.dto.user.UserRegisterRequest;
import com.example.nesta.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD) or hasRole(T(com.example.nesta.model.enums.UserRole).RENTIER)")
    @PostMapping("/logout")
    public void logout(@RequestBody UserLogoutRequest request) {
        userService.logoutUser(request);
    }
}

