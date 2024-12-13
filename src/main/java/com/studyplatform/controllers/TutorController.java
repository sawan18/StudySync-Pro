package com.studyplatform.controllers;

import com.studyplatform.models.Tutor;
import com.studyplatform.dao.TutorDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TutorController {
    private TutorDAO tutorDAO;

    public TutorController() {
        this.tutorDAO = new TutorDAO();
        try {
            this.tutorDAO.createTable();
            populateInitialTutors();
        } catch (SQLException e) {
            System.err.println("Error creating tutors table: " + e.getMessage());
        }
    }

    private void populateInitialTutors() {
        try {
            List<Tutor> existingTutors = tutorDAO.findAll();
            if (existingTutors.isEmpty()) {
                Tutor[] initialTutors = {
                    new Tutor("Tutor A", "Math", "12-10-2024", "Room 101", false),
                    new Tutor("Tutor B", "Science", "12-11-2024", "Room 202", false),
                    new Tutor("Tutor C", "Math", "12-12-2024", "Room 101", false),
                    new Tutor("Tutor D", "English", "12-13-2024", "Room 303", false)
                };

                for (Tutor tutor : initialTutors) {
                    tutorDAO.create(tutor);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error populating initial tutors: " + e.getMessage());
        }
    }

    public Tutor addTutor(String name, String tutorClass, String availableDate, String location) {
        try {
            Tutor tutor = new Tutor(name, tutorClass, availableDate, location, false);
            tutorDAO.create(tutor);
            return tutor;
        } catch (SQLException e) {
            System.err.println("Error adding tutor: " + e.getMessage());
            return null;
        }
    }

    public void scheduleTutorSession(String tutorName) {
        try {
            Tutor tutor = findTutorByName(tutorName);
            if (tutor != null && !tutor.isScheduled()) {
                tutor.setScheduled(true);
                tutorDAO.update(tutor);
            }
        } catch (SQLException e) {
            System.err.println("Error scheduling tutor session: " + e.getMessage());
        }
    }

    public List<Tutor> getTutorsByClass(String tutorClass) {
        try {
            return tutorDAO.findAll().stream()
                    .filter(tutor -> tutor.getTutorClass().equals(tutorClass))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error retrieving tutors by class: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Tutor findTutorByName(String name) {
        try {
            return tutorDAO.findAll().stream()
                    .filter(tutor -> tutor.getName().equals(name))
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            System.err.println("Error finding tutor by name: " + e.getMessage());
            return null;
        }
    }

    public List<String> getUniqueTutorClasses() {
        try {
            return tutorDAO.findAll().stream()
                    .map(Tutor::getTutorClass)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error retrieving unique tutor classes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Tutor> getAllTutors() {
        try {
            return tutorDAO.findAll();
        } catch (SQLException e) {
            System.err.println("Error retrieving all tutors: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Tutor> findScheduledTutorsForToday(Date today) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String todayStr = sdf.format(today);

        try {
            return tutorDAO.findAll().stream()
                    .filter(tutor -> tutor.isScheduled() && tutor.getAvailableDate().equals(todayStr))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error finding scheduled tutors for today: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
