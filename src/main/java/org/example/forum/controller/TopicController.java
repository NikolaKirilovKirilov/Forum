package org.example.forum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.forum.dto.TopicDetailResponse;
import org.example.forum.dto.TopicRequest;
import org.example.forum.dto.TopicResponse;
import org.example.forum.model.User;
import org.example.forum.security.ForumUserDetailsService;
import org.example.forum.service.TopicService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;
    private final ForumUserDetailsService forumUserDetailsService;

    @GetMapping
    public List<TopicResponse> getAllTopics() {
        return topicService.getAllTopics();
    }

    @GetMapping("/{id}")
    public TopicDetailResponse getTopic(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page
    ) {
        return topicService.getTopicWithReplies(id, page);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TopicResponse createTopic(
            @Valid @RequestBody TopicRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User author = forumUserDetailsService.loadUserEntity(userDetails.getUsername());
        return topicService.createTopic(request, author);
    }

    @PutMapping("/{id}")
    public TopicResponse updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody TopicRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = forumUserDetailsService.loadUserEntity(userDetails.getUsername());
        return topicService.updateTopic(id, request, currentUser);
    }
}
