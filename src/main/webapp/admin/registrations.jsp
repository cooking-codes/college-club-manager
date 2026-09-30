<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.collegeclub.model.Admin, com.collegeclub.model.ClubRegistration, com.collegeclub.model.EventRegistration, com.collegeclub.model.Club, com.collegeclub.model.Event" %>
<%
    Admin currentAdmin = (Admin) session.getAttribute("admin");
    if (currentAdmin == null) {
        response.sendRedirect("login.html?error=Session+expired.+Please+login.");
        return;
    }
    @SuppressWarnings("unchecked")
    List<ClubRegistration> clubRegs = (List<ClubRegistration>) request.getAttribute("clubRegistrations");
    @SuppressWarnings("unchecked")
    List<EventRegistration> eventRegs = (List<EventRegistration>) request.getAttribute("eventRegistrations");
    @SuppressWarnings("unchecked")
    List<Club> clubs = (List<Club>) request.getAttribute("clubs");
    @SuppressWarnings("unchecked")
    List<Event> events = (List<Event>) request.getAttribute("events");

    String activeTab = request.getParameter("tab");
    if (activeTab == null || (!activeTab.equals("events") && !activeTab.equals("clubs"))) {
        activeTab = "clubs";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Registrations - Admin Portal</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>

    <!-- Navigation -->
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
                <li><a href="dashboard">Dashboard</a></li>
                <li><a href="clubs">Manage Clubs</a></li>
                <li><a href="events">Manage Events</a></li>
                <li><a href="registrations" class="active">Registrations</a></li>
                <li><a href="../index.html" target="_blank">View Site ↗</a></li>
                <li><a href="logout" class="btn btn-danger btn-sm" style="color: white;">Logout</a></li>
            </ul>
        </div>
    </header>

    <main class="container">
        <div class="page-header" style="text-align: left; margin-bottom: 1.5rem;">
            <h1>Student Registrations</h1>
            <p>View, search, and audit student enrollments across collegiate clubs and competitions.</p>
        </div>

        <!-- Tab Selector -->
        <div style="display: flex; gap: 0.5rem; margin-bottom: 1.5rem; border-bottom: 2px solid var(--border-color); padding-bottom: 0.5rem;">
            <a href="registrations?tab=clubs" class="btn <%= activeTab.equals("clubs") ? "btn-primary" : "btn-secondary" %> btn-sm" style="<%= !activeTab.equals("clubs") ? "color: var(--text-muted); border-color: var(--border-color);" : "" %>">
                Club Memberships (<%= clubRegs != null ? clubRegs.size() : 0 %>)
            </a>
            <a href="registrations?tab=events" class="btn <%= activeTab.equals("events") ? "btn-primary" : "btn-secondary" %> btn-sm" style="<%= !activeTab.equals("events") ? "color: var(--text-muted); border-color: var(--border-color);" : "" %>">
                Event Registrations (<%= eventRegs != null ? eventRegs.size() : 0 %>)
            </a>
        </div>

        <!-- Search Bar -->
        <div class="search-filter-bar">
            <form action="registrations" method="GET" style="display: flex; gap: 1rem; width: 100%; flex-wrap: wrap;">
                <input type="hidden" name="tab" value="<%= activeTab %>">
                <div class="search-box" style="flex: 2;">
                    <input type="text" name="q" placeholder="Search by student name, roll number, or email..." value="<%= request.getParameter("q") != null ? request.getParameter("q") : "" %>">
                </div>

                <% if (activeTab.equals("clubs")) { %>
                    <div style="flex: 1; min-width: 200px;">
                        <select name="clubId" class="form-control" onchange="this.form.submit()">
                            <option value="">All Clubs</option>
                            <% if (clubs != null) {
                                for (Club c : clubs) {
                                    String sel = (request.getParameter("clubId") != null && request.getParameter("clubId").equals(String.valueOf(c.getId()))) ? "selected" : "";
                            %>
                                <option value="<%= c.getId() %>" <%= sel %>><%= c.getName() %></option>
                            <% }} %>
                        </select>
                    </div>
                <% } else { %>
                    <div style="flex: 1; min-width: 200px;">
                        <select name="eventId" class="form-control" onchange="this.form.submit()">
                            <option value="">All Events</option>
                            <% if (events != null) {
                                for (Event e : events) {
                                    String sel = (request.getParameter("eventId") != null && request.getParameter("eventId").equals(String.valueOf(e.getId()))) ? "selected" : "";
                            %>
                                <option value="<%= e.getId() %>" <%= sel %>><%= e.getEventName() %></option>
                            <% }} %>
                        </select>
                    </div>
                <% } %>

                <button type="submit" class="btn btn-outline btn-sm">Filter</button>
                <a href="registrations?tab=<%= activeTab %>" class="btn btn-secondary btn-sm" style="color: var(--text-muted); border-color: var(--border-color);">Reset</a>
            </form>
        </div>

        <% if (activeTab.equals("clubs")) { %>
            <!-- Club Memberships Table -->
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Reg ID</th>
                            <th>Student Name</th>
                            <th>Roll Number</th>
                            <th>Dept & Year</th>
                            <th>Contact</th>
                            <th>Registered Club</th>
                            <th>Date</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            if (clubRegs != null && !clubRegs.isEmpty()) {
                                for (ClubRegistration cr : clubRegs) {
                        %>
                        <tr>
                            <td>#<%= cr.getId() %></td>
                            <td><strong><%= cr.getStudentName() %></strong></td>
                            <td><code><%= cr.getStudentRollNumber() %></code> (Div <%= cr.getStudentDivision() %>)</td>
                            <td><%= cr.getStudentDepartment() %> - <%= cr.getStudentYear() %></td>
                            <td><small><%= cr.getStudentEmail() %><br><%= cr.getStudentPhone() %></small></td>
                            <td><strong><%= cr.getClubName() %></strong><br><small style="color: var(--text-muted);"><%= cr.getClubCategory() %></small></td>
                            <td><small><%= cr.getRegistrationDate() %></small></td>
                            <td><span class="card-badge badge-upcoming"><%= cr.getStatus() %></span></td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="8" style="text-align: center; color: var(--text-muted); padding: 2rem;">No club registrations found.</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>

        <% } else { %>
            <!-- Event Registrations Table -->
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Reg ID</th>
                            <th>Student Name</th>
                            <th>Roll Number</th>
                            <th>Dept & Year</th>
                            <th>Contact</th>
                            <th>Event Name</th>
                            <th>Date & Venue</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            if (eventRegs != null && !eventRegs.isEmpty()) {
                                for (EventRegistration er : eventRegs) {
                        %>
                        <tr>
                            <td>#<%= er.getId() %></td>
                            <td><strong><%= er.getStudentName() %></strong></td>
                            <td><code><%= er.getStudentRollNumber() %></code> (Div <%= er.getStudentDivision() %>)</td>
                            <td><%= er.getStudentDepartment() %> - <%= er.getStudentYear() %></td>
                            <td><small><%= er.getStudentEmail() %><br><%= er.getStudentPhone() %></small></td>
                            <td><strong><%= er.getEventName() %></strong><br><small style="color: var(--text-muted);">by <%= er.getOrganizingClubName() %></small></td>
                            <td><small><%= er.getEventDate() %> (<%= er.getEventTime() %>)<br><%= er.getEventVenue() %></small></td>
                            <td><span class="card-badge badge-upcoming"><%= er.getStatus() %></span></td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="8" style="text-align: center; color: var(--text-muted); padding: 2rem;">No event registrations found.</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>

    </main>

    <!-- Footer -->
    <footer>
        <div class="footer-bottom">
            <p>&copy; 2026 College Club Manager - Administrative Console</p>
        </div>
    </footer>

</body>
</html>
