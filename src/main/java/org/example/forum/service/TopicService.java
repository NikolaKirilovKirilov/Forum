package org.example.forum.service;

import lombok.RequiredArgsConstructor;
import org.example.forum.dto.TopicDetailResponse;
import org.example.forum.dto.TopicRequest;
import org.example.forum.dto.TopicResponse;
import org.example.forum.exception.DuplicateTitleException;
import org.example.forum.exception.ForbiddenOperationException;
import org.example.forum.exception.ResourceNotFoundException;
import org.example.forum.model.Reply;
import org.example.forum.model.Topic;
import org.example.forum.model.User;
import org.example.forum.repository.ReplyRepository;
import org.example.forum.repository.TopicRepository;
import org.example.forum.security.AuthorizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private static final int REPLIES_PAGE_SIZE = 10;

    private final TopicRepository topicRepository;
    private final ReplyRepository replyRepository;
    private final AuthorizationService authorizationService;

    @Transactional(readOnly = true)
    public List<TopicResponse> getAllTopics() {
        return topicRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(TopicResponse::from)
                .toList();
    }

    @Transactional
    public TopicDetailResponse getTopicWithReplies(Long topicId, int page) {
        Topic topic = findTopic(topicId);
        topic.setViewCount(topic.getViewCount() + 1);

        Pageable pageable = PageRequest.of(page, REPLIES_PAGE_SIZE, Sort.by("createdAt").ascending());
        Page<Reply> replies = replyRepository.findByTopicOrderByCreatedAtAsc(topic, pageable);

        return TopicDetailResponse.of(topic, replies);
    }

    @Transactional
    public TopicResponse createTopic(TopicRequest request, User author) {
        if (topicRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateTitleException("Topic title must be unique");
        }

        Topic topic = Topic.builder()
                .title(request.getTitle())
                .author(author)
                .build();

        return TopicResponse.from(topicRepository.save(topic));
    }

    @Transactional
    public TopicResponse updateTopic(Long topicId, TopicRequest request, User currentUser) {
        Topic topic = findTopic(topicId);

        if (!authorizationService.canEditTopic(currentUser, topic.getAuthor())) {
            throw new ForbiddenOperationException("You are not allowed to edit this topic");
        }

        if (topicRepository.existsByTitleAndIdNot(request.getTitle(), topicId)) {
            throw new DuplicateTitleException("Topic title must be unique");
        }

        topic.setTitle(request.getTitle());
        return TopicResponse.from(topicRepository.save(topic));
    }

    private Topic findTopic(Long topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));
    }
}
