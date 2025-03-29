package com.mainapp.utils;

import com.mainapp.repository.AccountMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CommonUtils {
    @Autowired
    private AccountMasterRepository accountMasterRepository;
    /**
     * Generates a username using first name, last name, and entity name.
     */
    public String generateUsername(String firstName, String lastName) {
        String baseUsername = (firstName + "." + lastName ).toLowerCase();
        return baseUsername.replaceAll("\\s+", ""); // Remove spaces
    }
    /**
     * Ensures the generated username is unique by appending a number if needed.
     */
    public String ensureUniqueUsername(String baseUsername) {
        String username = baseUsername;
        int count = 1;

        while (accountMasterRepository.existsByUsername(username)) {  // Check database for duplicates
            username = baseUsername + count;  // Append a number if the username exists
            count++;
        }
        return username;
    }
    /**
     * Generates a unique policy number for entities and accounts.
     *
     * @return A randomly generated 16-digit policy number.
     */
    public String generatePolicyNumber() {
        return String.format("%016d", new Random().nextLong() & Long.MAX_VALUE);
    }
}
