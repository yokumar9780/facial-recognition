package com.example.facialrecognition.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a Facial Template entity in the database.
 * This entity stores the facial embedding (features) associated with a user.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "facial_templates")
public class FacialTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Ensures a facial template is always linked to a user
    private User user;

    @Lob // Large Object: suitable for storing binary data like byte arrays
    @Column(name = "facial_embedding", nullable = false)
    private byte[] facialEmbedding; // Stores the unique numerical representation of a face

    @Column(name = "image_url")
    private String imageUrl; // Optional: URL to the original image used for enrollment

    @Column(name = "enrollment_date", nullable = false)
    @Builder.Default
    private LocalDateTime enrollmentDate = LocalDateTime.now(); // Date when the facial template was created


}
