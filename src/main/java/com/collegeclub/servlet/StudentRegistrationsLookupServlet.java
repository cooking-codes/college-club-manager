package com.collegeclub.servlet;

import com.collegeclub.model.ClubRegistration;
import com.collegeclub.model.EventRegistration;
import com.collegeclub.service.RegistrationService;
import com.collegeclub.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

/**
 * Jakarta Servlet for students to track their club memberships and event registrations.
 * Mapped to /api/my-registrations
 */
@WebServlet(name = "StudentRegistrationsLookupServlet", urlPatterns = {"/api/my-registrations"})
public class StudentRegistrationsLookupServlet extends HttpServlet {

    private final RegistrationService registrationService = new RegistrationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        try (PrintWriter out = response.getWriter()) {
            String query = request.getParameter("q");
            if (query == null || query.trim().isEmpty()) {
                out.print(JsonUtil.responseJson(true, "Please provide roll number or email", 
                        JsonUtil.studentRegistrationsToJson(Collections.emptyList(), Collections.emptyList())));
                return;
            }

            query = query.trim();
            List<ClubRegistration> clubs = registrationService.getClubRegistrations(query, null);
            List<EventRegistration> events = registrationService.getEventRegistrations(query, null);

            out.print(JsonUtil.responseJson(true, "Registrations fetched successfully", 
                    JsonUtil.studentRegistrationsToJson(clubs, events)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print(JsonUtil.responseJson(false, "Failed to retrieve student registrations: " + e.getMessage()));
            }
        }
    }
}