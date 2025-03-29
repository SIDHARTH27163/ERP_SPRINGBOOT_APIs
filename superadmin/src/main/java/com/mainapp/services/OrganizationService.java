package com.mainapp.services;

import com.mainapp.repository.AccountMasterRepository;
import com.mainapp.repository.EntityRepository;
import com.mainapp.repository.EntityRoleMasterRepository;
import com.mainapp.repository.RoleMasterRepository;
import com.mainapp.dto.CreateOrganizationRequest;
import com.mainapp.dto.EntityDTO;
import com.mainapp.dto.UpdateOrganization;
import com.mainapp.models.AccountMaster;
import com.mainapp.models.EntityRoleMaster;
import com.mainapp.models.EntityTable;
import com.mainapp.models.RoleMaster;
import com.mainapp.enums.Status;
import com.mainapp.utils.CommonUtils;
import com.mainapp.utils.PasswordUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing organization-related operations.
 * Handles entity creation, user account creation, and retrieval of entities.
 *
 * Author: Pankaj Kataria
 */
@Service
public class OrganizationService {

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private AccountMasterRepository accountMasterRepository;
    @Autowired
    private RoleMasterRepository roleMasterRepository;
    @Autowired
    private PasswordUtils passwordUtils;
    @Autowired
    private CommonUtils commonUtils;
    @Autowired
    private EntityRoleMasterRepository entityRoleMasterRepository;
    /**
     * Creates a new user along with their associated entity.
     *
     * @param request   The request containing user and entity details.
     * @param createdBy The ID of the user creating the new entity.
     */
    @Transactional
    public void createUser(CreateOrganizationRequest request, Long createdBy) {
        RoleMaster roleMaster = roleMasterRepository.findById(request.getRoleId()).orElseThrow(
                () -> new RuntimeException("RoleMaster not found for ID: " + request.getRoleId())
        );


        // Creating a new entity and saving it in the database
        EntityTable entity = new EntityTable();
        entity.setName(request.getEntityName());
        entity.setDescription(request.getEntityDescription());
        entity.setType(request.getEntityType());
        entity.setPolicy(commonUtils.generatePolicyNumber());
        entity.setCreatedBy(createdBy);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entityRepository.save(entity);


        EntityRoleMaster entityRoleMaster = new EntityRoleMaster();
        entityRoleMaster.setEntityTable(entity);
        entityRoleMaster.setRoleMaster(roleMaster);
        entityRoleMaster.setName(roleMaster.getName());
        entityRoleMaster.setDescription(roleMaster.getDescription());
        entityRoleMaster.setStatus(Status.ACTIVE);
        entityRoleMaster.setPolicy(commonUtils.generatePolicyNumber());
        entityRoleMaster.setCreatedBy(createdBy);

        entityRoleMasterRepository.save(entityRoleMaster);
        // Generate a unique username from first name, last name, and entity name
        String baseUsername = commonUtils.generateUsername(request.getFirstName(), request.getLastName());
        String uniqueUsername = commonUtils.ensureUniqueUsername(baseUsername); // Ensure uniqueness

        // Generating a secure password and creating an account for the user
        String generatedPassword = passwordUtils.generateSecurePassword();
        AccountMaster accountMaster = new AccountMaster();
        accountMaster.setUsername(uniqueUsername);
        accountMaster.setEmail(request.getEmail());
        accountMaster.setPhone(request.getPhone());
        accountMaster.setPassword(generatedPassword);
        accountMaster.setEntityType(request.getEntityType());
        accountMaster.setFirstName(request.getFirstName());
        accountMaster.setLastName(request.getLastName());
        accountMaster.setEntityTable(entity);
        accountMaster.setCreatedBy(createdBy);
        accountMaster.setCreatedAt(LocalDateTime.now());
        accountMaster.setUpdatedAt(LocalDateTime.now());
        accountMaster.setPolicy(commonUtils.generatePolicyNumber());
        accountMaster.setEntityRoleMaster(entityRoleMaster);
        accountMasterRepository.save(accountMaster);
    }





    /**
     * Retrieves a list of entities based on a predefined type.
     *
     * @return A list of EntityDTO objects representing entities of type "102".
     */
    public List<EntityDTO> getAllEntities() {
        return entityRepository.findByType("102");
    }
    public Optional<EntityTable> GetEntity(Long id) {
        return entityRepository.findById( id);
    }

    /**
     * Updates an existing entity based on the provided ID and request data.
     *
     * @param id        The unique identifier of the entity to be updated.
     * @param request   The request body containing updated entity details.
     * @param createdBy The ID of the user performing the update.
     * @throws EntityNotFoundException if the entity with the given ID is not found.
     * @author Pankaj Kataria
     */
    @Transactional
    public void UpdateEntity(Long id, UpdateOrganization request , Long createdBy) {
        // Find the entity by ID
        EntityTable entity = entityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id " + id + " not found"));
        // Update the entity with values from the DTO
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(createdBy);

        // Save the updated entity
        entityRepository.save(entity);
    }

    /**
     * Deletes an entity by its ID.
     *
     * @param id The unique identifier of the entity to be deleted.
     * @return A message indicating whether the deletion was successful or if the entity was not found.
     * @author Pankaj Kataria
     */
//    @Autowired
    public void  deleteEntity(Long id) {
        Optional<EntityTable> entity = entityRepository.findById(id);
        entityRepository.deleteById(id);
    }

}
