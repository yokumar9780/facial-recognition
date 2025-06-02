package com.example.facialrecognition.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * Represents a User entity in the database.
 * This entity stores basic user information.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users") // Renamed table to avoid conflict with SQL keywords
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;

}
