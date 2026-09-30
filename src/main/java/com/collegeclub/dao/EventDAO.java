package com.collegeclub.dao;

import com.collegeclub.model.Event;
import com.collegeclub.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Events.
 * Pure JDBC operations for events, capacity calculation, and status tracking.
 */
public class EventDAO {

    public List<Event> findAll() throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT e.*, c.name AS club_name, " +
                     "(SELECT COUNT(*) FROM event_registrations er WHERE er.event_id = e.id) AS registered_count " +
                     "FROM events e " +
                     "JOIN clubs c ON e.club_id = c.id " +
                     "ORDER BY e.event_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Event> findUpcoming() throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT e.*, c.name AS club_name, " +
                     "(SELECT COUNT(*) FROM event_registrations er WHERE er.event_id = e.id) AS registered_count " +
                     "FROM events e " +
                     "JOIN clubs c ON e.club_id = c.id " +
                     "WHERE e.status = 'Upcoming' AND e.event_date >= CURRENT_DATE " +
                     "ORDER BY e.event_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Event findById(int id) throws SQLException {
        String sql = "SELECT e.*, c.name AS club_name, " +
                     "(SELECT COUNT(*) FROM event_registrations er WHERE er.event_id = e.id) AS registered_count " +
                     "FROM events e " +
                     "JOIN clubs c ON e.club_id = c.id " +
                     "WHERE e.id = ?";
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

    public List<Event> searchAndFilter(String query, Integer clubId) throws SQLException {
        List<Event> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT e.*, c.name AS club_name, " +
                "(SELECT COUNT(*) FROM event_registrations er WHERE er.event_id = e.id) AS registered_count " +
                "FROM events e " +
                "JOIN clubs c ON e.club_id = c.id " +
                "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (e.event_name LIKE ? OR e.description LIKE ? OR e.venue LIKE ? OR c.name LIKE ?) ");
            String q = "%" + query.trim() + "%";
            params.add(q);
            params.add(q);
            params.add(q);
            params.add(q);
        }

        if (clubId != null && clubId > 0) {
            sql.append("AND e.club_id = ? ");
            params.add(clubId);
        }

        sql.append("ORDER BY e.event_date ASC");

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

    public boolean insert(Event e) throws SQLException {
        String sql = "INSERT INTO events (event_name, description, club_id, event_date, event_time, venue, " +
                     "registration_deadline, max_participants, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getEventName());
            ps.setString(2, e.getDescription());
            ps.setInt(3, e.getClubId());
            ps.setDate(4, e.getEventDate());
            ps.setString(5, e.getEventTime());
            ps.setString(6, e.getVenue());
            ps.setDate(7, e.getRegistrationDeadline());
            ps.setInt(8, e.getMaxParticipants());
            ps.setString(9, (e.getStatus() != null && !e.getStatus().isEmpty()) ? e.getStatus() : "Upcoming");
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        e.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean update(Event e) throws SQLException {
        String sql = "UPDATE events SET event_name = ?, description = ?, club_id = ?, event_date = ?, " +
                     "event_time = ?, venue = ?, registration_deadline = ?, max_participants = ?, status = ? " +
                     "WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getEventName());
            ps.setString(2, e.getDescription());
            ps.setInt(3, e.getClubId());
            ps.setDate(4, e.getEventDate());
            ps.setString(5, e.getEventTime());
            ps.setString(6, e.getVenue());
            ps.setDate(7, e.getRegistrationDeadline());
            ps.setInt(8, e.getMaxParticipants());
            ps.setString(9, e.getStatus());
            ps.setInt(10, e.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM events WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int getRegisteredCount(int eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM event_registrations WHERE event_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM events";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Event mapRow(ResultSet rs) throws SQLException {
        Event e = new Event(
                rs.getInt("id"),
                rs.getString("event_name"),
                rs.getString("description"),
                rs.getInt("club_id"),
                rs.getDate("event_date"),
                rs.getString("event_time"),
                rs.getString("venue"),
                rs.getDate("registration_deadline"),
                rs.getInt("max_participants"),
                rs.getString("status"),
                rs.getTimestamp("created_at")
        );
        try {
            e.setClubName(rs.getString("club_name"));
        } catch (SQLException ignored) {}
        try {
            e.setRegisteredCount(rs.getInt("registered_count"));
        } catch (SQLException ignored) {}
        return e;
    }
}
