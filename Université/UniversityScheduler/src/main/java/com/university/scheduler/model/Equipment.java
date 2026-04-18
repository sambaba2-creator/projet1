package com.university.scheduler.model;

public class Equipment {
    private int id;
    private String name;

    public enum EquipmentType {
        PROJECTOR("Vidéoprojecteur"),
        INTERACTIVE_BOARD("Tableau interactif"),
        AIR_CONDITIONING("Climatisation"),
        WHITEBOARD("Tableau blanc"),
        SOUND_SYSTEM("Système audio"),
        CAMERA("Caméra");

        private final String displayName;

        EquipmentType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Equipment() {}

    public Equipment(String name) {
        this.name = name;
    }

    public Equipment(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "Equipment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
