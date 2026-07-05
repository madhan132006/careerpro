# CareerPro AI - Intelligent Career Guidance System 🚀

CareerPro AI is an industry-level Full Stack application built as a final year project. It leverages Google Gemini AI to provide personalized career recommendations, resume analysis, custom learning roadmaps, and mock interview preparations.

## 🌟 Features

- **JWT Authentication & Security:** Secure login, registration, and role-based access.
- **AI Career Recommendations:** Analyzes user skills and interests to suggest top careers.
- **Resume Analyzer:** Upload PDF resumes for ATS scoring and improvement tips.
- **Skill Gap Analysis:** Highlights missing skills for a desired career path.
- **Learning Roadmaps:** Generates customized week-by-week learning plans.
- **Mock Interview Prep:** Dynamically creates Technical, HR, and Scenario-based interview questions with model answers.
- **Admin Dashboard:** Monitor platform analytics and manage users.

## 🛠️ Technology Stack

**Frontend:**
- HTML5, CSS3, Vanilla JavaScript (No React/Angular)
- Responsive Design & Glassmorphism UI
- Chart.js for Data Visualization
- Bootstrap Icons

**Backend:**
- Java 21, Spring Boot 3.2.x
- Spring MVC, Spring Data JPA, Spring Security (JWT)
- Google Gemini 1.5 Flash AI API (via WebClient)
- Apache PDFBox (Resume Parsing)
- Swagger OpenAPI Documentation

**Database:**
- MySQL 8.0

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- MySQL 8.0
- Google Gemini API Key

### Configuration
1. Open `backend/src/main/resources/application.yml`
2. Update the `gemini.api-key` with your valid API Key.
3. Update database credentials if needed.
4. Execute `database/schema.sql` in your MySQL database to create the schema and initial data.

### Running Locally (Without Docker)

1. **Start the Backend:**
```bash
cd backend
mvn clean spring-boot:run
```
The API will start at `http://localhost:8080`

2. **Access the Frontend:**
Since the frontend uses Vanilla HTML/JS/CSS, you can simply open `frontend/index.html` in your web browser, or use a local server like VS Code Live Server.

### Running with Docker Compose

1. Add your Gemini API key to your environment or replace it in `docker-compose.yml`.
2. Run the stack:
```bash
docker-compose up --build
```
This will start both the MySQL database (with auto-seeded schema) and the Spring Boot backend.

## 📖 API Documentation
Once the backend is running, access the Swagger UI at:
`http://localhost:8080/swagger-ui.html`

## 👥 Default Accounts (Testing)
- **Admin:** admin@careerpro.ai / Admin@123
- **User:** john@example.com / Password123
"# careerpro" 
