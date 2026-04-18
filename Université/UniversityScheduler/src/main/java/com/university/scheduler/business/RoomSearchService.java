package com.university.scheduler.business;

import com.university.scheduler.dao.*;
import com.university.scheduler.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class RoomSearchService {
    private static final Logger logger = LoggerFactory.getLogger(RoomSearchService.class);
    private RoomDAO roomDAO;
    private ReservationDAO reservationDAO;
    private CourseDAO courseDAO;
    private EquipmentDAO equipmentDAO;

    public RoomSearchService() {
        this.roomDAO = new RoomDAO();
        this.reservationDAO = new ReservationDAO();
        this.courseDAO = new CourseDAO();
        this.equipmentDAO = new EquipmentDAO();
    }

    // constructor for dependency injection (testing)
    public RoomSearchService(RoomDAO roomDAO,
                             ReservationDAO reservationDAO,
                             CourseDAO courseDAO,
                             EquipmentDAO equipmentDAO) {
        this.roomDAO = roomDAO;
        this.reservationDAO = reservationDAO;
        this.courseDAO = courseDAO;
        this.equipmentDAO = equipmentDAO;
    }

    /**
     * Search for available rooms based on criteria
     */
    public List<Room> searchAvailableRooms(RoomSearchCriteria criteria) {
        List<Room> allRooms = roomDAO.getAllRooms();
        ConflictDetectionService conflictService = new ConflictDetectionService();

        return allRooms.stream()
                .filter(room -> room.getCapacity() >= criteria.getMinCapacity())
                .filter(room -> criteria.getRoomType() == null || room.getType().equals(criteria.getRoomType()))
                .filter(room -> isRoomAvailable(room.getId(), criteria))
                .filter(room -> hasRequiredEquipment(room, criteria.getRequiredEquipments()))
                .sorted(Comparator.comparingInt(Room::getCapacity))
                .collect(Collectors.toList());
    }

    /**
     * Check if room is available at specified time/date
     */
    private boolean isRoomAvailable(int roomId, RoomSearchCriteria criteria) {
        if (criteria.getDate() == null || criteria.getStartTime() == null || criteria.getEndTime() == null) {
            return true;
        }

        List<Reservation> reservations = reservationDAO.getReservationsByRoomAndDate(roomId, criteria.getDate());

        LocalTime searchStart = parseTime(criteria.getStartTime());
        LocalTime searchEnd = parseTime(criteria.getEndTime());

        for (Reservation res : reservations) {
            if (res.getStatus().equals("CANCELLED")) {
                continue;
            }

            LocalTime resStart = parseTime(res.getStartTime());
            LocalTime resEnd = parseTime(res.getEndTime());

            // Check overlap
            if (searchStart.isBefore(resEnd) && searchEnd.isAfter(resStart)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if room has all required equipments
     */
    private boolean hasRequiredEquipment(Room room, List<Integer> requiredEquipmentIds) {
        if (requiredEquipmentIds == null || requiredEquipmentIds.isEmpty()) {
            return true;
        }

        List<Integer> roomEquipments = room.getEquipmentIds();
        for (Integer requiredId : requiredEquipmentIds) {
            if (!roomEquipments.contains(requiredId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Find best room based on occupancy and equipment match
     */
    public Room findBestRoom(RoomSearchCriteria criteria) {
        List<Room> availableRooms = searchAvailableRooms(criteria);

        if (availableRooms.isEmpty()) {
            return null;
        }

        // Prefer rooms with exact capacity match, then those with required equipment
        return availableRooms.stream()
                .sorted((r1, r2) -> {
                    int diffr1 = r1.getCapacity() - criteria.getMinCapacity();
                    int diffr2 = r2.getCapacity() - criteria.getMinCapacity();

                    if (diffr1 != diffr2) {
                        return Integer.compare(diffr1, diffr2);
                    }

                    // If capacity diff is equal, prefer room with more matching equipments
                    int r1EquipMatch = getEquipmentMatchCount(r1, criteria.getRequiredEquipments());
                    int r2EquipMatch = getEquipmentMatchCount(r2, criteria.getRequiredEquipments());

                    return Integer.compare(r2EquipMatch, r1EquipMatch);
                })
                .findFirst()
                .orElse(null);
    }

    /**
     * Count equipment matches
     */
    private int getEquipmentMatchCount(Room room, List<Integer> requiredEquipments) {
        if (requiredEquipments == null || requiredEquipments.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Integer equipId : requiredEquipments) {
            if (room.getEquipmentIds().contains(equipId)) {
                count++;
            }
        }
        return count;
    }

    private LocalTime parseTime(String time) {
        try {
            if (time.contains("h")) {
                time = time.replace("h", ":");
            }
            return LocalTime.parse(time, DateTimeFormatter.ofPattern("H:mm"));
        } catch (Exception e) {
            logger.warn("Invalid time format: {}", time);
            return LocalTime.MIN;
        }
    }

    /**
     * Class to hold room search criteria
     */
    public static class RoomSearchCriteria {
        private int minCapacity = 1;
        private String roomType;
        private List<Integer> requiredEquipments = new ArrayList<>();
        private String date;
        private String startTime;
        private String endTime;

        public RoomSearchCriteria() {}

        // Getters and Setters
        public int getMinCapacity() { return minCapacity; }
        public void setMinCapacity(int minCapacity) { this.minCapacity = minCapacity; }

        public String getRoomType() { return roomType; }
        public void setRoomType(String roomType) { this.roomType = roomType; }

        public List<Integer> getRequiredEquipments() { return requiredEquipments; }
        public void setRequiredEquipments(List<Integer> requiredEquipments) { this.requiredEquipments = requiredEquipments; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }

        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
    }
}
