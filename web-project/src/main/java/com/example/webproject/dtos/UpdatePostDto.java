package com.example.webproject.dtos;

import jakarta.validation.constraints.Size;

public class UpdatePostDto {

    @Size(min =16, max = 64, message = "Title should be between 16 and 64 symbols")
    private String title;

    @Size(min =32, max = 8192, message = "Content should be between 32 and 8192 symbols")
    private String content;

    public UpdatePostDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
