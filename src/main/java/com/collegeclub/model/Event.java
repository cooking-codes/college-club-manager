package com.collegeclub.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Model class representing a College Event.
 * Contains scheduling, capacity, organizer club, and status details.
 */
public class Event {
    private int id;
    private String eventName;
    private String description;
    private int clubId;
    private String clubName; // Helper for display
    private Date eventDate;
    private String eventTime;
    private String venue;
    private Date registrationDeadline;
    private int maxParticipants;
    private String status; // Upcoming, Completed, Cancelled
    private Timestamp createdAt;
    private int registeredCount; // Helper for capacity calculation

    public Event() {}

    public Event(int id, String eventName, String description, int clubId, Date eventDate,
                 String eventTime, String venue, Date registrationDeadline, int maxParticipants,
                 String status, Timestamp createdAt) {
        this.id = id;
        this.eventName = eventName;
        this.description = description;
        this.clubId = clubId;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.venue = venue;
        this.registrationDeadline = registrationDeadline;
        this.maxParticipants = maxParticipants;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Date getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(Date registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getRegisteredCount() {
        return registeredCount;
    }

    public void setRegisteredCount(int registeredCount) {
        this.registeredCount = registeredCount;
    }

    public boolean isFull() {
        return registeredCount >= maxParticipants;
    }

    public boolean isRegistrationClosed() {
        if (registrationDeadline == null) return false;
        java.time.LocalDate today = java.time.LocalDate.now();
        return today.isAfter(registrationDeadline.toLocalDate());
    }
}
