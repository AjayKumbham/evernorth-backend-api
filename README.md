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

## Overview
The Evernorth Payment APIs allow users to manage their payment methods, including retrieving, adding, updating, and deleting stored payment details. It supports multiple payment types:

- **Credit Card**
- **Debit Card**
- **UPI**
- **Bank Transfer**

This API ensures secure transactions by masking sensitive information such as card numbers and bank account details.

---

## Authentication
All requests require authentication via a **Bearer Token** in the request header. Unauthorized requests will be denied.

### Authorization Header Example
```http
Authorization: Bearer <token>
```

---

## Endpoints

### 1. Retrieve All Payment Methods

#### Endpoint
```http
GET /api/users/payments
```

#### Description
Retrieves a list of all stored payment methods for the authenticated user.

#### Request Example
```http
GET /api/users/payments HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

#### Response Example
```json
[
    {
        "paymentType": "creditcard",
        "maskedCardNumber": "**3456",
        "upiId": null,
        "nameOnCard": "John Doe",
        "expiryDate": "2025-12-31",
        "cardType": "VISA",
        "accountHolderName": null,
        "maskedBankAccountNumber": null,
        "ifscCode": null
    },
    {
        "paymentType": "debitcard",
        "maskedCardNumber": "**1234",
        "upiId": null,
        "nameOnCard": "John Doe",
        "expiryDate": "2026-08-31",
        "cardType": "MasterCard",
        "accountHolderName": null,
        "maskedBankAccountNumber": null,
        "ifscCode": null
    },
    {
        "paymentType": "upi",
        "maskedCardNumber": null,
        "upiId": "john.doe@upi",
        "nameOnCard": null,
        "expiryDate": null,
        "cardType": null,
        "accountHolderName": null,
        "maskedBankAccountNumber": null,
        "ifscCode": null
    },
    {
        "paymentType": "banktransfer",
        "maskedCardNumber": null,
        "upiId": null,
        "nameOnCard": null,
        "expiryDate": null,
        "cardType": null,
        "accountHolderName": "John Doe",
        "maskedBankAccountNumber": "**7890",
        "ifscCode": "ABCD0123456"
    }
]
```

#### Response Codes
- **200 OK** – Payment methods retrieved successfully.
- **401 Unauthorized** – Invalid or missing token.

---
### 2. Add a New Payment Method

### Endpoint

**POST** `/api/users/payments`

## Description

Adds a new payment method for the user. The request body should only contain relevant fields for the specified payment type. In the response, all fields will be present, but non-applicable ones will be set to `null`.

## Example Requests & Responses

### Adding a Credit Card

**Request Body:**
```json
{
    "paymentType": "creditcard",
    "maskedCardNumber": "**3456",
    "nameOnCard": "John Doe",
    "expiryDate": "2025-12-31",
    "cardType": "VISA"
}
```

**Response:**
```json
{
    "paymentType": "creditcard",
    "maskedCardNumber": "**3456",
    "upiId": null,
    "nameOnCard": "John Doe",
    "expiryDate": "2025-12-31",
    "cardType": "VISA",
    "accountHolderName": null,
    "maskedBankAccountNumber": null,
    "ifscCode": null
}
```

### Adding a Debit Card

**Request Body:**
```json
{
    "paymentType": "debitcard",
    "maskedCardNumber": "**1234",
    "nameOnCard": "John Doe",
    "expiryDate": "2026-08-31",
    "cardType": "MasterCard"
}
```

**Response:**
```json
{
    "paymentType": "debitcard",
    "maskedCardNumber": "**1234",
    "upiId": null,
    "nameOnCard": "John Doe",
    "expiryDate": "2026-08-31",
    "cardType": "MasterCard",
    "accountHolderName": null,
    "maskedBankAccountNumber": null,
    "ifscCode": null
}
```

### Adding a UPI Payment Method

**Request Body:**
```json
{
    "paymentType": "upi",
    "upiId": "john.doe@upi"
}
```

**Response:**
```json
{
    "paymentType": "upi",
    "maskedCardNumber": null,
    "upiId": "john.doe@upi",
    "nameOnCard": null,
    "expiryDate": null,
    "cardType": null,
    "accountHolderName": null,
    "maskedBankAccountNumber": null,
    "ifscCode": null
}
```

### Adding a Bank Transfer

**Request Body:**
```json
{
    "paymentType": "banktransfer",
    "accountHolderName": "John Doe",
    "maskedBankAccountNumber": "**7890",
    "ifscCode": "ABCD0123456"
}
```

**Response:**
```json
{
    "paymentType": "banktransfer",
    "maskedCardNumber": null,
    "upiId": null,
    "nameOnCard": null,
    "expiryDate": null,
    "cardType": null,
    "accountHolderName": "John Doe",
    "maskedBankAccountNumber": "**7890",
    "ifscCode": "ABCD0123456"
}
```

## Response Codes

- **201 Created** – Payment method added successfully.
- **400 Bad Request** – Invalid input format.
- **401 Unauthorized** – Invalid or missing token.



