package com.collegeclub.servlet;

import com.collegeclub.model.Event;
import com.collegeclub.service.EventService;
import com.collegeclub.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Jakarta Servlet for fetching Events dynamically from MySQL.
 * Mapped to /api/events
 */
@WebServlet(name = "EventServlet", urlPatterns = {"/api/events"})
public class EventServlet extends HttpServlet {

    private final EventService eventService = new EventService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        try (PrintWriter out = response.getWriter()) {
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.trim().isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr.trim());
                    Event event = eventService.getEventById(id);
                    if (event != null) {
                        out.print(JsonUtil.responseJson(true, "Event retrieved successfully", JsonUtil.toJson(event)));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(JsonUtil.responseJson(false, "Event with ID " + id + " not found."));
                    }
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(JsonUtil.responseJson(false, "Invalid Event ID format."));
                }
                return;
            }

            String query = request.getParameter("q");
            String clubIdStr = request.getParameter("clubId");
            String upcomingOnly = request.getParameter("upcoming");

            List<Event> events;
            if ("true".equalsIgnoreCase(upcomingOnly)) {
                events = eventService.getUpcomingEvents();
            } else if ((query != null && !query.trim().isEmpty()) || (clubIdStr != null && !clubIdStr.trim().isEmpty())) {
                Integer clubId = null;
                if (clubIdStr != null && !clubIdStr.trim().isEmpty()) {
                    try {
                        clubId = Integer.parseInt(clubIdStr.trim());
                    } catch (NumberFormatException ignored) {}
                }
                events = eventService.searchEvents(query, clubId);
            } else {
                events = eventService.getAllEvents();
            }

            out.print(JsonUtil.responseJson(true, "Events fetched successfully", JsonUtil.eventListToJson(events)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print(JsonUtil.responseJson(false, "Database error: " + e.getMessage()));
            }
        }
    }
}
