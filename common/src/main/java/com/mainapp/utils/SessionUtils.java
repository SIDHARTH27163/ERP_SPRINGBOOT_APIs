package com.mainapp.utils;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
/*
 * Author: Sidharth Guleria
 */
@Component
public class SessionUtils {

    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String ENTITY_TYPE = "entityType";
    private static final String ROLE = "role";

    /*
     * Create a session with the provided user details
     * This method creates a session and sets attributes like userId, username, email, phone, and entityType in the session.
     * The role attribute is currently commented out but can be added when required.
     */
    public void createSession(HttpSession session, Long userId, String username, String email, String phone, String entityType, Long entityId,
                              String entityName, String entityTypeDesc, String entityPolicy ) {
        session.setAttribute(USER_ID, userId);
        session.setAttribute(USERNAME, username);
        session.setAttribute(EMAIL, email);
        session.setAttribute(PHONE, phone);
        session.setAttribute(ENTITY_TYPE, entityType);
        session.setAttribute("entityId" ,entityId );
        session.setAttribute("entityName", entityName);
        session.setAttribute("entityTypeDesc", entityTypeDesc);
        session.setAttribute("entityPolicy", entityPolicy);

        // session.setAttribute(ROLE, role); // Uncomment this line to add role to the session
    }


    /*
     * Get the username from the session
     * This method retrieves the username stored in the session.
     */
    public String getUsername(HttpSession session) {
        return (String) session.getAttribute(USERNAME);
    }

    /*
     * Get the userId from the session
     * This method retrieves the userId stored in the session.
     */
    public Long getUserId(HttpSession session) {
        return (Long) session.getAttribute(USER_ID);
    }

    /*
     * Get the email from the session
     * This method retrieves the email stored in the session.
     */
    public String getEmail(HttpSession session) {
        return (String) session.getAttribute(EMAIL);
    }

    /*
     * Get the phone number from the session
     * This method retrieves the phone number stored in the session.
     */
    public String getPhone(HttpSession session) {
        return (String) session.getAttribute(PHONE);
    }

    /*
     * Get the entity type from the session
     * This method retrieves the entity type stored in the session.
     */
    public String getEntityType(HttpSession session) {
        return (String) session.getAttribute(ENTITY_TYPE);
    }

    /*
     * Get the role from the session
     * This method retrieves the role stored in the session.
     */
    public String getRole(HttpSession session) {
        return (String) session.getAttribute(ROLE);
    }

    /*
     * Invalidate the session
     * This method invalidates the session, removing all session attributes and making the session unusable.
     */
    public void invalidateSession(HttpSession session) {
        session.invalidate();
    }
}


