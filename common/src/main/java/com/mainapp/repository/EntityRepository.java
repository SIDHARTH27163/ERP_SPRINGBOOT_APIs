package com.mainapp.repository;

import com.mainapp.dto.EntityDTO;
import com.mainapp.models.EntityTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing EntityTable database operations.
 * Provides methods for retrieving entities based on specific criteria.
 *
 * Author: Pankaj Kataria
 */
@Repository
public interface EntityRepository extends JpaRepository<EntityTable, Long> {

        /**
         * Finds a list of entities based on their type.
         *
         * @param type The type of entities to retrieve.
         * @return A list of EntityDTO objects that match the specified type.
         */
        List<EntityDTO> findByType(String type);
        Optional<EntityTable> findById(Long id);

//        Optional<EntityTable> findByentity_Id(Long id);
}
