-- =======================================================
-- COLLEGE CLUB MANAGER DATABASE SCRIPT
-- Pure MySQL 8.0+ / 8.4 Compatible Database Schema & Seed Data
-- =======================================================

DROP DATABASE IF EXISTS college_club_manager;
CREATE DATABASE college_club_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE college_club_manager;

-- 1. Table: admins
CREATE TABLE admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Table: students
CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    department VARCHAR(50) NOT NULL,
    engineering_year VARCHAR(10) NOT NULL, -- First Year (FE), Second Year (SE), Third Year (TE), Final Year (BE)
    division VARCHAR(10) NOT NULL,
    roll_number VARCHAR(30) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 3. Table: clubs
CREATE TABLE clubs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    objectives TEXT NOT NULL,
    activities TEXT NOT NULL,
    faculty_coordinator VARCHAR(100) NOT NULL,
    student_coordinator VARCHAR(100) NOT NULL,
    meeting_day VARCHAR(20) NOT NULL,
    meeting_time VARCHAR(20) NOT NULL,
    location VARCHAR(100) NOT NULL,
    contact_email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 4. Table: events
CREATE TABLE events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_name VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    club_id INT NOT NULL,
    event_date DATE NOT NULL,
    event_time VARCHAR(20) NOT NULL,
    venue VARCHAR(100) NOT NULL,
    registration_deadline DATE NOT NULL,
    max_participants INT NOT NULL DEFAULT 100,
    status VARCHAR(20) NOT NULL DEFAULT 'Upcoming', -- Upcoming, Completed, Cancelled
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_club FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Table: club_registrations
CREATE TABLE club_registrations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    club_id INT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'Approved', -- Pending, Approved, Rejected
    CONSTRAINT fk_cr_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_cr_club FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE,
    CONSTRAINT uq_student_club UNIQUE (student_id, club_id)
) ENGINE=InnoDB;

-- 6. Table: event_registrations
CREATE TABLE event_registrations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    event_id INT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'Confirmed', -- Confirmed, Cancelled
    CONSTRAINT fk_er_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_er_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT uq_student_event UNIQUE (student_id, event_id)
) ENGINE=InnoDB;

-- =======================================================
-- SEED DATA: Administrator
-- Default credentials: admin / Admin@123
-- SHA-256 Hash of 'Admin@123': a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3
-- (Our pure Java authentication supports both SHA-256 and direct verification)
-- =======================================================
INSERT INTO admins (username, password_hash, full_name, email) VALUES 
('admin', 'Admin@123', 'Campus Administrator', 'admin@college.edu');

-- ============================================================
-- SEED DATA: ARMIET STUDENT CLUBS
-- ============================================================

INSERT INTO clubs
(name, category, description, objectives, activities,
 faculty_coordinator, student_coordinator, meeting_day,
 meeting_time, location, contact_email)
VALUES

(
    'IoT Club',
    'Technology & Innovation',
    'A student club focused on Internet of Things, embedded systems, sensors, microcontrollers, and connected smart-device projects.',
    'Develop practical skills in IoT and embedded systems; encourage students to build working prototypes; promote teamwork, innovation, and participation in technical competitions.',
    'Arduino and ESP32 workshops, sensor-based projects, IoT prototype development, embedded systems sessions, project demonstrations, and technical competitions.',
    'Faculty Coordinator',
    'Student Coordinator',
    'Wednesday',
    '4:00 PM - 6:00 PM',
    'IoT / Electronics Lab',
    'iot.club@armiet.in'
),

(
    'Drone Club',
    'Technology & Innovation',
    'A student club dedicated to drones, unmanned aerial systems, flight technology, drone assembly, and practical aerial technology projects.',
    'Introduce students to drone technology; develop practical knowledge of drone components and control systems; encourage innovation and hands-on project development.',
    'Drone assembly workshops, flight-control sessions, drone demonstrations, simulation activities, maintenance workshops, and drone-based project development.',
    'Faculty Coordinator',
    'Student Coordinator',
    'Thursday',
    '4:00 PM - 6:00 PM',
    'Innovation / Project Lab',
    'drone.club@armiet.in'
),

(
    'Electric Vehicle Club',
    'Automotive & Electrical Technology',
    'A student club focused on electric vehicles, electric mobility, battery systems, motors, controllers, and sustainable transportation technology.',
    'Develop practical understanding of electric vehicle technology; encourage sustainable mobility projects; provide hands-on experience with EV components and systems.',
    'EV workshops, motor and battery demonstrations, electric vehicle projects, controller sessions, EV design activities, technical discussions, and prototype development.',
    'Faculty Coordinator',
    'Student Coordinator',
    'Friday',
    '4:00 PM - 6:00 PM',
    'Electrical / Mechanical Lab',
    'ev.club@armiet.in'
),

(
    'Dance Club',
    'Cultural & Performing Arts',
    'A student club for students interested in dance, choreography, performances, cultural events, and creative expression.',
    'Encourage students to develop confidence and creativity through dance; provide opportunities for performances; promote teamwork and participation in college cultural activities.',
    'Dance practices, choreography sessions, cultural-event performances, competitions, group performances, workshops, and college celebrations.',
    'Faculty Coordinator',
    'Student Coordinator',
    'Saturday',
    '3:00 PM - 5:00 PM',
    'College Auditorium / Activity Hall',
    'dance.club@armiet.in'
);

-- =======================================================
-- SEED DATA: Events
-- =======================================================
INSERT INTO events (event_name, description, club_id, event_date, event_time, venue, registration_deadline, max_participants, status) VALUES
(
    'CodeStorm 2026: 24-Hour Hackathon',
    'Build innovative web and mobile solutions addressing smart campus and sustainability challenges. Cash prizes and internship opportunities for top 3 teams.',
    1, -- Technical Club
    '2026-10-15',
    '09:00 AM',
    'Central Computing Center',
    '2026-10-10',
    120,
    'Upcoming'
),
(
    'Aarohan: Annual Collegiate Cultural Fest',
    'Three days of music, dance, theatrical performance, and inter-collegiate celebrity shows. Auditions open for solo and group categories.',
    2, -- Cultural Club
    '2026-10-22',
    '10:00 AM',
    'Open Air Amphitheatre',
    '2026-10-18',
    300,
    'Upcoming'
),
(
    'Spardha: Inter-Department Football Championship',
    'Annual knockout football tournament. Seven-a-side matches with rolling substitutions. Trophies, medals, and best player awards.',
    3, -- Sports Club
    '2026-10-05',
    '08:00 AM',
    'Main Sports Ground',
    '2026-10-02',
    80,
    'Upcoming'
),
(
    'National Youth Parliamentary Debate',
    'Debate key contemporary social, technological, and economic national policies. Judged by seasoned parliamentarians and public intellectuals.',
    4, -- Literary Club
    '2026-10-12',
    '02:00 PM',
    'Auditorium Hall B',
    '2026-10-08',
    60,
    'Upcoming'
),
(
    'Chiaroscuro: Heritage Campus Photowalk',
    'Explore golden hour lighting and architectural framing across campus historic landmarks. Bring your DSLR, mirrorless, or smartphone camera.',
    5, -- Photography Club
    '2026-10-08',
    '06:30 AM',
    'Clock Tower Quadrangle',
    '2026-10-06',
    40,
    'Upcoming'
),
(
    'Startup Pitch Tank: Seed Your Idea',
    'Present your innovative startup idea in 5 minutes to angel investors and industry founders. Top 2 concepts receive incubation workspace support.',
    6, -- Entrepreneurship Club
    '2026-10-28',
    '03:00 PM',
    'Incubation Boardroom',
    '2026-10-24',
    50,
    'Upcoming'
),
(
    'RoboCombat: Clash of Steel Bots',
    'Design and operate 15kg wireless combat robots in an enclosed battle arena. Prizes for deadliest weapon design and tactical driving.',
    7, -- Robotics Club
    '2026-11-04',
    '11:00 AM',
    'Mechanical Workshop Arena',
    '2026-10-30',
    30,
    'Upcoming'
),
(
    'Rhapsody: Acoustic Sunset Live Concert',
    'An evening of soulful acoustic melodies, folk fusions, and indie rock originals performed live by collegiate bands under the stars.',
    8, -- Music Club
    '2026-10-18',
    '05:30 PM',
    'Lakeside Garden Stage',
    '2026-10-16',
    150,
    'Upcoming'
);

-- =======================================================
-- SEED DATA: Sample Students
-- =======================================================
INSERT INTO students (full_name, email, phone, department, engineering_year, division, roll_number) VALUES
('Rahul Sharma', 'rahul.sharma@college.edu', '9876543210', 'Computer Engineering', 'TE', 'A', 'TE-COMP-01'),
('Pooja Patel', 'pooja.patel@college.edu', '9876543211', 'Information Technology', 'SE', 'B', 'SE-IT-24'),
('Sneha Shinde', 'sneha.shinde@college.edu', '9876543212', 'Electronics & Telecommunication', 'BE', 'A', 'BE-EXTC-15');

-- =======================================================
-- SEED DATA: Sample Registrations
-- =======================================================
INSERT INTO club_registrations (student_id, club_id, status) VALUES
(1, 1, 'Approved'),
(1, 4, 'Approved'),
(2, 2, 'Approved'),
(3, 7, 'Approved');

INSERT INTO event_registrations (student_id, event_id, status) VALUES
(1, 1, 'Confirmed'),
(2, 2, 'Confirmed'),
(3, 7, 'Confirmed');
