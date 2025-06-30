# JWT Authentication for Microservices

This project now includes JWT-based authentication for all microservices. The authentication system consists of a dedicated auth-service and JWT validation in all other services.

## Architecture

- **Auth Service** (Port 8084): Handles user registration, login, and JWT token generation
- **Customer Service** (Port 8081): Protected by JWT authentication
- **Product Service** (Port 8083): Protected by JWT authentication  
- **Order Service** (Port 8082): Protected by JWT authentication

## Getting Started

### 1. Start the Services

```bash
# Start all services
mvn clean install
mvn spring-boot:run -pl auth-service
mvn spring-boot:run -pl customer-service
mvn spring-boot:run -pl product-service
mvn spring-boot:run -pl order-service
```

### 2. Register a User

```bash
curl -X POST http://localhost:8084/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "role": "USER"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "testuser",
  "role": "USER"
}
```

### 3. Login

```bash
curl -X POST http://localhost:8084/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 4. Access Protected APIs

Use the JWT token in the Authorization header:

```bash
# Get all customers
curl -X GET http://localhost:8081/customers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get all products
curl -X GET http://localhost:8083/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get all orders
curl -X GET http://localhost:8082/orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## API Endpoints

### Auth Service (Port 8084)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Login and get JWT token |
| POST | `/auth/validate` | Validate JWT token |
| GET | `/auth/health` | Health check |

### Protected Services

All endpoints in the following services require a valid JWT token:

- **Customer Service**: `/customers/**`
- **Product Service**: `/products/**`
- **Order Service**: `/orders/**`, `/orders-items/**`

## User Roles

- **USER**: Can access all basic operations
- **ADMIN**: Can access all operations (future enhancement for admin-only features)

## Configuration

### JWT Settings

All services use the same JWT secret for token validation. Update the secret in production:

```properties
jwt.secret=your-super-secret-jwt-key-that-should-be-at-least-256-bits-long-for-security
jwt.expiration=86400000
```

### Database Configuration

Each service has its own database configuration. The auth service uses `auth_db` while others use `ecommerce`.

## Security Features

1. **JWT Token Validation**: All protected endpoints validate JWT tokens
2. **Role-based Access**: Different roles can have different permissions
3. **Password Encryption**: Passwords are encrypted using BCrypt
4. **Stateless Authentication**: No server-side session storage
5. **Token Expiration**: Tokens expire after 24 hours by default

## Error Handling

- **401 Unauthorized**: Invalid or missing JWT token
- **403 Forbidden**: Valid token but insufficient permissions
- **400 Bad Request**: Invalid request data

## Testing

### Test User Registration

```bash
# Register an admin user
curl -X POST http://localhost:8084/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "email": "admin@example.com",
    "role": "ADMIN"
  }'
```

### Test Protected Endpoints

```bash
# Get JWT token
TOKEN=$(curl -s -X POST http://localhost:8084/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}' | jq -r '.token')

# Use token to access protected endpoint
curl -X GET http://localhost:8081/customers \
  -H "Authorization: Bearer $TOKEN"
```

## Troubleshooting

1. **Token Expired**: Re-login to get a new token
2. **Invalid Token**: Check the Authorization header format
3. **Database Connection**: Ensure MySQL is running and accessible
4. **Port Conflicts**: Ensure all services are running on different ports

## Production Considerations

1. **Change JWT Secret**: Use a strong, unique secret in production
2. **HTTPS**: Use HTTPS in production for secure token transmission
3. **Token Refresh**: Implement token refresh mechanism for better UX
4. **Rate Limiting**: Add rate limiting to prevent abuse
5. **Logging**: Configure proper logging for security events 