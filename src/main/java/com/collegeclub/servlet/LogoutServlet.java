package com.collegeclub.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Jakarta Servlet for Administrator Logout.
 * Invalidates session and redirects to login page.
 * Mapped to /admin/logout
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/admin/logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("admin");
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/admin/login.html?logout=true");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
