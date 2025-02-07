# Evernorth Backend API Documentation

## Authentication APIs

Authentication in the Evernorth backend is a two-step process for both user registration and login. This ensures security by verifying user identity before storing details or granting access.

---

## Sign-Up Process (2-Step)

The sign-up process consists of two steps:

1. **User Registration**: The user submits their details to initiate the registration process.
2. **Email Verification**: The user must verify their email using an OTP. Only after successful verification are the user details stored in the database, and a JWT token is issued.

### 1. User Registration

This endpoint is used to initiate the user registration process. Upon calling this API, an OTP is sent to the user's email for verification.

**Endpoint:**

```
POST /api/auth/register
```

**Request Parameters:**

- `fullName` (string, required): The full name of the user.
- `email` (string, required): The email address of the user.
- `contact` (string, required): The contact number of the user.
- `dob` (string, required): The date of birth of the user in `YYYY-MM-DD` format.

**Example Request:**

```http
POST /api/v1/auth/register HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
    "fullName": "John Doe",
    "email": "kumbhamajaygoud2004@gmail.com",
    "contact": "1234567890",
    "dob": "1990-01-01"
}
```

**Response:**

```http
200 OK
```

```
Registration initiated. Please verify your email with the OTP sent.
```

**Note:** Upon successful email verification, a JWT token is issued.

---

### 2. Email Verification

This endpoint verifies the OTP sent to the user's email. Only upon successful verification are the user details stored in the database, and a JWT token is issued for authentication.

**Endpoint:**

```
POST /api/auth/verify-email
```

**Request Parameters:**

- `email` (string, required): The email address of the user.
- `otp` (string, required): The OTP sent to the user's email.

**Example Request:**

```http
POST /api/v1/auth/verify-email HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
    "email": "kumbhamajaygoud2004@gmail.com",
    "otp": "813155"
}
```

**Response:**

```http
200 OK
```

```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKOTAwMSIsImlhdCI6MTczODQ3MzcwNiwiZXhwIjoxNzM4NTYwMTA2fQ.OLYEJtT8lXLiqAzxmNfBjQ4m7Tr3P289LcusvLjOt-I"
}
```

**Note:** The issued token must be used for authenticated requests.

---

## Login Process (2-Step)

The login process consists of two steps:

1. **Send OTP for Login**: The user requests an OTP to be sent to their registered email.
2. **Verify OTP and Retrieve Token**: The user enters the OTP to verify their identity and receive a JWT token for authentication.

### 3. Send OTP for Login

This endpoint is used to send an OTP to the registered email for login authentication.

**Endpoint:**

```
POST /api/auth/login/send-otp
```

**Request Parameters:**

- `email` (string, required): The registered email address of the user.

**Example Request:**

```http
POST /api/v1/auth/login/send-otp HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
    "email": "kumbhamajaygoud2004@gmail.com"
}
```

**Response:**

```http
200 OK
```

```
OTP sent successfully
```

---

### 4. Verify OTP for Login

This endpoint verifies the OTP entered by the user and returns a JWT token that is valid for authentication.

**Endpoint:**

```
POST /api/auth/login/verify-otp
```

**Request Parameters:**

- `email` (string, required): The registered email address of the user.
- `otp` (string, required): The OTP received via email.

**Example Request:**

```http
POST /api/v1/auth/login/verify-otp HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
    "email": "kumbhamajaygoud2004@gmail.com",
    "otp": "848734"
}
```

**Response:**

```http
200 OK
```

```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKOTAwMSIsImlhdCI6MTczODQyMDY3OCwiZXhwIjoxNzM4NTA3MDc4fQ.YwEjcQuBGJpl-DshR1-ffM5iiVcqu6Il_cfchuiT_Ww"
}
```

**Note:** The token returned is a JWT token and is valid for exactly **24 hours (1 day)**. This token must be included in the Authorization header for accessing protected resources.

---

## Payment APIs

The Evernorth backend supports multiple payment methods, including credit card, debit card, UPI, and bank transfer. These APIs allow users to securely store their payment details.

### 5. Add a Payment Method

**Endpoint:**

```
POST /api/users/payments
```

**Authorization:**

- Requires Bearer Token in the header.

**Request Parameters:**

- `paymentType` (string, required): Payment type (`creditcard`, `debitcard`, `upi`, `banktransfer`).
- `cardNumber` (string, optional): Card number (required for `creditcard` and `debitcard`).
- `nameOnCard` (string, optional): Name on the card.
- `expiryDate` (string, optional): Expiry date (`YYYY-MM-DD`).
- `cardType` (string, optional): Card type (`VISA`, `MasterCard`).
- `upiId` (string, optional): UPI ID (required for `upi`).
- `accountHolderName` (string, optional): Bank account holder's name (required for `banktransfer`).
- `bankAccountNumber` (string, optional): Bank account number.
- `ifscCode` (string, optional): IFSC code.

**Example Requests:** (for all payment methods) [To be added]

