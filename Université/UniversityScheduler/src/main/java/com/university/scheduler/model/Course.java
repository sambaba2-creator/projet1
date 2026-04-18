package com.university.scheduler.model;

public class Course {
    private int id;
    private String subject;
    private int teacherId;
    private int classId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private int duration; // in minutes
    private int roomId;
    private String createdAt;

    public Course() {}

    public Course(String subject, int teacherId, int classId, String dayOfWeek, 
                  String startTime, int duration, int roomId) {
        this.subject = subject;
        this.teacherId = teacherId;
        this.classId = classId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.duration = duration;
        this.roomId = roomId;
    }

    public Course(int id, String subject, int teacherId, int classId, String dayOfWeek,
                  String startTime, String endTime, int duration, int roomId) {
        this.id = id;
        this.subject = subject;
        this.teacherId = teacherId;
        this.classId = classId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.roomId = roomId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", teacherId=" + teacherId +
                ", classId=" + classId +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", roomId=" + roomId +
                '}';
    }
}
