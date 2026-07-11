package org.example.forum.security;

import lombok.RequiredArgsConstructor;
import org.example.forum.model.Role;
import org.example.forum.model.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationService {

    public boolean canEditTopic(User currentUser, User author) {
        return isOwner(currentUser, author) || isModeratorOrAdmin(currentUser);
    }

    public boolean canEditReply(User currentUser, User author) {
        return isOwner(currentUser, author) || isModeratorOrAdmin(currentUser);
    }

    public boolean canAssignRole(User currentUser, Role newRole) {
        if (currentUser.getRole() != Role.ADMIN) {
            return false;
        }
        return newRole == Role.MODERATOR || newRole == Role.USER;
    }

    private boolean isOwner(User currentUser, User author) {
        return currentUser.getId().equals(author.getId());
    }

    private boolean isModeratorOrAdmin(User user) {
        return user.getRole() == Role.MODERATOR || user.getRole() == Role.ADMIN;
    }
}
