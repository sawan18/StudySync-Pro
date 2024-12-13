package com.studyplatform.controllers;

import com.studyplatform.models.Notification;
import com.studyplatform.models.Task;
import com.studyplatform.models.Tutor;
import com.studyplatform.views.NotificationView;
import com.studyplatform.dao.NotificationDAO;
import com.studyplatform.dao.TaskDAO;
import com.studyplatform.dao.TutorDAO;
import com.studyplatform.dao.StudyPlanDAO;
import com.studyplatform.models.Coursework;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class NotificationController {
    private NotificationDAO notificationDAO;
    private TaskDAO taskDAO;
    private TutorDAO tutorDAO;
    private StudyPlanDAO studyPlanDAO;
    private ScheduledExecutorService scheduler;

    public NotificationController() {
        this.notificationDAO = new NotificationDAO();
        this.taskDAO = new TaskDAO();
        this.tutorDAO = new TutorDAO();
        this.studyPlanDAO = new StudyPlanDAO();
        try {
            this.notificationDAO.createTable();
        } catch (SQLException e) {
            System.err.println("Error creating notifications table: " + e.getMessage());
        }

        startDueTaskNotificationScheduler();
        startScheduledTutorNotificationScheduler();
        startCourseworkDueNotificationScheduler();
    }

    private void checkAndCreateDueTaskNotifications() {
        try {
            Date today = new Date();
            List<Task> dueTasks = taskDAO.findTasksDueToday(today);

            if (dueTasks.isEmpty()) {
                System.out.println("No tasks are due today.");
                return;
            } else {
                System.out.println("Found " + dueTasks.size() + " tasks due today.");
            }

            for (Task task : dueTasks) {
                // Check if a notification for this task already exists
                String taskNotificationContent = task.getTitle() + " is due today!";
                boolean notificationExists = false;
                
                try {
                    List<Notification> existingNotifications = notificationDAO.findAll();
                    notificationExists = existingNotifications.stream()
                        .anyMatch(n -> n.getDescription().equals(taskNotificationContent));
                } catch (SQLException e) {
                    System.err.println("Error checking existing notifications: " + e.getMessage());
                }
                
                // Only create notification if it doesn't exist
                if (!notificationExists) {
                    createNotification(
                            "Task Due Today",
                            taskNotificationContent);
                } else {
                    System.out.println("Notification for task '" + task.getTitle() + "' already exists.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking for due tasks: " + e.getMessage());
        }
    }

    private void startDueTaskNotificationScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            checkAndCreateDueTaskNotifications();
        }, 0, 10, TimeUnit.SECONDS);
    }

    private void checkAndCreateScheduledTutorNotifications() {
        try {
            Date today = new Date();
            List<Tutor> scheduledTutors = tutorDAO.findScheduledTutorsForToday(today);

            if (scheduledTutors.isEmpty()) {
                System.out.println("No tutor sessions are scheduled for today.");
                return;
            } else {
                System.out.println("Found " + scheduledTutors.size() + " tutor sessions scheduled for today.");
            }

            for (Tutor tutor : scheduledTutors) {
                // Check if a notification for this tutor session already exists
                String tutorNotificationContent = "Tutor session for '" + tutor.getName() + "' is scheduled today!";
                boolean notificationExists = false;

                try {
                    List<Notification> existingNotifications = notificationDAO.findAll();
                    notificationExists = existingNotifications.stream()
                        .anyMatch(n -> n.getDescription().equals(tutorNotificationContent));
                } catch (SQLException e) {
                    System.err.println("Error checking existing notifications: " + e.getMessage());
                }

                // Only create notification if it doesn't exist
                if (!notificationExists) {
                    createNotification(
                            "Tutor Session Scheduled Today",
                            tutorNotificationContent);
                } else {
                    System.out.println("Notification for tutor session '" + tutor.getName() + "' already exists.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking for scheduled tutor sessions: " + e.getMessage());
        }
    }

    private void startScheduledTutorNotificationScheduler() {
        scheduler.scheduleAtFixedRate(() -> {
            checkAndCreateScheduledTutorNotifications();
        }, 0, 10, TimeUnit.SECONDS);
    }

    private void startCourseworkDueNotificationScheduler() {
        scheduler.scheduleAtFixedRate(() -> {
            checkAndCreateCourseworkDueNotifications();
        }, 0, 10, TimeUnit.SECONDS);
    }

    private void checkAndCreateCourseworkDueNotifications() {
        try {
            Date today = new Date();
            List<Coursework> dueCourseworks = studyPlanDAO.findCourseworkDueOnDate(today);

            if (dueCourseworks.isEmpty()) {
                System.out.println("No coursework due today.");
                return;
            } else {
                System.out.println("Found " + dueCourseworks.size() + " coursework(s) due today.");
            }

            for (Coursework coursework : dueCourseworks) {
                // Check if a notification for this coursework already exists
                String courseworkNotificationContent = coursework.getName() + " is due today!";
                boolean notificationExists = false;
                
                try {
                    List<Notification> existingNotifications = notificationDAO.findAll();
                    notificationExists = existingNotifications.stream()
                        .anyMatch(n -> n.getDescription().equals(courseworkNotificationContent));
                } catch (SQLException e) {
                    System.err.println("Error checking existing notifications: " + e.getMessage());
                }
                
                // Only create notification if it doesn't exist
                if (!notificationExists) {
                    createNotification(
                            "Coursework Due Today",
                            courseworkNotificationContent);
                } else {
                    System.out.println("Notification for coursework '" + coursework.getName() + "' already exists.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking coursework due notifications: " + e.getMessage());
        }
    }

    public Notification createNotification(String title, String description) {
        try {
            Notification notification = new Notification(title, description);
            notificationDAO.create(notification);
            refreshViews();  // Refresh all views after creating a notification
            System.out.println("Created Notification: " + title + " - " + description);
            return notification;
        } catch (SQLException e) {
            System.err.println("Error creating notification: " + e.getMessage());
            return null;
        }
    }

    public List<Notification> getAllNotifications() {
        try {
            return notificationDAO.findAll();
        } catch (SQLException e) {
            System.err.println("Error retrieving notifications: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Notification> getUnreadNotifications() {
        try {
            return notificationDAO.findAll().stream()
                    .filter(notification -> !notification.isRead())
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error retrieving unread notifications: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void markNotificationAsRead(Notification notification) {
        try {
            notification.markAsRead();
            notificationDAO.update(notification);
        } catch (SQLException e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
        }
    }

    public void clearAllNotifications() {
        try {
            List<Notification> notifications = notificationDAO.findAll();
            for (Notification notification : notifications) {
                notification.markAsDeleted();
                notificationDAO.update(notification);
            }
        } catch (SQLException e) {
            System.err.println("Error clearing notifications: " + e.getMessage());
        }
    }

    public int getUnreadNotificationCount() {
        try {
            return (int) notificationDAO.findAll().stream()
                    .filter(notification -> !notification.isRead())
                    .count();
        } catch (SQLException e) {
            System.err.println("Error counting unread notifications: " + e.getMessage());
            return 0;
        }
    }

    private List<NotificationView> notificationViews = new ArrayList<>();

    public void registerView(NotificationView view) {
        notificationViews.add(view);
    }

    public void unregisterView(NotificationView view) {
        notificationViews.remove(view);
    }

    private void refreshViews() {
        for (NotificationView view : notificationViews) {
            view.refreshView();
        }
    }
}
