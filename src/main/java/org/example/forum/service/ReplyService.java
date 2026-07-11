package org.example.forum.service;

import lombok.RequiredArgsConstructor;
import org.example.forum.dto.ReplyRequest;
import org.example.forum.dto.ReplyResponse;
import org.example.forum.exception.ForbiddenOperationException;
import org.example.forum.exception.ResourceNotFoundException;
import org.example.forum.model.Reply;
import org.example.forum.model.Topic;
import org.example.forum.model.User;
import org.example.forum.repository.ReplyRepository;
import org.example.forum.repository.TopicRepository;
import org.example.forum.security.AuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final TopicRepository topicRepository;
    private final AuthorizationService authorizationService;

    @Transactional
    public ReplyResponse createReply(Long topicId, ReplyRequest request, User author) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));

        Reply reply = Reply.builder()
                .content(request.getContent())
                .author(author)
                .topic(topic)
                .build();

        return ReplyResponse.from(replyRepository.save(reply));
    }

    @Transactional
    public ReplyResponse updateReply(Long replyId, ReplyRequest request, User currentUser) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new ResourceNotFoundException("Reply not found: " + replyId));

        if (!authorizationService.canEditReply(currentUser, reply.getAuthor())) {
            throw new ForbiddenOperationException("You are not allowed to edit this reply");
        }

        reply.setContent(request.getContent());
        return ReplyResponse.from(replyRepository.save(reply));
    }
}
