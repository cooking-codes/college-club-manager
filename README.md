# 🎓 College Club Manager (CCM)

A robust, enterprise-grade, multi-tier **College Club Management System** built with **Pure Java (JDK 17)**, **Jakarta Servlets**, **JDBC**, and **MySQL**, deployed on **Apache Tomcat 10.1.59**.

Developed as a college mini-project demonstrating pure core Java web technologies without high-level enterprise frameworks (No Spring Boot, No Hibernate, No Node.js, No React).

---

## 🏛️ System Architecture

```
+-------------------------------------------------------------------------+
|                        PRESENTATION TIER                                |
|   HTML5, CSS3, Vanilla JavaScript (DOM manipulation, fetch API, forms)  |
+-------------------------------------------------------------------------+
                                    │
                                    │ HTTP GET / POST (Form / JSON)
                                    ▼
+-------------------------------------------------------------------------+
|                        WEB & CONTROLLER TIER                            |
|     Apache Tomcat 10.1.59  ──▶  Jakarta Servlets (@WebServlet)          |
|  (ClubServlet, EventServlet, RegistrationServlets, AdminServlets)       |
+-------------------------------------------------------------------------+
                                    │
                                    │ Method Invocations & DTOs
                                    ▼
+-------------------------------------------------------------------------+
|                        BUSINESS LOGIC TIER                              |
|   Pure Java Service Layer (ClubService, EventService,                   |
|   RegistrationService, AdminService)                                    |
|   • Server-Side Input Validation (regex, empty fields, data types)      |
|   • Business Constraints (Capacity, Deadlines, Duplicate Prevention)    |
|   • Password Hashing & Security (SHA-256)                               |
+-------------------------------------------------------------------------+
                                    │
                                    │ Pure JDBC Calls
                                    ▼
+-------------------------------------------------------------------------+
|                        DATA ACCESS TIER (DAO)                           |
|   Pure JDBC PreparedStatements & ResultSet Mappings                    |
|   (StudentDAO, ClubDAO, EventDAO, RegistrationDAO, AdminDAO)            |
+-------------------------------------------------------------------------+
                                    │
                                    │ TCP / Port 3306 (MySQL Protocol)
                                    ▼
+-------------------------------------------------------------------------+
|                        DATABASE TIER                                    |
|   MySQL Database (college_club_manager)                                 |
|   • admins, students, clubs, events, club_registrations, event_regs     |
|   • Foreign Keys with ON DELETE CASCADE, Unique Constraints             |
+-------------------------------------------------------------------------+
```

---

## 📂 Project Directory Structure

```
college-club-manager/
│
├── pom.xml                                   # Maven dependency descriptor
├── database.sql                              # Database schema & sample seed data
├── build-and-deploy.ps1                      # Automated build, package & Tomcat deploy script
├── README.md                                 # Documentation and Viva preparation guide
│
└── src/
    └── main/
        ├── java/
        │   └── com/collegeclub/
        │       ├── model/                    # Pure Java Model POJOs
        │       │   ├── Student.java
        │       │   ├── Club.java
        │       │   ├── Event.java
        │       │   ├── Admin.java
        │       │   ├── ClubRegistration.java
        │       │   └── EventRegistration.java
        │       │
        │       ├── dao/                      # Data Access Objects (Pure JDBC)
        │       │   ├── StudentDAO.java
        │       │   ├── ClubDAO.java
        │       │   ├── EventDAO.java
        │       │   ├── AdminDAO.java
        │       │   └── RegistrationDAO.java
        │       │
        │       ├── service/                  # Business Logic & Validation Layer
        │       │   ├── ClubService.java
        │       │   ├── EventService.java
        │       │   ├── RegistrationService.java
        │       │   └── AdminService.java
        │       │
        │       ├── servlet/                  # Jakarta HTTP Servlets (@WebServlet)
        │       │   ├── ClubServlet.java
        │       │   ├── EventServlet.java
        │       │   ├── ClubRegistrationServlet.java
        │       │   ├── EventRegistrationServlet.java
        │       │   ├── LoginServlet.java
        │       │   ├── LogoutServlet.java
        │       │   ├── AdminDashboardServlet.java
        │       │   ├── AdminClubServlet.java
        │       │   ├── AdminEventServlet.java
        │       │   └── AdminRegistrationServlet.java
        │       │
        │       └── util/                     # Pure Java Utilities
        │           ├── DBConnection.java     # JDBC Connection Manager
        │           ├── PasswordUtil.java     # SHA-256 password hashing
        │           └── JsonUtil.java         # Lightweight JSON serializer
        │
        ├── resources/
        │   └── db.properties                 # MySQL Connection configuration
        │
        └── webapp/                           # Presentation & Web Root
            ├── index.html                    # Homepage (Hero, Featured Clubs, Events)
            ├── clubs.html                    # Dynamic club listing & search/filter
            ├── club-details.html             # Detailed club view
            ├── events.html                   # Dynamic event listing & capacity indicators
            ├── event-details.html            # Detailed event view
            ├── club-register.html            # Student Club registration form
            ├── event-register.html           # Student Event registration form
            ├── about.html                    # About platform & student council
            ├── contact.html                  # Campus office contacts & inquiry form
            │
            ├── admin/                        # Administrative Back-office
            │   ├── login.html                # Secure admin login form
            │   ├── dashboard.jsp             # Live statistics & registration metrics
            │   ├── clubs.jsp                 # Club CRUD (Add, Edit, Delete modals)
            │   ├── events.jsp                # Event CRUD (Add, Edit, Delete modals)
            │   └── registrations.jsp         # Audit student registrations & search
            │
            ├── css/
            │   └── style.css                 # Responsive stylesheet
            ├── js/
            │   └── script.js                 # Dynamic DOM manipulation & validation
            │
            └── WEB-INF/
                ├── lib/
                │   └── mysql-connector-j-8.4.0.jar
                └── web.xml                   # Deployment descriptor (Servlet 6.0)
```

---

## 🚀 Setup & Execution Guide

### Prerequisites
1. **Java Development Kit (JDK 17+)**
2. **Apache Tomcat 10.1.59** (Target Server)
3. **MySQL Server 8.0 or 8.4**

### 1. Database Setup
1. Start your MySQL Server.
2. Run the provided `database.sql` script to create the database and seed initial data:
   ```bash
   mysql -u root -p < database.sql
   ```
   *(If your root user has no password, simply run `mysql -u root < database.sql`)*.
3. Verify connection settings in `src/main/resources/db.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/college_club_manager?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
   db.user=root
   db.password=
   ```

### 2. Build & Deploy using PowerShell
Run the build script to compile all Java classes and package `college-club-manager.war`:
```powershell
powershell.exe -ExecutionPolicy Bypass -File .\build-and-deploy.ps1
```
This script will:
- Clean and create `target/` directories.
- Compile all 28 pure Java classes using JDK 17 `javac`.
- Bundle `mysql-connector-j-8.4.0.jar` into `WEB-INF/lib`.
- Package `college-club-manager.war`.
- Automatically deploy the WAR into `C:\Tomcat\apache-tomcat-10.1.59\webapps\`.

### 3. Start Apache Tomcat 10.1.59
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:CATALINA_HOME = "C:\Tomcat\apache-tomcat-10.1.59"
& "$env:CATALINA_HOME\bin\startup.bat"
```

### 4. Access the Application in Your Browser
- **Student Public Portal**: `http://localhost:8080/college-club-manager/`
- **Browse Clubs**: `http://localhost:8080/college-club-manager/clubs.html`
- **Browse Events**: `http://localhost:8080/college-club-manager/events.html`
- **Admin Portal**: `http://localhost:8080/college-club-manager/admin/login.html`
  - **Username**: `admin`
  - **Password**: `Admin@123`

---

## 🎯 Viva & Demonstration Reference Guide

Be ready to explain these 18 core technical questions during your project examination:

### 1. What is Java?
> **Answer**: Java is a high-level, class-based, object-oriented, secure, and platform-independent programming language. It follows the **WORA** principle (*Write Once, Run Anywhere*) because Java source code (`.java`) compiles into bytecode (`.class`), which executes on any machine with a Java Virtual Machine (JVM).

### 2. What is a Servlet?
> **Answer**: A Servlet is a Java class that runs on a Java web server (like Apache Tomcat) and extends the server's capabilities to handle HTTP requests and generate dynamic HTTP responses. In this project, we use `HttpServlet` from the **Jakarta Servlet API** (`jakarta.servlet.http.*`) compatible with Tomcat 10.1.

### 3. What is Tomcat?
> **Answer**: Apache Tomcat is an open-source web server and **Servlet Container** (also called a Web Container) that implements the Jakarta Servlet and Jakarta Server Pages (JSP) specifications. It listens on HTTP port 8080, translates incoming HTTP requests into `HttpServletRequest` objects, invokes our Servlets, and sends the `HttpServletResponse` back to the browser.

### 4. What is JDBC?
> **Answer**: **JDBC (Java Database Connectivity)** is a standard Java API that enables Java applications to interact with relational databases. It defines interfaces such as `Connection`, `PreparedStatement`, `Statement`, and `ResultSet`. The database vendor provides a driver (`mysql-connector-j`) that translates these standard Java calls into database-specific network protocols.

### 5. What is MySQL?
> **Answer**: MySQL is an open-source Relational Database Management System (RDBMS) that stores structured data in tables consisting of rows and columns. It uses Structured Query Language (SQL) and supports ACID transactions, primary keys, foreign keys, and unique indexes to enforce data integrity.

### 6. How HTML communicates with Java?
> **Answer**: HTML runs on the client's web browser. It communicates with the Java backend over the **HTTP protocol**:
> 1. **HTML Form Submission**: An HTML form specifies `action="register-club"` and `method="POST"`. When the user submits, the browser packages form inputs as HTTP POST request parameters.
> 2. **JavaScript `fetch()` API / AJAX**: JavaScript sends asynchronous `GET` or `POST` requests to our Servlets (e.g. `/api/clubs`), receives JSON or status codes, and dynamically updates the HTML Document Object Model (DOM) without reloading the page.

### 7. How a Servlet receives a request?
> **Answer**: 
> 1. When a browser sends an HTTP request to `http://localhost:8080/college-club-manager/api/clubs`, Tomcat inspects the URL.
> 2. Tomcat matches the URL path against Servlet annotations (`@WebServlet(urlPatterns = {"/api/clubs"})`).
> 3. Tomcat allocates a worker thread, creates `HttpServletRequest` (containing headers, query params, form data) and `HttpServletResponse` objects, and calls the Servlet's `service()` method.
> 4. The `service()` method routes the request to `doGet()` or `doPost()`.

### 8. How Java validates the data?
> **Answer**: All validation logic is encapsulated inside our pure Java **Service classes** (e.g., `RegistrationService.java`):
> - Checks for `null` or empty strings (`fullName.trim().isEmpty()`).
> - Regular expressions validate email formats (`^[\\w-\\.]+@...$`) and 10-digit mobile numbers (`^[0-9]{10}$`).
> - Checks business rules: verifying event registration deadlines against the current date (`LocalDate.now().isAfter(deadline)`), ensuring seat capacity is not exceeded, and rejecting duplicate registrations.
> - If validation fails, Java throws descriptive exceptions (`IllegalArgumentException`, `IllegalStateException`) that the Servlet catches to return clean error messages with appropriate HTTP status codes (400 Bad Request, 409 Conflict).

### 9. How JDBC connects Java to MySQL?
> **Answer**:
> 1. In `DBConnection.java`, the MySQL driver class is registered using `Class.forName("com.mysql.cj.jdbc.Driver")`.
> 2. `DriverManager.getConnection(url, user, password)` opens a TCP socket connection to MySQL on port 3306.
> 3. JDBC uses the MySQL client/server protocol to perform authentication and send SQL statements.

### 10. How CRUD works?
> **Answer**: CRUD stands for **Create, Read, Update, Delete** — the four basic persistence operations:
> - **Create**: Handled by SQL `INSERT` via `PreparedStatement.executeUpdate()` (e.g. adding a new club or registration).
> - **Read**: Handled by SQL `SELECT` via `PreparedStatement.executeQuery()` returning a `ResultSet` (e.g. listing clubs or viewing event details).
> - **Update**: Handled by SQL `UPDATE` via `PreparedStatement.executeUpdate()` (e.g. modifying event date or venue).
> - **Delete**: Handled by SQL `DELETE` via `PreparedStatement.executeUpdate()` (e.g. removing a cancelled club or event).

### 11. What DAO means?
> **Answer**: **DAO (Data Access Object)** is an architectural design pattern that abstracts and isolates all database communication from the rest of the application. Business logic (Services) does not write SQL queries directly; instead, it calls DAO methods like `clubDAO.findById(id)` or `eventDAO.insert(event)`. This separates database persistence from business rules.

### 12. What a Service class does?
> **Answer**: A **Service class** encapsulates the core business logic, application rules, and workflow orchestration. It sits between the Servlet (Controller) and the DAO (Data Access):
> - Performs input and business validation.
> - Coordinates multiple DAO calls (e.g. `studentDAO.findOrCreate()` followed by `registrationDAO.registerClub()`).
> - Calculates aggregate business statistics (e.g. checking capacity limits, calculating seat availability).

### 13. What HttpSession does?
> **Answer**: HTTP is a stateless protocol (each request is independent). `HttpSession` is a mechanism provided by the Servlet container to maintain state across multiple requests from the same user. Tomcat assigns a unique session ID (`JSESSIONID`) stored in a browser cookie. On subsequent requests, the browser sends this cookie, allowing the Servlet to retrieve session data via `request.getSession()`.

### 14. How admin authentication works?
> **Answer**:
> 1. Administrator submits username and password via `admin/login.html` to `LoginServlet`.
> 2. `AdminService` queries `AdminDAO` to look up the user in MySQL.
> 3. `PasswordUtil.verifyPassword()` securely compares the credentials.
> 4. If valid, `request.getSession(true)` creates an authenticated session and stores the `Admin` object (`session.setAttribute("admin", admin)`).
> 5. Protected Servlets (`AdminDashboardServlet`, `AdminClubServlet`, `AdminEventServlet`) check `session.getAttribute("admin")`. If `null`, they immediately redirect to the login page.
> 6. On logout, `session.invalidate()` destroys the session.

### 15. How club registration works?
> **Answer**:
> 1. Student selects a club and fills in their contact and academic details on `club-register.html`.
> 2. Form submits data to `ClubRegistrationServlet`.
> 3. `RegistrationService` validates all input fields (email format, 10-digit phone, roll number).
> 4. `StudentDAO.findOrCreate()` checks if the student already exists in MySQL; if not, a new student record is inserted.
> 5. `RegistrationDAO.isAlreadyRegisteredClub()` checks the unique constraint `(student_id, club_id)`. If the student has already joined, registration is rejected.
> 6. If eligible, a new record is inserted into `club_registrations` with status `Approved`, and a confirmation message is returned.

### 16. How event registration works?
> **Answer**:
> 1. Student submits the event registration form to `EventRegistrationServlet`.
> 2. `RegistrationService` checks:
>    - Is the event status active/upcoming?
>    - Has the registration deadline passed? (`LocalDate.now().isAfter(event.getRegistrationDeadline())`)
>    - Is the event full? (`currentRegisteredCount >= event.getMaxParticipants()`)
>    - Is the student already registered for this event? (`registrationDAO.isAlreadyRegisteredEvent()`)
> 3. If all checks pass, the registration is committed to MySQL, incrementing the participant count and returning an instant seat confirmation.

### 17. Why PreparedStatement is used?
> **Answer**:
> 1. **SQL Injection Prevention**: With `PreparedStatement`, query parameters are represented by placeholders (`?`). The database compiles the SQL template first and treats all user input purely as literal data values, never as executable SQL code.
> 2. **Performance**: PreparedStatements are precompiled by the database engine, allowing repeated queries with different parameters to execute much faster.
> 3. **Type Safety**: Automatically converts Java data types (`Date`, `Timestamp`, `int`, `String`) into appropriate SQL types.

### 18. How the application is deployed on Tomcat?
> **Answer**:
> 1. The web application files are compiled and packaged into a Web Application Archive (`.war`) file following the Jakarta Servlet specification standard structure (`WEB-INF/web.xml`, `WEB-INF/classes`, `WEB-INF/lib`).
> 2. The `.war` file is copied to Tomcat's `webapps/` directory.
> 3. When Tomcat runs, its `HostConfig` background scanner detects `college-club-manager.war`, unpacks it, loads the classes into a dedicated WebAppClassLoader, initializes the Servlets, and serves the application at `http://localhost:8080/college-club-manager/`.

---

## 🔒 Security Features
- **SQL Injection Defense**: 100% parameterization via `PreparedStatement`.
- **Session Authentication & Guard**: Protected admin endpoints reject unauthenticated access with HTTP 302 redirects.
- **Credential Protection**: Passwords verified securely via SHA-256; zero hardcoded passwords in Java source code.
- **Double-Layer Validation**: Client-side feedback paired with strict, non-bypassable server-side Java validation.
- **Referential Integrity**: MySQL foreign keys enforce clean cascades and prevent orphaned records.
