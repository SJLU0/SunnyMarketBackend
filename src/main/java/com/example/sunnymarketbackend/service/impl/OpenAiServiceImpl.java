package com.example.sunnymarketbackend.service.impl;

import com.example.sunnymarketbackend.dao.OpenAiDao;
import com.example.sunnymarketbackend.dto.AiResponse;
import com.example.sunnymarketbackend.dto.UserQuestionRequest;
import com.example.sunnymarketbackend.entity.OpenAi;
import com.example.sunnymarketbackend.service.OpenAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private OpenAiDao openAiDao;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiURL;

    @Override
    public AiResponse openAiConnection(Long userId,
                                       UserQuestionRequest userQuestionRequest) {

        try {
            String requestBody = buildRequestBody(userQuestionRequest);

            // 發送請求並獲取響應
            String responseBody = sendHttpRequest(requestBody);

            // 處理 OpenAI API 回應
            String responseContent = parseResponse(responseBody);

            OpenAi openAi = new OpenAi();
            openAi.setUserId(userId);
            openAi.setAiRespond(responseContent);
            openAi.setUserAsk(userQuestionRequest.getUserAsk());
            openAi.setCreateDate(LocalDateTime.now());

            openAiDao.createAiRepondUserQusent(openAi);

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
                "  \"model\": \"gpt-4omini\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"system\", \"content\": \"You are a professional e-commerce assistant specializing in product recommendations, order issue resolutions, and helping users make shopping decisions. Based on user queries, browsing history, shopping preferences, and provided data, give personalized and specific advice. Responses should be concise, direct, and avoid being overly templated. If the question is beyond your scope, respond: 'This question is outside my scope of service. Please contact a relevant professional.'\"},\n" +
                "    {\"role\": \"user\", \"content\": \"" + userQuestionRequest.getUserAsk() + "\"},\n" +
                "    {\"role\": \"assistant\", \"content\": \"Please provide product suggestions based on the user's needs and data.\"}\n" +
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
