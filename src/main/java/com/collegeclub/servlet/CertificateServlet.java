package com.collegeclub.servlet;

import com.collegeclub.model.ClubRegistration;
import com.collegeclub.service.RegistrationService;
import com.collegeclub.util.CertificatePdfGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Generates a themed PDF certificate for an approved club membership.
 */
@WebServlet(name = "CertificateServlet", urlPatterns = {"/admin/certificate"})
public class CertificateServlet extends HttpServlet {

    private final RegistrationService registrationService = new RegistrationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.html?error=Please+login+first.");
            return;
        }

        String idParam = request.getParameter("registrationId");
        int registrationId;
        try {
            registrationId = Integer.parseInt(idParam);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid registration ID.");
            return;
        }

        try {
            ClubRegistration registration = registrationService.getClubRegistrationById(registrationId);
            if (registration == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Club registration not found.");
                return;
            }
            if (!"Approved".equalsIgnoreCase(registration.getStatus())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Certificates can only be generated for approved club memberships.");
                return;
            }

            String certificateId = CertificatePdfGenerator.certificateId(registration);
            String fileName = "Certificate-" + certificateId + ".pdf";
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Pragma", "no-cache");

            CertificatePdfGenerator.generate(registration, response.getOutputStream());
        } catch (Exception e) {
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to generate certificate: " + e.getMessage());
            } else {
                throw new ServletException("Unable to generate certificate", e);
            }
        }
    }
}
