package com.mainapp.Controllers;

import com.mainapp.Services.AttendanceService;
import com.mainapp.models.Attendance;
import com.mainapp.dto.SessionValidationResponse;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AttendanceController handles API endpoints related to employee attendance.
 * It provides functionalities for checking in, checking out, retrieving today's attendance,
 * and fetching attendance history within a specified date range.
 * <p>
 * Author: Sidharth Guleria
 */
@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * API endpoint for employee check-in.
     * <p>
     * Validates the employee session and registers the check-in time if the employee has not
     * already checked in for the day.
     *
     * @param session The current HTTP session containing employee details.
     * @return ResponseEntity containing the session validation response with status code.
     */
    @PostMapping("/check-in")
    public ResponseEntity<SessionValidationResponse> checkIn(HttpSession session) {
        SessionValidationResponse response = attendanceService.checkIn(session);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * API endpoint for employee check-out.
     * <p>
     * Validates the employee session and records the check-out time if the employee has
     * already checked in but not yet checked out.
     *
     * @param session The current HTTP session containing employee details.
     * @return ResponseEntity containing the session validation response with status code.
     */
    @PostMapping("/check-out")
    public ResponseEntity<SessionValidationResponse> checkOut(HttpSession session) {
        SessionValidationResponse response = attendanceService.checkOut(session);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * API endpoint to retrieve today's attendance for the logged-in employee.
     * <p>
     * Checks if the employee has a valid session and fetches their attendance record for today.
     *
     * @param session The current HTTP session containing employee details.
     * @return ResponseEntity containing attendance details if found, otherwise an error message.
     */
    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodayAttendance(HttpSession session) {
        Optional<Attendance> attendance = attendanceService.getTodayAttendance(session);

        Map<String, Object> response = new HashMap<>();

        if (attendance.isPresent()) {
            response.put("status", "success");
            response.put("attendance", attendance.get());
            response.put("code", 200);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Unauthorized or No Attendance Found");
            response.put("code", 403);
            return ResponseEntity.status(403).body(response);
        }
    }

    /**
     * API endpoint to retrieve attendance history within a specified date range.
     * <p>
     * Fetches all attendance records for the logged-in employee between the given start and end dates.
     *
     * @param session The current HTTP session containing employee details.
     * @param start   The start date in ISO format (e.g., "2024-03-01T00:00:00").
     * @param end     The end date in ISO format (e.g., "2024-03-10T23:59:59").
     * @return ResponseEntity containing a list of attendance records within the date range.
     */
    @GetMapping("/history")
    public ResponseEntity<List<Attendance>> getAttendanceBetweenDates(
            HttpSession session,
            @RequestParam("start") String start,
            @RequestParam("end") String end) {

        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);

        List<Attendance> attendanceList = attendanceService.getAttendanceBetweenDates(session, startDate, endDate);
        return ResponseEntity.ok(attendanceList);
    }
}
