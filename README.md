# Let's Play API

RESTful CRUD API built with **Spring Boot 3**, **MongoDB**, and **JWT authentication**.  
Manages Users and Products with role-based access control (Admin / User).

---

## Tech Stack

| Layer      | Technology                        |
|------------|-----------------------------------|
| Framework  | Spring Boot 3.2                   |
| Database   | MongoDB 7 (Docker)                |
| Security   | Spring Security + JWT (jjwt 0.12) |
| Build tool | Maven                             |
| Java       | 17+                               |

---

## Project Structure

```
src/main/java/ma/zone01/letsplay/
├── LetsPlayApplication.java          # Entry point
├── config/
│   ├── SecurityConfig.java           # Spring Security + CORS + JWT filter chain
│   └── DataInitializer.java          # Auto-creates admin on first startup
├── controller/
│   ├── AuthController.java           # POST /api/auth/register & /login
│   ├── ProductController.java        # /api/products  (CRUD)
│   └── UserController.java           # /api/users     (admin only)
├── dto/
│   ├── request/                      # RegisterRequest, LoginRequest, ProductRequest, UpdateUserRequest
│   └── response/                     # AuthResponse, UserResponse, ApiResponse
├── exception/
│   ├── GlobalExceptionHandler.java   # Catches all errors → clean JSON responses
│   ├── ConflictException.java        # 409
│   ├── ForbiddenException.java       # 403
│   └── ResourceNotFoundException.java# 404
├── model/
│   ├── User.java                     # id, name, email, password (hidden), role
│   └── Product.java                  # id, name, description, price, userId
├── repository/
│   ├── UserRepository.java
│   └── ProductRepository.java
├── security/
│   ├── JwtUtils.java                 # Generate & validate JWT tokens
│   ├── JwtAuthFilter.java            # Reads token from every request header
│   └── JwtAuthEntryPoint.java        # Returns 401 JSON when not authenticated
└── service/
    ├── AuthService.java              # Register / Login logic
    ├── UserService.java              # CRUD for users
    ├── ProductService.java           # CRUD for products + ownership check
    └── UserDetailsServiceImpl.java   # Loads user from DB for Spring Security
```

---

## Setup

### 1. Prerequisites

- Docker & Docker Compose
- Java 17+
- Maven 3.8+

### 2. Clone

```bash
git clone https://learn.zone01oujda.ma/git/abalouri/lets-play
cd lets-play
```

### 3. Configure environment

```bash
cp .env.example .env
```

Edit `.env` with your values:

```env
# MongoDB
MONGO_ROOT_USER=admin
MONGO_ROOT_PASSWORD=secret123
MONGO_DB=letsplay
MONGO_PORT=27017

# App
APP_PORT=8080

# Default admin (auto-created on first startup)
ADMIN_NAME=Admin
ADMIN_EMAIL=admin@letsplay.ma
ADMIN_PASSWORD=admin123

# JWT — generate a strong secret:
#   openssl rand -base64 64
JWT_SECRET=bXlTdXBlclNlY3JldEtleUZvckpXVEF1dGhlbnRpY2F0aW9uMTIzNDU2Nzg5MA==
JWT_EXPIRATION_MS=86400000
```

> `.env` is in `.gitignore` — never commit it.

### 4. Start MongoDB

```bash
docker compose up -d
```

- MongoDB runs on port `27017`
- Mongo Express (GUI) runs on http://localhost:8081

### 5. Run the app

```bash
./mvnw spring-boot:run
```

Or with Maven directly:

```bash
mvn spring-boot:run
```

API starts on **http://localhost:8080**

On first startup, the app automatically creates the default admin from `.env`.

---

## Authentication

All protected endpoints require a JWT token in the header:

```
Authorization: Bearer <token>
```

Get a token by registering or logging in.

---

## API Endpoints

### Auth — Public

| Method | Endpoint             | Description     |
|--------|----------------------|-----------------|
| POST   | `/api/auth/register` | Create account  |
| POST   | `/api/auth/login`    | Login, get JWT  |

#### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@example.com","password":"password123"}'
```

Response `201`:
```json
{
  "token": "eyJhbGci...",
  "id": "64f1...",
  "name": "Alice",
  "email": "alice@example.com",
  "role": "ROLE_USER"
}
```

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"password123"}'
```

---

### Products

| Method | Endpoint             | Auth | Who          |
|--------|----------------------|------|--------------|
| GET    | `/api/products`      | No   | Public       |
| GET    | `/api/products/{id}` | No   | Public       |
| POST   | `/api/products`      | Yes  | Any user     |
| PUT    | `/api/products/{id}` | Yes  | Owner, Admin |
| DELETE | `/api/products/{id}` | Yes  | Owner, Admin |

#### Create product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"PlayStation 5","description":"Sony console","price":499.99}'
```

Response `201`:
```json
{
  "id": "64f2...",
  "name": "PlayStation 5",
  "description": "Sony console",
  "price": 499.99,
  "userId": "64f1..."
}
```

#### Update product
```bash
curl -X PUT http://localhost:8080/api/products/<id> \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"PS5 Digital","description":"Digital edition","price":399.99}'
```

#### Delete product
```bash
curl -X DELETE http://localhost:8080/api/products/<id> \
  -H "Authorization: Bearer <token>"
```

---

### Users — Admin only

| Method | Endpoint          | Description    |
|--------|-------------------|----------------|
| GET    | `/api/users`      | Get all users  |
| GET    | `/api/users/{id}` | Get one user   |
| PUT    | `/api/users/{id}` | Update user    |
| DELETE | `/api/users/{id}` | Delete user    |

```bash
curl http://localhost:8080/api/users \
  -H "Authorization: Bearer <admin_token>"
```

---

## Default Admin

On first startup, if no admin exists in the database, the app creates one automatically using the values from `.env`:

```
ADMIN_EMAIL=admin@letsplay.ma
ADMIN_PASSWORD=admin123
```

Login with these credentials to get an admin token.

---

## Error Responses

All errors return JSON:

```json
{ "message": "User not found with id: abc123" }
```

| Status | Meaning                         |
|--------|---------------------------------|
| 400    | Validation error                |
| 401    | Missing or invalid JWT          |
| 403    | Forbidden (wrong role/owner)    |
| 404    | Resource not found              |
| 409    | Conflict (duplicate email)      |
| 500    | Unexpected server error         |

---

## Security

- Passwords hashed with **BCrypt**
- JWT signed with **HMAC-SHA512**
- `password` field never returned in any response (`@JsonIgnore`)
- Input validation on all endpoints (`@Valid`)
- Role-based access control: Admin manages everything, users manage only their own products
- Global exception handler — no raw 5xx errors leak to the client
- CORS configured (open by default, restrict for production)
******
/home/abalouri/.local/share/docker/volumes/let-s-play_mongo_data/_data/
******