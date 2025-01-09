package com.example.sunnymarketbackend.entity;

import java.time.LocalDateTime;

public class OpenAi {

    private Long openAiId;
    private Long userId;
    private String aiRepond;
    private String userAsk;
    private LocalDateTime createDate;

    public Long getOpenAiId() {
        return openAiId;
    }

    public void setOpenAiId(Long openAiId) {
        this.openAiId = openAiId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserAsk() {
        return userAsk;
    }

    public void setUserAsk(String userAsk) {
        this.userAsk = userAsk;
    }

    public String getAiRepond() {
        return aiRepond;
    }

    public void setAiRepond(String aiRepond) {
        this.aiRepond = aiRepond;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
}
