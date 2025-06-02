package com.example.facialrecognition.service;

/**
 * Interface for different facial recognition implementations (strategies).
 * This defines the contract for any facial recognition service.
 */
public interface FacialRecognitionStrategy {

    /**
     * Extracts a unique facial embedding (feature vector) from an image.
     * In a real implementation, this involves face detection and feature extraction
     * using machine learning models.
     *
     * @param imageData The raw byte array of the image (e.g., JPEG, PNG).
     * @return A byte array representing the facial embedding, or null if no face is detected or an error occurs.
     */
    byte[] extractFacialEmbedding(byte[] imageData);

    /**
     * Compares two facial embeddings to determine if they belong to the same person.
     *
     * @param embedding1 The first facial embedding.
     * @param embedding2 The second facial embedding.
     * @return True if the embeddings are considered a match based on a similarity threshold, false otherwise.
     */
    boolean isMatch(byte[] embedding1, byte[] embedding2);
}
