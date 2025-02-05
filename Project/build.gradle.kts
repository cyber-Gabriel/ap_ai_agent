plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation ("org.neo4j.driver:neo4j-java-driver:5.12.0")
    implementation ("org.slf4j:slf4j-api:2.0.7")
    implementation ("ch.qos.logback:logback-classic:1.4.7")
    implementation ("org.neo4j.driver:neo4j-java-driver:5.14.0")
    implementation ("org.json:json:20231013")
//    implementation ("org.slf4j:slf4j-api:2.0.0")
//    implementation ("org.slf4j:slf4j-simple:2.0.0")// Simple logging backend



    // HTTP Client (برای Ollama)
    implementation ("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

}
dependencies {
    // Neo4j Driver
    implementation ("org.neo4j.driver:neo4j-java-driver:5.14.0")

    // HTTP Client for Ollama
    implementation ("org.apache.httpcomponents:httpclient:4.5.13")

    // JSON Processing
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.15.2")
}
tasks.test {
    useJUnitPlatform()
}