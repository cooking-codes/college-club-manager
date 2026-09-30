package com.collegeclub.dao;

import com.collegeclub.model.Club;
import com.collegeclub.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Clubs.
 * Handles pure JDBC CRUD operations, searching, and filtering.
 */
public class ClubDAO {

    public List<Club> findAll() throws SQLException {
        List<Club> list = new ArrayList<>();
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM club_registrations cr WHERE cr.club_id = c.id) AS member_count " +
                     "FROM clubs c ORDER BY c.name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Club findById(int id) throws SQLException {
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM club_registrations cr WHERE cr.club_id = c.id) AS member_count " +
                     "FROM clubs c WHERE c.id = ?";
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

    public List<Club> searchAndFilter(String query, String category) throws SQLException {
        List<Club> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.*, (SELECT COUNT(*) FROM club_registrations cr WHERE cr.club_id = c.id) AS member_count " +
                "FROM clubs c WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (c.name LIKE ? OR c.description LIKE ? OR c.activities LIKE ? OR c.objectives LIKE ?) ");
            String q = "%" + query.trim() + "%";
            params.add(q);
            params.add(q);
            params.add(q);
            params.add(q);
        }

        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All")) {
            sql.append("AND c.category = ? ");
            params.add(category.trim());
        }

        sql.append("ORDER BY c.name ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public boolean insert(Club c) throws SQLException {
        String sql = "INSERT INTO clubs (name, category, description, objectives, activities, " +
                     "faculty_coordinator, student_coordinator, meeting_day, meeting_time, location, contact_email) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getCategory());
            ps.setString(3, c.getDescription());
            ps.setString(4, c.getObjectives());
            ps.setString(5, c.getActivities());
            ps.setString(6, c.getFacultyCoordinator());
            ps.setString(7, c.getStudentCoordinator());
            ps.setString(8, c.getMeetingDay());
            ps.setString(9, c.getMeetingTime());
            ps.setString(10, c.getLocation());
            ps.setString(11, c.getContactEmail());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean update(Club c) throws SQLException {
        String sql = "UPDATE clubs SET name = ?, category = ?, description = ?, objectives = ?, activities = ?, " +
                     "faculty_coordinator = ?, student_coordinator = ?, meeting_day = ?, meeting_time = ?, " +
                     "location = ?, contact_email = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getCategory());
            ps.setString(3, c.getDescription());
            ps.setString(4, c.getObjectives());
            ps.setString(5, c.getActivities());
            ps.setString(6, c.getFacultyCoordinator());
            ps.setString(7, c.getStudentCoordinator());
            ps.setString(8, c.getMeetingDay());
            ps.setString(9, c.getMeetingTime());
            ps.setString(10, c.getLocation());
            ps.setString(11, c.getContactEmail());
            ps.setInt(12, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM clubs WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM clubs";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Club mapRow(ResultSet rs) throws SQLException {
        Club c = new Club(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getString("objectives"),
                rs.getString("activities"),
                rs.getString("faculty_coordinator"),
                rs.getString("student_coordinator"),
                rs.getString("meeting_day"),
                rs.getString("meeting_time"),
                rs.getString("location"),
                rs.getString("contact_email"),
                rs.getTimestamp("created_at")
        );
        try {
            c.setMemberCount(rs.getInt("member_count"));
        } catch (SQLException ignored) {}
        return c;
    }
}
