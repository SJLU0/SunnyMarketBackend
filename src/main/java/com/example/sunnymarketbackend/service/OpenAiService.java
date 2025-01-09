package com.example.sunnymarketbackend.service;

import com.example.sunnymarketbackend.dto.AiResponse;
import com.example.sunnymarketbackend.dto.UserQuestionRequest;

public interface OpenAiService {

    AiResponse openAiConnection(UserQuestionRequest userQuestionRequest);

}
