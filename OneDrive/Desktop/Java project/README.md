# Universum – University Course Registration System

A full-stack web application for managing university course registrations.

## Tech Stack

**Backend:** Java 17, Spring Boot 3.4, Spring Security (JWT), Spring Data JPA, PostgreSQL, Flyway, MapStruct, Lombok  
**Frontend:** Vue.js 3, Vite, Vue Router, Pinia, Axios

## Features

- **Students** can browse the course catalogue, enroll in open courses, and track their enrollment status
- **Professors** can create and manage courses, review student enrollment requests, approve or reject them
- **Admins** have full access to all resources
- JWT-based stateless authentication
- Role-based access control with Spring Security `@PreAuthorize`
- Database migrations with Flyway

## Getting Started

### Prerequisites
- Java 17+
- Node.js 18+
- Docker (for PostgreSQL)

### Backend

```bash
cd backend
docker compose up -d        # starts PostgreSQL
./mvnw spring-boot:run      # starts the API on :8080
```

### Frontend

```bash
cd frontend
npm install
npm run dev                 # starts the app on :5173
```

### Default Admin Account
- Username: `admin`
- Password: `admin123`

## API Endpoints

| Method | Endpoint | Access |
|--------|----------|--------|
| POST | /api/auth/register | Public |
| POST | /api/auth/login | Public |
| GET | /api/courses | Authenticated |
| POST | /api/courses | Professor, Admin |
| PUT | /api/courses/{id} | Professor (own), Admin |
| DELETE | /api/courses/{id} | Professor (own), Admin |
| GET | /api/enrollments/my | Student |
| POST | /api/enrollments/enroll/{courseId} | Student |
| PATCH | /api/enrollments/{id}/status | Professor, Admin |
| DELETE | /api/enrollments/withdraw/{courseId} | Student |
