package com.mainapp.services;
import com.mainapp.repository.AccountMasterRepository;
import com.mainapp.repository.SessionRepository;
import com.mainapp.dto.LoginRequest;
import com.mainapp.dto.LoginResponse;
import com.mainapp.dto.SessionValidationResponse;
import com.mainapp.models.AccountMaster;
import com.mainapp.models.EntityTable;
import com.mainapp.models.Session;
import com.mainapp.utils.PasswordUtils;
import com.mainapp.utils.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Optional;
/**
 * Service class for handling authentication-related functionality such as login, logout, session validation, etc.
 * Author: Sidharth Guleria
 */
@Service
public class AuthService {

    @Autowired
    private AccountMasterRepository accountMasterRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionUtils sessionUtils;

    /**
     * Method to handle user login.
     * It validates the user credentials and starts a session if successful.
     *
     * @param loginRequest The login request containing credentials.
     * @param request The HTTP request object to retrieve client details.
     * @param session The HTTP session object to store session details.
     * @return LoginResponse containing login status and user information.
     * @throws UnknownHostException If there is an issue with retrieving the IP address.
     * Author: Sidharth Guleria
     */
    public LoginResponse login(LoginRequest loginRequest, HttpServletRequest request, HttpSession session) throws UnknownHostException {
        Optional<AccountMaster> accountMasterOptional = accountMasterRepository.findByUsernameOrEmailOrPhone(
                loginRequest.getUsername(), loginRequest.getEmail(), loginRequest.getPhone()
        );

        if (accountMasterOptional.isPresent()) {
            AccountMaster accountMaster = accountMasterOptional.get();

            if (PasswordUtils.matchPassword(loginRequest.getPassword(), accountMaster.getPassword())) {
                // Retrieve the IP address and User Agent
                String ipAddress = getIpAddress(request);
                String userAgent = request.getHeader("User-Agent");

                // Store these details in the session
                session.setAttribute("ipAddress", ipAddress);
                session.setAttribute("userAgent", userAgent);
                boolean isEntityType101 = "101".equals(accountMaster.getEntityType());

                // Get entity details only if entityType is NOT 101
                EntityTable entityTable = isEntityType101 ? null : accountMaster.getEntityTable();

                // Use SessionUtils to manage session creation
                sessionUtils.createSession(
                        session,
                        accountMaster.getId(),
                        accountMaster.getUsername(),
                        accountMaster.getEmail(),
                        accountMaster.getPhone(),
                        accountMaster.getEntityType(),
                        entityTable != null ? entityTable.getId() : null, // Handle null case
                        entityTable != null ? entityTable.getName() : null,
                        entityTable != null ? entityTable.getDescription() : null,
                        entityTable != null ? entityTable.getPolicy() : null
                );

                LoginResponse loginResponse = getLoginResponse(session, accountMaster);

                // Save session details to the database
                saveSessionDetails(session, accountMaster);

                // Set success response details
                loginResponse.setStatusCode(200);
                loginResponse.setMessage("Login successful");

                return loginResponse;
            }

        }

        // If credentials are invalid, return a failure response
        LoginResponse errorResponse = new LoginResponse();
        errorResponse.setStatusCode(401);
        errorResponse.setMessage("Invalid credentials");

        return errorResponse;
    }

    /**
     * Helper method to create a response for a successful login.
     *
     * @param session The HTTP session for the current user.
     * @param accountMaster The AccountMaster object containing user details.
     * @return LoginResponse containing the user information and session ID.
     * Author: Sidharth Guleria
     */
    private static LoginResponse getLoginResponse(HttpSession session, AccountMaster accountMaster) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUsername(accountMaster.getUsername());
        loginResponse.setEmail(accountMaster.getEmail());
        loginResponse.setPhone(accountMaster.getPhone());
        loginResponse.setEntityType(accountMaster.getEntityType());
        loginResponse.setSessionKey(session.getId()); // Set sessionId as sessionKey

        // Set success message and status code
        loginResponse.setMessage("Login successful, session created.");
        loginResponse.setStatusCode(200); // HTTP 200 OK for successful login

        return loginResponse;
    }

    /**
     * Helper method to retrieve the IP address of the client from the request.
     *
     * @param request The HTTP request object containing client details.
     * @return The IP address of the client.
     * @throws UnknownHostException If there is an issue with retrieving the IP address.
     * Author: Sidharth Guleria
     */
    private String getIpAddress(HttpServletRequest request) throws UnknownHostException {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0];
        }
        return ipAddress;
    }
    /**
     * Helper method to save session details to the database.
     *
     * @param session The HTTP session object containing session data.
     * @param accountMaster The AccountMaster object containing user data.
     * Author: Sidharth Guleria
     */
    private void saveSessionDetails(HttpSession session, AccountMaster accountMaster) {
        String sessionId = session.getId();
        String ipAddress = (String) session.getAttribute("ipAddress");
        String userAgent = (String) session.getAttribute("userAgent");
        String payload = accountMaster.getUsername();  // You can store more details here

        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(30);  // Set expiry to 30 minutes

        Session sessionEntity = new Session();
        sessionEntity.setId(sessionId);
        sessionEntity.setUserId(accountMaster.getId());
        sessionEntity.setIpAddress(ipAddress);
        sessionEntity.setUserAgent(userAgent);
        sessionEntity.setPayload(payload);
        sessionEntity.setLastActivity((int) (System.currentTimeMillis() / 1000));  // Convert to UNIX timestamp
        sessionEntity.setExpiry(expiryTime);

        sessionRepository.save(sessionEntity);  // Save the session to the DB
    }
    /**
     * Method to log out a user by invalidating the session.
     *
     * @param session The HTTP session to be invalidated.
     * Author: Sidharth Guleria
     */
    public void logout(HttpSession session) {
        sessionUtils.invalidateSession(session);
    }
    /**
     * Method to validate the current session.
     * It checks if the session exists and whether it has expired.
     *
     * @param session The HTTP session to be validated.
     * @return SessionValidationResponse containing session validity status.
     * Author: Sidharth Guleria
     */
    public SessionValidationResponse validateSession(HttpSession session) {

        SessionValidationResponse response = new SessionValidationResponse();

        if (session != null && session.getAttribute("userId") != null) {
            response.setSessionValid(true);
            response.setMessage("Session is valid.");
            response.setStatusCode(200); // OK
            response.setUserId((Long) session.getAttribute("userId"));
            response.setUsername((String) session.getAttribute("username"));
            response.setEmail((String) session.getAttribute("email"));
            response.setPhone((String) session.getAttribute("phone"));
            response.setEntityType((String) session.getAttribute("entityType"));
            response.setEntityTableName((String) session.getAttribute("entityName"));
            response.setEntityTableDescription((String) session.getAttribute("entityTypeDesc"));
//           response.setEntityID(Long.valueOf((String) session.getAttribute("entityId")));
            response.setEntityID((Long) session.getAttribute("entityId"));
            response.setEntityTablePolicy((String) session.getAttribute("entityPolicy"));
        } else {
            response.setSessionValid(false);
            response.setMessage("Session has expired or is invalid.");
            response.setStatusCode(403); // Forbidden (Session Expired)
        }

        return response;

    }
    /**
     * Method to validate SuperAdmin session.
     * It checks if the session is valid and if the user is a SuperAdmin.
     *
     * @param session The HTTP session to be validated.
     * @return SessionValidationResponse containing session validity and SuperAdmin check result.
     * Author: Sidharth Guleria
     */
    public SessionValidationResponse validateSuperAdminSession(HttpSession session) {
        SessionValidationResponse response = validateSession(session);

        // If the session is invalid, return the response immediately
        if (!response.isSessionValid()) {
            response.setStatusCode(403); // Forbidden
            return response;
        }

        // Check if the user is SuperAdmin
        if (!"101".equals(response.getEntityType())) {
            response.setSessionValid(false);
            response.setMessage("Access Denied: User is not a SuperAdmin.");
            response.setStatusCode(403); // Forbidden
            return response;
        }

        // If SuperAdmin, set the status code and return required details
        response.setStatusCode(200); // OK
        return response;
    }
    /**
     * Method to validate Employee session.
     * It checks if the session is valid and if the user is a SuperAdmin.
     *
     * @param session The HTTP session to be validated.
     * @return SessionValidationResponse containing session validity and SuperAdmin check result.
     * Author: Sidharth Guleria
     */
    public SessionValidationResponse validateEmployeeSession(HttpSession session) {
        SessionValidationResponse response = validateSession(session);
        // If the session is invalid, return the response immediately
        if (!response.isSessionValid()) {
            response.setStatusCode(403); // Forbidden
            return response;
        }
        // Check if the user is SuperAdmin
        if (!"102".equals(response.getEntityType())) {
            response.setSessionValid(false);
            response.setMessage("Access Denied: Only Employee Have the Access To The Api.");
            response.setStatusCode(403); // Forbidden
            return response;
        }
        // If SuperAdmin, set the status code and return required details
        response.setStatusCode(200); // OK
        return response;
    }
}
