package com.collegeclub.servlet;

import com.collegeclub.model.Club;
import com.collegeclub.model.Event;
import com.collegeclub.service.ClubService;
import com.collegeclub.service.EventService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.List;

/**
 * Jakarta Servlet for Administrative Event CRUD operations.
 * Mapped to /admin/events
 */
@WebServlet(name = "AdminEventServlet", urlPatterns = {"/admin/events"})
public class AdminEventServlet extends HttpServlet {

    private final EventService eventService = new EventService();
    private final ClubService clubService = new ClubService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.html?error=Please+login+first.");
            return;
        }

        try {
            String query = request.getParameter("q");
            String clubIdStr = request.getParameter("clubId");
            Integer clubId = null;
            if (clubIdStr != null && !clubIdStr.trim().isEmpty()) {
                try {
                    clubId = Integer.parseInt(clubIdStr.trim());
                } catch (NumberFormatException ignored) {}
            }

            List<Event> events = eventService.searchEvents(query, clubId);
            List<Club> clubs = clubService.getAllClubs();

            request.setAttribute("events", events);
            request.setAttribute("clubs", clubs);
            request.getRequestDispatcher("/admin/events.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading events: " + e.getMessage());
            request.getRequestDispatcher("/admin/events.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.html?error=Session+expired.");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("create".equalsIgnoreCase(action)) {
                Event event = extractEventFromRequest(request);
                eventService.createEvent(event);
                redirectWithSuccess(request, response, "Event '" + event.getEventName() + "' created successfully.");
            } else if ("update".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Event event = extractEventFromRequest(request);
                event.setId(id);
                eventService.updateEvent(event);
                redirectWithSuccess(request, response, "Event '" + event.getEventName() + "' updated successfully.");
            } else if ("delete".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                eventService.deleteEvent(id);
                redirectWithSuccess(request, response, "Event deleted successfully.");
            } else {
                redirectWithError(request, response, "Unknown event operation.");
            }
        } catch (IllegalArgumentException e) {
            redirectWithError(request, response, e.getMessage());
        } catch (Exception e) {
            redirectWithError(request, response, "Operation failed: " + e.getMessage());
        }
    }

    private Event extractEventFromRequest(HttpServletRequest request) {
        Event e = new Event();
        e.setEventName(request.getParameter("eventName"));
        e.setDescription(request.getParameter("description"));
        e.setClubId(Integer.parseInt(request.getParameter("clubId")));
        e.setEventDate(Date.valueOf(request.getParameter("eventDate")));
        e.setEventTime(request.getParameter("eventTime"));
        e.setVenue(request.getParameter("venue"));
        e.setRegistrationDeadline(Date.valueOf(request.getParameter("registrationDeadline")));
        e.setMaxParticipants(Integer.parseInt(request.getParameter("maxParticipants")));
        e.setStatus(request.getParameter("status"));
        return e;
    }

    private void redirectWithSuccess(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/events?status=success&msg=" + 
                URLEncoder.encode(msg, StandardCharsets.UTF_8));
    }

    private void redirectWithError(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/events?status=error&msg=" + 
                URLEncoder.encode(msg, StandardCharsets.UTF_8));
    }
}
