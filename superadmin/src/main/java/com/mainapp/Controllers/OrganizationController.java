package com.mainapp.Controllers;

import com.mainapp.dto.CreateOrganizationRequest;
import com.mainapp.dto.EntityDTO;
import com.mainapp.dto.SessionValidationResponse;
import com.mainapp.dto.UpdateOrganization;
import com.mainapp.models.EntityTable;
import com.mainapp.services.AuthService;
import com.mainapp.services.OrganizationService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for handling organization-related requests.
 * Provides endpoints for creating users and retrieving entities.
 *
 * Author: Pankaj Kataria
 */
@RestController
@RequestMapping("/organization")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private AuthService authService;

    /**
     * Endpoint to create a new user within an organization
     * @param request  The request body containing organization creation details.
     * @param session  The HTTP session for validating user authentication.
     * @return         ResponseEntity containing the status and message of the operation.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody CreateOrganizationRequest request, HttpSession session) {
        SessionValidationResponse sessionValidation = authService.validateSuperAdminSession(session);

        if (!sessionValidation.isSessionValid()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(sessionValidation);
        }

        organizationService.createUser(request, sessionValidation.getUserId());

        return ResponseEntity.ok(Map.of("statusCode", 201, "message", "User created successfully For Organization"));
    }

    /**
     * Endpoint to retrieve a list of all entities.
     *
     * @return A list of EntityDTO objects representing all entities.
     */
    @GetMapping("/list")
    public List<EntityDTO> getOrganizations() {

        return organizationService.getAllEntities();
    }

    /**
     * Retrieves an organization based on the provided ID.
     *
     * @param id The ID of the organization to retrieve.
     * @return An Optional containing the requested organization entity.
     * @author Pankaj Kataria
     */
    @GetMapping("/list/{id}")
    public Optional<EntityTable> getOrganization(@PathVariable Long id) {
        return organizationService.GetEntity(id);
    }

    /**
     * Updates an existing organization entity.
     *
     * @param id      The ID of the organization to update.
     * @param request The request body containing updated organization details.
     * @param session The HTTP session for validating user authentication.
     * @return A ResponseEntity indicating success or failure of the update operation.
     * @author Pankaj Kataria
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrganization(@PathVariable Long id, @RequestBody UpdateOrganization request, HttpSession session) {
        SessionValidationResponse sessionValidation = authService.validateSuperAdminSession(session);

        if (!sessionValidation.isSessionValid()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(sessionValidation);
        }
        organizationService.UpdateEntity(id, request , sessionValidation.getUserId());

        return ResponseEntity.ok(Map.of("statusCode", 201, "message", "Organization Updated successfully"));
    }

    /**
     * Deletes an organization entity based on the provided ID.
     *
     * @param id The ID of the organization to delete.
     * @return A string message indicating the success or failure of the deletion.
     * @author Pankaj Kataria
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteAccount(@PathVariable Long id, HttpSession session) {
        SessionValidationResponse validationResponse = authService.validateSuperAdminSession(session);
        if (!validationResponse.isSessionValid()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", HttpStatus.FORBIDDEN.value(), "message", validationResponse.getMessage()));
        }
        organizationService.deleteEntity(id);

        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Organization deleted successfully."
        ));
    }

}
