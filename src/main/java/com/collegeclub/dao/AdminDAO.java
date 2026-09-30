package com.collegeclub.dao;

import com.collegeclub.model.Admin;
import com.collegeclub.util.DBConnection;
import com.collegeclub.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) for Admin Authentication and Management.
 */
public class AdminDAO {

    public Admin findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM admins WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Authenticates administrator credentials.
     * Compares entered password using PasswordUtil against database record.
     */
    public Admin authenticate(String username, String password) throws SQLException {
        Admin admin = findByUsername(username);
        if (admin != null) {
            if (PasswordUtil.verifyPassword(password, admin.getPasswordHash())) {
                return admin;
            }
        }
        return null;
    }
}
