package com.studyplatform.views;

import com.studyplatform.controllers.TutorController;
import com.studyplatform.models.Tutor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class TutorView extends JPanel {
    private TutorController tutorController;
    private JComboBox<String> classDropdown;
    private DefaultTableModel sessionTableModel;
    private JTable sessionTable;

    public TutorView(TutorController tutorController) {
        this.tutorController = tutorController;
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        classDropdown = new JComboBox<>();
        classDropdown.addItem("Select a Class");
        
        List<String> uniqueClasses = tutorController.getUniqueTutorClasses();
        uniqueClasses.forEach(classDropdown::addItem);

        classDropdown.addActionListener(e -> updateSessionTable());
        topPanel.add(new JLabel("Class:"));
        topPanel.add(classDropdown);
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Name", "Available Date", "Location", "Scheduled"};
        sessionTableModel = new DefaultTableModel(columnNames, 0);
        sessionTable = new JTable(sessionTableModel);
        JScrollPane tableScrollPane = new JScrollPane(sessionTable);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton scheduleButton = new JButton("Schedule Session");
        scheduleButton.addActionListener(e -> scheduleSession());
        buttonPanel.add(scheduleButton);

        JButton volunteerButton = new JButton("Volunteer as Tutor");
        volunteerButton.addActionListener(e -> volunteerAsTutor());
        buttonPanel.add(volunteerButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateSessionTable() {
        sessionTableModel.setRowCount(0);

        String selectedClass = (String) classDropdown.getSelectedItem();
        if (selectedClass == null || selectedClass.equals("Select a Class")) {
            return;
        }

        List<Tutor> tutors = tutorController.getTutorsByClass(selectedClass);
        for (Tutor tutor : tutors) {
            sessionTableModel.addRow(new Object[]{
                tutor.getName(),
                tutor.getAvailableDate(),
                tutor.getLocation(),
                tutor.isScheduled() ? "Yes" : "No"
            });
        }
    }

    private void scheduleSession() {
        int selectedRow = sessionTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "No session selected.");
            return;
        }

        String tutorName = (String) sessionTableModel.getValueAt(selectedRow, 0);
        
        try {
            Tutor tutor = tutorController.findTutorByName(tutorName);
            if (tutor != null) {
                if (tutor.isScheduled()) {
                    JOptionPane.showMessageDialog(this, "This session is already scheduled.");
                    return;
                }

                tutorController.scheduleTutorSession(tutorName);
                
                updateSessionTable();
                
                JOptionPane.showMessageDialog(this, "Session scheduled successfully.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error scheduling session: " + e.getMessage());
        }
    }

    private void volunteerAsTutor() {
        // Create input panel for tutor details
        JTextField nameField = new JTextField();
        JTextField classField = new JTextField();
        JTextField availableDateField = new JTextField();
        availableDateField.setToolTipText("MM-dd-yyyy");
        JTextField locationField = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Class:"));
        inputPanel.add(classField);
        inputPanel.add(new JLabel("Available Date (MM-dd-yyyy):"));
        inputPanel.add(availableDateField);
        inputPanel.add(new JLabel("Location:"));
        inputPanel.add(locationField);

        int result = JOptionPane.showConfirmDialog(
                this, inputPanel, "Volunteer as Tutor", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String tutorClass = classField.getText().trim();
                String availableDate = availableDateField.getText().trim();
                String location = locationField.getText().trim();

                if (name.isEmpty() || tutorClass.isEmpty() || availableDate.isEmpty() || location.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields must be filled.");
                    return;
                }

                new SimpleDateFormat("MM-dd-yyyy").parse(availableDate);

                tutorController.addTutor(name, tutorClass, availableDate, location);

                List<String> classes = tutorController.getUniqueTutorClasses();
                classDropdown.removeAllItems();
                classDropdown.addItem("Select a Class");
                classes.forEach(classDropdown::addItem);

                JOptionPane.showMessageDialog(this, "Tutor added successfully!");
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM-dd-yyyy.");
            }
        }
    }
}
