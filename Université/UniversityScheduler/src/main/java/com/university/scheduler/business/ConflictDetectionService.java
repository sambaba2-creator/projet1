package com.university.scheduler.business;

import com.university.scheduler.dao.*;
import com.university.scheduler.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ConflictDetectionService {
    private static final Logger logger = LoggerFactory.getLogger(ConflictDetectionService.class);
    private ReservationDAO reservationDAO;
    private CourseDAO courseDAO;
    private UserDAO userDAO;
    private RoomDAO roomDAO;

    public ConflictDetectionService() {
        this.reservationDAO = new ReservationDAO();
        this.courseDAO = new CourseDAO();
        this.userDAO = new UserDAO();
        this.roomDAO = new RoomDAO();
    }

    // constructor for dependency injection (useful for unit testing)
    public ConflictDetectionService(ReservationDAO reservationDAO,
                                    CourseDAO courseDAO,
                                    UserDAO userDAO,
                                    RoomDAO roomDAO) {
        this.reservationDAO = reservationDAO;
        this.courseDAO = courseDAO;
        this.userDAO = userDAO;
        this.roomDAO = roomDAO;
    }

    /**
     * Check conflicts when creating a new reservation or course
     * Returns a list of conflicts found
     */
    public List<ConflictDetails> checkForConflicts(Reservation reservation) {
        List<ConflictDetails> conflicts = new ArrayList<>();

        // Check room conflict
        if (hasRoomConflict(reservation)) {
            conflicts.add(new ConflictDetails(
                    "ROOM",
                    "La salle est déjà réservée à cet horaire"
            ));
        }

        return conflicts;
    }

    /**
     * Check room conflicts
     * Two reservations conflict if one starts before the other ends and ends after the other starts
     */
    private boolean hasRoomConflict(Reservation reservation) {
        List<Reservation> roomReservations = reservationDAO.getReservationsByRoomAndDate(
                reservation.getRoomId(),
                reservation.getDate()
        );

        LocalTime newStart = parseTime(reservation.getStartTime());
        LocalTime newEnd = parseTime(reservation.getEndTime());

        for (Reservation existing : roomReservations) {
            if (existing.getStatus().equals("CANCELLED")) {
                continue;
            }

            LocalTime existingStart = parseTime(existing.getStartTime());
            LocalTime existingEnd = parseTime(existing.getEndTime());

            // Conflict if: newStart < existingEnd AND newEnd > existingStart
            if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check teacher conflicts
     * A teacher cannot teach two courses at the same time
     */
    public boolean hasTeacherConflict(Course course) {
        List<Course> teacherCourses = courseDAO.getCoursesByTeacher(course.getTeacherId());

        LocalTime newStart = parseTime(course.getStartTime());
        LocalTime newEnd = parseTime(course.getEndTime());

        for (Course existing : teacherCourses) {
            if (existing.getId() == course.getId()) {
                continue; // Skip if same course
            }

            if (!existing.getDayOfWeek().equals(course.getDayOfWeek())) {
                continue; // Different day, no conflict
            }

            LocalTime existingStart = parseTime(existing.getStartTime());
            LocalTime existingEnd = parseTime(existing.getEndTime());

            // Check for time overlap
            if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check class conflicts
     * A class cannot have two courses at the same time
     */
    public boolean hasClassConflict(Course course) {
        List<Course> classCourses = courseDAO.getCoursesByClass(course.getClassId());

        LocalTime newStart = parseTime(course.getStartTime());
        LocalTime newEnd = parseTime(course.getEndTime());

        for (Course existing : classCourses) {
            if (existing.getId() == course.getId()) {
                continue; // Skip if same course
            }

            if (!existing.getDayOfWeek().equals(course.getDayOfWeek())) {
                continue; // Different day, no conflict
            }

            LocalTime existingStart = parseTime(existing.getStartTime());
            LocalTime existingEnd = parseTime(existing.getEndTime());

            // Check for time overlap
            if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRoomConflict(Course course) {
        List<Course> roomCourses = courseDAO.getCoursesByRoom(course.getRoomId());

        LocalTime newStart = parseTime(course.getStartTime());
        LocalTime newEnd = parseTime(course.getEndTime());

        for (Course existing : roomCourses) {
            if (existing.getId() == course.getId()) {
                continue; // Skip if same course
            }

            if (!existing.getDayOfWeek().equals(course.getDayOfWeek())) {
                continue;
            }

            LocalTime existingStart = parseTime(existing.getStartTime());
            LocalTime existingEnd = parseTime(existing.getEndTime());

            if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a room is available at a specific time and date
     */
    public boolean isRoomAvailable(int roomId, String date, String startTime, String endTime) {
        List<Reservation> reservations = reservationDAO.getReservationsByRoomAndDate(roomId, date);

        LocalTime newStart = parseTime(startTime);
        LocalTime newEnd = parseTime(endTime);

        for (Reservation existing : reservations) {
            if (existing.getStatus().equals("CANCELLED")) {
                continue;
            }

            LocalTime existingStart = parseTime(existing.getStartTime());
            LocalTime existingEnd = parseTime(existing.getEndTime());

            if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convert time string to LocalTime
     */
    private LocalTime parseTime(String time) {
        try {
            if (time.contains("h")) {
                time = time.replace("h", ":");
            }
            return LocalTime.parse(time.trim(), DateTimeFormatter.ofPattern("H:mm"));
        } catch (Exception e) {
            logger.warn("Invalid time format: {}", time);
            return null;
        }
    }

    public boolean isValidTimeFormat(String time) {
        return parseTime(time) != null;
    }

    /**
     * Class to represent conflict details
     */
    public static class ConflictDetails {
        private String type;
        private String message;

        public ConflictDetails(String type, String message) {
            this.type = type;
            this.message = message;
        }

        public String getType() { return type; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return type + ": " + message;
        }
    }
}
