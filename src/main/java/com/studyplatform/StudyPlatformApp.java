package com.studyplatform;

import com.studyplatform.controllers.TaskController;
import com.studyplatform.controllers.GroupController;
import com.studyplatform.controllers.StudyPlanController;
import com.studyplatform.controllers.TutorController;
import com.studyplatform.controllers.NotificationController;

import com.studyplatform.views.TaskView;
import com.studyplatform.views.GroupView;
import com.studyplatform.views.StudyPlanView;
import com.studyplatform.views.TutorView;
import com.studyplatform.views.NotificationView;

import javax.swing.*;

public class StudyPlatformApp extends JFrame {
    // Controllers
    private TaskController taskController;
    private GroupController groupController;
    private StudyPlanController studyPlanController;
    private TutorController tutorController;
    private NotificationController notificationController;

    // Views
    private TaskView taskView;
    private GroupView groupView;
    private StudyPlanView studyPlanView;
    private TutorView tutorView;
    private NotificationView notificationView;

    public StudyPlatformApp() {
        // Initialize controllers
        taskController = new TaskController();
        groupController = new GroupController();
        studyPlanController = new StudyPlanController();
        tutorController = new TutorController();
        notificationController = new NotificationController();

        // Initialize views
        taskView = new TaskView(taskController);
        groupView = new GroupView(groupController);
        studyPlanView = new StudyPlanView(studyPlanController);
        tutorView = new TutorView(tutorController);
        notificationView = new NotificationView(notificationController);

        // Setup main frame
        setTitle("Study Management Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Task Manager", taskView);
        tabbedPane.addTab("Group Management", groupView);
        tabbedPane.addTab("Study Plans", studyPlanView);
        tabbedPane.addTab("Tutoring", tutorView);
        tabbedPane.addTab("Notifications", notificationView);

        // Add tabbed pane to frame
        getContentPane().add(tabbedPane);

        // Center the frame on screen
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        // Ensure GUI is created on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Create and display the application
            StudyPlatformApp app = new StudyPlatformApp();
            app.setVisible(true);
        });
    }
}