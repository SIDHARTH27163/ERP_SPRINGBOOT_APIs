package com.mainapp.repository;

import com.mainapp.models.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Find a single attendance record (for check-in validation)
    Optional<Attendance> findFirstByEmployeeIdAndCheckInTimeBetween(Long employeeId, LocalDateTime startDate, LocalDateTime endDate);

    // Find all attendance records (for history retrieval)
    List<Attendance> findAllByEmployeeIdAndCheckInTimeBetween(Long employeeId, LocalDateTime startDate, LocalDateTime endDate);
}
