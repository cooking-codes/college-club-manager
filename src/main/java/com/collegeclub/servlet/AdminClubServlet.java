package com.collegeclub.servlet;

import com.collegeclub.model.Club;
import com.collegeclub.service.ClubService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Jakarta Servlet for Administrative Club CRUD operations.
 * Mapped to /admin/clubs
 */
@WebServlet(name = "AdminClubServlet", urlPatterns = {"/admin/clubs"})
public class AdminClubServlet extends HttpServlet {

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
            String category = request.getParameter("category");
            List<Club> clubs = clubService.searchClubs(query, category);
            request.setAttribute("clubs", clubs);
            request.getRequestDispatcher("/admin/clubs.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading clubs: " + e.getMessage());
            request.getRequestDispatcher("/admin/clubs.jsp").forward(request, response);
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
                Club club = extractClubFromRequest(request);
                clubService.createClub(club);
                redirectWithSuccess(request, response, "Club '" + club.getName() + "' created successfully.");
            } else if ("update".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Club club = extractClubFromRequest(request);
                club.setId(id);
                clubService.updateClub(club);
                redirectWithSuccess(request, response, "Club '" + club.getName() + "' updated successfully.");
            } else if ("delete".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                clubService.deleteClub(id);
                redirectWithSuccess(request, response, "Club deleted successfully.");
            } else {
                redirectWithError(request, response, "Unknown club operation.");
            }
        } catch (IllegalArgumentException e) {
            redirectWithError(request, response, e.getMessage());
        } catch (Exception e) {
            redirectWithError(request, response, "Operation failed: " + e.getMessage());
        }
    }

    private Club extractClubFromRequest(HttpServletRequest request) {
        Club c = new Club();
        c.setName(request.getParameter("name"));
        c.setCategory(request.getParameter("category"));
        c.setDescription(request.getParameter("description"));
        c.setObjectives(request.getParameter("objectives"));
        c.setActivities(request.getParameter("activities"));
        c.setFacultyCoordinator(request.getParameter("facultyCoordinator"));
        c.setStudentCoordinator(request.getParameter("studentCoordinator"));
        c.setMeetingDay(request.getParameter("meetingDay"));
        c.setMeetingTime(request.getParameter("meetingTime"));
        c.setLocation(request.getParameter("location"));
        c.setContactEmail(request.getParameter("contactEmail"));
        return c;
    }

    private void redirectWithSuccess(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/clubs?status=success&msg=" + 
                URLEncoder.encode(msg, StandardCharsets.UTF_8));
    }

    private void redirectWithError(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/clubs?status=error&msg=" + 
                URLEncoder.encode(msg, StandardCharsets.UTF_8));
    }
}
