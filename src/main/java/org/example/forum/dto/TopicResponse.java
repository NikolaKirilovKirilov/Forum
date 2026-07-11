package org.example.forum.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.forum.model.Topic;

import java.time.LocalDateTime;

@Getter
@Builder
public class TopicResponse {

    private Long id;
    private String title;
    private UserResponse author;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private long viewCount;

    public static TopicResponse from(Topic topic) {
        return TopicResponse.builder()
                .id(topic.getId())
                .title(topic.getTitle())
                .author(UserResponse.from(topic.getAuthor()))
                .createdAt(topic.getCreatedAt())
                .modifiedAt(topic.getModifiedAt())
                .viewCount(topic.getViewCount())
                .build();
    }
}
