package org.example.forum.service;

import lombok.RequiredArgsConstructor;
import org.example.forum.dto.AssignRoleRequest;
import org.example.forum.dto.UserResponse;
import org.example.forum.exception.ForbiddenOperationException;
import org.example.forum.exception.ResourceNotFoundException;
import org.example.forum.model.Role;
import org.example.forum.model.User;
import org.example.forum.repository.UserRepository;
import org.example.forum.security.AuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional
    public UserResponse assignRole(Long userId, AssignRoleRequest request, User currentUser) {
        if (!authorizationService.canAssignRole(currentUser, request.getRole())) {
            throw new ForbiddenOperationException("Only administrators can assign moderator or user roles");
        }

        if (request.getRole() == Role.ADMIN) {
            throw new ForbiddenOperationException("Administrator role cannot be assigned through this endpoint");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (user.getRole() == Role.ADMIN) {
            throw new ForbiddenOperationException("Cannot change role of an administrator");
        }

        user.setRole(request.getRole());
        return UserResponse.from(userRepository.save(user));
    }
}
