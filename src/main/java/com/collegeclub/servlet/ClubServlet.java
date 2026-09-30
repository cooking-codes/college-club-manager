package com.collegeclub.servlet;

import com.collegeclub.model.Club;
import com.collegeclub.service.ClubService;
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
 * Jakarta Servlet for fetching Clubs dynamically from MySQL.
 * Mapped to /api/clubs
 */
@WebServlet(name = "ClubServlet", urlPatterns = {"/api/clubs"})
public class ClubServlet extends HttpServlet {

    private final ClubService clubService = new ClubService();

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
                    Club club = clubService.getClubById(id);
                    if (club != null) {
                        out.print(JsonUtil.responseJson(true, "Club retrieved successfully", JsonUtil.toJson(club)));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(JsonUtil.responseJson(false, "Club with ID " + id + " not found."));
                    }
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(JsonUtil.responseJson(false, "Invalid Club ID format."));
                }
                return;
            }

            String query = request.getParameter("q");
            String category = request.getParameter("category");

            List<Club> clubs;
            if ((query != null && !query.trim().isEmpty()) || (category != null && !category.trim().isEmpty())) {
                clubs = clubService.searchClubs(query, category);
            } else {
                clubs = clubService.getAllClubs();
            }

            out.print(JsonUtil.responseJson(true, "Clubs list fetched", JsonUtil.clubListToJson(clubs)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print(JsonUtil.responseJson(false, "Database error: " + e.getMessage()));
            }
        }
    }
}
