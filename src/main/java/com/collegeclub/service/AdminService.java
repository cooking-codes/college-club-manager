package com.collegeclub.service;

import com.collegeclub.dao.AdminDAO;
import com.collegeclub.dao.ClubDAO;
import com.collegeclub.dao.EventDAO;
import com.collegeclub.dao.RegistrationDAO;
import com.collegeclub.dao.StudentDAO;
import com.collegeclub.model.Admin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for Admin authentication and dashboard analytics.
 */
public class AdminService {

    private final AdminDAO adminDAO = new AdminDAO();
    private final ClubDAO clubDAO = new ClubDAO();
    private final EventDAO eventDAO = new EventDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    public Admin authenticate(String username, String password) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        return adminDAO.authenticate(username.trim(), password.trim());
    }

    /**
     * Aggregates key system metrics for the administrative dashboard.
     * Displays:
     * - Total Clubs
     * - Total Events
     * - Total Students
     * - Club Registrations
     * - Event Registrations
     */
    public Map<String, Integer> getDashboardStats() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalClubs", clubDAO.count());
        stats.put("totalEvents", eventDAO.count());
        stats.put("totalStudents", studentDAO.count());
        stats.put("totalClubRegistrations", registrationDAO.countClubRegistrations());
        stats.put("totalEventRegistrations", registrationDAO.countEventRegistrations());
        return stats;
    }
}
