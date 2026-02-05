package com.miniats;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for Mini-ATS.
 * Applicant Tracking System with Supabase backend.
 */
@SpringBootApplication
public class MiniAtsApplication {

    public static void main(String[] args) {
        // Load .env file if it exists (for local development)
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            // Set environment variables from .env
            dotenv.entries().forEach(entry ->
                    System.setProperty(entry.getKey(), entry.getValue())
            );
        } catch (Exception e) {
            System.out.println("No .env file found or error loading it: " + e.getMessage());
        }

        SpringApplication.run(MiniAtsApplication.class, args);
    }
}