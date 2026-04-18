package com.university.scheduler.model;

public class Reservation {
    private int id;
    private int roomId;
    private String date;
    private String startTime;
    private String endTime;
    private String type; // "COURSE" or "EVENT"
    private int courseId;
    private int userId;
    private String reason;
    private String createdAt;
    private String status; // "CONFIRMED", "PENDING", "CANCELLED"

    public enum ReservationType {
        COURSE("Cours"),
        EVENT("Événement");

        private final String displayName;

        ReservationType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Status {
        CONFIRMED("Confirmée"),
        PENDING("En attente"),
        CANCELLED("Annulée");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Reservation() {}

    public Reservation(int roomId, String date, String startTime, String endTime,
                      String type, int userId) {
        this.roomId = roomId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.userId = userId;
        this.status = "CONFIRMED";
    }

    public Reservation(int id, int roomId, String date, String startTime, String endTime,
                      String type, int courseId, int userId, String reason, String status) {
        this.id = id;
        this.roomId = roomId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.courseId = courseId;
        this.userId = userId;
        this.reason = reason;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", roomId=" + roomId +
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
