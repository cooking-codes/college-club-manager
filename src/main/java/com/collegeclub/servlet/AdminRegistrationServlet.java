package com.collegeclub.servlet;

import com.collegeclub.model.Club;
import com.collegeclub.model.ClubRegistration;
import com.collegeclub.model.Event;
import com.collegeclub.model.EventRegistration;
import com.collegeclub.service.ClubService;
import com.collegeclub.service.EventService;
import com.collegeclub.service.RegistrationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Jakarta Servlet for Viewing and Searching Club and Event Registrations.
 * Mapped to /admin/registrations
 */
@WebServlet(name = "AdminRegistrationServlet", urlPatterns = {"/admin/registrations"})
public class AdminRegistrationServlet extends HttpServlet {

    private final RegistrationService registrationService = new RegistrationService();
    private final ClubService clubService = new ClubService();
    private final EventService eventService = new EventService();

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
            String eventIdStr = request.getParameter("eventId");

            Integer clubId = null;
            if (clubIdStr != null && !clubIdStr.trim().isEmpty()) {
                try {
                    clubId = Integer.parseInt(clubIdStr.trim());
                } catch (NumberFormatException ignored) {}
            }

            Integer eventId = null;
            if (eventIdStr != null && !eventIdStr.trim().isEmpty()) {
                try {
                    eventId = Integer.parseInt(eventIdStr.trim());
                } catch (NumberFormatException ignored) {}
            }

            List<ClubRegistration> clubRegistrations = registrationService.getClubRegistrations(query, clubId);
            List<EventRegistration> eventRegistrations = registrationService.getEventRegistrations(query, eventId);
            List<Club> clubs = clubService.getAllClubs();
            List<Event> events = eventService.getAllEvents();

            request.setAttribute("clubRegistrations", clubRegistrations);
            request.setAttribute("eventRegistrations", eventRegistrations);
            request.setAttribute("clubs", clubs);
            request.setAttribute("events", events);

            request.getRequestDispatcher("/admin/registrations.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading registrations: " + e.getMessage());
            request.getRequestDispatcher("/admin/registrations.jsp").forward(request, response);
        }
    }
}
