# Application Test Guide

## Pre-requisites

Before testing, ensure you have the following services running:

1. **MySQL Database** (port 3306)
2. **Redis Server** (port 6379) - for rate limiting
3. **Environment Variables** configured

## Environment Setup

1. Copy the environment template:
   ```bash
   cp env.example .env
   ```

2. Configure the required environment variables in `.env`:
   ```bash
   # Database
   DB_USERNAME=your_db_username
   DB_PASSWORD=your_db_password
   
   # Email (for OTP functionality)
   MAIL_USERNAME=your-email@gmail.com
   MAIL_PASSWORD=your-app-specific-password
   
   # JWT Secret (generate a secure key)
   JWT_SECRET_KEY=your-very-long-and-secure-jwt-secret-key-at-least-256-bits
   
   # CORS (adjust for your frontend)
   CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
   ```

3. Generate a secure JWT secret key:
   ```bash
   openssl rand -base64 32
   ```

## Application Startup Test

1. **Build the application:**
   ```bash
   mvn clean compile
   ```

2. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Check application logs** for any errors during startup

## API Endpoint Tests

### 1. Health Check
```bash
curl -X GET http://localhost:8080/actuator/health
```

### 2. Registration Flow
```bash
# Step 1: Register user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "email": "test@example.com",
    "contact": "1234567890",
    "dob": "1990-01-01"
  }'

# Step 2: Verify email with OTP (check email for OTP)
curl -X POST http://localhost:8080/api/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "otp": "123456"
  }'
```

### 3. Login Flow
```bash
# Step 1: Send OTP for login
curl -X POST http://localhost:8080/api/auth/login/send-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com"
  }'

# Step 2: Verify OTP and login
curl -X POST http://localhost:8080/api/auth/login/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "otp": "123456"
  }'
```

### 4. Protected Endpoint Test
```bash
# Get user profile (requires authentication)
curl -X GET http://localhost:8080/api/users/profile \
  -H "Cookie: jwt_token=your-jwt-token-here"
```

### 5. Logout Test
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer your-jwt-token-here"
```

## Expected Results

### ✅ Success Indicators:
- Application starts without errors
- Database tables are created automatically
- Registration sends OTP email
- Login creates JWT cookie
- Protected endpoints require authentication
- Rate limiting works (429 status after too many requests)
- Logout clears JWT cookie

### ❌ Common Issues:
- **Database connection errors**: Check MySQL is running and credentials are correct
- **Redis connection errors**: Check Redis is running on port 6379
- **Email sending errors**: Check SMTP credentials and enable "less secure apps" for Gmail
- **JWT errors**: Ensure JWT_SECRET_KEY is properly set
- **CORS errors**: Check CORS_ALLOWED_ORIGINS configuration

## Security Verification

1. **JWT Token Security**: Tokens should be in HTTP-only cookies, not response body
2. **Rate Limiting**: Multiple rapid requests should return 429 status
3. **Input Validation**: Invalid data should return 400 status with validation errors
4. **Authentication**: Unauthenticated requests to protected endpoints should return 401
5. **Error Handling**: No sensitive information should be exposed in error responses

## Performance Test

```bash
# Test rate limiting (should fail after 100 requests in 15 minutes)
for i in {1..110}; do
  curl -X POST http://localhost:8080/api/auth/login/send-otp \
    -H "Content-Type: application/json" \
    -d '{"email": "test@example.com"}'
  echo "Request $i"
done
```

## Troubleshooting

### Application Won't Start:
1. Check all required environment variables are set
2. Verify MySQL and Redis are running
3. Check port 8080 is available
4. Review application logs for specific error messages

### Database Issues:
1. Ensure MySQL is running on port 3306
2. Check database credentials in environment variables
3. Verify database user has CREATE privileges

### Email Issues:
1. Check SMTP credentials
2. For Gmail, enable "App Passwords" and use app-specific password
3. Verify email service is not blocking the application

### Redis Issues:
1. Ensure Redis is running on port 6379
2. Check Redis connection in application logs
3. Verify Redis configuration in application.yml

## Success Criteria

✅ Application starts successfully  
✅ Database tables are created  
✅ Registration flow works  
✅ Login flow works  
✅ JWT tokens are in cookies  
✅ Protected endpoints require authentication  
✅ Rate limiting is functional  
✅ Logout clears cookies  
✅ No sensitive data in error responses  
✅ Input validation works  
✅ CORS is properly configured  

If all criteria are met, the application is working correctly and ready for production deployment! 