package com.collegeclub.service;

import com.collegeclub.dao.ClubDAO;
import com.collegeclub.model.Club;

import java.sql.SQLException;
import java.util.List;

/**
 * Service class for Club business logic and validation.
 */
public class ClubService {

    private final ClubDAO clubDAO = new ClubDAO();

    public List<Club> getAllClubs() throws SQLException {
        return clubDAO.findAll();
    }

    public Club getClubById(int id) throws SQLException {
        return clubDAO.findById(id);
    }

    public List<Club> searchClubs(String query, String category) throws SQLException {
        return clubDAO.searchAndFilter(query, category);
    }

    public String validateClub(Club club) {
        if (club == null) return "Club data cannot be empty.";
        if (club.getName() == null || club.getName().trim().isEmpty()) return "Club name is required.";
        if (club.getCategory() == null || club.getCategory().trim().isEmpty()) return "Category is required.";
        if (club.getDescription() == null || club.getDescription().trim().isEmpty()) return "Description is required.";
        if (club.getFacultyCoordinator() == null || club.getFacultyCoordinator().trim().isEmpty()) return "Faculty Coordinator is required.";
        if (club.getStudentCoordinator() == null || club.getStudentCoordinator().trim().isEmpty()) return "Student Coordinator is required.";
        if (club.getContactEmail() == null || !club.getContactEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return "Please provide a valid contact email.";
        }
        return null;
    }

    public boolean createClub(Club club) throws Exception {
        String error = validateClub(club);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
        return clubDAO.insert(club);
    }

    public boolean updateClub(Club club) throws Exception {
        if (club.getId() <= 0) {
            throw new IllegalArgumentException("Invalid club ID.");
        }
        String error = validateClub(club);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
        return clubDAO.update(club);
    }

    public boolean deleteClub(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid club ID.");
        }
        return clubDAO.delete(id);
    }

    public int getTotalClubsCount() throws SQLException {
        return clubDAO.count();
    }
}
