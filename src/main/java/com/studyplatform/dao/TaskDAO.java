package com.studyplatform.dao;

import com.studyplatform.models.Task;
import com.studyplatform.util.DatabaseUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskDAO implements BaseDAO<Task> {
    private static final String TABLE_NAME = "TASKS";

    public void createTable() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            // Use CREATE TABLE to prevent errors if table already exists
            String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "title VARCHAR(255), " +
                    "description VARCHAR(1000), " +
                    "type VARCHAR(100), " +
                    "due_date VARCHAR(50), " +
                    "status VARCHAR(50))";
            stmt.execute(createTableSQL);
        }
    }

    @Override
    public void create(Task task) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (title, description, type, due_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getType());
            pstmt.setString(4, task.getDueDate());
            pstmt.setString(5, task.getStatus());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    task.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Task read(int id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setType(rs.getString("type"));
                    task.setDueDate(rs.getString("due_date"));
                    task.setStatus(rs.getString("status"));
                    
                    return task;
                }
            }
        }
        return null;
    }

    @Override
    public void update(Task task) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET title = ?, description = ?, type = ?, due_date = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getType());
            pstmt.setString(4, task.getDueDate());
            pstmt.setString(5, task.getStatus());
            pstmt.setInt(6, task.getId());
            
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
    public List<Task> findAll() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setType(rs.getString("type"));
                task.setDueDate(rs.getString("due_date"));
                task.setStatus(rs.getString("status"));
                
                tasks.add(task);
            }
        }
        
        return tasks;
    }

    public List<Task> findTasksDueToday(Date today) throws SQLException {
        List<Task> dueTasks = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String todayString = dateFormat.format(today);

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE due_date = ? AND status != 'COMPLETED'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, todayString);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setType(rs.getString("type"));
                    task.setDueDate(rs.getString("due_date"));
                    task.setStatus(rs.getString("status"));
                    
                    dueTasks.add(task);
                }
            }
        }
        
        return dueTasks;
    }
}
