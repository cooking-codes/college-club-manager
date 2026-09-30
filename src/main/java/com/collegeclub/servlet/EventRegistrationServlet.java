package com.collegeclub.servlet;

import com.collegeclub.service.RegistrationService;
import com.collegeclub.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Jakarta Servlet for Student Event Registration.
 * Mapped to /register-event
 */
@WebServlet(name = "EventRegistrationServlet", urlPatterns = {"/register-event"})
public class EventRegistrationServlet extends HttpServlet {

    private final RegistrationService registrationService = new RegistrationService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String department = request.getParameter("department");
        String engineeringYear = request.getParameter("engineeringYear");
        String division = request.getParameter("division");
        String rollNumber = request.getParameter("rollNumber");
        String eventIdStr = request.getParameter("eventId");

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With")) ||
                         (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

        try {
            int eventId = 0;
            if (eventIdStr != null && !eventIdStr.trim().isEmpty()) {
                eventId = Integer.parseInt(eventIdStr.trim());
            }

            boolean registered = registrationService.registerForEvent(
                    fullName, email, phone, department, engineeringYear, division, rollNumber, eventId
            );

            if (registered) {
                if (isAjax) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_OK);
                    try (PrintWriter out = response.getWriter()) {
                        out.print(JsonUtil.responseJson(true, "Registration Confirmed! Your seat for the event has been successfully reserved."));
                    }
                } else {
                    response.sendRedirect(request.getContextPath() + "/event-register.html?status=success&msg=" + 
                            URLEncoder.encode("Event registration confirmed successfully!", StandardCharsets.UTF_8));
                }
            } else {
                throw new Exception("Unable to complete event registration at this moment.");
            }

        } catch (IllegalArgumentException e) {
            handleError(request, response, isAjax, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            handleError(request, response, isAjax, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (Exception e) {
            handleError(request, response, isAjax, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error: " + e.getMessage());
        }
    }

    private void handleError(HttpServletRequest req, HttpServletResponse resp, boolean isAjax, int status, String message) 
            throws IOException {
        resp.setStatus(status);
        if (isAjax) {
            resp.setContentType("application/json;charset=UTF-8");
            try (PrintWriter out = resp.getWriter()) {
                out.print(JsonUtil.responseJson(false, message));
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/event-register.html?status=error&msg=" + 
                    URLEncoder.encode(message, StandardCharsets.UTF_8));
        }
    }
}
