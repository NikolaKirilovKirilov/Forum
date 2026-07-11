package org.example.forum.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.forum.model.Reply;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReplyResponse {

    private Long id;
    private String content;
    private UserResponse author;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ReplyResponse from(Reply reply) {
        return ReplyResponse.builder()
                .id(reply.getId())
                .content(reply.getContent())
                .author(UserResponse.from(reply.getAuthor()))
                .createdAt(reply.getCreatedAt())
                .modifiedAt(reply.getModifiedAt())
                .build();
    }

    public static List<ReplyResponse> fromPage(Page<Reply> page) {
        return page.map(ReplyResponse::from).getContent();
    }
}
