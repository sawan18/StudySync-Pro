package com.studyplatform.controllers;

import com.studyplatform.models.Coursework;
import com.studyplatform.models.StudyPlan;
import com.studyplatform.dao.StudyPlanDAO;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;

public class StudyPlanController {
    private StudyPlanDAO studyPlanDAO;

    public StudyPlanController() {
        this.studyPlanDAO = new StudyPlanDAO();
        try {
            this.studyPlanDAO.createTable();
        } catch (SQLException e) {
            System.err.println("Error creating study plans table: " + e.getMessage());
        }
    }

    public void addCourse(String name) {
        try {
            studyPlanDAO.addCourse(name);
        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
        }
    }

    public List<String> getAllCourses() {
        try {
            return studyPlanDAO.getAllCourses();
        } catch (SQLException e) {
            System.err.println("Error retrieving courses: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    public StudyPlan createStudyPlan(String course, String name) {
        if (course == null || course.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course and name cannot be empty.");
        }

        try {
            // Create the study plan
            StudyPlan studyPlan = new StudyPlan(course, name);
            studyPlanDAO.create(studyPlan);
            return studyPlan;
        } catch (SQLException e) {
            System.err.println("Error creating study plan: " + e.getMessage());
            return null;
        }
    }

    public void deleteStudyPlan(String course, String studyPlanName) {
        try {
            studyPlanDAO.deleteStudyPlan(course, studyPlanName);
        } catch (SQLException e) {
            System.err.println("Error deleting study plan: " + e.getMessage());
        }
    }

    public List<StudyPlan> getStudyPlansForCourse(String course) {
        try {
            return studyPlanDAO.getStudyPlansForCourse(course);
        } catch (SQLException e) {
            System.err.println("Error retrieving study plans for course: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    public void addCourseworkToStudyPlan(StudyPlan studyPlan, Coursework coursework) {
        try {
            studyPlanDAO.addCourseworkToStudyPlan(studyPlan, coursework);
        } catch (SQLException e) {
            System.err.println("Error adding coursework to study plan: " + e.getMessage());
        }
    }

    public void updateCoursework(StudyPlan studyPlan, Coursework coursework) {
        try {
            studyPlanDAO.updateCoursework(studyPlan, coursework);
        } catch (SQLException e) {
            System.err.println("Error updating coursework: " + e.getMessage());
        }
    }

    public void removeCourseworkFromStudyPlan(StudyPlan studyPlan, Coursework coursework) {
        try {
            studyPlanDAO.removeCourseworkFromStudyPlan(studyPlan, coursework);
        } catch (SQLException e) {
            System.err.println("Error removing coursework from study plan: " + e.getMessage());
        }
    }

    @Deprecated
    public void addCourseworkToStudyPlan(String course, String studyPlanName, 
                                         String courseworkName, String details, 
                                         String dueDate, String status) {
        try {
            List<StudyPlan> coursePlans = getStudyPlansForCourse(course);
            StudyPlan targetPlan = coursePlans.stream()
                    .filter(sp -> sp.getName().equals(studyPlanName))
                    .findFirst()
                    .orElse(null);
            
            if (targetPlan != null) {
                Coursework coursework = new Coursework(courseworkName, details, dueDate, status);
                addCourseworkToStudyPlan(targetPlan, coursework);
            }
        } catch (Exception e) {
            System.err.println("Error adding coursework to study plan: " + e.getMessage());
        }
    }

    @Deprecated
    public void updateCoursework(String course, String studyPlanName, 
                                 String courseworkName, String newName, 
                                 String newDetails, String newDueDate, 
                                 String newStatus) {
        try {
            List<StudyPlan> coursePlans = getStudyPlansForCourse(course);
            StudyPlan targetPlan = coursePlans.stream()
                    .filter(sp -> sp.getName().equals(studyPlanName))
                    .findFirst()
                    .orElse(null);
            
            if (targetPlan != null) {
                List<Coursework> courseworkList = targetPlan.getCourseworkList();
                Coursework targetCoursework = courseworkList.stream()
                        .filter(cw -> cw.getName().equals(courseworkName))
                        .findFirst()
                        .orElse(null);
                
                if (targetCoursework != null) {
                    targetCoursework.setName(newName);
                    targetCoursework.setDetails(newDetails);
                    targetCoursework.setDueDate(newDueDate);
                    targetCoursework.setStatus(newStatus);
                    
                    updateCoursework(targetPlan, targetCoursework);
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating coursework: " + e.getMessage());
        }
    }

    @Deprecated
    public void deleteCoursework(String course, String studyPlanName, String courseworkName) {
        try {
            List<StudyPlan> coursePlans = getStudyPlansForCourse(course);
            StudyPlan targetPlan = coursePlans.stream()
                    .filter(sp -> sp.getName().equals(studyPlanName))
                    .findFirst()
                    .orElse(null);
            
            if (targetPlan != null) {
                Coursework targetCoursework = targetPlan.getCourseworkList().stream()
                        .filter(cw -> cw.getName().equals(courseworkName))
                        .findFirst()
                        .orElse(null);
                
                if (targetCoursework != null) {
                    removeCourseworkFromStudyPlan(targetPlan, targetCoursework);
                }
            }
        } catch (Exception e) {
            System.err.println("Error deleting coursework: " + e.getMessage());
        }
    }

    public Map<String, List<StudyPlan>> getAllStudyPlans() {
        try {
            List<StudyPlan> allPlans = studyPlanDAO.findAll();
            Map<String, List<StudyPlan>> groupedPlans = new HashMap<>();
            
            for (StudyPlan plan : allPlans) {
                groupedPlans.computeIfAbsent(plan.getCourse(), k -> new java.util.ArrayList<>()).add(plan);
            }
            
            return groupedPlans;
        } catch (SQLException e) {
            System.err.println("Error retrieving all study plans: " + e.getMessage());
            return new HashMap<>();
        }
    }
}
