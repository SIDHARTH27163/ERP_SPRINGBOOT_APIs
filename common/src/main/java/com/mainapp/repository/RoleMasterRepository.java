package com.mainapp.repository;

import com.mainapp.models.RoleMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing EntityTable database operations.
 * Provides methods for retrieving entities based on specific criteria.
 *
 * Author: Pankaj Kataria
 */
@Repository
public interface RoleMasterRepository extends JpaRepository<RoleMaster, Long> {

    /**
     * Finds a list of entities based on their type.
     *
     * @param type The type of entities to retrieve.
     * @return A list of EntityDTO objects that match the specified type.
     */

}
