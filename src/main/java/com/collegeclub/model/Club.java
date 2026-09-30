package com.collegeclub.model;

import java.sql.Timestamp;

/**
 * Model class representing a College Club.
 * Captures core details, objectives, activities, coordinators, and schedules.
 */
public class Club {
    private int id;
    private String name;
    private String category;
    private String description;
    private String objectives;
    private String activities;
    private String facultyCoordinator;
    private String studentCoordinator;
    private String meetingDay;
    private String meetingTime;
    private String location;
    private String contactEmail;
    private Timestamp createdAt;
    private int memberCount; // Dynamic helper field for admin/display

    public Club() {}

    public Club(int id, String name, String category, String description, String objectives,
                String activities, String facultyCoordinator, String studentCoordinator,
                String meetingDay, String meetingTime, String location, String contactEmail,
                Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.objectives = objectives;
        this.activities = activities;
        this.facultyCoordinator = facultyCoordinator;
        this.studentCoordinator = studentCoordinator;
        this.meetingDay = meetingDay;
        this.meetingTime = meetingTime;
        this.location = location;
        this.contactEmail = contactEmail;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public String getFacultyCoordinator() {
        return facultyCoordinator;
    }

    public void setFacultyCoordinator(String facultyCoordinator) {
        this.facultyCoordinator = facultyCoordinator;
    }

    public String getStudentCoordinator() {
        return studentCoordinator;
    }

    public void setStudentCoordinator(String studentCoordinator) {
        this.studentCoordinator = studentCoordinator;
    }

    public String getMeetingDay() {
        return meetingDay;
    }

    public void setMeetingDay(String meetingDay) {
        this.meetingDay = meetingDay;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
}
