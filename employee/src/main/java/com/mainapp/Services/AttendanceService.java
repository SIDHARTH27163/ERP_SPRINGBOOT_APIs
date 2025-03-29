package com.mainapp.Services;
import com.mainapp.models.Attendance;
import com.mainapp.models.AccountMaster;
import com.mainapp.enums.AttendanceStatus;
import com.mainapp.repository.AttendanceRepository;
import com.mainapp.dto.SessionValidationResponse;
import com.mainapp.services.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * AttendanceService is responsible for handling all operations related to employee attendance,
 * including check-in, check-out, and fetching attendance records.
 * <p>
 * Author: Sidharth Guleria
 */
@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AuthService authService; // Handles session validation

    /**
     * Handles employee check-in.
     * <p>
     * This method first validates the employee session. If the session is valid, it checks whether
     * the employee has already checked in for the day. If an existing check-in record is found,
     * the method returns an error message. Otherwise, a new attendance entry is created,
     * storing the check-in time and marking the employee as "PRESENT".
     *
     * @param session The current HTTP session containing employee details.
     * @return A SessionValidationResponse indicating success or failure.
     */
    public SessionValidationResponse checkIn(HttpSession session) {
        SessionValidationResponse response = authService.validateEmployeeSession(session);
        if (!response.isSessionValid()) return response;

        Long employeeId = response.getUserId();
        LocalDateTime now = LocalDateTime.now();

        Optional<Attendance> existingAttendance = attendanceRepository
                .findFirstByEmployeeIdAndCheckInTimeBetween(employeeId, now.toLocalDate().atStartOfDay(), now.toLocalDate().atTime(23, 59));

        if (existingAttendance.isPresent()) {
            response.setMessage("You have already checked in today.");
            response.setStatusCode(400);
            return response;
        }

        Attendance attendance = new Attendance();
        AccountMaster employee = new AccountMaster();
        employee.setId(employeeId);

        attendance.setEmployee(employee);
        attendance.setCheckInTime(now);
        attendance.setStatus(AttendanceStatus.PRESENT);

        attendanceRepository.save(attendance);

        response.setMessage("Check-in successful.");
        response.setStatusCode(200);
        return response;
    }

    /**
     * Handles employee check-out.
     * <p>
     * This method first validates the employee session. If the session is valid, it checks if
     * the employee has a valid check-in record for the current day. If no check-in record is found,
     * an error message is returned. If the employee has already checked out, another error message
     * is sent. Otherwise, the check-out time is recorded, and total hours worked are calculated.
     *
     * @param session The current HTTP session containing employee details.
     * @return A SessionValidationResponse indicating success or failure.
     */
    public SessionValidationResponse checkOut(HttpSession session) {
        SessionValidationResponse response = authService.validateEmployeeSession(session);
        if (!response.isSessionValid()) return response;

        Long employeeId = response.getUserId();
        LocalDateTime now = LocalDateTime.now();

        List<Attendance> attendanceList = attendanceRepository
                .findAllByEmployeeIdAndCheckInTimeBetween(employeeId, now.toLocalDate().atStartOfDay(), now.toLocalDate().atTime(23, 59));

        if (attendanceList.isEmpty()) {
            response.setMessage("No check-in record found for today.");
            response.setStatusCode(400);
            return response;
        }

        Attendance attendance = attendanceList.get(0);
        if (attendance.getCheckOutTime() != null) {
            response.setMessage("You have already checked out today.");
            response.setStatusCode(400);
            return response;
        }

        attendance.setCheckOutTime(now);
        Duration workedHours = Duration.between(attendance.getCheckInTime(), now);
        attendance.setTotalHours(workedHours.toHours() + (workedHours.toMinutesPart() / 60.0));

        attendanceRepository.save(attendance);

        response.setMessage("Check-out successful.");
        response.setStatusCode(200);
        return response;
    }

    /**
     * Retrieves today's attendance record for the logged-in employee.
     * <p>
     * This method first validates the employee session. If the session is valid,
     * it fetches the employee's attendance record for the current day.
     *
     * @param session The current HTTP session containing employee details.
     * @return An Optional containing the attendance record if found, otherwise empty.
     */
    public Optional<Attendance> getTodayAttendance(HttpSession session) {
        SessionValidationResponse response = authService.validateEmployeeSession(session);
        if (!response.isSessionValid()) return Optional.empty();

        Long employeeId = response.getUserId();
        LocalDateTime now = LocalDateTime.now();

        return attendanceRepository.findFirstByEmployeeIdAndCheckInTimeBetween(employeeId, now.toLocalDate().atStartOfDay(), now.toLocalDate().atTime(23, 59));
    }

    /**
     * Retrieves a list of attendance records for the logged-in employee within a specified date range.
     * <p>
     * This method first validates the employee session. If the session is valid,
     * it fetches all attendance records for the employee within the given start and end dates.
     *
     * @param session The current HTTP session containing employee details.
     * @param startDate The starting date and time of the range.
     * @param endDate The ending date and time of the range.
     * @return A list of Attendance records within the specified date range.
     */
    public List<Attendance> getAttendanceBetweenDates(HttpSession session, LocalDateTime startDate, LocalDateTime endDate) {
        SessionValidationResponse response = authService.validateEmployeeSession(session);
        if (!response.isSessionValid()) return List.of();

        Long employeeId = response.getUserId();
        return attendanceRepository.findAllByEmployeeIdAndCheckInTimeBetween(employeeId, startDate, endDate);
    }
}
