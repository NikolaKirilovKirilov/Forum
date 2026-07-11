package org.example.forum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyRequest {

    @NotBlank
    private String content;
}
