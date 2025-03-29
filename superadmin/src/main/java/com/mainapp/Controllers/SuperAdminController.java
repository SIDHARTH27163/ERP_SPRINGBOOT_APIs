package com.mainapp.Controllers;
import com.mainapp.dto.SessionValidationResponse;
import com.mainapp.services.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Controller class for handling admin-related requests.
 * Author: Sidharth Guleria
 */
@RestController
@RequestMapping("/admin")
public class SuperAdminController {
    private final DataSource dataSource;

    /**
     * Constructor to initialize the dataSource for database connection.
     *
     * @param dataSource The DataSource object to be used for establishing DB connections.
     * Author: Sidharth Guleria
     */
    public SuperAdminController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired
    private AuthService authService;

    /**
     * Endpoint to get the admin dashboard after validating the SuperAdmin session.
     *
     * @param session The HTTP session to validate the user's session.
     * @return ResponseEntity containing either a success message or an error message based on session validation.
     * Author: Sidharth Guleria
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getAdminDashboard(HttpSession session) {
        // Validate SuperAdmin session
        SessionValidationResponse validationResponse = authService.validateSuperAdminSession(session);

        if (!validationResponse.isSessionValid()) {
            return ResponseEntity
                    .status(403) // HTTP 403 Forbidden
                    .body(validationResponse.getMessage());
        }

        // If validation is successful, return the admin dashboard response
        return ResponseEntity.ok("Welcome to the SuperAdmin Dashboard");
    }

    /**
     * Endpoint to test the database connection for the Auth module.
     *
     * @return A string message indicating whether the database connection was successful or failed.
     * Author: Sidharth Guleria
     */
    @GetMapping("")
    public String testDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return "Auth Module DB Connection Successful!";
        } catch (SQLException e) {
            return "Auth Module DB Connection Failed: " + e.getMessage();
        }
    }
}
