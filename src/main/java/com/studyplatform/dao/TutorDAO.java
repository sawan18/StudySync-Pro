package com.studyplatform.dao;

// tutor data access object

import com.studyplatform.models.Tutor;
import com.studyplatform.util.DatabaseUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TutorDAO implements BaseDAO<Tutor> {
    private static final String TABLE_NAME = "TUTORS";

    // table to store tutors
    public void createTable() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            // sql statement to create table with title, description, timestamp, is_read, is_deleted
            String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "name VARCHAR(255), " +
                    "tutor_class VARCHAR(255), " +
                    "available_date VARCHAR(50), " +
                    "location VARCHAR(255), " +
                    "scheduled BOOLEAN)";
            stmt.execute(createTableSQL);
        }
    }

    // make new tutor in database
    @Override
    public void create(Tutor tutor) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (name, tutor_class, available_date, location, scheduled) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, tutor.getName());
            pstmt.setString(2, tutor.getTutorClass());
            pstmt.setString(3, tutor.getAvailableDate());
            pstmt.setString(4, tutor.getLocation());
            pstmt.setBoolean(5, tutor.isScheduled());
            
            pstmt.executeUpdate();
            
            // get back generated id
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tutor.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // find tutor by id
    @Override
    public Tutor read(int id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // make new tutor object
                    Tutor tutor = new Tutor();
                    tutor.setId(rs.getInt("id"));
                    tutor.setName(rs.getString("name"));
                    tutor.setTutorClass(rs.getString("tutor_class"));
                    tutor.setAvailableDate(rs.getString("available_date"));
                    tutor.setLocation(rs.getString("location"));
                    tutor.setScheduled(rs.getBoolean("scheduled"));
                    
                    return tutor;
                }
            }
        }
        // return null if no tutor found
        return null;
    }

    // update tutor in database
    @Override
    public void update(Tutor tutor) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET name = ?, tutor_class = ?, available_date = ?, location = ?, scheduled = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set all tutor details
            pstmt.setString(1, tutor.getName());
            pstmt.setString(2, tutor.getTutorClass());
            pstmt.setString(3, tutor.getAvailableDate());
            pstmt.setString(4, tutor.getLocation());
            pstmt.setBoolean(5, tutor.isScheduled());
            pstmt.setInt(6, tutor.getId());
            
            // do update magic
            pstmt.executeUpdate();
        }
    }

    // delete tutor from database
    @Override
    public void delete(int id) throws SQLException {
        // prepare sql for delete tutor
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set id parameter
            pstmt.setInt(1, id);
            
            // do delete magic
            pstmt.executeUpdate();
        }
    }

    // get all tutors from database. return list of tutors
    @Override
    public List<Tutor> findAll() throws SQLException {
        // make list for store tutors
        List<Tutor> tutors = new ArrayList<>();
        // prepare sql for get all tutors
        String sql = "SELECT * FROM " + TABLE_NAME;
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // loop through all tutors
            while (rs.next()) {
                // make new tutor object
                Tutor tutor = new Tutor();
                tutor.setId(rs.getInt("id"));
                tutor.setName(rs.getString("name"));
                tutor.setTutorClass(rs.getString("tutor_class"));
                tutor.setAvailableDate(rs.getString("available_date"));
                tutor.setLocation(rs.getString("location"));
                tutor.setScheduled(rs.getBoolean("scheduled"));
                
                // add tutor to list
                tutors.add(tutor);
            }
        }
        
        // return list of tutors
        return tutors;
    }

    // find tutors scheduled for today. return list of tutors
    public List<Tutor> findScheduledTutorsForToday(Date date) throws SQLException {
        // dis be simple date format. make date string
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String todayString = dateFormat.format(date);
        
        // make list for store tutors
        List<Tutor> scheduledTutors = new ArrayList<>();
        // prepare sql for get tutors scheduled today
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE scheduled = true AND available_date = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set today's date parameter
            pstmt.setString(1, todayString);
            
            // execute query and get result
            try (ResultSet rs = pstmt.executeQuery()) {
                // loop through all tutors
                while (rs.next()) {
                    // make new tutor object
                    Tutor tutor = new Tutor();
                    tutor.setId(rs.getInt("id"));
                    tutor.setName(rs.getString("name"));
                    tutor.setTutorClass(rs.getString("tutor_class"));
                    tutor.setAvailableDate(rs.getString("available_date"));
                    tutor.setLocation(rs.getString("location"));
                    tutor.setScheduled(rs.getBoolean("scheduled"));
                    
                    // add tutor to list
                    scheduledTutors.add(tutor);
                }
            }
        }
        
        // return list of scheduled tutors
        return scheduledTutors;
    }
}
