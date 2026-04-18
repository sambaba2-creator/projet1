package com.university.scheduler.business;

import com.university.scheduler.dao.*;
import com.university.scheduler.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class StatisticsService {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);
    private ReservationDAO reservationDAO;
    private RoomDAO roomDAO;
    private CourseDAO courseDAO;

    public StatisticsService() {
        this.reservationDAO = new ReservationDAO();
        this.roomDAO = new RoomDAO();
        this.courseDAO = new CourseDAO();
    }

    /**
     * Calculate occupancy rate for a room
     * Occupancy rate = (hours used / total available hours) * 100
     */
    public double calculateOccupancyRate(int roomId, String date) {
        List<Reservation> dayReservations = reservationDAO.getReservationsByRoomAndDate(roomId, date);

        double hoursUsed = 0;
        for (Reservation res : dayReservations) {
            if (!res.getStatus().equals("CANCELLED")) {
                hoursUsed += calculateDuration(res.getStartTime(), res.getEndTime());
            }
        }

        double totalHours = 8; // Assuming 8 hours working day
        return (hoursUsed / totalHours) * 100;
    }

    /**
     * Get rooms that are critically used (>90% occupancy)
     */
    public List<RoomStatistic> getCriticalRooms(String date) {
        List<RoomStatistic> criticalRooms = new ArrayList<>();
        List<Room> allRooms = roomDAO.getAllRooms();

        for (Room room : allRooms) {
            double occupancy = calculateOccupancyRate(room.getId(), date);
            if (occupancy > 90) {
                criticalRooms.add(new RoomStatistic(room, occupancy, "CRITICAL"));
            }
        }

        return criticalRooms;
    }

    /**
     * Get rooms that are underutilized (<20% occupancy)
     */
    public List<RoomStatistic> getUnderutilizedRooms(String date) {
        List<RoomStatistic> underutilizedRooms = new ArrayList<>();
        List<Room> allRooms = roomDAO.getAllRooms();

        for (Room room : allRooms) {
            double occupancy = calculateOccupancyRate(room.getId(), date);
            if (occupancy < 20) {
                underutilizedRooms.add(new RoomStatistic(room, occupancy, "UNDERUTILIZED"));
            }
        }

        return underutilizedRooms;
    }

    /**
     * Get all room statistics
     */
    public List<RoomStatistic> getAllRoomStatistics(String date) {
        List<RoomStatistic> statistics = new ArrayList<>();
        List<Room> allRooms = roomDAO.getAllRooms();

        for (Room room : allRooms) {
            double occupancy = calculateOccupancyRate(room.getId(), date);
            String status = getStatus(occupancy);
            statistics.add(new RoomStatistic(room, occupancy, status));
        }

        return statistics;
    }

    /**
     * Get room usage report
     */
    public Map<String, Object> getRoomUsageReport(int roomId, String startDate, String endDate) {
        Map<String, Object> report = new HashMap<>();

        Room room = roomDAO.getRoomById(roomId);
        List<Reservation> reservations = reservationDAO.getReservationsByRoom(roomId);

        double totalHoursUsed = 0;
        int reservationCount = 0;

        for (Reservation res : reservations) {
            if (!res.getStatus().equals("CANCELLED") && isDateInRange(res.getDate(), startDate, endDate)) {
                totalHoursUsed += calculateDuration(res.getStartTime(), res.getEndTime());
                reservationCount++;
            }
        }

        report.put("roomId", roomId);
        report.put("roomNumber", room.getNumber());
        report.put("totalHoursUsed", totalHoursUsed);
        report.put("reservationCount", reservationCount);
        report.put("avgDuration", reservationCount > 0 ? totalHoursUsed / reservationCount : 0);

        return report;
    }

    /**
     * Get teacher workload statistics
     */
    public Map<String, Object> getTeacherWorkload(int teacherId) {
        Map<String, Object> stats = new HashMap<>();

        List<Course> courses = courseDAO.getCoursesByTeacher(teacherId);
        double totalHours = 0;

        for (Course course : courses) {
            totalHours += course.getDuration() / 60.0;
        }

        stats.put("teacherId", teacherId);
        stats.put("courseCount", courses.size());
        stats.put("totalHours", totalHours);
        stats.put("hoursPerWeek", calculateHoursPerWeek(courses));

        return stats;
    }

    /**
     * Calculate hours per week for a list of courses
     */
    private Map<String, Double> calculateHoursPerWeek(List<Course> courses) {
        Map<String, Double> hoursPerDay = new HashMap<>();

        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
        for (String day : days) {
            hoursPerDay.put(day, 0.0);
        }

        for (Course course : courses) {
            double hours = course.getDuration() / 60.0;
            hoursPerDay.put(course.getDayOfWeek(), hoursPerDay.get(course.getDayOfWeek()) + hours);
        }

        return hoursPerDay;
    }

    /**
     * Get class schedule statistics
     */
    public Map<String, Object> getClassScheduleStats(int classId) {
        Map<String, Object> stats = new HashMap<>();

        List<Course> courses = courseDAO.getCoursesByClass(classId);
        Set<String> subjects = new HashSet<>();
        Set<Integer> teachers = new HashSet<>();
        double totalHours = 0;

        for (Course course : courses) {
            subjects.add(course.getSubject());
            teachers.add(course.getTeacherId());
            totalHours += course.getDuration() / 60.0;
        }

        stats.put("classId", classId);
        stats.put("courseCount", courses.size());
        stats.put("uniqueSubjects", subjects.size());
        stats.put("uniqueTeachers", teachers.size());
        stats.put("totalHours", totalHours);

        return stats;
    }

    private double calculateDuration(String startTime, String endTime) {
        try {
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");

            int startHours = Integer.parseInt(startParts[0]);
            int startMins = Integer.parseInt(startParts[1]);
            int endHours = Integer.parseInt(endParts[0]);
            int endMins = Integer.parseInt(endParts[1]);

            int startTotalMins = startHours * 60 + startMins;
            int endTotalMins = endHours * 60 + endMins;

            return (endTotalMins - startTotalMins) / 60.0;
        } catch (Exception e) {
            logger.warn("Error calculating duration", e);
            return 0;
        }
    }

    private String getStatus(double occupancy) {
        if (occupancy > 90) return "CRITICAL";
        if (occupancy < 20) return "UNDERUTILIZED";
        return "NORMAL";
    }

    private boolean isDateInRange(String date, String startDate, String endDate) {
        return date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0;
    }

    /**
     * Class to hold room statistics
     */
    public static class RoomStatistic {
        private Room room;
        private double occupancyRate;
        private String status;

        public RoomStatistic(Room room, double occupancyRate, String status) {
            this.room = room;
            this.occupancyRate = occupancyRate;
            this.status = status;
        }

        public Room getRoom() { return room; }
        public double getOccupancyRate() { return occupancyRate; }
        public String getStatus() { return status; }
    }
}
