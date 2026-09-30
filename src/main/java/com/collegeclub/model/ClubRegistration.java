package com.collegeclub.model;

import java.sql.Timestamp;

/**
 * Model class representing a Club Registration.
 * Associates a Student with a Club.
 */
public class ClubRegistration {
    private int id;
    private int studentId;
    private int clubId;
    private Timestamp registrationDate;
    private String status; // Approved, Pending, Rejected

    // Joined helper fields for views & reports
    private String studentName;
    private String studentEmail;
    private String studentPhone;
    private String studentDepartment;
    private String studentYear;
    private String studentDivision;
    private String studentRollNumber;
    private String clubName;
    private String clubCategory;

    public ClubRegistration() {}

    public ClubRegistration(int id, int studentId, int clubId, Timestamp registrationDate, String status) {
        this.id = id;
        this.studentId = studentId;
        this.clubId = clubId;
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

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
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

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubCategory() {
        return clubCategory;
    }

    public void setClubCategory(String clubCategory) {
        this.clubCategory = clubCategory;
    }
}
