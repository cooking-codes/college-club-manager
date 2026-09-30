package com.collegeclub.servlet;

import com.collegeclub.model.Admin;
import com.collegeclub.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Jakarta Servlet for Administrator Login and Session Management.
 * Mapped to /admin/login
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/admin/login"})
public class LoginServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("admin") != null) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/admin/login.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Admin admin = adminService.authenticate(username, password);
            if (admin != null) {
                // Successful login: create session and store admin object
                HttpSession session = request.getSession(true);
                session.setAttribute("admin", admin);
                session.setMaxInactiveInterval(30 * 60); // 30 minutes session timeout
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/login.html?error=" + 
                        URLEncoder.encode("Invalid username or password. Please try again.", StandardCharsets.UTF_8));
            }
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/admin/login.html?error=" + 
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/login.html?error=" + 
                    URLEncoder.encode("Database authentication error: " + e.getMessage(), StandardCharsets.UTF_8));
        }
    }
}
