package com.ecommerce.endpoint;

import com.ecommerce.dto.*;
import com.ecommerce.service.AuthService;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserEndpoint {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.creatUser(request));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUserAll(@RequestHeader(value = "X-UserService-Role", required = false) String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Integer id,
                                                    @RequestHeader(value = "X-UserService-UserId", required = false) Integer currentUserId,
                                                    @RequestHeader(value = "X-UserService-Role", required = false) String role) {
        if (!"ADMIN".equals(role) && !id.equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/seller-info")
    public ResponseEntity<SellerResponse> getSellerInfoById(@PathVariable("id") Integer id,
                                                        @RequestHeader(value = "X-UserService-UserId", required = false) Integer currentUserId) {
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userService.getSellerInfoById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> changeUserById(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UserRequest userRequest,
            @RequestHeader(value = "X-UserService-UserId", required = false) Integer currentUserId,
            @RequestHeader(value = "X-UserService-Role", required = false) String role) {
        if (!"ADMIN".equals(role) && !id.equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.changeUserById(id, userRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(
            @PathVariable("id") Integer id,
            @RequestHeader(value = "X-UserService-Role", required = false) String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<UserResponse> blockUser(
            @PathVariable("id") Integer id,
            @RequestHeader(value = "X-UserService-Role", required = false) String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.blockUser(id));
    }

    @PatchMapping("/{id}/unblock")
    public ResponseEntity<UserResponse> unblockUser(
            @PathVariable("id") Integer id,
            @RequestHeader(value = "X-UserService-Role", required = false) String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.unblockUser(id));
    }
}
