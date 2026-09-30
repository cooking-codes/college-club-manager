package com.collegeclub.dao;

import com.collegeclub.model.Student;
import com.collegeclub.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object (DAO) for Students.
 * Implements pure JDBC with PreparedStatements.
 */
public class StudentDAO {

    public Student findById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Student findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM students WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Student findByRollNumber(String rollNumber) throws SQLException {
        String sql = "SELECT * FROM students WHERE roll_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Inserts or returns existing student ID based on email or roll number.
     * Ensures we don't duplicate student master records when registering for clubs or events.
     */
    public int findOrCreate(Student s) throws SQLException {
        Student existing = findByEmail(s.getEmail());
        if (existing == null) {
            existing = findByRollNumber(s.getRollNumber());
        }

        if (existing != null) {
            // Update latest contact/academic info if changed
            String updateSql = "UPDATE students SET full_name = ?, phone = ?, department = ?, " +
                               "engineering_year = ?, division = ?, roll_number = ? WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, s.getFullName());
                ps.setString(2, s.getPhone());
                ps.setString(3, s.getDepartment());
                ps.setString(4, s.getEngineeringYear());
                ps.setString(5, s.getDivision());
                ps.setString(6, s.getRollNumber());
                ps.setInt(7, existing.getId());
                ps.executeUpdate();
            }
            return existing.getId();
        }

        // Insert new student
        String insertSql = "INSERT INTO students (full_name, email, phone, department, engineering_year, division, roll_number) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getFullName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPhone());
            ps.setString(4, s.getDepartment());
            ps.setString(5, s.getEngineeringYear());
            ps.setString(6, s.getDivision());
            ps.setString(7, s.getRollNumber());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("department"),
                rs.getString("engineering_year"),
                rs.getString("division"),
                rs.getString("roll_number"),
                rs.getTimestamp("created_at")
        );
    }
}
