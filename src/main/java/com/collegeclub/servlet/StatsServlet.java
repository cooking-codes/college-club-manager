package com.collegeclub.servlet;

import com.collegeclub.service.AdminService;
import com.collegeclub.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Jakarta Servlet for dynamically delivering platform statistics from MySQL.
 * Mapped to /api/stats
 */
@WebServlet(name = "StatsServlet", urlPatterns = {"/api/stats"})
public class StatsServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        try (PrintWriter out = response.getWriter()) {
            Map<String, Integer> stats = adminService.getDashboardStats();
            out.print(JsonUtil.responseJson(true, "Platform stats fetched successfully", JsonUtil.statsToJson(stats)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print(JsonUtil.responseJson(false, "Failed to load platform stats: " + e.getMessage()));
            }
        }
    }
}