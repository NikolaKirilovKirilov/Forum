package org.example.forum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.forum.model.Topic;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class TopicDetailResponse {

    private TopicResponse topic;
    private List<ReplyResponse> replies;
    private int page;
    private int size;
    private long totalReplies;
    private int totalPages;

    public static TopicDetailResponse of(Topic topic, Page<org.example.forum.model.Reply> replyPage) {
        return new TopicDetailResponse(
                TopicResponse.from(topic),
                ReplyResponse.fromPage(replyPage),
                replyPage.getNumber(),
                replyPage.getSize(),
                replyPage.getTotalElements(),
                replyPage.getTotalPages()
        );
    }
}
