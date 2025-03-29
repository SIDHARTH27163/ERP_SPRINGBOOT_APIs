package com.mainapp.Controller;


import com.mainapp.dto.LoginRequest;
import com.mainapp.dto.LoginResponse;
import com.mainapp.dto.SessionValidationResponse;
import com.mainapp.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;

/**
 * Controller class to handle authentication-related requests.
 * Author: Sidharth Guleria
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint for logging in a user.
     * This method processes the login request by validating the user's credentials
     * and starting a session if successful.
     *
     * @param loginRequest The login details (username and password).
     * @param request The HTTP request to retrieve client details (e.g., IP address).
     * @param session The HTTP session object to store session information.
     * @return The login response, containing success or failure details.
     * @throws UnknownHostException If there is an issue with retrieving the host.
     * Author: Sidharth Guleria
     */
    @PostMapping("/login")

    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                               HttpServletRequest request,
                                               HttpServletResponse response,
                                               HttpSession session) throws UnknownHostException {
        LoginResponse loginResponse = authService.login(loginRequest, request, session);

        // If login is successful, set the session ID in the response header
        if (loginResponse.getStatusCode() == 200) {
            String sessionId = session.getId(); // Retrieve the session ID
            response.setHeader("Set-Cookie", "JSESSIONID=" + sessionId + "; Path=/; HttpOnly; Secure; SameSite=Strict");
        }

        return ResponseEntity.status(loginResponse.getStatusCode())
                .body(loginResponse);

    }

    /**
     * Endpoint for logging out a user.
     * This method destroys the user's session and logs them out of the system.
     *
     * @param session The HTTP session object to be destroyed.
     * @return A success message indicating that the user has been logged out.
     * Author: Sidharth Guleria
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout(session);
        return "Logout successful, session destroyed.";
    }

    /**
     * Endpoint for validating the current session.
     * This method checks if the current user session is valid and returns the session status.
     *
     * @param session The HTTP session to be validated.
     * @return The session validation response, indicating if the session is valid or not.
     * Author: Sidharth Guleria
     */
    @PostMapping("/validate-session")

    public ResponseEntity<SessionValidationResponse> validateSession(HttpSession session) {
        SessionValidationResponse response = authService.validateSession(session);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    /**
     * Endpoint for validating the current session.
     * This method checks if the current user session is valid and returns the session status.
     *
     * @param session The HTTP session to be validated.
     * @return The session validation response, indicating if the session is valid or not.
     * Author: Sidharth Guleria
     */
    @PostMapping("/validate-employee-session")
    public ResponseEntity<SessionValidationResponse> validateEmployeeSession(HttpSession session) {
        SessionValidationResponse response = authService.validateEmployeeSession(session);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

  

}
