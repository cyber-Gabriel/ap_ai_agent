package com.movierecommender.service;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LLMService {
    private static final String OLLAMA_URL = "http://127.0.0.3:11434/api/generate";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(LLMService.class);

    public String extractGenreFromQuery(String userQuery) throws Exception {
        // Escape quotes in the user query for safety
        String safeQuery = userQuery.replace("\"", "\\\"");

        String prompt = String.format(
                "{\"model\": \"mistral\", \"prompt\": \"%s\", \"system\": " +
                        "\"Extract the primary movie genre from the user query. Respond only with the genre name.\"}",
                safeQuery
        );

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(OLLAMA_URL);
            post.setEntity(new StringEntity(prompt));
            post.setHeader("Content-Type", "application/json");

            // Execute the request and get the response
            String response = EntityUtils.toString(client.execute(post).getEntity());
            logger.debug("Raw API Response: {}", response); // Logging raw response

            // Parse the JSON response
            JsonNode root = objectMapper.readTree(response);

            // Log the parsed response
            logger.debug("Parsed JSON Response: {}", root);

            // Check if the "response" field exists
            if (root.has("response")) {
                return root.get("response").asText().trim();
            } else {
                logger.error("Invalid API response: 'response' field not found. Full response: {}", response);
                throw new RuntimeException("Invalid API response: 'response' field not found");
            }
        } catch (Exception e) {
            logger.error("Error during genre extraction", e);
            throw new RuntimeException("Failed to extract genre from query", e);
        }
    }
}
