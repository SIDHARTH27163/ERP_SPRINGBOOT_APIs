package com.mainapp.repository;

import com.mainapp.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, String> {
    // Custom queries can be added here if needed
}
