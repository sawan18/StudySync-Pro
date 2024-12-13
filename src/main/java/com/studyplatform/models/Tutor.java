package com.studyplatform.models;

public class Tutor {
    private int id;
    private String name;
    private String tutorClass;
    private String availableDate;
    private String location;
    private boolean scheduled;

    public Tutor() {}

    public Tutor(String name, String tutorClass, String availableDate, String location, boolean scheduled) {
        this.name = name;
        this.tutorClass = tutorClass;
        this.availableDate = availableDate;
        this.location = location;
        this.scheduled = scheduled;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTutorClass() { return tutorClass; }
    public void setTutorClass(String tutorClass) { this.tutorClass = tutorClass; }

    public String getAvailableDate() { return availableDate; }
    public void setAvailableDate(String availableDate) { this.availableDate = availableDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isScheduled() { return scheduled; }
    public void setScheduled(boolean scheduled) { this.scheduled = scheduled; }
}
