// MainApp.java
package com.movierecommender;

import com.movierecommender.service.LLMService;
import com.movierecommender.service.Neo4jService;
import com.movierecommender.model.Movie;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        LLMService llmService = new LLMService();
        Neo4jService neo4jService = new Neo4jService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Movie Recommender!");
        System.out.print("Enter your movie preference: ");
        String query = scanner.nextLine();

        try {
            // Step 1: Get genre using LLM
            String genre = llmService.extractGenreFromQuery(query);
            System.out.println("Identified genre: " + genre);

            // Step 2: Get recommendations from Neo4j
            List<Movie> recommendations = neo4jService.getTopMoviesByGenre(genre, 5);

            System.out.println("\nTop recommendations:");
            recommendations.forEach(movie -> System.out.printf(
                    "- %s (%.1f) [%d]%n",
                    movie.getTitle(),
                    movie.getRating(),
                    movie.getYear()
            ));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}