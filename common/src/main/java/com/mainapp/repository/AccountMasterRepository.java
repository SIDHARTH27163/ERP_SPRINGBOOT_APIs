package com.mainapp.repository;


import com.mainapp.models.AccountMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountMasterRepository extends JpaRepository<AccountMaster, Long> {
    Optional<AccountMaster> findByUsernameOrEmailOrPhone(String username, String email, String phone);
    @Query("SELECT COUNT(a) > 0 FROM AccountMaster a WHERE a.username = :username")
    boolean existsByUsername(@Param("username") String username);

List<AccountMaster> findByEntityType(String entityType);
    List<AccountMaster> findByEntityTypeAndEntityTable_Id(String entityType, Long entityTableId);
    Optional<AccountMaster> findById(Long id);
    void deleteById(Long id);
    Optional<AccountMaster> findByEmail(String email);
    Optional<AccountMaster> findByPhone(String phone);
}
