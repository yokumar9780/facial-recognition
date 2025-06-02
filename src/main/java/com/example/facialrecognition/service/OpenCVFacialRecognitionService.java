package com.example.facialrecognition.service;


import lombok.extern.slf4j.Slf4j;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imdecode;

/**
 * OpenCV-based Facial Recognition Service implementation.
 * This service demonstrates basic OpenCV integration for image processing.
 * <p>
 * IMPORTANT NOTE:
 * This implementation provides a *simplified* and *deterministic* embedding.
 * <p>
 * For REAL facial recognition (face detection, robust feature extraction, accurate comparison):
 * 1. You would need pre-trained deep learning models (e.g., Haar Cascades for detection,
 * FaceNet or ArcFace models for embedding extraction).
 * 2. Loading and running these models with OpenCV/JavaCV is significantly more complex
 * and requires proper model files (.xml, .onnx, .pb, etc.) and their specific APIs.
 * 3. The `extractFacialEmbedding` method would involve:
 * a. Decoding `imageData` into an OpenCV `Mat`.
 * b. Using `CascadeClassifier` or a DNN module to detect faces.
 * c. If faces are found, passing the face region through another pre-trained model
 * to get a high-dimensional embedding vector (e.g., 128, 512, 1024 floats).
 * d. Converting that float array to a byte array for storage.
 * 4. The `isMatch` method would then calculate cosine similarity or Euclidean distance
 * between two *real* embeddings and compare it to a refined threshold.
 * <p>
 * For this example, we'll generate a deterministic embedding based on image properties
 * so that uploading the same image consistently produces the same "embedding" for testing.
 */
@Service
@ConditionalOnProperty( // This annotation makes the bean conditional
        name = "facial.recognition.strategy", // Name of the property to check
        havingValue = "opencv"               // Value the property must have for this bean to be active
)
@Slf4j
public class OpenCVFacialRecognitionService implements FacialRecognitionStrategy {

    private static final double SIMILARITY_THRESHOLD = 0.95; // Higher threshold for deterministic matching

    @Override
    public byte[] extractFacialEmbedding(byte[] imageData) {
        if (imageData == null || imageData.length == 0) {
            log.info("OpenCV: No image data provided for embedding extraction. Returning null.");
            return null;
        }

        Mat imageMat = null;
        try {
            // Convert byte array to OpenCV Mat
            imageMat = imdecode(new Mat(imageData), IMREAD_COLOR);

            if (imageMat.empty()) {
                log.info("OpenCV: Failed to decode image data.");
                return null;
            }

            // --- SIMPLIFIED EMBEDDING GENERATION (NOT REAL ML) ---
            // In a real scenario, this is where you'd use pre-trained models
            // to detect faces and extract robust facial features.
            // For demonstration, we'll create a "deterministic" embedding based on
            // a simple hash of image properties, so the same image gives the same embedding.
            long sumPixels = 0;
            for (int i = 0; i < imageMat.rows(); i++) {
                for (int j = 0; j < imageMat.cols(); j++) {
                    // Sum of pixel values (simplified hash)
                    sumPixels += imageMat.ptr(i, j).get(); // Gets byte value of pixel
                }
            }

            // Create a fixed-size embedding based on deterministic values
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2); // Two long values for embedding
            buffer.putLong(sumPixels);
            buffer.putLong(imageMat.total() * imageMat.channels()); // Example: total pixels * channels
            byte[] embedding = buffer.array();
            // --- END SIMPLIFIED EMBEDDING GENERATION ---

            log.info("OpenCV: Extracted deterministic facial embedding.");
            return embedding;

        } catch (Exception e) {
            log.error("OpenCV: Error during embedding extraction: {}", e.getMessage());
            return null;
        } finally {
            if (imageMat != null) {
                imageMat.release(); // Release native memory
            }
        }
    }

    @Override
    public boolean isMatch(byte[] embedding1, byte[] embedding2) {
        if (embedding1 == null || embedding2 == null || embedding1.length != embedding2.length) {
            return false; // Cannot compare
        }

        // For deterministic embeddings, a simple equality check is sufficient
        boolean match = Arrays.equals(embedding1, embedding2);
        log.info("OpenCV: Compared embeddings. Match: {}", match);
        return match; // For deterministic embeddings, exact equality is the "match"
    }
}