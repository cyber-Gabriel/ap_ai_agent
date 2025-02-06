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
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

public class LLMService {
    private static final String OLLAMA_URL = "http://127.0.0.3:11434/api/generate";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(LLMService.class);

    // Define valid genres as a Set for validation
    private static final Set<String> VALID_GENRES = Set.of(
            "Action", "Adventure", "Animation", "Children's", "Comedy", "Crime",
            "Documentary", "Drama", "Fantasy", "Film-Noir", "Horror", "Musical",
            "Mystery", "Romance", "Sci-Fi", "Thriller", "War", "Western"
    );

    // Define a map to fix common genre fragments
    private static final Map<String, String> GENRE_FIX_MAP = new HashMap<>();
    static {
        GENRE_FIX_MAP.put("sci", "Sci-Fi");
        GENRE_FIX_MAP.put("fi", "Sci-Fi");
        GENRE_FIX_MAP.put("dr", "Drama");
        GENRE_FIX_MAP.put("com", "Comedy");
        GENRE_FIX_MAP.put("advent", "Adventure");
        GENRE_FIX_MAP.put("an", "Animation");
        GENRE_FIX_MAP.put("mus", "Musical");
        GENRE_FIX_MAP.put("myst", "Mystery");
        GENRE_FIX_MAP.put("hor", "Horror");
        GENRE_FIX_MAP.put("act", "Action");
        GENRE_FIX_MAP.put("children", "Children's");
        GENRE_FIX_MAP.put("document", "Documentary");
        GENRE_FIX_MAP.put("fant", "Fantasy");
        GENRE_FIX_MAP.put("rom", "Romance");
        GENRE_FIX_MAP.put("th", "Thriller");


    }

    public String extractGenreFromQuery(String userQuery) throws Exception {
        // Escape quotes in the user query for safety
        String safeQuery = userQuery.replace("\"", "\\\"");

        String prompt = String.format(
                "{\"model\": \"mistral\", \"prompt\": \"%s\", \"system\": " +
                        "\"You are a movie AI agent that suggests movies. Use this knowledge: Genres are: " +
                        "Action, Adventure, Animation, Children's, Comedy, Crime, Documentary, Drama, " +
                        "Fantasy, Film-Noir, Horror, Musical, Mystery, Romance, Sci-Fi, Thriller, War, and Western. " +
                        "Extract the primary movie genre from the user query. Respond ONLY with the genre name, nothing else.\"}",
                safeQuery
        );

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(OLLAMA_URL);
            post.setEntity(new StringEntity(prompt));
            post.setHeader("Content-Type", "application/json");

            // Execute the request and get the response
            String response = EntityUtils.toString(client.execute(post).getEntity());
            logger.debug("Raw API Response: {}", response);

            // Parse the JSON response
            JsonNode root = objectMapper.readTree(response);
            logger.debug("Parsed JSON Response: {}", root);

            if (root.has("response")) {
                String genre = root.get("response").asText().trim();

                // Normalize and correct genre
                genre = fixGenre(genre);

                if (!VALID_GENRES.contains(genre)) {
                    logger.warn("Invalid genre detected: {}. Returning default 'Drama'", genre);
                    return "Drama";  // Default fallback
                }

                logger.debug("Processed Genre: {}", genre);
                return genre;
            } else {
                logger.error("Invalid API response: 'response' field not found. Full response: {}", response);
                throw new RuntimeException("Invalid API response: 'response' field not found");
            }
        } catch (Exception e) {
            logger.error("Error during genre extraction", e);
            throw new RuntimeException("Failed to extract genre from query", e);
        }
    }

    // Fix fragmented or incorrect genre names
    private String fixGenre(String genre) {
        genre = genre.trim().toLowerCase();
        return GENRE_FIX_MAP.getOrDefault(genre, capitalize(genre));
    }

    // Capitalize first letter (e.g., "drama" -> "Drama")
    private String capitalize(String genre) {
        if (genre.isEmpty()) return genre;
        return genre.substring(0, 1).toUpperCase() + genre.substring(1);
    }
}
