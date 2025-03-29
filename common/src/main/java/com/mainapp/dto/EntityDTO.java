package com.mainapp.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) interface for representing entity details.
 * Provides methods to retrieve entity attributes and associated accounts.
 *
 * Author: Pankaj Kataria
 */
public interface EntityDTO {

    /**
     * Retrieves the entity ID.
     *
     * @return The unique identifier of the entity.
     */
    Long getId();

    /**
     * Retrieves the name of the entity.
     *
     * @return The entity name.
     */
    String getName();

    /**
     * Retrieves the description of the entity.
     *
     * @return The entity description.
     */
    String getDescription();

    /**
     * Retrieves the type of the entity.
     *
     * @return The entity type.
     */
    String getType();

    /**
     * Retrieves the policy number associated with the entity.
     *
     * @return The entity's policy number.
     */
    String getPolicy();

    /**
     * Retrieves the list of associated accounts for the entity.
     *
     * @return A list of AccountDTO representing account details.
     */
    List<AccountDTO> getAccounts();

    /**
     * Nested interface representing account details associated with an entity.
     */
    interface AccountDTO {

        /**
         * Retrieves the account ID.
         *
         * @return The unique identifier of the account.
         */
        Long getId();

        /**
         * Retrieves the email associated with the account.
         *
         * @return The account's email address.
         */
        String getEmail();

        /**
         * Retrieves the phone number associated with the account.
         *
         * @return The account's phone number.
         */
        String getPhone();

        /**
         * Retrieves the policy number associated with the account.
         *
         * @return The account's policy number.
         */
        String getPolicy();
    }
}
