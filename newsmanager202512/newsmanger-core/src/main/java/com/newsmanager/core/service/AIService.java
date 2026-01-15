package com.newsmanager.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AIService {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.api-url}")
    private String apiUrl;

    @Value("${deepseek.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT = "你是一个精通中文语境的新闻评论审核专家。\n" +
            "你的任务是审核用户提交的评论内容，识别其中的不当内容。\n" +
            "请检查：辱骂、人身攻击、非法广告、引战、黄赌毒、政治敏感等。\n" +
            "请严格按照以下 JSON 数组格式返回结果，不要包含任何多余文字，不要使用 markdown 格式输出：\n" +
            "[\n" +
            "    {\n" +
            "        \"content\": \"评论原文\",\n" +
            "        \"suggestion\": \"pass\" | \"block\" | \"warn\",\n" +
            "        \"reason\": \"简短的违规原因说明，如果没有违规请写'正常'\",\n" +
            "        \"risk_level\": \"low\" | \"medium\" | \"high\"\n" +
            "    }\n" +
            "]";

    /**
     * 智能审核评论列表
     * 
     * @param contents 评论内容列表
     * @return 审核结果列表
     */
    public List<Map<String, Object>> auditComments(List<String> contents) {
        if (contents == null || contents.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            // 构建消息内容
            StringBuilder userPrompt = new StringBuilder("请审核以下评论列表：\n");
            for (int i = 0; i < contents.size(); i++) {
                userPrompt.append(i + 1).append(". ").append(contents.get(i)).append("\n");
            }

            return invokeAI(userPrompt.toString());
        } catch (Exception e) {
            System.err.println("AI 审核异常: " + e.getMessage());
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    /**
     * 智能审核评论列表并关联ID
     * 
     * @param comments 评论对象列表
     * @return cid -> 审核结果
     */
    public Map<Integer, Map<String, Object>> auditCommentsWithCid(
            List<com.newsmanager.api.models.CommentModel> comments) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyMap();
        }

        // 为了防止列表过长，如果超过 50 条，建议分批或只审前50条（此处先简单处理，只审前50条或全部）
        int limit = Math.min(comments.size(), 50);
        List<com.newsmanager.api.models.CommentModel> targetComments = comments.subList(0, limit);

        StringBuilder userPrompt = new StringBuilder("请审核以下评论列表：\n");
        for (int i = 0; i < targetComments.size(); i++) {
            userPrompt.append(i + 1).append(". ").append(targetComments.get(i).getContent()).append("\n");
        }

        List<Map<String, Object>> aiResults = invokeAI(userPrompt.toString());
        Map<Integer, Map<String, Object>> resultMap = new HashMap<>();

        // 映射回去
        for (int i = 0; i < aiResults.size() && i < targetComments.size(); i++) {
            resultMap.put(targetComments.get(i).getCid(), aiResults.get(i));
        }

        return resultMap;
    }

    private List<Map<String, Object>> invokeAI(String userPrompt) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", Arrays.asList(
                    createMessage("system", SYSTEM_PROMPT),
                    createMessage("user", userPrompt)));
            requestBody.put("stream", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return parseAIResult(response.getBody());
            }
        } catch (Exception e) {
            System.err.println("AI 请求异常: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private Map<String, String> createMessage(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private List<Map<String, Object>> parseAIResult(String responseBody) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            // 去掉可能存在的 markdown 标记
            content = content.replace("```json", "").replace("```", "").trim();

            JsonNode resultsArray = objectMapper.readTree(content);
            if (resultsArray.isArray()) {
                for (JsonNode node : resultsArray) {
                    Map<String, Object> res = new HashMap<>();
                    res.put("content", node.path("content").asText());
                    res.put("suggestion", node.path("suggestion").asText());
                    res.put("reason", node.path("reason").asText());
                    res.put("risk_level", node.path("risk_level").asText());
                    results.add(res);
                }
            }
        } catch (Exception e) {
            System.err.println("解析 AI 结果失败: " + e.getMessage());
        }
        return results;
    }
}
