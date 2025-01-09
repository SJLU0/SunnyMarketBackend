package com.example.sunnymarketbackend.service.impl;

import com.example.sunnymarketbackend.dto.AiResponse;
import com.example.sunnymarketbackend.dto.UserQuestionRequest;
import com.example.sunnymarketbackend.service.OpenAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiServiceImpl implements OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiURL;

    @Override
    public AiResponse openAiConnection(UserQuestionRequest userQuestionRequest) {

        try {
            String requestBody = buildRequestBody(userQuestionRequest);

            // 發送請求並獲取響應
            String responseBody = sendHttpRequest(requestBody);

            // 處理 OpenAI API 回應
            String responseContent = parseResponse(responseBody);
            AiResponse aiResponse = new AiResponse();
            aiResponse.setAiRepond(responseContent);

            System.out.println("--------------\n" + responseContent + "\n--------------");

            return aiResponse;
        } catch (Exception e) {
            AiResponse errorAiResponse = new AiResponse();
            errorAiResponse.setMessage(e.getMessage());
            return errorAiResponse;
        }
    }

    private String buildRequestBody(UserQuestionRequest userQuestionRequest) {
        return "{\n" +
                "  \"model\": \"gpt-4o\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"system\", \"content\": \"你是一名理財專員，知道所有財務金融相關知識。你將根據使用者的具體問題，以及財務數據、過去的對話紀錄來提供具體且個性化的財務建議。" +
                "請根據上下文進行具體回答，避免使用過於模板化的回應，請根據所有的資料來量身定制建議，避免重複回應已分析過的財務數據或問題，但請參考過去的問題和回答，進行前後呼應的回應。" +
                "根據使用者的財務狀況，提出理財建議。若使用者問非財務、金融、投資相關問題，請回應「此問題超出我的工作範圍，請諮詢相關領域人士」。若發現財務數據異常，請主動提醒用戶，並建議財務規劃以及數據異常猜測。" +
                "提供財務規劃時，請具體到行動步驟，並以正面鼓勵語句結尾。限制字數為50字。\"},\n" +
                "    {\"role\": \"user\", \"content\": \"這是我最近的財務摘要：\\n" +
                "      - 日期: " + LocalDateTime.now() + "\\n" +
                "    \"},\n" +
                "    {\"role\": \"user\", \"content\": \"" + userQuestionRequest.getUserAsk() + "\"},\n" +  // 將具體問題加入
                "    {\"role\": \"assistant\", \"content\": \"請針對具體問題回應。\"}\n" +  // 指導 AI 生成具體回應
                "  ]\n" +
                "}";
    }

    // 發送 HTTP 請求並返回回應
    private String sendHttpRequest(String requestBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiURL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response code: " + response.statusCode());
        return response.body();
    }

    // 處理回應並提取有用的內容
    private String parseResponse(String responseBody) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = mapper.readValue(responseBody, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("No choices available in the response.");
        }
        return (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
    }
}
