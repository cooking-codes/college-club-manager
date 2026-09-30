package com.collegeclub.service;

import com.collegeclub.dao.EventDAO;
import com.collegeclub.model.Event;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service class for Event business logic and validation.
 */
public class EventService {

    private final EventDAO eventDAO = new EventDAO();

    public List<Event> getAllEvents() throws SQLException {
        return eventDAO.findAll();
    }

    public List<Event> getUpcomingEvents() throws SQLException {
        return eventDAO.findUpcoming();
    }

    public Event getEventById(int id) throws SQLException {
        return eventDAO.findById(id);
    }

    public List<Event> searchEvents(String query, Integer clubId) throws SQLException {
        return eventDAO.searchAndFilter(query, clubId);
    }

    public String validateEvent(Event event) {
        if (event == null) return "Event data cannot be empty.";
        if (event.getEventName() == null || event.getEventName().trim().isEmpty()) return "Event name is required.";
        if (event.getDescription() == null || event.getDescription().trim().isEmpty()) return "Event description is required.";
        if (event.getClubId() <= 0) return "Valid organizing club must be selected.";
        if (event.getEventDate() == null) return "Event date is required.";
        if (event.getEventTime() == null || event.getEventTime().trim().isEmpty()) return "Event time is required.";
        if (event.getVenue() == null || event.getVenue().trim().isEmpty()) return "Venue is required.";
        if (event.getRegistrationDeadline() == null) return "Registration deadline is required.";
        if (event.getMaxParticipants() <= 0) return "Maximum participants must be at least 1.";

        // Compare deadline and event date
        if (event.getRegistrationDeadline().after(event.getEventDate())) {
            return "Registration deadline cannot be after the event date.";
        }
        return null;
    }

    public boolean createEvent(Event event) throws Exception {
        String error = validateEvent(event);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
        return eventDAO.insert(event);
    }

    public boolean updateEvent(Event event) throws Exception {
        if (event.getId() <= 0) {
            throw new IllegalArgumentException("Invalid event ID.");
        }
        String error = validateEvent(event);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
        return eventDAO.update(event);
    }

    public boolean deleteEvent(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid event ID.");
        }
        return eventDAO.delete(id);
    }

    public int getTotalEventsCount() throws SQLException {
        return eventDAO.count();
    }
}
