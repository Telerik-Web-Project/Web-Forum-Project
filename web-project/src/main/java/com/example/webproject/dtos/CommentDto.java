package com.example.webproject.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommentDto {
    @NotNull
    @Size(min = 1, max = 250, message = "Comments can be no more than 250 symbols.")
    private String content;

    public CommentDto() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
