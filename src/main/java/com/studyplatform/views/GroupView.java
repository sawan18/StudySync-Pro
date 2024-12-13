package com.studyplatform.views;

import com.studyplatform.controllers.GroupController;
import com.studyplatform.models.Group;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupView extends JPanel {
    private GroupController groupController;
    private JTextField groupNameField;
    private JPanel groupListPanel;

    public GroupView(GroupController groupController) {
        this.groupController = groupController;
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Group Management"));

        // Group form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Group Name
        JLabel groupNameLabel = new JLabel("Group Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(groupNameLabel, gbc);

        groupNameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(groupNameField, gbc);

        // Create Group Button
        JButton createGroupButton = new JButton("Create Group");
        createGroupButton.addActionListener(e -> createGroup());
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(createGroupButton, gbc);

        // Delete Group Button
        JButton deleteGroupButton = new JButton("Delete Group");
        deleteGroupButton.addActionListener(e -> deleteGroup());
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(deleteGroupButton, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Group List
        groupListPanel = new JPanel(new GridLayout(0, 1));
        JScrollPane groupScrollPane = new JScrollPane(groupListPanel);
        add(groupScrollPane, BorderLayout.CENTER);

        // Update initial group list
        updateGroupList();
    }

    private void createGroup() {
        String groupName = groupNameField.getText().trim();
        try {
            Group group = groupController.createGroup(groupName);
            addGroupToList(group);
            groupNameField.setText("");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void deleteGroup() {
        String groupName = groupNameField.getText().trim();
        try {
            groupController.deleteGroup(groupName);
            removeGroupFromList(groupName);
            groupNameField.setText("");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void addGroupToList(Group group) {
        JPanel groupPanel = new JPanel(new BorderLayout());
        groupPanel.setBorder(BorderFactory.createTitledBorder(group.getName()));

        JTextArea groupDetails = new JTextArea();
        groupDetails.setEditable(false);
        groupPanel.add(new JScrollPane(groupDetails), BorderLayout.CENTER);

        // Update initial group details
        updateGroupDetails(group, groupDetails);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5));

        JButton addMemberButton = new JButton("Add Member");
        addMemberButton.addActionListener(e -> addMember(group, groupDetails));
        buttonPanel.add(addMemberButton);

        JButton removeMemberButton = new JButton("Remove Member");
        removeMemberButton.addActionListener(e -> removeMember(group, groupDetails));
        buttonPanel.add(removeMemberButton);

        JButton scheduleMeetingButton = new JButton("Schedule Meeting");
        scheduleMeetingButton.addActionListener(e -> scheduleMeeting(group, groupDetails));
        buttonPanel.add(scheduleMeetingButton);

        JButton uploadFileButton = new JButton("Upload File");
        uploadFileButton.addActionListener(e -> uploadFile(group, groupDetails));
        buttonPanel.add(uploadFileButton);

        JButton downloadFileButton = new JButton("Download File");
        downloadFileButton.addActionListener(e -> downloadFile(group));
        buttonPanel.add(downloadFileButton);

        groupPanel.add(buttonPanel, BorderLayout.SOUTH);

        groupListPanel.add(groupPanel);
        groupListPanel.revalidate();
        groupListPanel.repaint();
    }

    private void removeGroupFromList(String groupName) {
        for (Component comp : groupListPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel groupPanel = (JPanel) comp;
                if (groupPanel.getBorder() instanceof javax.swing.border.TitledBorder) {
                    javax.swing.border.TitledBorder border = (javax.swing.border.TitledBorder) groupPanel.getBorder();
                    if (border.getTitle().equals(groupName)) {
                        groupListPanel.remove(groupPanel);
                        break;
                    }
                }
            }
        }
        groupListPanel.revalidate();
        groupListPanel.repaint();
    }

    private void addMember(Group group, JTextArea groupDetails) {
        String memberName = JOptionPane.showInputDialog(this, "Enter member name:");
        if (memberName != null && !memberName.isEmpty()) {
            groupController.addMemberToGroup(group.getName(), memberName);
            updateGroupDetails(group, groupDetails);
        }
    }

    private void removeMember(Group group, JTextArea groupDetails) {
        String memberName = JOptionPane.showInputDialog(this, "Enter member name to remove:");
        if (memberName != null && !memberName.isEmpty()) {
            groupController.removeMemberFromGroup(group.getName(), memberName);
            updateGroupDetails(group, groupDetails);
        }
    }

    private void scheduleMeeting(Group group, JTextArea groupDetails) {
        JTextField dateField = new JTextField();
        dateField.setToolTipText("MM-dd-yyyy");
        JTextField timeField = new JTextField();
        timeField.setToolTipText("HH:mm");

        JPanel schedulePanel = new JPanel(new GridLayout(2, 2));
        schedulePanel.add(new JLabel("Meeting Date (MM-dd-yyyy):"));
        schedulePanel.add(dateField);
        schedulePanel.add(new JLabel("Meeting Time (HH:mm):"));
        schedulePanel.add(timeField);

        int result = JOptionPane.showConfirmDialog(
                this, schedulePanel, "Schedule Meeting", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Date meetingTime = new SimpleDateFormat("MM-dd-yyyy HH:mm").parse(
                        dateField.getText() + " " + timeField.getText());
                groupController.scheduleMeeting(group.getName(), meetingTime);
                updateGroupDetails(group, groupDetails);
                JOptionPane.showMessageDialog(this, "Meeting scheduled successfully!");
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date/time format. Please use MM-dd-yyyy and HH:mm.");
            }
        }
    }

    private void uploadFile(Group group, JTextArea groupDetails) {
        String fileName = JOptionPane.showInputDialog(this, "Enter file name to upload:");
        if (fileName != null && !fileName.isEmpty()) {
            groupController.uploadFileToGroup(group.getName(), fileName);
            updateGroupDetails(group, groupDetails);
        }
    }

    private void downloadFile(Group group) {
        JOptionPane.showMessageDialog(this, "Downloading file for group: " + group.getName());
    }

    private void updateGroupList() {
        groupListPanel.removeAll();
        for (Group group : groupController.getAllGroups().values()) {
            addGroupToList(group);
        }
        groupListPanel.revalidate();
        groupListPanel.repaint();
    }

    private void updateGroupDetails(Group group, JTextArea groupDetails) {
        // Retrieve updated group from controller
        Group updatedGroup = groupController.getGroup(group.getName());
        
        if (updatedGroup != null) {
            // Format meeting time
            String formattedMeetingTime = (updatedGroup.getMeetingTime() != null)
                    ? new SimpleDateFormat("MM-dd-yyyy HH:mm").format(updatedGroup.getMeetingTime())
                    : "No meeting scheduled.";

            // Prepare member details
            StringBuilder membersBuilder = new StringBuilder("Members:\n");
            for (String member : updatedGroup.getMembers()) {
                membersBuilder.append("- ").append(member).append("\n");
            }

            // Prepare file details
            StringBuilder filesBuilder = new StringBuilder("Files:\n");
            for (String file : updatedGroup.getFiles()) {
                filesBuilder.append("- ").append(file).append("\n");
            }

            // Update group details text area
            groupDetails.setText(
                    membersBuilder.toString() + "\n" +
                            filesBuilder.toString() + "\n" +
                            "Meetings: \n" + formattedMeetingTime);
        }
    }
}
