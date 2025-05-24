package com.example.nesta.dto.user;
import com.example.nesta.model.enums.UserRole;
import lombok.Data;

@Data
public class UserRegisterRequest {
    private String username;
    private String email;
    private String password;
    private UserRole role;
}
