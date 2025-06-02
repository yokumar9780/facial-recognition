package com.example.facialrecognition.repository;

import com.example.facialrecognition.model.FacialTemplate;
import com.example.facialrecognition.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for FacialTemplate entities.
 * Extends JpaRepository to provide standard CRUD operations for FacialTemplate.
 */
@Repository
public interface FacialTemplateRepository extends JpaRepository<FacialTemplate, Long> {
    /**
     * Finds a FacialTemplate associated with a specific User.
     * @param user The User entity to find the facial template for.
     * @return An Optional containing the FacialTemplate if found, or empty if not.
     */
    Optional<FacialTemplate> findByUser(User user);
}
