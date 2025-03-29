package com.mainapp.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PasswordUtils {

    /*
     * Hash the password using BCrypt
     * This method takes the raw password and returns the hashed version using BCrypt with a work factor of 12.
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    /*
     * Verify the raw password against the hashed password
     * This method compares the raw password with the hashed password to check if they match.
     */
    public static boolean matchPassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }

    /*
     * Generate a secure random password
     * This method creates a 16-byte random password, then encodes it into a Base64 string for secure use.
     */
    public String generateSecurePassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes); // Encoded random password
    }
}
