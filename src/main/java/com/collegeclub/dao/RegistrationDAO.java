package com.collegeclub.dao;

import com.collegeclub.model.ClubRegistration;
import com.collegeclub.model.EventRegistration;
import com.collegeclub.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Club and Event Registrations.
 * Uses pure JDBC with PreparedStatement to prevent SQL Injection and ensure data integrity.
 */
public class RegistrationDAO {

    public boolean isAlreadyRegisteredClub(int studentId, int clubId) throws SQLException {
        String sql = "SELECT id FROM club_registrations WHERE student_id = ? AND club_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean registerClub(int studentId, int clubId) throws SQLException {
        String sql = "INSERT INTO club_registrations (student_id, club_id, status) VALUES (?, ?, 'Approved')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, clubId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean isAlreadyRegisteredEvent(int studentId, int eventId) throws SQLException {
        String sql = "SELECT id FROM event_registrations WHERE student_id = ? AND event_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean registerEvent(int studentId, int eventId) throws SQLException {
        String sql = "INSERT INTO event_registrations (student_id, event_id, status) VALUES (?, ?, 'Confirmed')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, eventId);
            return ps.executeUpdate() > 0;
        }
    }

    public ClubRegistration findClubRegistrationById(int registrationId) throws SQLException {
        String sql =
                "SELECT cr.*, s.full_name, s.email, s.phone, s.department, s.engineering_year, " +
                "s.division, s.roll_number, c.name AS club_name, c.category AS club_category " +
                "FROM club_registrations cr " +
                "JOIN students s ON cr.student_id = s.id " +
                "JOIN clubs c ON cr.club_id = c.id " +
                "WHERE cr.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, registrationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                ClubRegistration cr = new ClubRegistration(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("club_id"),
                        rs.getTimestamp("registration_date"),
                        rs.getString("status")
                );
                cr.setStudentName(rs.getString("full_name"));
                cr.setStudentEmail(rs.getString("email"));
                cr.setStudentPhone(rs.getString("phone"));
                cr.setStudentDepartment(rs.getString("department"));
                cr.setStudentYear(rs.getString("engineering_year"));
                cr.setStudentDivision(rs.getString("division"));
                cr.setStudentRollNumber(rs.getString("roll_number"));
                cr.setClubName(rs.getString("club_name"));
                cr.setClubCategory(rs.getString("club_category"));
                return cr;
            }
        }
    }

    public List<ClubRegistration> findAllClubRegistrations() throws SQLException {
        return searchClubRegistrations(null, null);
    }

    public List<ClubRegistration> searchClubRegistrations(String query, Integer clubId) throws SQLException {
        List<ClubRegistration> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT cr.*, s.full_name, s.email, s.phone, s.department, s.engineering_year, " +
                "s.division, s.roll_number, c.name AS club_name, c.category AS club_category " +
                "FROM club_registrations cr " +
                "JOIN students s ON cr.student_id = s.id " +
                "JOIN clubs c ON cr.club_id = c.id " +
                "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (s.full_name LIKE ? OR s.email LIKE ? OR s.roll_number LIKE ? OR s.department LIKE ?) ");
            String q = "%" + query.trim() + "%";
            params.add(q);
            params.add(q);
            params.add(q);
            params.add(q);
        }

        if (clubId != null && clubId > 0) {
            sql.append("AND cr.club_id = ? ");
            params.add(clubId);
        }

        sql.append("ORDER BY cr.registration_date DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ClubRegistration cr = new ClubRegistration(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("club_id"),
                            rs.getTimestamp("registration_date"),
                            rs.getString("status")
                    );
                    cr.setStudentName(rs.getString("full_name"));
                    cr.setStudentEmail(rs.getString("email"));
                    cr.setStudentPhone(rs.getString("phone"));
                    cr.setStudentDepartment(rs.getString("department"));
                    cr.setStudentYear(rs.getString("engineering_year"));
                    cr.setStudentDivision(rs.getString("division"));
                    cr.setStudentRollNumber(rs.getString("roll_number"));
                    cr.setClubName(rs.getString("club_name"));
                    cr.setClubCategory(rs.getString("club_category"));
                    list.add(cr);
                }
            }
        }
        return list;
    }

    public List<EventRegistration> findAllEventRegistrations() throws SQLException {
        return searchEventRegistrations(null, null);
    }

    public List<EventRegistration> searchEventRegistrations(String query, Integer eventId) throws SQLException {
        List<EventRegistration> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT er.*, s.full_name, s.email, s.phone, s.department, s.engineering_year, " +
                "s.division, s.roll_number, e.event_name, e.event_date, e.event_time, e.venue, c.name AS organizing_club_name " +
                "FROM event_registrations er " +
                "JOIN students s ON er.student_id = s.id " +
                "JOIN events e ON er.event_id = e.id " +
                "JOIN clubs c ON e.club_id = c.id " +
                "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (s.full_name LIKE ? OR s.email LIKE ? OR s.roll_number LIKE ? OR s.department LIKE ? OR e.event_name LIKE ?) ");
            String q = "%" + query.trim() + "%";
            params.add(q);
            params.add(q);
            params.add(q);
            params.add(q);
            params.add(q);
        }

        if (eventId != null && eventId > 0) {
            sql.append("AND er.event_id = ? ");
            params.add(eventId);
        }

        sql.append("ORDER BY er.registration_date DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EventRegistration er = new EventRegistration(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("event_id"),
                            rs.getTimestamp("registration_date"),
                            rs.getString("status")
                    );
                    er.setStudentName(rs.getString("full_name"));
                    er.setStudentEmail(rs.getString("email"));
                    er.setStudentPhone(rs.getString("phone"));
                    er.setStudentDepartment(rs.getString("department"));
                    er.setStudentYear(rs.getString("engineering_year"));
                    er.setStudentDivision(rs.getString("division"));
                    er.setStudentRollNumber(rs.getString("roll_number"));
                    er.setEventName(rs.getString("event_name"));
                    er.setEventDate(rs.getDate("event_date"));
                    er.setEventTime(rs.getString("event_time"));
                    er.setEventVenue(rs.getString("venue"));
                    er.setOrganizingClubName(rs.getString("organizing_club_name"));
                    list.add(er);
                }
            }
        }
        return list;
    }

    public int countClubRegistrations() throws SQLException {
        String sql = "SELECT COUNT(*) FROM club_registrations";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countEventRegistrations() throws SQLException {
        String sql = "SELECT COUNT(*) FROM event_registrations";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
