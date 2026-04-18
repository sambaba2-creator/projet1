package com.university.scheduler.model;

public class AcademicClass {
    private int id;
    private String name;
    private int capacity;
    private String level;
    private String specialization;

    public AcademicClass() {}

    public AcademicClass(String name, int capacity, String level, String specialization) {
        this.name = name;
        this.capacity = capacity;
        this.level = level;
        this.specialization = specialization;
    }

    public AcademicClass(int id, String name, int capacity, String level, String specialization) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.level = level;
        this.specialization = specialization;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    @Override
    public String toString() {
        return "AcademicClass{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", level='" + level + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}
