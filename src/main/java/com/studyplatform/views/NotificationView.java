package com.studyplatform.views;

// notification view
import com.studyplatform.controllers.NotificationController;
import com.studyplatform.models.Notification;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

// view for notification. display and manage notification ui
public class NotificationView extends JPanel {
    private NotificationController notificationController;
    private DefaultTableModel notificationTableModel;
    // table for display notifications
    private JTable notificationTable;
    // label for show unread count
    private JLabel unreadCountLabel;

    public NotificationView(NotificationController notificationController) {
        this.notificationController = notificationController;
        // register this view with controller
        this.notificationController.registerView(this);
        // initialize ui components
        initializeComponents();
    }

    // initialize ui components
    private void initializeComponents() {
        // set layout for panel
        setLayout(new BorderLayout());
        // set border for panel
        setBorder(BorderFactory.createTitledBorder("Notifications"));


        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // make label for show unread count
        unreadCountLabel = new JLabel("Unread Notifications: 0");
        // add label to top panel
        topPanel.add(unreadCountLabel);
        // add top panel to main panel
        add(topPanel, BorderLayout.NORTH);

        // make table model for store notifications
        String[] columnNames = {"Title", "Description", "Timestamp", "Status"};
        notificationTableModel = new DefaultTableModel(columnNames, 0);
        // make table for display notifications
        notificationTable = new JTable(notificationTableModel);
        JScrollPane tableScrollPane = new JScrollPane(notificationTable);
        // add table to main panel
        add(tableScrollPane, BorderLayout.CENTER);

        // button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // marks as read button
        JButton markReadButton = new JButton("Mark as Read");
        markReadButton.addActionListener(e -> markSelectedNotificationAsRead());
        buttonPanel.add(markReadButton);

        // clear all button
        JButton clearAllButton = new JButton("Clear All");
        clearAllButton.addActionListener(e -> clearAllNotifications());
        buttonPanel.add(clearAllButton);

        // add button panel to main panel
        add(buttonPanel, BorderLayout.SOUTH);

        // refresh view to show notifications
        refreshView();
    }

    // mark selected notification as read
    private void markSelectedNotificationAsRead() {
        // get selected row from table
        int selectedRow = notificationTable.getSelectedRow();
        // check if row selected
        if (selectedRow >= 0) {
            // get all notifications from controller
            List<Notification> notifications = notificationController.getAllNotifications();
            // get selected notification
            Notification selectedNotification = notifications.get(selectedRow);
            
            // mark notification as read in controller
            notificationController.markNotificationAsRead(selectedNotification);
            refreshView();
        } else {
            // show dialog if no notification selected
            JOptionPane.showMessageDialog(this, "No notification selected.");
        }
    }

    // clear all notifications
    private void clearAllNotifications() {
        // show confirm dialog
        int confirmDialog = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to clear all notifications?", 
            "Confirm Clear", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmDialog == JOptionPane.YES_OPTION) {
            // clear all notifications in controller
            notificationController.clearAllNotifications();
            refreshView();
        }
    }

    // refresh view
    public void refreshView() {
        try {
            // clear existing rows from table model
            notificationTableModel.setRowCount(0);
            
            // get all non-deleted notifications from controller
            List<Notification> notifications = notificationController.getAllNotifications();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
            
            // track unread count
            int unreadCount = 0;
            
            // populate table model with notifications
            for (Notification notification : notifications) {
                // skip deleted notifications
                if (notification.isDeleted()) continue;
                
                // check if notification is unread
                if (!notification.isRead()) {
                    // increment unread count
                    unreadCount++;
                }
                
                // add notification to table model
                notificationTableModel.addRow(new Object[]{
                    notification.getTitle(),
                    notification.getDescription(),
                    sdf.format(notification.getTimestamp()),
                    notification.isRead() ? "Read" : "Unread"
                });
            }
            
            // update unread count label
            unreadCountLabel.setText("Unread Notifications: " + unreadCount);
            
        } catch (Exception e) {
            System.err.println("Error refreshing notifications: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
