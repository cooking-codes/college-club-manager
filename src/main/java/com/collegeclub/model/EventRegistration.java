package com.collegeclub.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Model class representing an Event Registration.
 * Associates a Student with an Event.
 */
public class EventRegistration {
    private int id;
    private int studentId;
    private int eventId;
    private Timestamp registrationDate;
    private String status; // Confirmed, Cancelled

    // Joined helper fields for views & reports
    private String studentName;
    private String studentEmail;
    private String studentPhone;
    private String studentDepartment;
    private String studentYear;
    private String studentDivision;
    private String studentRollNumber;
    private String eventName;
    private Date eventDate;
    private String eventTime;
    private String eventVenue;
    private String organizingClubName;

    public EventRegistration() {}

    public EventRegistration(int id, int studentId, int eventId, Timestamp registrationDate, String status) {
        this.id = id;
        this.studentId = studentId;
        this.eventId = eventId;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }

    public String getStudentDepartment() {
        return studentDepartment;
    }

    public void setStudentDepartment(String studentDepartment) {
        this.studentDepartment = studentDepartment;
    }

    public String getStudentYear() {
        return studentYear;
    }

    public void setStudentYear(String studentYear) {
        this.studentYear = studentYear;
    }

    public String getStudentDivision() {
        return studentDivision;
    }

    public void setStudentDivision(String studentDivision) {
        this.studentDivision = studentDivision;
    }

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public void setStudentRollNumber(String studentRollNumber) {
        this.studentRollNumber = studentRollNumber;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
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

    public String getEventVenue() {
        return eventVenue;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public String getOrganizingClubName() {
        return organizingClubName;
    }

    public void setOrganizingClubName(String organizingClubName) {
        this.organizingClubName = organizingClubName;
    }
}
