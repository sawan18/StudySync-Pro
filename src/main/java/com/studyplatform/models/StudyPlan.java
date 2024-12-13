package com.studyplatform.models;

import java.util.ArrayList;
import java.util.List;

public class StudyPlan {
    private int id;
    private String course;
    private String name;
    private List<Coursework> courseworkList;

    public StudyPlan() {
        this.courseworkList = new ArrayList<>();
    }

    public StudyPlan(String course, String name) {
        this.course = course;
        this.name = name;
        this.courseworkList = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDetails() { return name; }

    public List<Coursework> getCourseworkList() { return courseworkList; }

    public void addCoursework(Coursework coursework) { 
        courseworkList.add(coursework); 
    }

    public void removeCoursework(Coursework coursework) { 
        courseworkList.remove(coursework); 
    }

    public void updateCoursework(Coursework coursework) {
        // Find the existing coursework and update it
        for (int i = 0; i < courseworkList.size(); i++) {
            if (courseworkList.get(i).getId() == coursework.getId()) {
                courseworkList.set(i, coursework);
                break;
            }
        }
    }
}
