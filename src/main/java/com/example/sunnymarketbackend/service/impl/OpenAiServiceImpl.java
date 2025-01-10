package com.example.sunnymarketbackend.service.impl;

import com.example.sunnymarketbackend.dao.OpenAiDao;
import com.example.sunnymarketbackend.dao.ProductDao;
import com.example.sunnymarketbackend.dto.AiResponse;
import com.example.sunnymarketbackend.dto.UserQuestionRequest;
import com.example.sunnymarketbackend.entity.OpenAi;
import com.example.sunnymarketbackend.entity.Product;
import com.example.sunnymarketbackend.service.OpenAiService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiServiceImpl implements OpenAiService {

    @Autowired
    private OpenAiDao openAiDao;

    @Autowired
    private ProductDao productDao;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiURL;

    @Override
    public AiResponse openAiConnection(Long userId,
                                       UserQuestionRequest userQuestionRequest) {

        try {

            Map<String, Object> map = new HashMap<>();
            List<Product> productList = productDao.selectAllProducts(map);
            String productListJson = bulidJson(productList);
            System.out.println(productListJson);

            String requestBody = buildRequestBody(userQuestionRequest, productListJson);

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
            aiResponse.setAiRespond(responseContent);

            System.out.println("--------------\n" + responseContent + "\n--------------");

            return aiResponse;
        } catch (Exception e) {
            AiResponse errorAiResponse = new AiResponse();
            errorAiResponse.setMessage(e.getMessage());
            return errorAiResponse;
        }
    }

    private String buildRequestBody(UserQuestionRequest userQuestionRequest,
                                    String productListJson) {
        ObjectMapper mapper = new ObjectMapper();

        // 建立主結構
        Map<String, Object> json = new HashMap<>();
        json.put("model", "gpt-4o-mini");

        // 建立 messages 陣列
        List<Map<String, String>> messages = new ArrayList<>();

        // 系統訊息
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "您是一名專業的電商助理，專門提供商品推薦、訂單問題解決，並幫助用戶做出購物決策。根據用戶的提問、瀏覽記錄、購物偏好和提供的數據，給出個性化且具體的建議。回覆應該簡潔、直接，避免過於模板化。如果問題超出您的服務範圍，請回覆：「這個問題超出了我的服務範圍，請聯繫相關專業人士。」");
        messages.add(systemMessage);

        // 使用者訊息
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userQuestionRequest.getUserAsk());
        userMessage.put("language", "zh-TW");
        messages.add(userMessage);

        // 助理訊息
        Map<String, String> assistantMessage = new HashMap<>();
        assistantMessage.put("role", "assistant");
        assistantMessage.put("content", "請根據用戶的需求和數據提供商品推薦。以下是相關的商品清單數據：");
        messages.add(assistantMessage);

        // 組裝 JSON
        json.put("messages", messages);

        // 生成 JSON 字串
        try{
            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            System.out.println(jsonString);
            return jsonString;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
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

    private String bulidJson(List<Product> productList){
        StringBuilder productListJson = new StringBuilder();
        for (Product product : productList) {
            productListJson.append("{")
                    .append("\"Product Name\": \"").append(product.getProductName()).append("\", ")
                    .append("\"Price\": \"").append(product.getPrice()).append("\", ")
                    .append("\"Description\": \"").append(product.getDescription()).append("\"")
                    .append("}, ");
        }

        // 移除最後多餘的逗號和空格
        if (productListJson.length() > 0) {
            productListJson.setLength(productListJson.length() - 2);
        }

        return productListJson.toString();
    }
}
