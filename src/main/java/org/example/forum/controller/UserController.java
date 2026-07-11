package org.example.forum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.forum.dto.AssignRoleRequest;
import org.example.forum.dto.UserResponse;
import org.example.forum.model.User;
import org.example.forum.security.ForumUserDetailsService;
import org.example.forum.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ForumUserDetailsService forumUserDetailsService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/role")
    public UserResponse assignRole(
            @PathVariable Long id,
            @Valid @RequestBody AssignRoleRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = forumUserDetailsService.loadUserEntity(userDetails.getUsername());
        return userService.assignRole(id, request, currentUser);
    }
}
