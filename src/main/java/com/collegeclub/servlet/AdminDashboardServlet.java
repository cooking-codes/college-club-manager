package com.collegeclub.servlet;

import com.collegeclub.model.Admin;
import com.collegeclub.service.AdminService;
import com.collegeclub.service.RegistrationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

/**
 * Jakarta Servlet for Admin Dashboard metrics.
 * Ensures active authentication session before rendering dashboard.
 * Mapped to /admin/dashboard
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();
    private final RegistrationService registrationService = new RegistrationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.html?error=Session+expired.+Please+login.");
            return;
        }

        try {
            Map<String, Integer> stats = adminService.getDashboardStats();
            request.setAttribute("stats", stats);

            // Also load recent registrations for summary lists
            request.setAttribute("recentClubRegistrations", registrationService.getClubRegistrations(null, null));
            request.setAttribute("recentEventRegistrations", registrationService.getEventRegistrations(null, null));

            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading dashboard metrics: " + e.getMessage());
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        }
    }
}
