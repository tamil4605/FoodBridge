FoodBridge: Smart Food Donation and Redistribution Platform

«"Good Things Deserve to Be Shared."»

FoodBridge is a full-stack social-impact web application designed to connect restaurants, hotels, individual donors, NGOs, orphanages, and community shelters. It aims to reduce food waste by simplifying the redistribution of surplus food, essential materials, and simulated charitable fundraising.

Features

- Food Donations: Register and manage surplus food donations.
- Material Donations: Donate essential items such as clothes, books, school supplies, and hygiene kits.
- Cash Fundraising: Create fundraising campaigns with simulated educational demo payments.
- Donor–Recipient Connectivity: Help donors and organizations coordinate donations and collections.
- Location-Based Discovery: Filter listings by city and pincode.
- Pickup Coordination: Manage collection scheduling and handover.
- Notifications and Feedback: Support in-app notifications, reviews, and activity tracking.
- Administration: Support organization verification and campaign moderation.
- MySQL Integration: Persist application data using a relational database.

Technology Stack

- Frontend: HTML, CSS, JavaScript, Thymeleaf, Bootstrap
- Backend: Java, Spring Boot
- Database: MySQL
- ORM and Persistence: Spring Data JPA, Hibernate
- Build Tool: Maven
- Testing: JUnit 5, Spring Boot Test, Spring Security Test, MockMvc

The project follows the technology restrictions specified for the academic project.

Prerequisites

- Java JDK compatible with the project
- MySQL Server
- Maven or the included Maven wrapper
- Visual Studio Code or another Java IDE

Setup and Run

1. Clone the repository

git clone https://github.com/tamil4605/FoodBridge.git
cd FoodBridge

2. Create the database

Run this SQL in MySQL Workbench:

CREATE DATABASE IF NOT EXISTS foodbridge_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

3. Configure database credentials

Configure "DB_USERNAME" and "DB_PASSWORD" as environment variables for your local MySQL account. Check "src/main/resources/application.properties" for the required configuration.

Do not commit database passwords, API keys, or other secrets to GitHub.

4. Run the application

From the project root in the VS Code terminal:

.\mvnw.cmd spring-boot:run

5. Open FoodBridge

Once Spring Boot starts successfully, visit:

http://localhost:8082

The port may differ if your local configuration changes.

Project Objective

To reduce food wastage, simplify the donation process, and connect available resources with organizations and communities that need support.

Academic Disclaimer

This project is developed for academic purposes. Fundraising payments are simulated for demonstration and are not real financial transactions.

Author

Tamil Arasu M
Computer Science and Engineering Student

License

For educational and academic use.
