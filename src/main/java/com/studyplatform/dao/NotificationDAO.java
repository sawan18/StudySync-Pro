package com.studyplatform.dao;

// handle all database operation related to notification
import com.studyplatform.models.Notification;
import com.studyplatform.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO implements BaseDAO<Notification> {
    // table name
    private static final String TABLE_NAME = "NOTIFICATIONS";

    public void createTable() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            // sql statement to create table with title, description, timestamp, is_read, is_deleted
            String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "title VARCHAR(255), " +
                    "description VARCHAR(1000), " +
                    "timestamp TIMESTAMP, " +
                    "is_read BOOLEAN, " +
                    "is_deleted BOOLEAN DEFAULT FALSE)";
            stmt.execute(createTableSQL);
        }
    }

    // make new notification
    @Override
    public void create(Notification notification) throws SQLException {
        // sql statement to insert notification
        String sql = "INSERT INTO " + TABLE_NAME + " (title, description, timestamp, is_read, is_deleted) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // set all notification details
            pstmt.setString(1, notification.getTitle());
            pstmt.setString(2, notification.getDescription());
            pstmt.setTimestamp(3, new Timestamp(notification.getTimestamp().getTime()));
            pstmt.setBoolean(4, notification.isRead());
            pstmt.setBoolean(5, notification.isDeleted());
            
            // insert
            pstmt.executeUpdate();
            
            // get notification id
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    notification.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // find notification by id and return it
    @Override
    public Notification read(int id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // make new notification object
                    Notification notification = new Notification();
                    notification.setId(rs.getInt("id"));
                    notification.setTitle(rs.getString("title"));
                    notification.setDescription(rs.getString("description"));
                    notification.setTimestamp(new Date(rs.getTimestamp("timestamp").getTime()));
                    
                    // check for if notification is read
                    if (rs.getBoolean("is_read")) {
                        notification.markAsRead();
                    }
                    
                    return notification;
                }
            }
        }
        // return null if no notification found
        return null;
    }

    // update notification in database
    @Override
    public void update(Notification notification) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET title = ?, description = ?, is_read = ?, is_deleted = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set all notification with updated details
            pstmt.setString(1, notification.getTitle());
            pstmt.setString(2, notification.getDescription());
            pstmt.setBoolean(3, notification.isRead());
            pstmt.setBoolean(4, notification.isDeleted());
            pstmt.setInt(5, notification.getId());
            
            pstmt.executeUpdate();
        }
    }

    // mark notification as deleted. we dont really delete it, so that we know which notifications are deleted
    @Override
    public void delete(int id) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET is_deleted = TRUE WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // get all notification from database and return list
    @Override
    public List<Notification> findAll() throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // loop through all notification from database
            while (rs.next()) {
                // make new notification object
                Notification notification = new Notification();
                notification.setId(rs.getInt("id"));
                notification.setTitle(rs.getString("title"));
                notification.setDescription(rs.getString("description"));
                notification.setTimestamp(new Date(rs.getTimestamp("timestamp").getTime()));
                
                // check for if notification is read
                if (rs.getBoolean("is_read")) {
                    notification.markAsRead();
                }

                // check for if notification is deleted
                if (rs.getBoolean("is_deleted")) {
                    notification.markAsDeleted();
                }
                
                // add notification to list
                notifications.add(notification);
            }
        }
        
        // return list of notification
        return notifications;
    }
}
