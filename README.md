FoodBridge – Food Donation & Redistribution Platform
📌 About the Project
FoodBridge is a full-stack web application designed to connect food donors, restaurants, NGOs, orphanages, and recipient organizations. It aims to reduce food waste and support communities in need by simplifying the donation and redistribution process.
The platform supports surplus food donations, material donations, and cash fundraising campaigns.
✨ Features
- 🍱 Food Donations: Register and manage surplus food donations.
- 📦 Material Donations: Support donations of essential materials.
- 💰 Cash Fundraising: Support fundraising campaigns.
- 🤝 Donor–Recipient Connectivity: Connect donors with organizations in need.
- 🗄️ Database Integration: Store application data using MySQL.
- 🌐 Web Interface: Access project features through a web-based interface.
🛠️ Technologies Used
- Frontend: HTML, CSS, JavaScript, Thymeleaf
- Backend: Java, Spring Boot
- Database: MySQL
- ORM: Spring Data JPA, Hibernate
- Build Tool: Maven
⚙️ Prerequisites
Install the following before running the project:
- Java JDK
- Maven
- MySQL Server
- Visual Studio Code or another Java IDE
🚀 How to Run the Project
1. Clone the repository
git clone https://github.com/tamil4605/FoodBridge.git
cd FoodBridge
Replace "YOUR-USERNAME" with your GitHub username.
2. Configure MySQL
Create the database in MySQL:
CREATE DATABASE foodbridge_db;
Configure your database connection using environment variables:
- "DB_HOST" — database host, usually "localhost"
- "DB_PORT" — database port, usually "3306"
- "DB_NAME" — "foodbridge_db"
- "DB_USERNAME" — tamil4605
- "DB_PASSWORD" — your MySQL password
3. Run the application
Open the project folder in VS Code. In the terminal, set the database environment variables and run:
mvn spring-boot:run
4. Open the application
Visit:http://localhost:8082
The application will be available locally when Spring Boot starts successfully.
🎯 Project Objective
To provide a technology-based platform that helps reduce food waste, simplify donations, and connect available resources with organizations and communities that need support.
👨‍💻 Author
Tamil Arasu M
CSE Student

📄 License

This project is intended for educational and academic purposes.
