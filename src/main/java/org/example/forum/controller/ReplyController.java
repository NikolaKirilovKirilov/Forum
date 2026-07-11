package org.example.forum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.forum.dto.ReplyRequest;
import org.example.forum.dto.ReplyResponse;
import org.example.forum.model.User;
import org.example.forum.security.ForumUserDetailsService;
import org.example.forum.service.ReplyService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;
    private final ForumUserDetailsService forumUserDetailsService;

    @PostMapping("/api/topics/{topicId}/replies")
    @ResponseStatus(HttpStatus.CREATED)
    public ReplyResponse createReply(
            @PathVariable Long topicId,
            @Valid @RequestBody ReplyRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User author = forumUserDetailsService.loadUserEntity(userDetails.getUsername());
        return replyService.createReply(topicId, request, author);
    }

    @PutMapping("/api/replies/{id}")
    public ReplyResponse updateReply(
            @PathVariable Long id,
            @Valid @RequestBody ReplyRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = forumUserDetailsService.loadUserEntity(userDetails.getUsername());
        return replyService.updateReply(id, request, currentUser);
    }
}
