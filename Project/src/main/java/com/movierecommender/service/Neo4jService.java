package com.movierecommender.service;

import com.movierecommender.config.Neo4jConfig;
import com.movierecommender.model.Movie;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Neo4jService {
    private final Driver driver = Neo4jConfig.getDriver();

    public List<Movie> getTopMoviesByGenre(String genre, int limit) {
        String query = "MATCH (m:Movie)-[:HAS_GENRE]->(g:Genre) " +
                "WHERE g.name = $genre " +
                "RETURN m.title AS title, m.rating AS rating, substring(m.releaseDate, 7, 4) AS year " +
                "ORDER BY m.rating DESC LIMIT $limit";

        try (Session session = driver.session()) {
            Result result = session.run(query, Map.of("genre", genre, "limit", limit));

            List<Movie> movies = new ArrayList<>();
            result.forEachRemaining(record -> {
                // Extract year as a string and convert it to an integer
                String yearStr = record.get("year").asString();
                int year = Integer.parseInt(yearStr);

                // Create Movie using constructor
                Movie movie = new Movie(
                        record.get("title").asString(),
                        record.get("rating").asDouble(),
                        year
                );
                movies.add(movie);
            });
            return movies;
        }
    }
}
