<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Map, com.collegeclub.model.Admin, com.collegeclub.model.ClubRegistration, com.collegeclub.model.EventRegistration" %>
<%
    Admin currentAdmin = (Admin) session.getAttribute("admin");
    if (currentAdmin == null) {
        response.sendRedirect("login.html?error=Session+expired.+Please+login.");
        return;
    }
    @SuppressWarnings("unchecked")
    Map<String, Integer> stats = (Map<String, Integer>) request.getAttribute("stats");
    @SuppressWarnings("unchecked")
    List<ClubRegistration> recentClubRegs = (List<ClubRegistration>) request.getAttribute("recentClubRegistrations");
    @SuppressWarnings("unchecked")
    List<EventRegistration> recentEventRegs = (List<EventRegistration>) request.getAttribute("recentEventRegistrations");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - ARMIET</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>

    <!-- Admin Navigation Bar -->
    <header class="navbar">
        <div class="nav-container">
            <a href="dashboard" class="nav-logo">
                <span class="brand-badge">ARMIET</span>
                <div class="brand-titles">
                    <span class="brand-college-name">ARMIET Administration</span>
                    <span class="brand-portal-title">Club & Event Management Portal</span>
                </div>
            </a>
            <ul class="nav-links">
                <li><a href="dashboard" class="active">Dashboard</a></li>
                <li><a href="clubs">Manage Clubs</a></li>
                <li><a href="events">Manage Events</a></li>
                <li><a href="registrations">Registrations</a></li>
                <li><a href="../index.html" target="_blank">View Site ?</a></li>
                <li><a href="logout" class="btn btn-danger btn-sm" style="color: white;">Logout</a></li>
            </ul>
        </div>
    </header>

    <main class="container">
        <div class="page-header" style="text-align: left; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
            <div>
                <h1>Dashboard Overview</h1>
                <p>Welcome back, <strong><%= currentAdmin.getFullName() %></strong> (<%= currentAdmin.getUsername() %>)</p>
            </div>
            <div style="display: flex; gap: 0.75rem;">
                <a href="clubs" class="btn btn-primary btn-sm">+ Add New Club</a>
                <a href="events" class="btn btn-accent btn-sm">+ Add New Event</a>
            </div>
        </div>

        <!-- Metric Statistics Cards -->
        <section class="stats-grid">
            <div class="stat-card">
                <div class="stat-value"><%= (stats != null && stats.get("totalClubs") != null) ? stats.get("totalClubs") : 0 %></div>
                <div class="stat-label">Total Clubs</div>
            </div>
            <div class="stat-card">
                <div class="stat-value"><%= (stats != null && stats.get("totalEvents") != null) ? stats.get("totalEvents") : 0 %></div>
                <div class="stat-label">Total Events</div>
            </div>
            <div class="stat-card">
                <div class="stat-value"><%= (stats != null && stats.get("totalStudents") != null) ? stats.get("totalStudents") : 0 %></div>
                <div class="stat-label">Total Students</div>
            </div>
            <div class="stat-card">
                <div class="stat-value"><%= (stats != null && stats.get("totalClubRegistrations") != null) ? stats.get("totalClubRegistrations") : 0 %></div>
                <div class="stat-label">Club Memberships</div>
            </div>
            <div class="stat-card">
                <div class="stat-value"><%= (stats != null && stats.get("totalEventRegistrations") != null) ? stats.get("totalEventRegistrations") : 0 %></div>
                <div class="stat-label">Event Bookings</div>
            </div>
        </section>

        <!-- Quick Summary Tables -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(480px, 1fr)); gap: 2rem; margin-top: 2rem;">
            
            <!-- Recent Club Registrations -->
            <div class="card" style="padding: 1.5rem;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                    <h3 style="color: var(--primary);">Recent Club Memberships</h3>
                    <a href="registrations?tab=clubs" style="color: var(--primary); font-size: 0.85rem; font-weight: 600;">View All &rarr;</a>
                </div>
                <div class="table-responsive" style="margin-bottom: 0;">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Student</th>
                                <th>Roll No</th>
                                <th>Club</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (recentClubRegs != null && !recentClubRegs.isEmpty()) {
                                    int count = 0;
                                    for (ClubRegistration cr : recentClubRegs) {
                                        if (count++ >= 5) break;
                            %>
                            <tr>
                                <td><strong><%= cr.getStudentName() %></strong><br><small style="color: var(--text-muted);"><%= cr.getStudentDepartment() %></small></td>
                                <td><code><%= cr.getStudentRollNumber() %></code></td>
                                <td><%= cr.getClubName() %></td>
                                <td><span class="card-badge badge-upcoming"><%= cr.getStatus() %></span></td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="4" style="text-align: center; color: var(--text-muted); padding: 1.5rem;">No club memberships registered yet.</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Recent Event Registrations -->
            <div class="card" style="padding: 1.5rem;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                    <h3 style="color: var(--primary);">Recent Event Registrations</h3>
                    <a href="registrations?tab=events" style="color: var(--primary); font-size: 0.85rem; font-weight: 600;">View All &rarr;</a>
                </div>
                <div class="table-responsive" style="margin-bottom: 0;">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Student</th>
                                <th>Roll No</th>
                                <th>Event</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (recentEventRegs != null && !recentEventRegs.isEmpty()) {
                                    int count = 0;
                                    for (EventRegistration er : recentEventRegs) {
                                        if (count++ >= 5) break;
                            %>
                            <tr>
                                <td><strong><%= er.getStudentName() %></strong><br><small style="color: var(--text-muted);"><%= er.getStudentDepartment() %></small></td>
                                <td><code><%= er.getStudentRollNumber() %></code></td>
                                <td><%= er.getEventName() %></td>
                                <td><span class="card-badge badge-upcoming"><%= er.getStatus() %></span></td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="4" style="text-align: center; color: var(--text-muted); padding: 1.5rem;">No event registrations recorded yet.</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </main>

    <!-- Footer -->
    <footer>
        <div class="footer-bottom" style="border-top: none; padding-top: 0;">
            <p>&copy; 2026 Alamuri Ratnamala Institute of Engineering & Technology (ARMIET) - Administrative Portal</p>
        </div>
    </footer>

</body>
</html>
