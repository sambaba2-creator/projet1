package com.university.scheduler.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private int id;
    private String number;
    private int capacity;
    private String type;
    private int buildingId;
    private List<Integer> equipmentIds;

    public enum RoomType {
        TD("TD"),
        TP("TP"),
        AMPHI("Amphithéâtre"),
        SEMINAR("Séminaire"),
        CONFERENCE("Conférence");

        private final String displayName;

        RoomType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Room() {
        this.equipmentIds = new ArrayList<>();
    }

    public Room(String number, int capacity, String type, int buildingId) {
        this.number = number;
        this.capacity = capacity;
        this.type = type;
        this.buildingId = buildingId;
        this.equipmentIds = new ArrayList<>();
    }

    public Room(int id, String number, int capacity, String type, int buildingId) {
        this.id = id;
        this.number = number;
        this.capacity = capacity;
        this.type = type;
        this.buildingId = buildingId;
        this.equipmentIds = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getBuildingId() { return buildingId; }
    public void setBuildingId(int buildingId) { this.buildingId = buildingId; }

    public List<Integer> getEquipmentIds() { return equipmentIds; }
    public void setEquipmentIds(List<Integer> equipmentIds) { this.equipmentIds = equipmentIds; }

    public void addEquipment(int equipmentId) {
        if (!equipmentIds.contains(equipmentId)) {
            equipmentIds.add(equipmentId);
        }
    }

    public void removeEquipment(int equipmentId) {
        equipmentIds.remove(Integer.valueOf(equipmentId));
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", capacity=" + capacity +
                ", type='" + type + '\'' +
                ", buildingId=" + buildingId +
                '}';
    }
}
