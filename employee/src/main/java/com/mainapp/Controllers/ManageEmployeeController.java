package com.mainapp.Controllers;

import com.mainapp.Services.ManageEmployeeService;
import com.mainapp.dto.AddEmployeeRequest;
import com.mainapp.dto.EmployeeResponse;
import com.mainapp.dto.SessionValidationResponse;
import com.mainapp.models.AccountMaster;
import com.mainapp.services.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing employees.
 * Provides endpoints for adding, retrieving, updating, and deleting employees.
 *
 * @author Sidharth Guleria
 */
@RestController
@RequestMapping("/manage-employees")
public class ManageEmployeeController {

    private final AuthService authService;
    private final ManageEmployeeService manageEmployeeService;

    public ManageEmployeeController(AuthService authService, ManageEmployeeService manageEmployeeService) {
        this.authService = authService;
        this.manageEmployeeService = manageEmployeeService;
    }

    /**
     * Adds a new employee to the system.
     *
     * @param request Employee details from the request body.
     * @param session HTTP session to validate authentication.
     * @return Response indicating the success or failure of the operation.
     */
    @PostMapping("/add-employee")
    public ResponseEntity<Map<String, Object>> addEmployee(@Valid  @RequestBody AddEmployeeRequest request, BindingResult result, HttpSession session) {
        SessionValidationResponse validationResponse = authService.validateEmployeeSession(session);
        if (!validationResponse.isSessionValid()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", HttpStatus.FORBIDDEN.value(), "message", validationResponse.getMessage()));
        }
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(Map.of("status", HttpStatus.BAD_REQUEST.value(), "errors", errors));
        }

        manageEmployeeService.addEmployee(request, validationResponse.getUserId(), session);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("status", HttpStatus.CREATED.value(), "message", "User created successfully for Employee"));
    }

    /**
     * Retrieves a list of employees based on entity type.
     *
     * @param session HTTP session to validate authentication.
     * @return List of employees with relevant details.
     */
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getEmployeesByEntityType(HttpSession session) {
        SessionValidationResponse validationResponse = authService.validateEmployeeSession(session);
        if (!validationResponse.isSessionValid()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", HttpStatus.FORBIDDEN.value(), "message", validationResponse.getMessage()));
        }

        List<EmployeeResponse> employees = manageEmployeeService.getEmployeesByEntityType("102", session);

        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Employee list fetched successfully.",
                "data", employees
        ));
    }

    /**
     * Retrieves an employee account by ID.
     *
     * @param id Employee ID.
     * @param session HTTP session to validate authentication.
     * @return Employee details if found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAccountById(@PathVariable Long id, HttpSession session) {
        SessionValidationResponse validationResponse = authService.validateEmployeeSession(session);
        if (!validationResponse.isSessionValid()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", HttpStatus.FORBIDDEN.value(), "message", validationResponse.getMessage()));
        }

        EmployeeResponse employee = manageEmployeeService.getAccountById(id);

        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Account details fetched successfully.",
                "data", employee
        ));
    }

    /**
     * Updates an existing employee account.
     *
     * @param id Employee ID.
     * @param request Updated employee details.
     * @param session HTTP session to validate authentication.
     * @return Updated employee details.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAccount(
            @PathVariable Long id,
            @RequestBody AccountMaster request, // Directly bind JSON to entity
            HttpSession session) {

        if (!authService.validateEmployeeSession(session).isSessionValid()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", 403, "message", "Invalid session"));
        }

        EmployeeResponse updatedEmployee = manageEmployeeService.updateAccount(id, request, session);

        return ResponseEntity.ok(Map.of("status", 200, "message", "Updated successfully", "data", updatedEmployee));
    }

    /**
     * Deletes an employee account.
     *
     * @param id Employee ID.
     * @param session HTTP session to validate authentication.
     * @return Response indicating the success or failure of the deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAccount(@PathVariable Long id, HttpSession session) {
        SessionValidationResponse validationResponse = authService.validateEmployeeSession(session);
        if (!validationResponse.isSessionValid()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", HttpStatus.FORBIDDEN.value(), "message", validationResponse.getMessage()));
        }

        manageEmployeeService.deleteAccount(id);

        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Employee deleted successfully."
        ));
    }
    /**
     * API endpoint to toggle the status of an employee.
     * If the current status is ACTIVE, it becomes INACTIVE.
     * If the current status is INACTIVE, it becomes ACTIVE.
     *
     * @param id The ID of the employee whose status needs to be toggled.
     * @return A success message after updating the status.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> toggleEmployeeStatus(@PathVariable Long id , HttpSession session) {
        SessionValidationResponse validationResponse = authService.validateEmployeeSession(session);
        if (!validationResponse.isSessionValid()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", HttpStatus.FORBIDDEN.value(), "message", validationResponse.getMessage()));
        }
        manageEmployeeService.toggleEmployeeStatus(id);
        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Employee status toggled successfully."

        ));
    }
}
