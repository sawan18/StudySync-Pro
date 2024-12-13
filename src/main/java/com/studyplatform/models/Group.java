package com.studyplatform.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Group {
    private int id;
    private String name;
    private List<String> members;
    private List<String> files;
    private Date meetingTime;

    public Group() {
        this.members = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public Group(String name) {
        this.name = name;
        this.members = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getMembers() { return members; }

    public void addMember(String memberName) { 
        if (!members.contains(memberName)) {
            members.add(memberName); 
        }
    }

    public void removeMember(String memberName) { 
        members.remove(memberName); 
    }

    public List<String> getFiles() { return files; }

    public void uploadFile(String fileName) { 
        if (!files.contains(fileName)) {
            files.add(fileName); 
        }
    }

    public Date getMeetingTime() { return meetingTime; }

    public void setMeetingTime(Date meetingTime) { this.meetingTime = meetingTime; }
}
