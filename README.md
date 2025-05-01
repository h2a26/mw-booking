# MW Booking System

A robust, concurrent, and country-aware booking system built with Java, Spring Boot, and Redis. This platform allows users to register, log in, purchase country-specific packages, and book classes with advanced waitlist and concurrency handling.

---

## 🚀 Features

### 1. User Module
- **Registration & Login:** Secure user registration and authentication. Registration requires email verification (mocked for local/dev).
- **Profile Management:** Get profile, change password, reset password, etc.
- **Email Verification:** Integrated mock service for email verification.

### 2. Package Module
- **Country-Specific Packages:** Users can view and purchase packages available for their country. Each package provides a set number of credits, a price, and an expiry date.
- **Package Management:** Users can view their purchased packages, including status (active/expired) and remaining credits.

### 3. Schedule/Booking Module
- **Class Schedules:** Users can view available class schedules for their country.
- **Booking:** Users can book classes using credits from a valid package for the class's country.
- **Credit Deduction:** Credits are deducted upon successful booking.
- **Cancellation & Refund:** Users can cancel bookings. Credits are refunded if cancellation is made 4+ hours before class start.
- **Waitlist:** If a class is full, users can join a FIFO waitlist. Promotion from waitlist is automatic when slots open.
- **Concurrent Booking:** Uses distributed locks and Redis cache to prevent overbooking under high concurrency.
- **No Overlapping Bookings:** Users cannot book overlapping classes.
- **Waitlist Promotion:** When a slot opens, the first user in the waitlist is promoted and credits are deducted.
- **Waitlist Refund:** If a waitlisted user is not promoted when the class ends, no credits are deducted.

### 4. Admin & Infrastructure
- **Spring Boot Actuator:** For health checks and monitoring.
- **PostgreSQL:** Main data storage.
- **Redis:** Used for caching and FIFO waitlist queue.
- **Dockerized:** Ready for deployment with Docker and Docker Compose.

---

## Business Rules

- Packages, classes, and bookings are country-specific.
- Credits are only deducted when a booking is confirmed (not for waitlist).
- Waitlist is strictly FIFO and consistent between DB and Redis.
- Bookings and waitlists are concurrency-safe.
- Email and payment services are mocked for development.

---

## ⚙️ Tech Stack

- **Java 21**
- **Spring Boot 3.4.5**
- **Spring Data JPA**
- **PostgreSQL**
- **Redis**
- **Lombok**
- **Docker & Docker Compose**

---

## Project Structure

```
src/main/java/org/codigo/middleware/mwbooking/
├── api/           # API input/output DTOs
├── commons/       # Common enums and utilities
├── config/        # Spring, Redis, and security configs
├── entity/        # JPA entities (User, Booking, Class_, Package_, WaitList, etc.)
├── exceptions/    # Custom exception classes
├── repository/    # Spring Data repositories
├── scheduler/     # Scheduled tasks (e.g., waitlist refunds)
├── security/      # JWT and security filters
├── service/       # Service interfaces and implementations
│   ├── cache/     # Redis and cache-related services
│   └── impl/      # Main business logic implementations
├── utils/         # Utility classes
└── MwBookingApplication.java # Main Spring Boot entry point
```

---

## ⚡ Setup & Run

### Prerequisites
- Java 21
- Maven
- Docker & Docker Compose

### Environment Variables
Configure your database and Redis settings in `src/main/resources/application.yml`.

### Build & Run

```bash
# Build the application
./mvnw clean package

# Start PostgreSQL and Redis via Docker Compose
docker-compose up -d

# Run the Spring Boot application
./mvnw spring-boot:run
```


## 🔌 API Access

The MW Booking System exposes RESTful APIs for all core modules:

| Module                | Base Endpoint         | Description                              |
|-----------------------|-----------------------|------------------------------------------|
| **User Module**       | `/booking/user/*`     | User profile, password, and account actions |
| **Package Module**    | `/booking/package/*`  | Country-specific package listing & purchase |
| **Booking Module**    | `/booking/bookings/*` | Class schedules, bookings, cancellations, waitlists |
| **Authentication**    | `/booking/auth/*`     | User registration, login, and token management |

## 📘 API Documentation

Explore and test all endpoints using the integrated Swagger UI:

➡️ **[Swagger UI – http://localhost:8080/booking/swagger-ui/index.html](http://localhost:8080/booking/swagger-ui/index.html)**

Swagger provides detailed information about request/response schemas, parameters, and available operations for each endpoint.






## Concurrency & Waitlist Design

- **Booking is protected by distributed locks** to ensure no overbooking.
- **Waitlist is managed in both Redis (for speed, FIFO) and DB (for persistence).**
- **Promotion from waitlist** is atomic and deducts credits only on promotion.
- **Credit refunds** are handled by schedule and cancellation policies.

---

## Mock Services

- **Email Verification:** Uses a mock email sender for development.
- **Payment Processing:** Uses mock payment card and charge methods.

---

## Contribution

1. Fork the repo.
2. Create your feature branch (`git checkout -b feature/fooBar`).
3. Commit your changes (`git commit -am 'Add some fooBar'`).
4. Push to the branch (`git push origin feature/fooBar`).
5. Create a new Pull Request.

---

## License

This project is for educational and demonstration purposes.

---

## Contact

For questions or support, please contact the maintainer.

---

**Note:**  
This README reflects the latest business requirements and code structure, including robust waitlist and concurrency handling. If you add new features, update this file accordingly.
