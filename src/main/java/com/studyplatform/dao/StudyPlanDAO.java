package com.studyplatform.dao;

import com.studyplatform.models.StudyPlan;
import com.studyplatform.models.Coursework;
import com.studyplatform.util.DatabaseUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class StudyPlanDAO implements BaseDAO<StudyPlan> {
    private static final String TABLE_NAME = "STUDY_PLANS";
    private static final String COURSEWORK_TABLE_NAME = "COURSEWORKS";
    private static final String COURSES_TABLE_NAME = "COURSES";

    public void createTable() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            // Create Courses Table
            String createCoursesTableSQL = "CREATE TABLE " + COURSES_TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "name VARCHAR(255) UNIQUE NOT NULL)";
            
            // Create Study Plans Table
            String createStudyPlanTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "course_id INT, " +
                    "name VARCHAR(255), " +
                    "FOREIGN KEY (course_id) REFERENCES " + COURSES_TABLE_NAME + "(id))";
            
            // Create Coursework Table
            String createCourseworkTableSQL = "CREATE TABLE " + COURSEWORK_TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "study_plan_id INT, " +
                    "name VARCHAR(255), " +
                    "details VARCHAR(500), " +
                    "due_date VARCHAR(50), " +
                    "status VARCHAR(50), " +
                    "FOREIGN KEY (study_plan_id) REFERENCES " + TABLE_NAME + "(id))";
            
            stmt.execute(createCoursesTableSQL);
            stmt.execute(createStudyPlanTableSQL);
            stmt.execute(createCourseworkTableSQL);
        }
    }

    public void addCourse(String name) throws SQLException {
        String sql = "INSERT INTO " + COURSES_TABLE_NAME + " (name) VALUES (?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, name);
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return;
                }
            }
        }
    }

    public int getCourseId(String courseName) throws SQLException {
        String sql = "SELECT id FROM " + COURSES_TABLE_NAME + " WHERE name = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, courseName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
            
            throw new SQLException("Course not found: " + courseName);
        }
    }

    @Override
    public void create(StudyPlan studyPlan) throws SQLException {
        // First, get the course ID
        int courseId = getCourseId(studyPlan.getCourse());
        
        String sql = "INSERT INTO " + TABLE_NAME + " (course_id, name) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, courseId);
            pstmt.setString(2, studyPlan.getName());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    studyPlan.setId(generatedKeys.getInt(1));
                }
            }
            
            // Insert associated courseworks
            insertCourseworks(studyPlan);
        }
    }

    private void insertCourseworks(StudyPlan studyPlan) throws SQLException {
        if (studyPlan.getCourseworkList() == null || studyPlan.getCourseworkList().isEmpty()) {
            return;
        }
        
        String sql = "INSERT INTO " + COURSEWORK_TABLE_NAME + " (study_plan_id, name, details, due_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            for (Coursework coursework : studyPlan.getCourseworkList()) {
                pstmt.setInt(1, studyPlan.getId());
                pstmt.setString(2, coursework.getName());
                pstmt.setString(3, coursework.getDetails());
                pstmt.setString(4, coursework.getDueDate());
                pstmt.setString(5, coursework.getStatus());
                
                pstmt.executeUpdate();
                
                // Retrieve the generated coursework ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        coursework.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    @Override
    public StudyPlan read(int id) throws SQLException {
        String sql = "SELECT sp.id, sp.name, c.name AS course_name " +
                     "FROM " + TABLE_NAME + " sp " +
                     "JOIN " + COURSES_TABLE_NAME + " c ON sp.course_id = c.id " +
                     "WHERE sp.id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    StudyPlan studyPlan = new StudyPlan();
                    studyPlan.setId(rs.getInt("id"));
                    studyPlan.setName(rs.getString("name"));
                    studyPlan.setCourse(rs.getString("course_name"));
                    
                    // Fetch associated courseworks
                    fetchCourseworks(studyPlan);
                    
                    return studyPlan;
                }
            }
        }
        return null;
    }

    private void fetchCourseworks(StudyPlan studyPlan) throws SQLException {
        String sql = "SELECT id, name, details, due_date, status " +
                     "FROM " + COURSEWORK_TABLE_NAME + " " +
                     "WHERE study_plan_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studyPlan.getId());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Coursework coursework = new Coursework();
                    coursework.setId(rs.getInt("id"));
                    coursework.setName(rs.getString("name"));
                    coursework.setDetails(rs.getString("details"));
                    
                    // Handle potential null date
                    String dueDate = rs.getString("due_date");
                    if (dueDate != null) {
                        coursework.setDueDate(dueDate);
                    }
                    
                    coursework.setStatus(rs.getString("status"));
                    
                    studyPlan.addCoursework(coursework);
                }
            } catch(SQLException ex) {
                System.err.println("Error fetching courseworks for study plan: " + studyPlan.getId());
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void update(StudyPlan studyPlan) throws SQLException {
        // First, get the course ID
        int courseId = getCourseId(studyPlan.getCourse());
        
        String sql = "UPDATE " + TABLE_NAME + " SET course_id = ?, name = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setString(2, studyPlan.getName());
            pstmt.setInt(3, studyPlan.getId());
            
            pstmt.executeUpdate();
            
            // Remove existing courseworks and re-insert
            removeCourseworks(studyPlan.getId());
            insertCourseworks(studyPlan);
        }
    }

    private void removeCourseworks(int studyPlanId) throws SQLException {
        String sql = "DELETE FROM " + COURSEWORK_TABLE_NAME + " WHERE study_plan_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studyPlanId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        // First remove associated courseworks
        removeCourseworks(id);
        
        // Then delete the study plan
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<StudyPlan> findAll() throws SQLException {
        List<StudyPlan> studyPlans = new ArrayList<>();
        String sql = "SELECT sp.id, sp.name, c.name AS course_name " +
                     "FROM " + TABLE_NAME + " sp " +
                     "JOIN " + COURSES_TABLE_NAME + " c ON sp.course_id = c.id";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                StudyPlan studyPlan = new StudyPlan();
                studyPlan.setId(rs.getInt("id"));
                studyPlan.setCourse(rs.getString("course_name"));
                studyPlan.setName(rs.getString("name"));
                
                // Fetch associated courseworks
                fetchCourseworks(studyPlan);
                
                studyPlans.add(studyPlan);
            }
        }
        
        return studyPlans;
    }

    public void addCourseworkToStudyPlan(int studyPlanId, Coursework coursework) throws SQLException {
        // Add coursework directly to the COURSEWORKS table with the study plan ID
        String createCourseworkSQL = "INSERT INTO " + COURSEWORK_TABLE_NAME + 
                " (study_plan_id, name, details, due_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(createCourseworkSQL, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, studyPlanId);
            pstmt.setString(2, coursework.getName());
            pstmt.setString(3, coursework.getDetails());
            pstmt.setString(4, coursework.getDueDate());
            pstmt.setString(5, coursework.getStatus());
            
            pstmt.executeUpdate();
            
            // Retrieve the generated coursework ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int courseworkId = generatedKeys.getInt(1);
                    coursework.setId(courseworkId);
                } else {
                    throw new SQLException("Creating coursework failed, no ID obtained.");
                }
            }
        }
    }

    public void updateCoursework(int studyPlanId, Coursework coursework) throws SQLException {
        // Update the coursework in the COURSEWORKS table
        String updateCourseworkSQL = "UPDATE " + COURSEWORK_TABLE_NAME + " SET name = ?, details = ?, due_date = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateCourseworkSQL)) {
            
            pstmt.setString(1, coursework.getName());
            pstmt.setString(2, coursework.getDetails());
            pstmt.setString(3, coursework.getDueDate());
            pstmt.setString(4, coursework.getStatus());
            pstmt.setInt(5, coursework.getId());
            
            pstmt.executeUpdate();
        }
    }

    public void removeCourseworkFromStudyPlan(int studyPlanId, int courseworkId) throws SQLException {
        // Delete the specific coursework from the COURSEWORKS table
        String deleteCourseworkSQL = "DELETE FROM " + COURSEWORK_TABLE_NAME + " WHERE id = ? AND study_plan_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement deleteCourseworkStmt = conn.prepareStatement(deleteCourseworkSQL)) {
            
            deleteCourseworkStmt.setInt(1, courseworkId);
            deleteCourseworkStmt.setInt(2, studyPlanId);
            deleteCourseworkStmt.executeUpdate();
        }
    }

    // Method to get all courses
    public List<String> getAllCourses() throws SQLException {
        List<String> courses = new ArrayList<>();
        String sql = "SELECT name FROM " + COURSES_TABLE_NAME + " ORDER BY name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                courses.add(rs.getString("name"));
            }
        }
        
        return courses;
    }

    // Method to get study plans for a specific course
    public List<StudyPlan> getStudyPlansForCourse(String courseName) throws SQLException {
        List<StudyPlan> studyPlans = new ArrayList<>();
        
        String sql = "SELECT sp.id, sp.name " +
                     "FROM " + TABLE_NAME + " sp " +
                     "JOIN " + COURSES_TABLE_NAME + " c ON sp.course_id = c.id " +
                     "WHERE c.name = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, courseName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    StudyPlan studyPlan = new StudyPlan();
                    studyPlan.setId(rs.getInt("id"));
                    studyPlan.setName(rs.getString("name"));
                    studyPlan.setCourse(courseName);
                    
                    // Fetch associated courseworks
                    fetchCourseworks(studyPlan);
                    
                    studyPlans.add(studyPlan);
                }
            }
        }
        
        return studyPlans;
    }

    // Method to add a coursework to a study plan
    public void addCourseworkToStudyPlan(StudyPlan studyPlan, Coursework coursework) throws SQLException {
        String sql = "INSERT INTO " + COURSEWORK_TABLE_NAME + " (study_plan_id, name, details, due_date, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, studyPlan.getId());
            pstmt.setString(2, coursework.getName());
            pstmt.setString(3, coursework.getDetails());
            pstmt.setString(4, coursework.getDueDate());
            pstmt.setString(5, coursework.getStatus());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    coursework.setId(generatedKeys.getInt(1));
                }
            }
            
            // Add to study plan's coursework list
            studyPlan.addCoursework(coursework);
        }
    }

    // Method to update a coursework
    public void updateCoursework(StudyPlan studyPlan, Coursework coursework) throws SQLException {
        String sql = "UPDATE " + COURSEWORK_TABLE_NAME + " SET name = ?, details = ?, due_date = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, coursework.getName());
            pstmt.setString(2, coursework.getDetails());
            pstmt.setString(3, coursework.getDueDate());
            pstmt.setString(4, coursework.getStatus());
            pstmt.setInt(5, coursework.getId());
            
            pstmt.executeUpdate();
            
            // Update in study plan's coursework list
            studyPlan.updateCoursework(coursework);
        }
    }

    // Method to remove a coursework from a study plan
    public void removeCourseworkFromStudyPlan(StudyPlan studyPlan, Coursework coursework) throws SQLException {
        String sql = "DELETE FROM " + COURSEWORK_TABLE_NAME + " WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, coursework.getId());
            pstmt.executeUpdate();
            
            // Remove from study plan's coursework list
            studyPlan.removeCoursework(coursework);
        }
    }

    // Method to delete a study plan
    public void deleteStudyPlan(String courseName, String studyPlanName) throws SQLException {
        // First, get the course ID
        int courseId = getCourseId(courseName);
        
        // Delete associated courseworks first
        String deleteCourseworksSql = "DELETE FROM " + COURSEWORK_TABLE_NAME + " WHERE study_plan_id IN " +
                                      "(SELECT id FROM " + TABLE_NAME + " WHERE course_id = ? AND name = ?)";
        
        // Then delete the study plan
        String deleteStudyPlanSql = "DELETE FROM " + TABLE_NAME + " WHERE course_id = ? AND name = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmtCourseworks = conn.prepareStatement(deleteCourseworksSql);
             PreparedStatement pstmtStudyPlan = conn.prepareStatement(deleteStudyPlanSql)) {

            
            // Delete courseworks
            pstmtCourseworks.setInt(1, courseId);
            pstmtCourseworks.setString(2, studyPlanName);
            pstmtCourseworks.executeUpdate();
            
            // Delete study plan
            pstmtStudyPlan.setInt(1, courseId);
            pstmtStudyPlan.setString(2, studyPlanName);
            pstmtStudyPlan.executeUpdate();
            
            // Commit transaction
            conn.commit();
        } catch (SQLException e) {
            // Rollback in case of error
            try (Connection conn = DatabaseUtil.getConnection()) {
                conn.rollback();
            }
            throw e;
        }
    }

    public List<Coursework> findCourseworkDueOnDate(Date today) throws SQLException {
        List<Coursework> dueCourseworks = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String todayString = dateFormat.format(today);
    
        String sql = "SELECT * FROM " + COURSEWORK_TABLE_NAME + " WHERE due_date = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, todayString);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Coursework coursework = new Coursework();
                    coursework.setId(rs.getInt("id"));
                    coursework.setName(rs.getString("name"));
                    coursework.setDetails(rs.getString("details"));
                    coursework.setDueDate(rs.getString("due_date"));
                    coursework.setStatus(rs.getString("status"));
                    
                    dueCourseworks.add(coursework);
                }
            }
        }
        
        return dueCourseworks;
    }
}
