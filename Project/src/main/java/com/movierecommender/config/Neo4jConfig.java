// Neo4jConfig.java
package com.movierecommender.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class Neo4jConfig {
    private static final String URI = "bolt://localhost:7687";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "12345678";

    public static Driver getDriver() {
        return GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD));
    }
}