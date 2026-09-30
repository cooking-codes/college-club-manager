<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.collegeclub.model.Admin, com.collegeclub.model.Event, com.collegeclub.model.Club" %>
<%
    Admin currentAdmin = (Admin) session.getAttribute("admin");
    if (currentAdmin == null) {
        response.sendRedirect("login.html?error=Session+expired.+Please+login.");
        return;
    }
    @SuppressWarnings("unchecked")
    List<Event> events = (List<Event>) request.getAttribute("events");
    @SuppressWarnings("unchecked")
    List<Club> clubs = (List<Club>) request.getAttribute("clubs");
    String status = request.getParameter("status");
    String msg = request.getParameter("msg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Events - Admin Portal</title>
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
                <li><a href="events" class="active">Manage Events</a></li>
                <li><a href="registrations">Registrations</a></li>
                <li><a href="../index.html" target="_blank">View Site ↗</a></li>
                <li><a href="logout" class="btn btn-danger btn-sm" style="color: white;">Logout</a></li>
            </ul>
        </div>
    </header>

    <main class="container">
        <div class="page-header" style="text-align: left; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
            <div>
                <h1>Manage Campus Events</h1>
                <p>Create and monitor workshops, hackathons, and sports meets with automatic capacity and deadline controls.</p>
            </div>
            <button class="btn btn-primary" onclick="openAddModal()">+ Add New Event</button>
        </div>

        <!-- Feedback Alert -->
        <% if (msg != null && !msg.trim().isEmpty()) { %>
            <div class="alert <%= "success".equalsIgnoreCase(status) ? "alert-success" : "alert-danger" %>">
                <span><%= msg %></span>
            </div>
        <% } %>

        <!-- Search & Filter Bar -->
        <div class="search-filter-bar">
            <form action="events" method="GET" style="display: flex; gap: 1rem; width: 100%; flex-wrap: wrap;">
                <div class="search-box" style="flex: 2;">
                    <input type="text" name="q" placeholder="Search events..." value="<%= request.getParameter("q") != null ? request.getParameter("q") : "" %>">
                </div>
                <div style="flex: 1; min-width: 200px;">
                    <select name="clubId" class="form-control" onchange="this.form.submit()">
                        <option value="">All Organizing Clubs</option>
                        <% if (clubs != null) {
                            for (Club c : clubs) {
                                String selected = (request.getParameter("clubId") != null && request.getParameter("clubId").equals(String.valueOf(c.getId()))) ? "selected" : "";
                        %>
                            <option value="<%= c.getId() %>" <%= selected %>><%= c.getName() %></option>
                        <% }} %>
                    </select>
                </div>
                <button type="submit" class="btn btn-outline btn-sm">Filter</button>
                <a href="events" class="btn btn-secondary btn-sm" style="color: var(--text-muted); border-color: var(--border-color);">Reset</a>
            </form>
        </div>

        <!-- Events Table -->
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Event Name</th>
                        <th>Organizing Club</th>
                        <th>Date & Time</th>
                        <th>Venue</th>
                        <th>Deadline</th>
                        <th>Capacity</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (events != null && !events.isEmpty()) {
                            for (Event e : events) {
                    %>
                    <tr>
                        <td><%= e.getId() %></td>
                        <td><strong><%= e.getEventName() %></strong></td>
                        <td><span class="card-badge"><%= e.getClubName() %></span></td>
                        <td><%= e.getEventDate() %><br><small style="color: var(--text-muted);"><%= e.getEventTime() %></small></td>
                        <td><%= e.getVenue() %></td>
                        <td><%= e.getRegistrationDeadline() %></td>
                        <td><strong><%= e.getRegisteredCount() %></strong> / <%= e.getMaxParticipants() %></td>
                        <td>
                            <% if ("Upcoming".equalsIgnoreCase(e.getStatus())) { %>
                                <span class="card-badge badge-upcoming">Upcoming</span>
                            <% } else if ("Cancelled".equalsIgnoreCase(e.getStatus())) { %>
                                <span class="card-badge badge-closed">Cancelled</span>
                            <% } else { %>
                                <span class="card-badge"><%= e.getStatus() %></span>
                            <% } %>
                        </td>
                        <td>
                            <div style="display: flex; gap: 0.5rem;">
                                <button class="btn btn-outline btn-sm" onclick='openEditModal(<%= JsonUtilScript(e) %>)'>Edit</button>
                                <form action="events" method="POST" onsubmit="return confirm('Are you sure you want to delete event <%= e.getEventName() %>?');" style="display: inline;">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="<%= e.getId() %>">
                                    <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                    <%
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="9" style="text-align: center; color: var(--text-muted); padding: 2rem;">No events found. Click "+ Add New Event" above to create one.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </main>

    <!-- Modal: Add / Edit Event -->
    <div class="modal-overlay" id="event-modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3 id="modal-title" style="color: var(--primary-dark);">Add New Event</h3>
                <button class="modal-close" onclick="closeModal()">&times;</button>
            </div>
            <form id="event-form" action="events" method="POST">
                <input type="hidden" name="action" id="form-action" value="create">
                <input type="hidden" name="id" id="event-id" value="">

                <div class="form-group">
                    <label class="form-label">Event Name <span class="req">*</span></label>
                    <input type="text" name="eventName" id="eventName" class="form-control" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Organizing Club <span class="req">*</span></label>
                    <select name="clubId" id="clubId" class="form-control" required>
                        <option value="">-- Choose Club --</option>
                        <% if (clubs != null) {
                            for (Club c : clubs) { %>
                            <option value="<%= c.getId() %>"><%= c.getName() %> (<%= c.getCategory() %>)</option>
                        <% }} %>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label">Event Description <span class="req">*</span></label>
                    <textarea name="description" id="description" class="form-control" rows="3" required></textarea>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Event Date <span class="req">*</span></label>
                        <input type="date" name="eventDate" id="eventDate" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Event Time <span class="req">*</span></label>
                        <input type="text" name="eventTime" id="eventTime" class="form-control" placeholder="e.g. 10:00 AM" required>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Venue / Room <span class="req">*</span></label>
                    <input type="text" name="venue" id="venue" class="form-control" placeholder="e.g. Auditorium Hall B" required>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Registration Deadline <span class="req">*</span></label>
                        <input type="date" name="registrationDeadline" id="registrationDeadline" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Maximum Participants <span class="req">*</span></label>
                        <input type="number" name="maxParticipants" id="maxParticipants" class="form-control" min="1" value="100" required>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Status</label>
                    <select name="status" id="status" class="form-control">
                        <option value="Upcoming">Upcoming</option>
                        <option value="Completed">Completed</option>
                        <option value="Cancelled">Cancelled</option>
                    </select>
                </div>

                <div style="display: flex; justify-content: flex-end; gap: 1rem; margin-top: 1.5rem;">
                    <button type="button" class="btn btn-secondary" onclick="closeModal()" style="color: var(--text-muted); border-color: var(--border-color);">Cancel</button>
                    <button type="submit" class="btn btn-primary" id="save-btn">Save Event</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        const modal = document.getElementById('event-modal');
        const form = document.getElementById('event-form');
        const modalTitle = document.getElementById('modal-title');
        const formAction = document.getElementById('form-action');

        function openAddModal() {
            modalTitle.textContent = 'Add New Event';
            formAction.value = 'create';
            form.reset();
            document.getElementById('event-id').value = '';
            document.getElementById('maxParticipants').value = '100';
            modal.classList.add('active');
        }

        function openEditModal(e) {
            modalTitle.textContent = 'Edit Event: ' + e.eventName;
            formAction.value = 'update';
            document.getElementById('event-id').value = e.id;
            document.getElementById('eventName').value = e.eventName;
            document.getElementById('clubId').value = e.clubId;
            document.getElementById('description').value = e.description;
            document.getElementById('eventDate').value = e.eventDate;
            document.getElementById('eventTime').value = e.eventTime;
            document.getElementById('venue').value = e.venue;
            document.getElementById('registrationDeadline').value = e.registrationDeadline;
            document.getElementById('maxParticipants').value = e.maxParticipants;
            document.getElementById('status').value = e.status;
            modal.classList.add('active');
        }

        function closeModal() {
            modal.classList.remove('active');
        }
    </script>

</body>
</html>
<%!
    private String JsonUtilScript(Event e) {
        String dateStr = (e.getEventDate() != null) ? e.getEventDate().toString() : "";
        String deadlineStr = (e.getRegistrationDeadline() != null) ? e.getRegistrationDeadline().toString() : "";
        return "{" +
            "id:" + e.getId() + "," +
            "eventName:" + escapeJs(e.getEventName()) + "," +
            "clubId:" + e.getClubId() + "," +
            "description:" + escapeJs(e.getDescription()) + "," +
            "eventDate:" + escapeJs(dateStr) + "," +
            "eventTime:" + escapeJs(e.getEventTime()) + "," +
            "venue:" + escapeJs(e.getVenue()) + "," +
            "registrationDeadline:" + escapeJs(deadlineStr) + "," +
            "maxParticipants:" + e.getMaxParticipants() + "," +
            "status:" + escapeJs(e.getStatus()) +
        "}";
    }

    private String escapeJs(String s) {
        if (s == null) return "''";
        return "'" + s.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "") + "'";
    }
%>
