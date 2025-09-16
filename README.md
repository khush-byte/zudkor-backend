# Zudkor Backend

This is the backend for the **Zudkor** service, a platform for managing users with authentication and role-based access control. 
Built using **Spring Boot**, **Spring Data JPA**, **Spring Security**, and **PostgreSQL**.  

---

## Table of Contents

- [Features](#features)  
- [Technologies](#technologies)  
- [Setup](#setup)  
- [Database](#database)  
- [Running the Application](#running-the-application)  
- [API Endpoints](#api-endpoints)  
- [Authentication](#authentication)  

---

## Features

- Register users with email and username validation.  
- Hash passwords using **BCrypt**.  
- Role-based authentication (Basic Auth initially).  
- List all users or retrieve a user by ID.  
- Basic error handling for duplicate entries.  

---

## Technologies

- Java 21  
- Spring Boot 3.x  
- Spring Data JPA  
- Spring Security  
- PostgreSQL  
- Lombok  
- Gradle  

---

## Setup

```bash
Clone the repository:
git clone https://github.com/yourusername/zudkor-backend.git
cd zudkor-backend
Create a PostgreSQL database and user:

sql:
CREATE DATABASE zudkor;
CREATE USER zudkor_user WITH PASSWORD 'yourpassword';
GRANT ALL PRIVILEGES ON DATABASE zudkor TO zudkor_user;
Configure application.properties or application.yml with your database credentials:

properties:
spring.datasource.url=jdbc:postgresql://localhost:5432/zudkor
spring.datasource.username=zudkor_user
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

Running the Application:
./gradlew bootRun
The backend will run by default on http://localhost:8080.

API Endpoints
1. Register a new user (no authentication required)

POST http://localhost:8080/api/users
Content-Type: application/json

Request Body:
{
  "username": "testuser",
  "email": "test@example.com",
  "passwordHash": "mypassword",
  "role": "CLIENT"
}

Response:
200 OK: Returns created user (password is hashed and hidden).
409 Conflict: Email or username already exists.

2. Get all users (requires Basic Auth)

GET /api/users
Authorization: Basic Auth (username and password of existing user)

Response:
json
Копировать код
[
  {
    "id": "uuid",
    "username": "testuser",
    "email": "test@example.com",
    "role": "CLIENT",
    "createdAt": "2025-09-15T12:31:35.848448",
    "lastLogin": null,
    "reputation": null
  }
]

3. Get user by ID (requires Basic Auth)

GET /api/users/{id}
Authorization: Basic Auth

Response:
200 OK: User found.
404 Not Found: User not found.

Authentication:
Initially uses Basic Auth via Spring Security.
Username and password from the database.
Passwords are hashed using BCrypt.
Registration endpoint (POST /api/users) is public.

Notes
Passwords are never returned in API responses.
All other endpoints require authentication.
