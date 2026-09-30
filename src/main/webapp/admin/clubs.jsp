<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.collegeclub.model.Admin, com.collegeclub.model.Club" %>

<%
    Admin currentAdmin = (Admin) session.getAttribute("admin");

    if (currentAdmin == null) {
        response.sendRedirect("login.html?error=Session+expired.+Please+login.");
        return;
    }

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

    <title>Manage Clubs - ARMIET Administration Portal</title>

    <!-- ARMIET ADMIN THEME -->
    <link rel="stylesheet" href="admin.css?v=2">
</head>

<body>

    <!-- =========================================================
         ARMIET ADMIN NAVIGATION
         ========================================================= -->
    <header class="navbar">
        <div class="nav-container">

            <a href="dashboard" class="nav-logo">

                <span class="brand-badge">ARMIET</span>

                <div class="brand-titles">
                    <span class="brand-college-name">
                        ARMIET Administration
                    </span>

                    <span class="brand-portal-title">
                        Club &amp; Event Management Portal
                    </span>
                </div>

            </a>

            <ul class="nav-links">

                <li>
                    <a href="dashboard">
                        Dashboard
                    </a>
                </li>

                <li>
                    <a href="clubs" class="active">
                        Manage Clubs
                    </a>
                </li>

                <li>
                    <a href="events">
                        Manage Events
                    </a>
                </li>

                <li>
                    <a href="registrations">
                        Registrations
                    </a>
                </li>

                <li>
                    <a href="../index.html" target="_blank">
                        View Site ↗
                    </a>
                </li>

                <li>
                    <a href="logout"
                       class="btn btn-danger btn-sm"
                       style="color: white;">
                        Logout
                    </a>
                </li>

            </ul>

        </div>
    </header>


    <!-- =========================================================
         MAIN CONTENT
         ========================================================= -->
    <main class="container">

        <!-- Page Header -->
        <div class="page-header"
             style="
                 text-align: left;
                 display: flex;
                 justify-content: space-between;
                 align-items: center;
                 flex-wrap: wrap;
                 gap: 1rem;
             ">

            <div>

                <h1>
                    Manage College Clubs
                </h1>

                <p>
                    Manage student clubs and societies at
                    Alamuri Ratnamala Institute of Engineering &amp; Technology.
                </p>

            </div>

            <button
                class="btn btn-primary"
                onclick="openAddModal()">

                + Add New Club

            </button>

        </div>


        <!-- =====================================================
             FEEDBACK ALERT
             ===================================================== -->

        <% if (msg != null && !msg.trim().isEmpty()) { %>

            <div class="alert
                <%= "success".equalsIgnoreCase(status)
                    ? "alert-success"
                    : "alert-danger" %>">

                <span>
                    <%= msg %>
                </span>

            </div>

        <% } %>


        <!-- =====================================================
             SEARCH & FILTER
             ===================================================== -->

        <div class="search-filter-bar">

            <form
                action="clubs"
                method="GET"
                style="
                    display: flex;
                    gap: 1rem;
                    width: 100%;
                    flex-wrap: wrap;
                ">

                <!-- Search -->
                <div
                    class="search-box"
                    style="flex: 2;">

                    <input
                        type="text"
                        name="q"
                        placeholder="Search clubs..."
                        value="<%= request.getParameter("q") != null
                            ? request.getParameter("q")
                            : "" %>">

                </div>


                <!-- Category -->
                <div
                    style="
                        flex: 1;
                        min-width: 180px;
                    ">

                    <select
                        name="category"
                        class="form-control"
                        onchange="this.form.submit()">

                        <option value="">
                            All Categories
                        </option>

                        <option
                            value="Technology"
                            <%= "Technology".equals(
                                request.getParameter("category"))
                                ? "selected"
                                : "" %>>

                            Technology

                        </option>

                        <option
                            value="Arts & Culture"
                            <%= "Arts & Culture".equals(
                                request.getParameter("category"))
                                ? "selected"
                                : "" %>>

                            Arts &amp; Culture

                        </option>

                        <option
                            value="Sports & Fitness"
                            <%= "Sports & Fitness".equals(
                                request.getParameter("category"))
                                ? "selected"
                                : "" %>>

                            Sports &amp; Fitness

                        </option>

                        <option
                            value="Literature"
                            <%= "Literature".equals(
                                request.getParameter("category"))
                                ? "selected"
                                : "" %>>

                            Literature

                        </option>

                        <option
                            value="Media & Arts"
                            <%= "Media & Arts".equals(
                                request.getParameter("category"))
                                ? "selected"
                                : "" %>>

                            Media

                        </option>

                        <option
                            value="Business & Innovation"
                            <%= "Business & Innovation".equals(
                                request.getParameter("category"))
                                ? "selected"
                                : "" %>>

                            Entrepreneurship

                        </option>

                        <option
                            value="Engineering & Hardware"
                            <%= "Engineering & Hardware".equals(
                                request.getParameter("category"))
                                ? "selected"
                                : "" %>>

                            Robotics

                        </option>

                        <option
                            value="Music & Performing Arts"
                            <%= "Music & Performing Arts".equals(
                                request.getParameter("category"))
                                ? "selected"
                                : "" %>>

                            Music

                        </option>

                    </select>

                </div>


                <!-- Filter -->
                <button
                    type="submit"
                    class="btn btn-outline btn-sm">

                    Filter

                </button>


                <!-- Reset -->
                <a
                    href="clubs"
                    class="btn btn-secondary btn-sm"
                    style="
                        color: var(--text-muted);
                        border-color: var(--border-color);
                    ">

                    Reset

                </a>

            </form>

        </div>


        <!-- =====================================================
             CLUBS TABLE
             ===================================================== -->

        <div class="table-responsive">

            <table class="data-table">

                <thead>

                    <tr>

                        <th>ID</th>

                        <th>
                            Club Name
                        </th>

                        <th>
                            Category
                        </th>

                        <th>
                            Faculty In-Charge
                        </th>

                        <th>
                            Student Lead
                        </th>

                        <th>
                            Schedule &amp; Venue
                        </th>

                        <th>
                            Members
                        </th>

                        <th>
                            Actions
                        </th>

                    </tr>

                </thead>


                <tbody>

                    <%

                        if (clubs != null && !clubs.isEmpty()) {

                            for (Club c : clubs) {

                    %>

                    <tr>

                        <!-- ID -->
                        <td>
                            <%= c.getId() %>
                        </td>


                        <!-- CLUB NAME -->
                        <td>

                            <strong>
                                <%= c.getName() %>
                            </strong>

                            <br>

                            <small
                                style="color: var(--text-muted);">

                                <%= c.getContactEmail() %>

                            </small>

                        </td>


                        <!-- CATEGORY -->
                        <td>

                            <span class="card-badge">

                                <%= c.getCategory() %>

                            </span>

                        </td>


                        <!-- FACULTY -->
                        <td>
                            <%= c.getFacultyCoordinator() %>
                        </td>


                        <!-- STUDENT COORDINATOR -->
                        <td>
                            <%= c.getStudentCoordinator() %>
                        </td>


                        <!-- SCHEDULE -->
                        <td>

                            <small>

                                <%= c.getMeetingDay() %>

                                <%= c.getMeetingTime() %>

                                <br>

                                <%= c.getLocation() %>

                            </small>

                        </td>


                        <!-- MEMBERS -->
                        <td>

                            <strong>
                                <%= c.getMemberCount() %>
                            </strong>

                        </td>


                        <!-- ACTIONS -->
                        <td>

                            <div
                                style="
                                    display: flex;
                                    gap: 0.5rem;
                                    flex-wrap: wrap;
                                ">

                                <!-- EDIT -->

                                <button
                                   type="button"
                                   class="btn btn-outline btn-sm"
                                   data-club-id="<%= c.getId() %>"
                                   onclick="openEditModal(this.dataset.clubId)">
                                   Edit
                                </button>
                              
                                
                                <!-- DELETE -->
                                <form
                                    action="clubs"
                                    method="POST"
                                    onsubmit="return confirm(
                                        'Are you sure you want to delete club <%= c.getName() %>? All associated events and registrations will also be removed.'
                                    );"
                                    style="display: inline;">

                                    <input
                                        type="hidden"
                                        name="action"
                                        value="delete">

                                    <input
                                        type="hidden"
                                        name="id"
                                        value="<%= c.getId() %>">

                                    <button
                                        type="submit"
                                        class="btn btn-danger btn-sm">

                                        Delete

                                    </button>

                                </form>

                            </div>

                        </td>

                    </tr>

                    <%

                            }

                        } else {

                    %>

                    <tr>

                        <td
                            colspan="8"
                            style="
                                text-align: center;
                                color: var(--text-muted);
                                padding: 2rem;
                            ">

                            No clubs found.
                            Click "+ Add New Club" above to create one.

                        </td>

                    </tr>

                    <% } %>

                </tbody>

            </table>

        </div>

    </main>


    <!-- =========================================================
         ADD / EDIT CLUB MODAL
         ========================================================= -->

    <div
        class="modal-overlay"
        id="club-modal">

        <div class="modal-content">

            <!-- Modal Header -->
            <div class="modal-header">

                <h3
                    id="modal-title"
                    style="color: var(--primary-dark);">

                    Add New Club

                </h3>

                <button
                    class="modal-close"
                    onclick="closeModal()">

                    &times;

                </button>

            </div>


            <!-- Club Form -->
            <form
                id="club-form"
                action="clubs"
                method="POST">

                <input
                    type="hidden"
                    name="action"
                    id="form-action"
                    value="create">

                <input
                    type="hidden"
                    name="id"
                    id="club-id"
                    value="">


                <!-- NAME + CATEGORY -->
                <div class="form-row">

                    <div class="form-group">

                        <label class="form-label">

                            Club Name
                            <span class="req">*</span>

                        </label>

                        <input
                            type="text"
                            name="name"
                            id="name"
                            class="form-control"
                            required>

                    </div>


                    <div class="form-group">

                        <label class="form-label">

                            Category
                            <span class="req">*</span>

                        </label>

                        <select
                            name="category"
                            id="category"
                            class="form-control"
                            required>

                            <option value="Technology">
                                Technology
                            </option>

                            <option value="Arts & Culture">
                                Arts &amp; Culture
                            </option>

                            <option value="Sports & Fitness">
                                Sports &amp; Fitness
                            </option>

                            <option value="Literature">
                                Literature
                            </option>

                            <option value="Media & Arts">
                                Media &amp; Arts
                            </option>

                            <option value="Business & Innovation">
                                Business &amp; Innovation
                            </option>

                            <option value="Engineering & Hardware">
                                Engineering &amp; Hardware
                            </option>

                            <option value="Music & Performing Arts">
                                Music &amp; Performing Arts
                            </option>

                        </select>

                    </div>

                </div>


                <!-- DESCRIPTION -->
                <div class="form-group">

                    <label class="form-label">

                        Description
                        <span class="req">*</span>

                    </label>

                    <textarea
                        name="description"
                        id="description"
                        class="form-control"
                        rows="2"
                        required></textarea>

                </div>


                <!-- OBJECTIVES -->
                <div class="form-group">

                    <label class="form-label">

                        Objectives
                        <span class="req">*</span>

                    </label>

                    <textarea
                        name="objectives"
                        id="objectives"
                        class="form-control"
                        rows="2"
                        required></textarea>

                </div>


                <!-- ACTIVITIES -->
                <div class="form-group">

                    <label class="form-label">

                        Activities &amp; Initiatives
                        <span class="req">*</span>

                    </label>

                    <textarea
                        name="activities"
                        id="activities"
                        class="form-control"
                        rows="2"
                        required></textarea>

                </div>


                <!-- COORDINATORS -->
                <div class="form-row">

                    <div class="form-group">

                        <label class="form-label">

                            Faculty Coordinator
                            <span class="req">*</span>

                        </label>

                        <input
                            type="text"
                            name="facultyCoordinator"
                            id="facultyCoordinator"
                            class="form-control"
                            required>

                    </div>


                    <div class="form-group">

                        <label class="form-label">

                            Student Coordinator
                            <span class="req">*</span>

                        </label>

                        <input
                            type="text"
                            name="studentCoordinator"
                            id="studentCoordinator"
                            class="form-control"
                            required>

                    </div>

                </div>


                <!-- MEETING DETAILS -->
                <div class="form-row">

                    <div class="form-group">

                        <label class="form-label">
                            Meeting Day
                        </label>

                        <input
                            type="text"
                            name="meetingDay"
                            id="meetingDay"
                            class="form-control"
                            placeholder="e.g. Wednesday">

                    </div>


                    <div class="form-group">

                        <label class="form-label">
                            Meeting Time
                        </label>

                        <input
                            type="text"
                            name="meetingTime"
                            id="meetingTime"
                            class="form-control"
                            placeholder="e.g. 4:00 PM - 6:00 PM">

                    </div>

                </div>


                <!-- LOCATION + EMAIL -->
                <div class="form-row">

                    <div class="form-group">

                        <label class="form-label">
                            Location / Lab
                        </label>

                        <input
                            type="text"
                            name="location"
                            id="location"
                            class="form-control"
                            placeholder="e.g. Lab 3, Tech Block">

                    </div>


                    <div class="form-group">

                        <label class="form-label">

                            Contact Email
                            <span class="req">*</span>

                        </label>

                        <input
                            type="email"
                            name="contactEmail"
                            id="contactEmail"
                            class="form-control"
                            required>

                    </div>

                </div>


                <!-- MODAL BUTTONS -->
                <div
                    style="
                        display: flex;
                        justify-content: flex-end;
                        gap: 1rem;
                        margin-top: 1.5rem;
                    ">

                    <button
                        type="button"
                        class="btn btn-secondary"
                        onclick="closeModal()"
                        style="
                            color: var(--text-muted);
                            border-color: var(--border-color);
                        ">

                        Cancel

                    </button>


                    <button
                        type="submit"
                        class="btn btn-primary"
                        id="save-btn">

                        Save Club

                    </button>

                </div>

            </form>

        </div>

    </div>


    <!-- =========================================================
         JAVASCRIPT
         ========================================================= -->

    <script>

        const modal =
            document.getElementById('club-modal');

        const form =
            document.getElementById('club-form');

        const modalTitle =
            document.getElementById('modal-title');

        const formAction =
            document.getElementById('form-action');


        /* ---------------------------------------------------------
           ADD CLUB
           --------------------------------------------------------- */

        function openAddModal() {

            modalTitle.textContent =
                'Add New Club';

            formAction.value =
                'create';

            form.reset();

            document.getElementById('club-id').value =
                '';

            modal.classList.add('active');
        }


        /* ---------------------------------------------------------
           EDIT CLUB
           --------------------------------------------------------- */

        function openEditModal(c) {

            modalTitle.textContent =
                'Edit Club: ' + c.name;

            formAction.value =
                'update';

            document.getElementById('club-id').value =
                c.id;

            document.getElementById('name').value =
                c.name;

            document.getElementById('category').value =
                c.category;

            document.getElementById('description').value =
                c.description;

            document.getElementById('objectives').value =
                c.objectives;

            document.getElementById('activities').value =
                c.activities;

            document.getElementById('facultyCoordinator').value =
                c.facultyCoordinator;

            document.getElementById('studentCoordinator').value =
                c.studentCoordinator;

            document.getElementById('meetingDay').value =
                c.meetingDay;

            document.getElementById('meetingTime').value =
                c.meetingTime;

            document.getElementById('location').value =
                c.location;

            document.getElementById('contactEmail').value =
                c.contactEmail;

            modal.classList.add('active');
        }


        /* ---------------------------------------------------------
           CLOSE MODAL
           --------------------------------------------------------- */

        function closeModal() {

            modal.classList.remove('active');

        }


        /* ---------------------------------------------------------
           CLOSE WHEN CLICKING OUTSIDE MODAL
           --------------------------------------------------------- */

        modal.addEventListener('click', function(event) {

            if (event.target === modal) {

                closeModal();

            }

        });

    </script>

</body>

</html>


<%!
    /*
     * Helper method to safely pass Club object
     * to JavaScript JSON-like literal.
     */

    private String JsonUtilScript(Club c) {

        return "{"
            + "id:" + c.getId() + ","
            + "name:" + escapeJs(c.getName()) + ","
            + "category:" + escapeJs(c.getCategory()) + ","
            + "description:" + escapeJs(c.getDescription()) + ","
            + "objectives:" + escapeJs(c.getObjectives()) + ","
            + "activities:" + escapeJs(c.getActivities()) + ","
            + "facultyCoordinator:" + escapeJs(c.getFacultyCoordinator()) + ","
            + "studentCoordinator:" + escapeJs(c.getStudentCoordinator()) + ","
            + "meetingDay:" + escapeJs(c.getMeetingDay()) + ","
            + "meetingTime:" + escapeJs(c.getMeetingTime()) + ","
            + "location:" + escapeJs(c.getLocation()) + ","
            + "contactEmail:" + escapeJs(c.getContactEmail())
            + "}";
    }


    private String escapeJs(String s) {

        if (s == null) {
            return "''";
        }

        return "'"
            + s.replace("\\", "\\\\")
               .replace("'", "\\'")
               .replace("\n", "\\n")
               .replace("\r", "")
            + "'";
    }
%>