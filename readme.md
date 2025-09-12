# 🧠 Online Booking System
## Quick Start

A containerized Spring Boot application powered by PostgreSQL and Redis, designed for rapid development and testing.

---
## Core Functionality

- User Management: Registration, authentication, and profile management with JWT tokens
- Package System: Credit-based packages with country-specific pricing
- Class Booking: Real-time booking with capacity management
- Waitlist Management: Automatic waitlist handling when classes are full
- Distributed Locking: Redis-based locking for concurrent booking operations
- Credit System: Automatic credit deduction and refund mechanisms

## Tech Stack

- Backend: Spring Boot 3.5.5, Java 21
- Database: PostgreSQL
- Cache: Redis
- Security: Spring Security + JWT
- Documentation: SpringDoc OpenAPI 3
- Build Tool: Maven
- ORM: Spring Data JPA/Hibernate

## Quick Start
### 1.Clone the Repository
```
    git clone <your-repository-url>
    cd bookingsystem
```

### 2.Docker start

```
    docker-compose up -d
```

### 3.Database Setup
```
    docker exec -i booking-postgres psql -U postgres -d bookingsystem < ./init-db/01-schema.sql
    docker exec -i booking-postgres psql -U postgres -d bookingsystem < ./init-db/02-data.sql    
```
### 4.Run Spring Boot Application
```
    ./mvnw spring-boot:run  
```

## API Endpoints

### Authentication

- POST /api/auth/register - Register new user
- POST /api/auth/login - User login
- GET /api/auth/verify-email - Verify email address

### User Management

- GET /api/users/{userId}/profile - Get user profile
- POST /api/users/{userId}/change-password - Change password
- POST /api/users/request-reset - Request password reset
- POST /api/users/reset-password - Reset password
- POST /api/users/{userId}/deactivate - Deactivate user

### Package Management
- GET /api/packages - Get all active packages
- GET /api/packages/country/{countryId} - Get packages by country
- POST /api/packages/purchase/{userId} - Purchase a package
- GET /api/packages/user/{userId}/purchases - Get user purchases
- GET /api/packages/user/{userId}/active-purchases - Get active purchases

### Classes Management
- GET /api/classes/classes/available - Get available classes for user
- GET /api/classes/classes/country/{countryId} - Get classes by country

### Booking Management
- POST /api/bookings/book - Book a class
- POST /api/bookings/cancel/{bookingId} - Cancel booking
- GET /api/bookings/my-bookings - Get user bookings
- GET /api/bookings/my-confirmed-bookings - Get confirmed bookings




## Swagger UI
***Access Swagger UI at:*** http://localhost:8080/swagger-ui/index.html

***API Documentation at:*** http://localhost:8080/api-docs