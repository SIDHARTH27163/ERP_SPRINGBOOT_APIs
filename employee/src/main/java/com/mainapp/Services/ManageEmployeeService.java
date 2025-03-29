package com.mainapp.Services;
import com.mainapp.repository.AccountMasterRepository;
import com.mainapp.repository.EntityRepository;
import com.mainapp.repository.EntityRoleMasterRepository;
import com.mainapp.repository.RoleMasterRepository;
import com.mainapp.dto.AddEmployeeRequest;
import com.mainapp.dto.EmployeeResponse;
import com.mainapp.models.AccountMaster;
import com.mainapp.models.EntityRoleMaster;
import com.mainapp.models.EntityTable;
import com.mainapp.models.RoleMaster;
import com.mainapp.enums.Status;
import com.mainapp.utils.CommonUtils;
import com.mainapp.utils.PasswordUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class to handle employee management operations such as adding, updating,
 * retrieving, and deleting employees.
 *
 * @author Sidharth Guleria
 */
@Service
public class ManageEmployeeService {

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private AccountMasterRepository accountMasterRepository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private RoleMasterRepository roleMasterRepository;

    @Autowired
    private EntityRoleMasterRepository entityRoleMasterRepository;

    @Autowired
    private PasswordUtils passwordUtils;

    /**
     * Adds a new employee to the system. It associates the employee with an entity
     * and assigns a role. The username is generated uniquely, and a secure password is assigned.
     *
     * @param request   Employee details received in the request.
     * @param createdBy ID of the user who is creating this employee.
     * @param session   HTTP session containing entity information.
     */
    @Transactional
    public AccountMaster addEmployee(AddEmployeeRequest request, Long createdBy, HttpSession session) {
        // Check if email already exists
        if (accountMasterRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Email is already registered!");
        }

        // Check if phone already exists
        if (accountMasterRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Phone number is already in use!");
        }

        Long entityId = (Long) session.getAttribute("entityId");
        EntityTable entityTable = entityRepository.findById(entityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found for ID: " + entityId));

        // Fetch RoleMaster from request
        RoleMaster roleMaster = roleMasterRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found for ID: " + request.getRoleId()));

        // Create and save EntityRoleMaster
        EntityRoleMaster entityRoleMaster = new EntityRoleMaster();
        entityRoleMaster.setEntityTable(entityTable);
        entityRoleMaster.setRoleMaster(roleMaster);
        entityRoleMaster.setName(roleMaster.getName());
        entityRoleMaster.setDescription(roleMaster.getDescription());
        entityRoleMaster.setStatus(Status.ACTIVE);
        entityRoleMaster.setPolicy(commonUtils.generatePolicyNumber());
        entityRoleMaster.setCreatedBy(createdBy);

        entityRoleMasterRepository.save(entityRoleMaster);

        // Generate unique username
        String uniqueUsername = commonUtils.ensureUniqueUsername(
                commonUtils.generateUsername(request.getFirstName(), request.getLastName()));

        // Generate and hash password before saving
        String rawPassword = passwordUtils.generateSecurePassword();
        String hashedPassword = passwordUtils.hashPassword(rawPassword); // Ensure password is hashed

        // Create and save AccountMaster
        AccountMaster accountMaster = new AccountMaster();
        accountMaster.setUsername(uniqueUsername);
        accountMaster.setEmail(request.getEmail());
        accountMaster.setPhone(request.getPhone());
        accountMaster.setPassword(hashedPassword); // Store hashed password
        accountMaster.setEntityType(request.getEntityType());
        accountMaster.setFirstName(request.getFirstName());
        accountMaster.setLastName(request.getLastName());
        accountMaster.setEntityTable(entityTable);
        accountMaster.setCreatedBy(createdBy);
        accountMaster.setCreatedAt(LocalDateTime.now());
        accountMaster.setUpdatedAt(LocalDateTime.now());
        accountMaster.setPolicy(commonUtils.generatePolicyNumber());
        accountMaster.setEntityRoleMaster(entityRoleMaster);

        return accountMasterRepository.save(accountMaster);
    }

    /**
     * Retrieves a list of employees based on the entity type.
     *
     * @param entityType The type of entity employees belong to.
     * @param session    HTTP session containing entity information.
     * @return A list of employee details.
     */
    public List<EmployeeResponse> getEmployeesByEntityType(String entityType, HttpSession session) {
        Long entityId = (Long) session.getAttribute("entityId");
        if (entityId == null) {
            throw new RuntimeException("Entity ID not found in session.");
        }

        List<AccountMaster> accounts = accountMasterRepository.findByEntityTypeAndEntityTable_Id(entityType, entityId);

        return accounts.stream()
                .map(account -> new EmployeeResponse(
                        account.getId(),
                        account.getFirstName(),
                        account.getLastName(),
                        account.getUsername(),
                        account.getEmail(),
                        account.getPhone(),
                        account.getEntityType(),
                        account.getEntityRoleMaster().getName()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves employee details by ID.
     *
     * @param id Employee ID.
     * @return EmployeeResponse object containing employee details.
     */
    public EmployeeResponse getAccountById(Long id) {
        AccountMaster account = accountMasterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found for ID: " + id));

        return new EmployeeResponse(
                account.getId(),
                account.getFirstName(),
                account.getLastName(),
                account.getUsername(),
                account.getEmail(),
                account.getPhone(),
                account.getEntityType(),
                account.getEntityRoleMaster() != null ? account.getEntityRoleMaster().getName() : null
        );
    }

    /**
     * Updates an existing employee's details.
     *
     * @param id      Employee ID to be updated.
     * @param request The updated employee details.
     * @param session HTTP session containing user information.
     * @return EmployeeResponse object with updated details.
     */
    public EmployeeResponse updateAccount(Long id, AccountMaster request, HttpSession session) {
        AccountMaster account = accountMasterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Update only allowed fields (excluding entityType)
        account.setFirstName(request.getFirstName());
        account.setLastName(request.getLastName());
        account.setEmail(request.getEmail());
        account.setPhone(request.getPhone());
        account.setUpdatedBy((Long) session.getAttribute("userId"));

        accountMasterRepository.save(account);

        return new EmployeeResponse(
                account.getId(),
                account.getFirstName(),
                account.getLastName(),
                account.getUsername(),
                account.getEmail(),
                account.getPhone(),
                account.getEntityType(), // Keep existing entityType from DB
                account.getEntityRoleMaster() != null ? account.getEntityRoleMaster().getName() : null
        );
    }




    /**
     * Deletes an employee account by ID.
     *
     * @param id Employee ID to be deleted.
     */
    public void deleteAccount(Long id) {
        if (!accountMasterRepository.existsById(id)) {
            throw new RuntimeException("Account not found for ID: " + id);
        }
        accountMasterRepository.deleteById(id);
    }
    /**
     * Toggles the status of an employee.
     * If the current status is ACTIVE, it becomes INACTIVE.
     * If the current status is INACTIVE, it becomes ACTIVE.
     *
     * @param id The ID of the employee whose status needs to be toggled.
     * @throws RuntimeException if the employee account is not found.
     */
    @Transactional
    public void toggleEmployeeStatus(Long id) {
        // Retrieve the employee account by ID or throw an exception if not found
        AccountMaster account = accountMasterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + id));

        // Toggle status
        account.setStatus(account.getStatus() == Status.ACTIVE ? Status.INACTIVE : Status.ACTIVE);

        // Save updated status
        accountMasterRepository.save(account);
    }
}
