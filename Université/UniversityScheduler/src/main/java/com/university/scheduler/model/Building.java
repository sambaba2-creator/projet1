package com.university.scheduler.model;

public class Building {
    private int id;
    private String name;
    private String location;
    private int floors;

    public Building() {}

    public Building(String name, String location, int floors) {
        this.name = name;
        this.location = location;
        this.floors = floors;
    }

    public Building(int id, String name, String location, int floors) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.floors = floors;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getFloors() { return floors; }
    public void setFloors(int floors) { this.floors = floors; }

    @Override
    public String toString() {
        return "Building{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", floors=" + floors +
                '}';
    }
}
