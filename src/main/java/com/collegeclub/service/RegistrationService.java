package com.collegeclub.service;

import com.collegeclub.dao.ClubDAO;
import com.collegeclub.dao.EventDAO;
import com.collegeclub.dao.RegistrationDAO;
import com.collegeclub.dao.StudentDAO;
import com.collegeclub.model.Club;
import com.collegeclub.model.Event;
import com.collegeclub.model.Student;
import com.collegeclub.model.ClubRegistration;
import com.collegeclub.model.EventRegistration;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service class for handling Student Registrations for Clubs and Events.
 * Implements strict Java-side validations, business constraints (duplicate checks,
 * capacity checks, deadline checks), and database orchestrations.
 */
public class RegistrationService {

    private final StudentDAO studentDAO = new StudentDAO();
    private final ClubDAO clubDAO = new ClubDAO();
    private final EventDAO eventDAO = new EventDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    /**
     * Validates common student academic and contact information.
     */
    public void validateStudentInput(String fullName, String email, String phone, 
                                     String department, String engineeringYear, 
                                     String division, String rollNumber) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full Name is required.");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("A valid College Email Address is required.");
        }
        if (phone == null || !PHONE_PATTERN.matcher(phone.trim().replaceAll("[^0-9]", "")).matches()) {
            throw new IllegalArgumentException("Phone Number must be a valid 10-digit mobile number.");
        }
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department is required.");
        }
        if (engineeringYear == null || engineeringYear.trim().isEmpty()) {
            throw new IllegalArgumentException("Engineering Year is required.");
        }
        if (division == null || division.trim().isEmpty()) {
            throw new IllegalArgumentException("Division is required.");
        }
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Roll Number is required.");
        }
    }

    /**
     * Registers a student for a club.
     * Checks for:
     * 1. Student input validity
     * 2. Existing club existence
     * 3. Duplicate club membership
     */
    public boolean registerForClub(String fullName, String email, String phone, 
                                   String department, String engineeringYear, 
                                   String division, String rollNumber, int clubId) throws Exception {
        validateStudentInput(fullName, email, phone, department, engineeringYear, division, rollNumber);

        if (clubId <= 0) {
            throw new IllegalArgumentException("Please select a valid Club.");
        }

        Club club = clubDAO.findById(clubId);
        if (club == null) {
            throw new IllegalArgumentException("The selected club does not exist.");
        }

        // Upsert or retrieve student
        Student student = new Student(fullName.trim(), email.trim().toLowerCase(), 
                                      phone.trim(), department.trim(), 
                                      engineeringYear.trim(), division.trim(), rollNumber.trim());
        int studentId = studentDAO.findOrCreate(student);
        if (studentId <= 0) {
            throw new SQLException("Failed to record student profile.");
        }

        // Check duplicate club registration
        if (registrationDAO.isAlreadyRegisteredClub(studentId, clubId)) {
            throw new IllegalStateException("You are already registered for the " + club.getName() + ".");
        }

        return registrationDAO.registerClub(studentId, clubId);
    }

    /**
     * Registers a student for an event.
     * Checks for:
     * 1. Student input validity
     * 2. Event existence and active status
     * 3. Registration deadline expiry
     * 4. Maximum participant capacity
     * 5. Duplicate event registration
     */
    public boolean registerForEvent(String fullName, String email, String phone, 
                                    String department, String engineeringYear, 
                                    String division, String rollNumber, int eventId) throws Exception {
        validateStudentInput(fullName, email, phone, department, engineeringYear, division, rollNumber);

        if (eventId <= 0) {
            throw new IllegalArgumentException("Please select a valid Event.");
        }

        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("The selected event does not exist.");
        }

        if ("Cancelled".equalsIgnoreCase(event.getStatus())) {
            throw new IllegalStateException("This event has been cancelled.");
        }

        // Check registration deadline
        if (event.getRegistrationDeadline() != null) {
            LocalDate deadline = event.getRegistrationDeadline().toLocalDate();
            LocalDate today = LocalDate.now();
            if (today.isAfter(deadline)) {
                throw new IllegalStateException("Registration for this event closed on " + deadline + ".");
            }
        }

        // Check capacity limit
        int currentCount = eventDAO.getRegisteredCount(eventId);
        if (currentCount >= event.getMaxParticipants()) {
            throw new IllegalStateException("Registration is full. Maximum limit of " + 
                                           event.getMaxParticipants() + " participants reached.");
        }

        // Upsert or retrieve student
        Student student = new Student(fullName.trim(), email.trim().toLowerCase(), 
                                      phone.trim(), department.trim(), 
                                      engineeringYear.trim(), division.trim(), rollNumber.trim());
        int studentId = studentDAO.findOrCreate(student);
        if (studentId <= 0) {
            throw new SQLException("Failed to record student profile.");
        }

        // Check duplicate registration
        if (registrationDAO.isAlreadyRegisteredEvent(studentId, eventId)) {
            throw new IllegalStateException("You are already registered for " + event.getEventName() + ".");
        }

        return registrationDAO.registerEvent(studentId, eventId);
    }

    public List<ClubRegistration> getClubRegistrations(String query, Integer clubId) throws SQLException {
        return registrationDAO.searchClubRegistrations(query, clubId);
    }

    public List<EventRegistration> getEventRegistrations(String query, Integer eventId) throws SQLException {
        return registrationDAO.searchEventRegistrations(query, eventId);
    }

    public int getTotalClubRegistrations() throws SQLException {
        return registrationDAO.countClubRegistrations();
    }

    public int getTotalEventRegistrations() throws SQLException {
        return registrationDAO.countEventRegistrations();
    }
}
