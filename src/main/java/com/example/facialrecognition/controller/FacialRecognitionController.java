package com.example.facialrecognition.controller;

import com.example.facialrecognition.model.FacialTemplate;
import com.example.facialrecognition.model.User;
import com.example.facialrecognition.repository.FacialTemplateRepository;
import com.example.facialrecognition.repository.UserRepository;
import com.example.facialrecognition.service.FacialRecognitionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Facial Recognition operations.
 * Exposes endpoints for user enrollment and facial recognition/verification.
 */
@RestController
@RequestMapping("/api/v1/facial")
@Slf4j
@RequiredArgsConstructor
public class FacialRecognitionController {

    private final UserRepository userRepository;
    private final FacialTemplateRepository facialTemplateRepository;
    private final FacialRecognitionStrategy facialRecognitionStrategy;

    /**
     * Endpoint for enrolling a user's facial template.
     *
     * @param username The username of the user to enroll.
     * @param file     The image file containing the user's face.
     * @return ResponseEntity indicating success or failure of enrollment.
     */
    @PostMapping("/enroll")
    public ResponseEntity<String> enrollFacialTemplate(@RequestParam(value = "username") String username,
                                                       @RequestParam(value = "file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select an image file to enroll.");
        }
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be empty.");
        }

        try {
            // 1. Get or create user
            User user = userRepository.findByUsername(username)
                    .orElseGet(() -> {
                        User newUser = new User(null, username);
                        return userRepository.save(newUser);
                    });

            // 2. Extract facial embedding from the provided image
            byte[] imageData = file.getBytes();
            byte[] facialEmbedding = facialRecognitionStrategy.extractFacialEmbedding(imageData);

            if (facialEmbedding == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No face detected or failed to extract embedding from the image.");
            }

            // 3. Check if user already has a facial template
            Optional<FacialTemplate> existingTemplate = facialTemplateRepository.findByUser(user);
            if (existingTemplate.isPresent()) {
                // Update existing template
                FacialTemplate templateToUpdate = existingTemplate.get();
                templateToUpdate.setFacialEmbedding(facialEmbedding);
                templateToUpdate.setImageUrl(file.getOriginalFilename()); // Or a proper storage URL
                templateToUpdate.setEnrollmentDate(java.time.LocalDateTime.now());
                facialTemplateRepository.save(templateToUpdate);
                return ResponseEntity.ok("Facial template updated successfully for user: " + username);
            } else {
                // Save new facial template
                FacialTemplate newTemplate = new FacialTemplate(null, user, facialEmbedding, file.getOriginalFilename(),
                        LocalDateTime.now());
                facialTemplateRepository.save(newTemplate);
                return ResponseEntity.status(HttpStatus.CREATED).body("Facial template enrolled successfully for user: " + username);
            }

        } catch (IOException e) {
            log.error("Error reading image file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to read image file.");
        } catch (Exception e) {
            log.error("Error during facial enrollment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during facial enrollment.");
        }
    }

    /**
     * Endpoint for recognizing a face from an image against enrolled templates.
     *
     * @param file The image file containing the face to recognize.
     * @return ResponseEntity with the recognized username or "No match found".
     */
    @PostMapping("/recognize")
    public ResponseEntity<String> recognizeFacialTemplate(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select an image file to recognize.");
        }

        try {
            // 1. Extract facial embedding from the provided image
            byte[] imageData = file.getBytes();
            byte[] queryEmbedding = facialRecognitionStrategy.extractFacialEmbedding(imageData);

            if (queryEmbedding == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No face detected or failed to extract embedding from the image.");
            }

            // 2. Retrieve all stored facial templates
            List<FacialTemplate> allTemplates = facialTemplateRepository.findAll();

            // 3. Compare the query embedding with all stored templates
            for (FacialTemplate storedTemplate : allTemplates) {
                if (facialRecognitionStrategy.isMatch(queryEmbedding, storedTemplate.getFacialEmbedding())) {
                    return ResponseEntity.ok("Match found for user: " + storedTemplate.getUser().getUsername());
                }
            }

            // 4. No match found
            return ResponseEntity.ok("No match found.");

        } catch (IOException e) {
            log.error("Error reading image file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to read image file.");
        } catch (Exception e) {
            log.error("Error during facial recognition: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during facial recognition.");
        }
    }

    /**
     * Endpoint for verifying a face against a specific user's enrolled template.
     *
     * @param username The username to verify against.
     * @param file     The image file containing the face to verify.
     * @return ResponseEntity indicating if the face matches the specified user.
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyFacialTemplate(@RequestParam("username") String username,
                                                       @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select an image file for verification.");
        }
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be empty for verification.");
        }

        try {
            // 1. Get the target user
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + username);
            }
            User targetUser = userOptional.get();

            // 2. Get the target user's facial template
            Optional<FacialTemplate> targetTemplateOptional = facialTemplateRepository.findByUser(targetUser);
            if (targetTemplateOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No facial template found for user: " + username);
            }
            FacialTemplate targetTemplate = targetTemplateOptional.get();

            // 3. Extract facial embedding from the provided image
            byte[] imageData = file.getBytes();
            byte[] queryEmbedding = facialRecognitionStrategy.extractFacialEmbedding(imageData);

            if (queryEmbedding == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No face detected or failed to extract embedding from the image.");
            }

            // 4. Compare the query embedding with the target user's template
            if (facialRecognitionStrategy.isMatch(queryEmbedding, targetTemplate.getFacialEmbedding())) {
                return ResponseEntity.ok("Verification successful: Face matches user " + username);
            } else {
                return ResponseEntity.ok("Verification failed: Face does NOT match user " + username);
            }

        } catch (IOException e) {
            log.error("Error reading image file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to read image file.");
        } catch (Exception e) {
            log.error("Error during facial verification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during facial verification.");
        }
    }
}
