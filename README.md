# FoodBridge: Smart Food Donation and Redistribution Platform

> **"Good Things Deserve to Be Shared."**

FoodBridge is a full-stack social-impact web application built to connect restaurants, hotels, banquet halls, individual donors, NGOs, orphanages, and community shelters. The platform reduces food wastage and facilitates the efficient redistribution of surplus cooked meals, essential materials, and simulated charitable fundraising within local city zones (Chennai, Tamil Nadu).

---

## 1. Strict Technology Stack Compliance

In accordance with strict academic guidelines, FoodBridge utilizes **only** approved college technologies:

- **Backend:** Java Core, Spring Boot 3.3.4 (Java 21 bytecode target)
- **Persistence & ORM:** Spring Data JPA, Hibernate, MySQL 8.0
- **Security:** Spring Security 6 with session management and BCrypt password hashing
- **Frontend Views:** Server-Side Rendered Thymeleaf Templates
- **Styling:** CSS3 Custom Design System (Forest Green `#166534`, Fresh Green `#22C55E`, Off-White `#F8FAF7`) with Bootstrap 5
- **Client Logic:** Plain Vanilla JavaScript (ES6+) and Asynchronous Fetch API
- **Build System:** Apache Maven (`pom.xml` & `mvnw.cmd`)
- **Testing:** JUnit 5, Spring Boot Starter Test, Spring Security Test, MockMvc

> **Important:** No unapproved frameworks or databases (such as React, Angular, Vue, Node.js, Express, MongoDB, Python, PHP, or Firebase) are used.

---

## 2. Layered Project Architecture

```
foodbridge/
├── pom.xml                               # Maven project dependencies & plugins
├── mvnw.cmd                              # Maven wrapper execution script
├── README.md                             # Complete setup & operational documentation
├── schema.sql                            # Full MySQL DDL schema script
└── src/
    ├── main/
    │   ├── java/com/foodbridge/
    │   │   ├── FoodBridgeApplication.java # Spring Boot main entry point
    │   │   ├── config/                   # Spring Security & App configuration
    │   │   ├── controller/               # MVC & REST Controllers
    │   │   ├── dto/                      # Data Transfer Objects & form validation
    │   │   ├── entity/                   # Hibernate JPA Entities & Enum models
    │   │   ├── exception/                # Centralized exception handling
    │   │   ├── init/                     # DataInitializer (Chennai demo dataset)
    │   │   ├── repository/               # Spring Data JPA Repositories
    │   │   ├── security/                 # CustomUserDetails & UserDetailsService
    │   │   └── service/                  # Transactional business logic services
    │   └── resources/
    │       ├── application.properties    # Production/Local MySQL configuration
    │       ├── application.properties.example # Sample configuration template
    │       ├── static/
    │       │   ├── css/style.css         # Custom design system stylesheet
    │       │   └── js/main.js            # Vanilla JavaScript & notifications polling
    │       └── templates/                # Thymeleaf HTML5 templates
    │           ├── fragments/            # Layout, Navbar, Footer, and Alert fragments
    │           ├── auth/                 # Sign In, Registration, and Profile pages
    │           ├── food/                 # Food donation listings, details, and creation
    │           ├── materials/            # Material listings, details, and creation
    │           ├── cash/                 # Fundraising campaigns, details, and receipts
    │           ├── needs/                # Community needs browsing and creation
    │           ├── pickups/              # Coordination, scheduling, and handover tracking
    │           ├── dashboard/            # Donor and Organization dashboards
    │           ├── admin/                # Administrator moderation and audit logs
    │           ├── notifications/        # In-app notifications center
    │           ├── feedback/             # Ratings and review submission
    │           └── error/                # 403, 404, and 500 error views
    └── test/
        ├── java/com/foodbridge/          # JUnit 5 integration test suite
        └── resources/
            └── application.properties    # Test-scoped database configuration
```

---

## 3. Prerequisites & Environment Setup

1. **Java JDK 21+** (or Java 26 EA) installed and available in system PATH.
2. **MySQL Server 8.0** installed and running on port `3306`.
3. **Apache Maven 3.9+** (included via `./mvnw.cmd`).

---

## 4. MySQL Database Setup

1. Launch your MySQL command-line or MySQL Workbench and create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS foodbridge_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Verify MySQL credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/foodbridge_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=root
   ```
   *(You can also set environment variables `DB_USERNAME` and `DB_PASSWORD` or edit `application.properties` directly).*

---

## 5. How to Run the Application

Execute using the included Maven wrapper from the project root:

```powershell
.\mvnw.cmd spring-boot:run
```

Once started, open your web browser and navigate to:
**`http://localhost:8080`**

---

## 6. Pre-Configured Demo Accounts

For convenient evaluation, `DataInitializer` seeds test accounts with sample data in Chennai, Tamil Nadu:

| Role | Email Address | Password | Account Purpose |
|---|---|---|---|
| **Administrator** | `admin@foodbridge.org` | `Admin@123` | Platform oversight, verifying NGOs, approving campaigns, reviewing audit logs |
| **Restaurant Donor** | `saravana@demorestaurant.com` | `Donor@123` | Saravana Grand (T. Nagar), bulk surplus meals, accepting requests |
| **Individual Donor** | `rajesh@demodonor.com` | `Donor@123` | Rajesh Kumar (Anna Nagar), donating bakery items, dry rations, and books |
| **Recipient Org (Verified)** | `hope@demoorphanage.org` | `Org@123` | Hope Child Haven (Tambaram), requesting food, scheduling pickups |
| **Recipient Org (Verified)** | `karunai@demongo.org` | `Org@123` | Karunai Care NGO (Velachery), publishing community needs |
| **Recipient Org (Pending)** | `anbu@demoelderly.org` | `Org@123` | Anbu Elder Home (Guindy), testing admin verification workflow |

*(Tip: The Sign In page features a **1-Click Auto Fill** panel that instantly populates credentials for any selected role).*

---

## 7. Key System Modules & Workflows

### A. Food Donation Lifecycle
- **Status Progression:** `AVAILABLE` &rarr; `REQUESTED` &rarr; `ACCEPTED` &rarr; `PICKUP_SCHEDULED` &rarr; `COLLECTED` &rarr; `COMPLETED`.
- Also supports `REJECTED`, `WITHDRAWN`, and `EXPIRED`.
- Strict validation ensures safe collection deadlines are in the future and preparation timestamps are valid.
- Automatic backend expiration checking updates past-deadline listings to `EXPIRED`.

### B. Material Donation Module
- Donors share essential non-food bundles: clothes & footwear, winter blankets, school supplies, textbooks, dry rations, and hygiene kits.
- Quality conditions classified as `NEW`, `LIKE_NEW`, `GOOD`, or `USED`.

### C. Cash Donation & Fundraising Module
- Verified recipient organizations create fundraising campaigns with targets in INR and deadlines.
- Campaigns require administrator review before going live.
- Clearly labeled **educational demo payment mode** (`DEMO_SIMULATION`) simulates confirmed contributions with preset buttons (₹100, ₹250, ₹500, ₹1000, ₹2500, or custom amounts).
- Increases campaign `raisedAmount` and generates an official contribution summary receipt with unique reference IDs (`FB-DEMO-...`).

### D. Community Needs Module
- Organizations publish community requirements with urgency levels (`URGENT`, `HIGH`, `MEDIUM`, `LOW`).
- Visual progress bars track target vs. fulfilled quantities.

### E. Location-Based Matching
- City and pincode filtering allows local discovery across Chennai zones (T. Nagar `600017`, Mylapore `600004`, Anna Nagar `600040`, Adyar `600020`, Velachery `600042`, Tambaram `600045`, Guindy `600032`).

### F. Pickup Coordination & Handover
- After a donor accepts a request, either party coordinates date/time, vehicle instructions, and collection address.
- Recipient confirms collection (`COLLECTED`); donor confirms final delivery handover (`HANDED_OVER`), which closes the request and triggers a feedback reminder.

### G. In-App Notifications & Audit Trail
- Stored in MySQL with real-time unread counter badge updates via AJAX.
- Audit trail logs all critical actions (registrations, verifications, approvals, cancellations).

---

## 8. REST API Endpoints

| Endpoint | Method | Description |
|---|---|---|
| `/api/auth/me` | GET | Retrieve authenticated user profile and role |
| `/api/food` | GET | List available food listings with city/pincode/keyword filters |
| `/api/food/{id}` | GET | Get food listing details |
| `/api/materials` | GET | List available material donations |
| `/api/materials/{id}` | GET | Get material listing details |
| `/api/campaigns` | GET | List approved fundraising campaigns |
| `/api/campaigns/{id}` | GET | Get campaign details |
| `/api/needs` | GET | List open community needs |
| `/api/admin/stats` | GET | Get platform metrics & analytics |
| `/api/notifications/unread-count` | GET | Retrieve user unread notification count |
| `/api/notifications/read/{id}` | POST | Mark notification as read |
| `/api/notifications/read-all` | POST | Mark all notifications as read |

---

## 9. Running Automated Tests

Run the full JUnit 5 test suite using Maven:

```powershell
.\mvnw.cmd test
```

The test suite validates:
1. Registration & password hashing (`UserAuthenticationTest`)
2. Duplicate email and unauthorized admin registration prevention (`UserAuthenticationTest`)
3. Food listing creation, quantity, and deadline validation (`FoodDonationServiceTest`)
4. Complete donation workflow from request to pickup and handover (`DonationWorkflowTest`)
5. Campaign approvals and simulated demo transactions (`CashCampaignServiceTest`)
6. Role-based URL security and authorization enforcement (`SecurityAuthorizationTest`)

---

## 10. License & Academic Disclaimer
Developed as a Full-Stack Engineering Capstone Platform. All demonstration transactions and fictional organizations are strictly for academic evaluation.
