package com.studyplatform.dao;

import com.studyplatform.models.Group;
import com.studyplatform.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO implements BaseDAO<Group> {
    private static final String TABLE_NAME = "GROUPS";

    public void createTable() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "name VARCHAR(255), " +
                    "meeting_time TIMESTAMP, " +
                    "members VARCHAR(1000), " +
                    "files VARCHAR(1000))";
            stmt.execute(createTableSQL);
        }
    }

    @Override
    public void create(Group group) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (name, meeting_time, members, files) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, group.getName());
            pstmt.setTimestamp(2, group.getMeetingTime() != null ? new Timestamp(group.getMeetingTime().getTime()) : null);
            pstmt.setString(3, String.join(",", group.getMembers()));
            pstmt.setString(4, String.join(",", group.getFiles()));
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    group.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Group read(int id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Group group = new Group();
                    group.setId(rs.getInt("id"));
                    group.setName(rs.getString("name"));
                    
                    Timestamp meetingTime = rs.getTimestamp("meeting_time");
                    if (meetingTime != null) {
                        group.setMeetingTime(new java.util.Date(meetingTime.getTime()));
                    }
                    
                    String membersStr = rs.getString("members");
                    if (membersStr != null && !membersStr.isEmpty()) {
                        String[] members = membersStr.split(",");
                        for (String member : members) {
                            group.addMember(member);
                        }
                    }
                    
                    String filesStr = rs.getString("files");
                    if (filesStr != null && !filesStr.isEmpty()) {
                        String[] files = filesStr.split(",");
                        for (String file : files) {
                            group.uploadFile(file);
                        }
                    }
                    
                    return group;
                }
            }
        }
        return null;
    }

    @Override
    public void update(Group group) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET name = ?, meeting_time = ?, members = ?, files = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, group.getName());
            pstmt.setTimestamp(2, group.getMeetingTime() != null ? new Timestamp(group.getMeetingTime().getTime()) : null);
            pstmt.setString(3, String.join(",", group.getMembers()));
            pstmt.setString(4, String.join(",", group.getFiles()));
            pstmt.setInt(5, group.getId());
            
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Group> findAll() throws SQLException {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Group group = new Group();
                group.setId(rs.getInt("id"));
                group.setName(rs.getString("name"));
                
                Timestamp meetingTime = rs.getTimestamp("meeting_time");
                if (meetingTime != null) {
                    group.setMeetingTime(new java.util.Date(meetingTime.getTime()));
                }
                
                String membersStr = rs.getString("members");
                if (membersStr != null && !membersStr.isEmpty()) {
                    String[] members = membersStr.split(",");
                    for (String member : members) {
                        group.addMember(member);
                    }
                }
                
                String filesStr = rs.getString("files");
                if (filesStr != null && !filesStr.isEmpty()) {
                    String[] files = filesStr.split(",");
                    for (String file : files) {
                        group.uploadFile(file);
                    }
                }
                
                groups.add(group);
            }
        }
        
        return groups;
    }
}
