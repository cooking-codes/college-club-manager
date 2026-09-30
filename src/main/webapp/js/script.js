/**
 * Alamuri Ratnamala Institute of Engineering & Technology (ARMIET)
 * Student Club & Event Management Portal - Client Script
 * Handles dynamic data fetching from Jakarta Servlets, client validation,
 * search filtering, real-time platform statistics, and student lookup.
 */

// Helper: Context Path Resolver
function getContextPath() {
    const path = window.location.pathname;
    const parts = path.split('/');
    if (parts.length > 2 && parts[1] === 'college-club-manager') {
        return '/college-club-manager';
    }
    return '';
}

const API_BASE = getContextPath() + '/api';

// Helper: Get URL Search Parameters
function getUrlParam(param) {
    const params = new URLSearchParams(window.location.search);
    return params.get(param);
}

// Helper: Show Alert Message
function showAlert(containerId, message, type = 'danger') {
    const container = document.getElementById(containerId);
    if (!container) return;
    container.innerHTML = `
        <div class="alert alert-${type}">
            <span>${message}</span>
        </div>
    `;
    container.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

// Helper: Mobile Navigation Menu Toggle
function toggleMobileMenu() {
    const navLinks = document.querySelector('.nav-links');
    if (navLinks) {
        navLinks.classList.toggle('mobile-open');
    }
}

// -------------------------------------------------------------
// 1. DYNAMIC PLATFORM STATISTICS (Actual Database Data Only)
// -------------------------------------------------------------
async function loadDynamicStats() {
    try {
        const res = await fetch(`${API_BASE}/stats`);
        if (!res.ok) return;
        const result = await res.json();
        if (result.success && result.data) {
            const d = result.data;
            const clubsEl = document.getElementById('stat-clubs');
            const eventsEl = document.getElementById('stat-events');
            const studentsEl = document.getElementById('stat-students');
            const membershipsEl = document.getElementById('stat-memberships');

            if (clubsEl) clubsEl.textContent = d.totalClubs !== undefined ? d.totalClubs : '0';
            if (eventsEl) eventsEl.textContent = d.totalEvents !== undefined ? d.totalEvents : '0';
            if (studentsEl) studentsEl.textContent = d.totalStudents !== undefined ? d.totalStudents : '0';
            if (membershipsEl) {
                const totalRegs = (d.totalClubRegistrations || 0) + (d.totalEventRegistrations || 0);
                membershipsEl.textContent = totalRegs;
            }
        }
    } catch (e) {
        console.warn('Unable to load live database stats:', e);
    }
}

// -------------------------------------------------------------
// 2. CLUBS HANDLING (Real Database Records)
// -------------------------------------------------------------
async function loadClubs(containerId, limit = null, category = null, searchQuery = null) {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--text-muted); padding: 2rem;">Loading registered clubs from database...</p>';

    try {
        let url = `${API_BASE}/clubs`;
        const params = [];
        if (category && category !== 'All') params.push(`category=${encodeURIComponent(category)}`);
        if (searchQuery) params.push(`q=${encodeURIComponent(searchQuery)}`);
        if (params.length > 0) url += `?${params.join('&')}`;

        const res = await fetch(url);
        const data = await res.json();

        if (!data.success || !data.data) {
            container.innerHTML = `<div class="empty-state"><div class="empty-state-icon">→</div><h3>Database Notice</h3><p>${data.message || 'Error connecting to database.'}</p></div>`;
            return;
        }

        let clubs = data.data;
        if (limit && clubs.length > limit) {
            clubs = clubs.slice(0, limit);
        }

        if (clubs.length === 0) {
            const msg = searchQuery ? 'No clubs matching your search criteria.' : 'No clubs have been added yet.';
            container.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">↗</div>
                    <h3>${msg}</h3>
                    <p>Official student organizations will appear here dynamically once registered.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = clubs.map(club => `
            <div class="card">
                <div class="card-header">
                    <div>
                        <span class="card-badge">${club.category}</span>
                        <h3 class="card-title">${club.name}</h3>
                    </div>
                </div>
                <div class="card-body">
                    <p class="card-desc">${club.description}</p>
                    <ul class="card-details-list">
                        <li><strong>Coordinator:</strong> <span>${club.studentCoordinator}</span></li>
                        <li><strong>Schedule:</strong> <span>${club.meetingDay} (${club.meetingTime})</span></li>
                        <li><strong>Venue:</strong> <span>${club.location}</span></li>
                    </ul>
                </div>
                <div class="card-footer">
                    <a href="${getContextPath()}/club-details.html?id=${club.id}" class="btn btn-outline btn-sm">View Club</a>
                    <a href="${getContextPath()}/club-register.html?clubId=${club.id}" class="btn btn-primary btn-sm">Join Club</a>
                </div>
            </div>
        `).join('');

    } catch (err) {
        container.innerHTML = `<div class="empty-state"><div class="empty-state-icon">→</div><h3>Connection Error</h3><p>Failed to connect to backend: ${err.message}</p></div>`;
    }
}

async function loadClubDetails() {
    const clubId = getUrlParam('id');
    const container = document.getElementById('club-detail-content');
    if (!container) return;

    if (!clubId) {
        container.innerHTML = '<p style="color: var(--danger);">Error: No Club ID specified.</p>';
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/clubs?id=${clubId}`);
        const result = await res.json();

        if (!result.success || !result.data) {
            container.innerHTML = `<div class="empty-state"><div class="empty-state-icon">→</div><h3>Club Not Found</h3><p>${result.message || 'Club record does not exist in the database.'}</p></div>`;
            return;
        }

        const club = result.data;
        document.title = `${club.name} - ARMIET Club Portal`;

        container.innerHTML = `
            <div class="detail-header">
                <span class="card-badge" style="font-size: 0.85rem;">${club.category}</span>
                <h1 class="detail-title">${club.name}</h1>
                <p style="color: var(--text-muted); font-size: 1.08rem; margin-top: 0.5rem;">${club.description}</p>
            </div>
            <div class="detail-grid">
                <div>
                    <div class="detail-section">
                        <h3>Club Objectives</h3>
                        <p style="line-height: 1.7;">${club.objectives}</p>
                    </div>
                    <div class="detail-section">
                        <h3>Key Activities & Initiatives</h3>
                        <p style="line-height: 1.7;">${club.activities}</p>
                    </div>
                </div>
                <div>
                    <div class="sidebar-box">
                        <div>
                            <strong>Faculty Coordinator</strong>
                            <p style="color: var(--text-main); font-weight: 500;">${club.facultyCoordinator}</p>
                        </div>
                        <div>
                            <strong>Student Lead</strong>
                            <p style="color: var(--text-main); font-weight: 500;">${club.studentCoordinator}</p>
                        </div>
                        <div>
                            <strong>Regular Meetings</strong>
                            <p style="color: var(--text-main);">${club.meetingDay}, ${club.meetingTime}</p>
                        </div>
                        <div>
                            <strong>Venue / Lab</strong>
                            <p style="color: var(--text-main);">${club.location}</p>
                        </div>
                        <div>
                            <strong>Contact Email</strong>
                            <p><a href="mailto:${club.contactEmail}" style="color: var(--primary); font-weight: 600;">${club.contactEmail}</a></p>
                        </div>
                        <a href="${getContextPath()}/club-register.html?clubId=${club.id}" class="btn btn-primary" style="width: 100%;">Join ${club.name}</a>
                    </div>
                </div>
            </div>
        `;
    } catch (err) {
        container.innerHTML = `<p style="color: var(--danger);">Failed to load club details: ${err.message}</p>`;
    }
}

// -------------------------------------------------------------
// 3. EVENTS HANDLING (Real Database Records)
// -------------------------------------------------------------
async function loadEvents(containerId, limit = null, upcomingOnly = false, searchQuery = null, clubId = null) {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--text-muted); padding: 2rem;">Loading campus events from database...</p>';

    try {
        let url = `${API_BASE}/events`;
        const params = [];
        if (upcomingOnly) params.push('upcoming=true');
        if (searchQuery) params.push(`q=${encodeURIComponent(searchQuery)}`);
        if (clubId) params.push(`clubId=${encodeURIComponent(clubId)}`);
        if (params.length > 0) url += `?${params.join('&')}`;

        const res = await fetch(url);
        const data = await res.json();

        if (!data.success || !data.data) {
            container.innerHTML = `<div class="empty-state"><div class="empty-state-icon">→</div><h3>Database Notice</h3><p>${data.message || 'Error connecting to database.'}</p></div>`;
            return;
        }

        let events = data.data;
        if (limit && events.length > limit) {
            events = events.slice(0, limit);
        }

        if (events.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">→</div>
                    <h3>No upcoming events have been added yet.</h3>
                    <p>New events and technical competitions will appear here once announced by student organizations.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = events.map(event => {
            let statusBadge = '<span class="card-badge badge-upcoming">Open</span>';
            let canRegister = true;

            if (event.isClosed) {
                statusBadge = '<span class="card-badge badge-closed">Deadline Passed</span>';
                canRegister = false;
            } else if (event.isFull) {
                statusBadge = '<span class="card-badge badge-full">Seats Full</span>';
                canRegister = false;
            } else if (event.status === 'Cancelled') {
                statusBadge = '<span class="card-badge badge-closed">Cancelled</span>';
                canRegister = false;
            }

            return `
                <div class="card">
                    <div class="card-header">
                        <div>
                            ${statusBadge}
                            <h3 class="card-title">${event.eventName}</h3>
                        </div>
                    </div>
                    <div class="card-body">
                        <p class="card-desc">${event.description}</p>
                        <ul class="card-details-list">
                            <li><strong>Organized by:</strong> <span>${event.clubName}</span></li>
                            <li><strong>Date:</strong> <span>${event.eventDate} (${event.eventTime})</span></li>
                            <li><strong>Venue:</strong> <span>${event.venue}</span></li>
                            <li><strong>Deadline:</strong> <span>${event.registrationDeadline}</span></li>
                            <li><strong>Capacity:</strong> <span>${event.registeredCount} / ${event.maxParticipants} Registered</span></li>
                        </ul>
                    </div>
                    <div class="card-footer">
                        <a href="${getContextPath()}/event-details.html?id=${event.id}" class="btn btn-outline btn-sm">View Details</a>
                        ${canRegister ? 
                            `<a href="${getContextPath()}/event-register.html?eventId=${event.id}" class="btn btn-primary btn-sm">Register Now</a>` : 
                            `<button class="btn btn-secondary btn-sm" disabled style="opacity: 0.6; cursor: not-allowed;">Closed</button>`
                        }
                    </div>
                </div>
            `;
        }).join('');

    } catch (err) {
        container.innerHTML = `<div class="empty-state"><div class="empty-state-icon">→</div><h3>Connection Error</h3><p>Failed to connect to backend: ${err.message}</p></div>`;
    }
}

async function loadEventDetails() {
    const eventId = getUrlParam('id');
    const container = document.getElementById('event-detail-content');
    if (!container) return;

    if (!eventId) {
        container.innerHTML = '<p style="color: var(--danger);">Error: No Event ID specified.</p>';
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/events?id=${eventId}`);
        const result = await res.json();

        if (!result.success || !result.data) {
            container.innerHTML = `<div class="empty-state"><div class="empty-state-icon">→</div><h3>Event Not Found</h3><p>${result.message || 'Event record not found.'}</p></div>`;
            return;
        }

        const ev = result.data;
        document.title = `${ev.eventName} - ARMIET Event Portal`;

        let statusBadge = '<span class="card-badge badge-upcoming">Open for Registration</span>';
        let registerBtn = `<a href="${getContextPath()}/event-register.html?eventId=${ev.id}" class="btn btn-primary" style="width: 100%;">Register for Event</a>`;

        if (ev.isClosed) {
            statusBadge = '<span class="card-badge badge-closed">Registration Deadline Expired</span>';
            registerBtn = `<button class="btn btn-secondary" style="width: 100%; opacity: 0.6;" disabled>Registration Closed</button>`;
        } else if (ev.isFull) {
            statusBadge = '<span class="card-badge badge-full">Event Full (Capacity Reached)</span>';
            registerBtn = `<button class="btn btn-secondary" style="width: 100%; opacity: 0.6;" disabled>House Full</button>`;
        } else if (ev.status === 'Cancelled') {
            statusBadge = '<span class="card-badge badge-closed">Event Cancelled</span>';
            registerBtn = `<button class="btn btn-secondary" style="width: 100%; opacity: 0.6;" disabled>Cancelled</button>`;
        }

        container.innerHTML = `
            <div class="detail-header">
                ${statusBadge}
                <h1 class="detail-title">${ev.eventName}</h1>
                <p style="color: var(--primary); font-weight: 600; margin-top: 0.5rem;">Organized by: ${ev.clubName}</p>
            </div>
            <div class="detail-grid">
                <div>
                    <div class="detail-section">
                        <h3>About the Event</h3>
                        <p style="font-size: 1.05rem; line-height: 1.8;">${ev.description}</p>
                    </div>
                </div>
                <div>
                    <div class="sidebar-box">
                        <div>
                            <strong>Event Date & Time</strong>
                            <p style="color: var(--text-main); font-weight: 500;">${ev.eventDate} | ${ev.eventTime}</p>
                        </div>
                        <div>
                            <strong>Venue</strong>
                            <p style="color: var(--text-main); font-weight: 500;">${ev.venue}</p>
                        </div>
                        <div>
                            <strong>Registration Deadline</strong>
                            <p style="color: var(--text-main);">${ev.registrationDeadline}</p>
                        </div>
                        <div>
                            <strong>Capacity & Registrations</strong>
                            <p style="color: var(--text-main);">${ev.registeredCount} of ${ev.maxParticipants} slots booked</p>
                        </div>
                        ${registerBtn}
                    </div>
                </div>
            </div>
        `;
    } catch (err) {
        container.innerHTML = `<p style="color: var(--danger);">Failed to load event details: ${err.message}</p>`;
    }
}

// -------------------------------------------------------------
// 4. STUDENT REGISTRATIONS LOOKUP (Real Database Query)
// -------------------------------------------------------------
async function lookupStudentRegistrations(formId, resultsContainerId) {
    const form = document.getElementById(formId);
    const container = document.getElementById(resultsContainerId);
    if (!form || !container) return;

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        const input = form.studentQuery.value.trim();
        if (!input) return;

        container.innerHTML = '<p style="text-align: center; color: var(--text-muted); padding: 2rem;">Searching student records in database...</p>';

        try {
            const res = await fetch(`${API_BASE}/my-registrations?q=${encodeURIComponent(input)}`);
            const data = await res.json();

            if (!data.success || !data.data) {
                container.innerHTML = `<div class="empty-state"><div class="empty-state-icon">→</div><h3>Query Error</h3><p>${data.message || 'Unable to query student records.'}</p></div>`;
                return;
            }

            const { clubMemberships, eventRegistrations } = data.data;

            if (clubMemberships.length === 0 && eventRegistrations.length === 0) {
                container.innerHTML = `
                    <div class="empty-state">
                        <div class="empty-state-icon">→</div>
                        <h3>No Records Found for "${input}"</h3>
                        <p>No club memberships or event registrations were found under this roll number or email address.</p>
                        <div style="margin-top: 1.25rem; display: flex; gap: 1rem; justify-content: center;">
                            <a href="club-register.html" class="btn btn-primary btn-sm">Join a Club</a>
                            <a href="event-register.html" class="btn btn-secondary btn-sm">Register for an Event</a>
                        </div>
                    </div>
                `;
                return;
            }

            let html = `
                <div style="margin-bottom: 2rem;">
                    <div class="section-header">
                        <h2>Registered Club Memberships (${clubMemberships.length})</h2>
                    </div>
            `;

            if (clubMemberships.length > 0) {
                html += `
                    <div class="table-responsive" style="margin-bottom: 2rem;">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Club Name</th>
                                    <th>Category</th>
                                    <th>Student Name</th>
                                    <th>Roll Number</th>
                                    <th>Registration Date</th>
                                    <th>Membership Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${clubMemberships.map(c => `
                                    <tr>
                                        <td><strong>${c.clubName}</strong></td>
                                        <td><span class="card-badge">${c.clubCategory}</span></td>
                                        <td>${c.studentName}</td>
                                        <td><code>${c.studentRollNumber}</code></td>
                                        <td>${c.registrationDate ? c.registrationDate.substring(0, 10) : '-'}</td>
                                        <td><span class="card-badge badge-upcoming">${c.status}</span></td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                `;
            } else {
                html += '<p style="color: var(--text-muted); margin-bottom: 2rem;">No club memberships registered yet.</p>';
            }

            html += `
                <div class="section-header">
                    <h2>Registered Events (${eventRegistrations.length})</h2>
                </div>
            `;

            if (eventRegistrations.length > 0) {
                html += `
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Event Name</th>
                                    <th>Organizing Club</th>
                                    <th>Event Date & Time</th>
                                    <th>Venue</th>
                                    <th>Student Roll No</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${eventRegistrations.map(ev => `
                                    <tr>
                                        <td><strong>${ev.eventName}</strong></td>
                                        <td>${ev.organizingClubName}</td>
                                        <td>${ev.eventDate} (${ev.eventTime})</td>
                                        <td>${ev.venue}</td>
                                        <td><code>${ev.studentRollNumber}</code></td>
                                        <td><span class="card-badge badge-upcoming">${ev.status}</span></td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                `;
            } else {
                html += '<p style="color: var(--text-muted);">No event registrations found.</p>';
            }

            html += '</div>';
            container.innerHTML = html;

        } catch (err) {
            container.innerHTML = `<p style="color: var(--danger);">Failed to query records: ${err.message}</p>`;
        }
    });
}

// -------------------------------------------------------------
// 5. REGISTRATION FORM HANDLING (Ajax + Validation)
// -------------------------------------------------------------
async function populateClubDropdown(selectId, selectedId = null) {
    const select = document.getElementById(selectId);
    if (!select) return;

    try {
        const res = await fetch(`${API_BASE}/clubs`);
        const data = await res.json();
        if (data.success && data.data) {
            select.innerHTML = '<option value="">-- Choose a Club --</option>' + 
                data.data.map(c => `
                    <option value="${c.id}" ${selectedId && parseInt(selectedId) === c.id ? 'selected' : ''}>
                        ${c.name} (${c.category})
                    </option>
                `).join('');
        }
    } catch (e) {
        console.error('Failed to populate clubs:', e);
    }
}

async function populateEventDropdown(selectId, selectedId = null) {
    const select = document.getElementById(selectId);
    if (!select) return;

    try {
        const res = await fetch(`${API_BASE}/events?upcoming=true`);
        const data = await res.json();
        if (data.success && data.data) {
            select.innerHTML = '<option value="">-- Choose an Upcoming Event --</option>' + 
                data.data.map(e => `
                    <option value="${e.id}" ${selectedId && parseInt(selectedId) === e.id ? 'selected' : ''} ${e.isClosed || e.isFull ? 'disabled' : ''}>
                        ${e.eventName} (${e.eventDate}) ${e.isClosed ? '- [CLOSED]' : ''} ${e.isFull ? '- [FULL]' : ''}
                    </option>
                `).join('');
        }
    } catch (e) {
        console.error('Failed to populate events:', e);
    }
}

function handleRegistrationSubmit(formId, servletEndpoint, alertContainerId) {
    const form = document.getElementById(formId);
    if (!form) return;

    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const fullName = form.fullName.value.trim();
        const email = form.email.value.trim();
        const phone = form.phone.value.trim().replace(/\D/g, '');
        const department = form.department.value;
        const engineeringYear = form.engineeringYear.value;
        const division = form.division.value.trim();
        const rollNumber = form.rollNumber.value.trim();

        if (!fullName || !email || !phone || !department || !engineeringYear || !division || !rollNumber) {
            showAlert(alertContainerId, 'Please complete all required fields.', 'danger');
            return;
        }

        const emailPattern = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$/;
        if (!emailPattern.test(email)) {
            showAlert(alertContainerId, 'Please provide a valid College Email address.', 'danger');
            return;
        }

        if (phone.length !== 10) {
            showAlert(alertContainerId, 'Please enter a valid 10-digit mobile number.', 'danger');
            return;
        }

        const submitBtn = form.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;
        submitBtn.disabled = true;
        submitBtn.innerHTML = 'Submitting to Server...';

        try {
            const formData = new FormData(form);
            const urlEncoded = new URLSearchParams(formData);

            const res = await fetch(`${getContextPath()}${servletEndpoint}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: urlEncoded
            });

            const data = await res.json();

            if (res.ok && data.success) {
                showAlert(alertContainerId, data.message, 'success');
                form.reset();
            } else {
                showAlert(alertContainerId, data.message || 'Registration error occurred.', 'danger');
            }
        } catch (err) {
            showAlert(alertContainerId, 'Failed to connect with Java Servlet: ' + err.message, 'danger');
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalText;
        }
    });
}

function checkUrlStatus(containerId) {
    const status = getUrlParam('status');
    const msg = getUrlParam('msg');
    const err = getUrlParam('error');

    if (msg) {
        showAlert(containerId, decodeURIComponent(msg), status === 'success' ? 'success' : 'danger');
    } else if (err) {
        showAlert(containerId, decodeURIComponent(err), 'danger');
    }
}
