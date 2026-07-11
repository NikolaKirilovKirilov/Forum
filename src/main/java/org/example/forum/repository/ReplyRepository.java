package org.example.forum.repository;

import org.example.forum.model.Reply;
import org.example.forum.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    Page<Reply> findByTopicOrderByCreatedAtAsc(Topic topic, Pageable pageable);
}
