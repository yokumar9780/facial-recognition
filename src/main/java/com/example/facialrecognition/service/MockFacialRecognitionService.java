package com.example.facialrecognition.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Mock Facial Recognition Service implementation.
 * This service simulates the process by generating random embeddings
 * and performing a simple "comparison" based on a similarity threshold.
 * It implements the FacialRecognitionStrategy interface.
 */
@Service
@ConditionalOnProperty( // This annotation makes the bean conditional
        name = "facial.recognition.strategy", // Name of the property to check
        havingValue = "mock",                // Value the property must have for this bean to be active
        matchIfMissing = true                // If the property is missing, this bean will be active (default strategy)
)
@Slf4j
public class MockFacialRecognitionService implements FacialRecognitionStrategy {

    private static final double SIMILARITY_THRESHOLD = 0.8; // Example threshold for matching

    @Override
    public byte[] extractFacialEmbedding(byte[] imageData) {
        // --- MOCKED LOGIC START ---
        if (imageData == null || imageData.length == 0) {
            log.info("Mock: No image data provided for embedding extraction. Returning null.");
            return null; // Simulate no face detected due to invalid image
        }

        // Simulate a fixed-size embedding (e.g., 128-dimensional vector)
        byte[] embedding = new byte[128];
        new Random().nextBytes(embedding); // Fill with random bytes
        log.info("Mock: Extracted facial embedding (random).");
        return embedding;
        // --- MOCKED LOGIC END ---
    }

    @Override
    public boolean isMatch(byte[] embedding1, byte[] embedding2) {
        // --- MOCKED LOGIC START ---
        if (embedding1 == null || embedding2 == null || embedding1.length != embedding2.length) {
            return false; // Cannot compare
        }

        // Simulate similarity:
        // For demonstration purposes, we'll say they are "similar" if their first few bytes match.
        // This is NOT how real facial recognition works.
        int matchingBytes = 0;
        for (int i = 0; i < Math.min(10, embedding1.length); i++) { // Check first 10 bytes
            if (embedding1[i] == embedding2[i]) {
                matchingBytes++;
            }
        }
        double simulatedSimilarity = (double) matchingBytes / 10.0;
        log.info("Mock: Compared embeddings. Simulated similarity: {}", simulatedSimilarity);
        return simulatedSimilarity >= SIMILARITY_THRESHOLD;
        // --- MOCKED LOGIC END ---
    }
}