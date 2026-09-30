package com.collegeclub.model;

import java.sql.Timestamp;

/**
 * Model class representing a Student.
 * Contains academic and contact details.
 */
public class Student {
    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String department;
    private String engineeringYear;
    private String division;
    private String rollNumber;
    private Timestamp createdAt;

    public Student() {}

    public Student(int id, String fullName, String email, String phone, String department, 
                   String engineeringYear, String division, String rollNumber, Timestamp createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.engineeringYear = engineeringYear;
        this.division = division;
        this.rollNumber = rollNumber;
        this.createdAt = createdAt;
    }

    public Student(String fullName, String email, String phone, String department, 
                   String engineeringYear, String division, String rollNumber) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.engineeringYear = engineeringYear;
        this.division = division;
        this.rollNumber = rollNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEngineeringYear() {
        return engineeringYear;
    }

    public void setEngineeringYear(String engineeringYear) {
        this.engineeringYear = engineeringYear;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
