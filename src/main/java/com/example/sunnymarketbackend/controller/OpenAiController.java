package com.example.sunnymarketbackend.controller;

import com.example.sunnymarketbackend.dto.AiResponse;
import com.example.sunnymarketbackend.dto.UserQuestionRequest;
import com.example.sunnymarketbackend.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/openAi")
public class OpenAiController {

    @Autowired
    private OpenAiService openAiService;

    @PostMapping("/{userId}/chet")
    public ResponseEntity<?> aiChet(@PathVariable Long userId,
                                    @RequestBody UserQuestionRequest userQuestionRequest) {
        AiResponse aiResponse = openAiService.openAiConnection(userId, userQuestionRequest);
        return ResponseEntity.status(HttpStatus.OK).body(aiResponse);
    }
}
