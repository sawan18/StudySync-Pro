package com.studyplatform.controllers;

// group controller
import com.studyplatform.dao.GroupDAO;
import com.studyplatform.models.Group;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupController {
    private GroupDAO groupDAO;

    public GroupController() {
        this.groupDAO = new GroupDAO();
        try {
            this.groupDAO.createTable();
        } catch (SQLException e) {
            System.err.println("Error creating groups table: " + e.getMessage());
        }
    }

    // create new group
    public Group createGroup(String groupName) {
        // check if group name empty
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be empty.");
        }

        try {
            // get all groups from database
            List<Group> existingGroups = groupDAO.findAll();
            // check if group already exist
            boolean groupExists = existingGroups.stream()
                    .anyMatch(g -> g.getName().equals(groupName));
            
            // throw error if group already exist
            if (groupExists) {
                throw new IllegalStateException("Group already exists.");
            }

            // make new group with group name
            Group group = new Group(groupName);
            // try to add group to database
            groupDAO.create(group);
            // return group if success
            return group;
        } catch (SQLException e) {
            System.err.println("Error creating group: " + e.getMessage());
            return null;
        }
    }

    // delete group
    public void deleteGroup(String groupName) {
        // check if group name empty
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be empty.");
        }

        try {
            // get group from database
            Group group = getGroup(groupName);
            // check if group exist
            if (group == null) {
                // throw error if group not exist
                throw new IllegalStateException("Group does not exist.");
            }

            groupDAO.delete(group.getId());
        } catch (SQLException e) {
            System.err.println("Error deleting group: " + e.getMessage());
        }
    }

    // get group
    public Group getGroup(String groupName) {
        try {
            // get all groups from database
            return groupDAO.findAll().stream()
                    .filter(g -> g.getName().equals(groupName))
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            System.err.println("Error finding group: " + e.getMessage());
            return null;
        }
    }

    // add member to group
    public void addMemberToGroup(String groupName, String memberName) {
        try {
            // get group from database
            Group group = getGroup(groupName);
            // check if group exist
            if (group != null) {
                // add member to group
                group.addMember(memberName);
                // update group in database
                groupDAO.update(group);
            }
        } catch (SQLException e) {
            System.err.println("Error adding member to group: " + e.getMessage());
        }
    }

    // remove member from group
    public void removeMemberFromGroup(String groupName, String memberName) {
        try {
            // get group from database
            Group group = getGroup(groupName);
            // check if group exist
            if (group != null) {
                // remove member from group
                group.removeMember(memberName);
                groupDAO.update(group);
            }
        } catch (SQLException e) {
            System.err.println("Error removing member from group: " + e.getMessage());
        }
    }

    // upload file to group
    public void uploadFileToGroup(String groupName, String fileName) {
        try {
            // get group from database
            Group group = getGroup(groupName);
            // check if group exist
            if (group != null) {
                // upload file to group
                group.uploadFile(fileName);
                // update group in database
                groupDAO.update(group);
            }
        } catch (SQLException e) {
            System.err.println("Error uploading file to group: " + e.getMessage());
        }
    }

    // schedule meeting
    public void scheduleMeeting(String groupName, Date meetingTime) {
        try {
            // get group from database
            Group group = getGroup(groupName);
            // check if group exist
            if (group != null) {
                // schedule meeting
                group.setMeetingTime(meetingTime);
                // update group in database
                groupDAO.update(group);
            }
        } catch (SQLException e) {
            System.err.println("Error scheduling meeting: " + e.getMessage());
        }
    }

    // get all groups
    public Map<String, Group> getAllGroups() {
        try {
            // get all groups from database
            List<Group> groups = groupDAO.findAll();
            // return map of groups
            return groups.stream()
                    .collect(Collectors.toMap(Group::getName, g -> g));
        } catch (SQLException e) {
            System.err.println("Error retrieving all groups: " + e.getMessage());
            return new HashMap<>();
        }
    }
}
