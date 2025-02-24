# Evernorth Backend API Documentation
---

## Authentication APIs

Authentication in the Evernorth backend is a two-step process for both user registration and login. This ensures security by verifying user identity before storing details or granting access.

### Request Parameters

| Parameter   | Type   | Required | Description |
|------------|--------|----------|-------------|
| `fullName` | string | Yes      | The full name of the user. |
| `email`    | string | Yes      | The email address of the user. |
| `contact`  | string | Yes      | The contact number of the user. |
| `dob`      | string | Yes      | The date of birth of the user in `YYYY-MM-DD` format. |

## Sign-Up Process (2-Step)

The sign-up process consists of two steps:

1. **User Registration**: The user submits their details to initiate the registration process.
2. **Email Verification**: The user must verify their email using an OTP. Only after successful verification are the user details stored in the database, and a JWT token is issued.

### 1. User Registration

**Endpoint:**
```
POST /api/auth/register
```

**Request Example:**
```http
POST /api/v1/auth/register HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

**Request Body**
```json
{
    "fullName": "John Doe",
    "email": "kumbhamajaygoud2004@gmail.com",
    "contact": "1234567890",
    "dob": "1990-01-01"
}
```

**Response Example:**
```http
200 OK
```
```
Registration initiated. Please verify your email with the OTP sent.
```

**Note:** Upon successful email verification, a JWT token is issued.



### 2. Email Verification

**Endpoint:**
```
POST /api/auth/verify-email
```

**Request Parameters:**

| Parameter | Type   | Required | Description |
|-----------|--------|----------|-------------|
| `email`   | string | Yes      | The email address of the user. |
| `otp`     | string | Yes      | The OTP sent to the user's email. |

**Request Example:**
```http
POST /api/auth/verify-email HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

**Request Body:**
```json
{
    "email": "kumbhamajaygoud2004@gmail.com",
    "otp": "813155"
}
```

**Response Example:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKOTAwMSIsImlhdCI6MTczODQ3MzcwNiwiZXhwIjoxNzM4NTYwMTA2fQ.OLYEJtT8lXLiqAzxmNfBjQ4m7Tr3P289LcusvLjOt-I"
}
```

**Note:** The issued token must be used for authenticated requests.


## Login Process (2-Step)

The login process consists of two steps:

1. **Send OTP for Login**: The user requests an OTP to be sent to their registered email.
2. **Verify OTP and Retrieve Token**: The user enters the OTP to verify their identity and receive a JWT token for authentication.

### 3. Send OTP for Login

**Endpoint:**
```
POST /api/auth/login/send-otp
```

**Request Parameters:**

| Parameter | Type   | Required | Description |
|-----------|--------|----------|-------------|
| `email`   | string | Yes      | The registered email address of the user. |

**Example Request:**
```http
POST /api/auth/login/send-otp HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

**Request Body**
```
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



### 4. Verify OTP for Login

**Endpoint:**
```
POST /api/auth/login/verify-otp
```

**Request Parameters:**

| Parameter | Type   | Required | Description |
|-----------|--------|----------|-------------|
| `email`   | string | Yes      | The registered email address of the user. |
| `otp`     | string | Yes      | The OTP received via email. |

**Request Example:**
```http
POST /api/auth/login/verify-otp HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

**Request Body**
```json
{
    "email": "kumbhamajaygoud2004@gmail.com",
    "otp": "848734"
}
```

**Response Example:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKOTAwMSIsImlhdCI6MTczODQyMDY3OCwiZXhwIjoxNzM4NTA3MDc4fQ.YwEjcQuBGJpl-DshR1-ffM5iiVcqu6Il_cfchuiT_Ww"
}
```

**Note:** The token returned is a JWT token and is valid for exactly **24 hours (1 day)**. This token must be included in the Authorization header for accessing protected resources.

## Logout User

### Authentication
All requests require authentication via a **Bearer Token** in the request header.

### Authorization Header Example
```http
Authorization: Bearer <token>
```

**Endpoint:**
```
POST /api/auth/logout
```

### Description
The Evernorth Logout API allows users to securely log out by invalidating their authentication token. When this API is called with a valid Bearer Token in the request header, the backend will add that token to a blacklist. Once blacklisted, the token becomes unusable for any further API requests, effectively logging the user out.

Additionally, blacklisted tokens will be automatically cleaned up upon their expiration to optimize storage and security.

### Request Example:
```http
POST /api/auth/logout HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

### Response Example:
```http
200 OK
```

### Response Codes
- **200 OK** – User successfully logged out.
- **401 Unauthorized** – Invalid or missing token.

---

## Profile APIs

The Profile API allows users to retrieve, update, and verify their profile information. The API includes endpoints for fetching profile data, updating profile details, and updating the email address through a two-step verification process.

### Authentication
All requests require authentication via a **Bearer Token** in the request header. Unauthorized requests will be denied.

### Authorization Header Example
```http
Authorization: Bearer <token>
```


## Endpoints

### 1. Get Profile Data
**Endpoint:**
```
GET /api/users/profile
```
**Description:** Retrieves the profile data of the authenticated user.

**Request Example:**
```http
GET /api/users/profile HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

**Response Example:**
```json
{
    "memberId": "J9001",
    "fullName": "John Doe",
    "email": "kumbhamajaygoud2004@gmail.com",
    "contact": "1234567890",
    "dob": "1990-01-01",
    "createdAt": "2025-02-01T20:03:25.559487"
}
```

### 2. Update Profile Data
**Endpoint:**
```
PUT /api/users/profile
```
**Description:** Updates the user's profile information, except for the `email` and `memberId`, which cannot be changed. All fields are optional; only include the fields that need to be updated.

**Request Example:**
```
PUT /api/users/profile HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```
**Request Body:**
```json
{
    "fullName": "Ajay Kumbham",
    "contact": "9876543210",
    "dob": "2004-01-01"
}
```

**Response Example:**
```json
{
    "memberId": "J9001",
    "fullName": "Ajay Kumbham",
    "email": "kumbhamajaygoud2004@gmail.com",
    "contact": "9876543210",
    "dob": "2004-01-01",
    "createdAt": "2025-02-06T21:49:51.768617"
}
```



### 3. Email Update (Two-Step Process)
#### Step 1: Request Email Verification
**Endpoint:**
```
POST /api/users/profile/verify-email
```
**Description:** Sends a verification email to the new email address provided by the user.

**Request Example:**
```
POST /api/users/profile/verify-email HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```
**Request Body:**
```json
{
    "email": "kumbhamajaygoud22cs@student.vardhaman.org"
}
```

**Response Example:**
```
Verification email sent successfully
```

#### Step 2: Verify Email with OTP
**Endpoint:**
```
PUT /api/users/profile/verify-email
```
**Description:** Verifies the new email using an OTP received in the verification email.

**Request Example:**
```
PUT /api/users/profile/verify-email HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```
**Request Body:**
```json
{
    "email": "kumbhamajaygoud22cs@student.vardhaman.org",
    "otp": "375394"
}
```

**Response Example:**
```json
{
    "memberId": "K0401",
    "fullName": "Kumbham Ajay Goud",
    "email": "kumbhamajaygoud22cs@student.vardhaman.org",
    "contact": "9391942662",
    "dob": "2004-08-25",
    "createdAt": "2025-02-05T10:36:00.766806"
}
```

### OTP Expiration Details
- **For sign-up:** OTP expires in **5 minutes**.
- **For login:** OTP expires in **1 minute**.
- **After expiration, the OTP value becomes null.**


### Notes
- All requests require authentication using a Bearer Token.
- Updating a profile does not allow changes to `email` and `memberId`.
- Email updates require verification using an OTP sent to the new email.
- Ensure the `otp` is correctly entered within the validity period for successful verification.

---

## Payment APIs

The Evernorth Payment APIs allow users to manage their payment methods, including retrieving, adding, updating, and deleting stored payment details. It supports multiple payment types:

- **Credit Card**
- **Debit Card**
- **UPI**
- **Bank Transfer**

This API ensures secure transactions by masking sensitive information such as card numbers and bank account details.

### Authentication
All requests require authentication via a **Bearer Token** in the request header. Unauthorized requests will be denied.

### Authorization Header Example
```http
Authorization: Bearer <token>
```

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

### 2. Add a New Payment Method

#### Endpoint

```http
POST /api/users/payments
```

#### Description

Adds a new payment method for the user. The request body should only contain relevant fields for the specified payment type. In the response, all fields will be present, but non-applicable ones will be set to `null`.

#### Example Requests & Responses

#### I.Adding a Credit Card

**Request:**
```http
POST /api/users/payments HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```
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

#### II.Adding a Debit Card

**Request:**
```http
POST /api/users/payments HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```

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

#### III.Adding a UPI Payment Method

**Request:**
```http
POST /api/users/payments HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```

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

#### IV.Adding a Bank Transfer

**Request:**
```http
POST /api/users/payments HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```

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

#### Response Codes

- **201 Created** – Payment method added successfully.
- **400 Bad Request** – Invalid input format.
- **401 Unauthorized** – Invalid or missing token.

### 3. Update a Payment Method

#### Endpoint

```http
PUT /api/users/payments/{paymentType}
```

#### Description

Updates an existing payment method. Only applicable fields for the specific payment type should be included.
The `paymentType` itself cannot be updated.

#### Example Requests & Responses for Each Payment Type

#### I. Updating a Credit Card

**Request:**
```
PUT /api/users/payments/creditcard HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
    "expiryDate": "2026-07-31",
    "cardType": "MasterCard"
}
```

**Response:**
```json
{
    "paymentType": "creditcard",
    "maskedCardNumber": "**3456",
    "upiId": null,
    "nameOnCard": "John Doe",
    "expiryDate": "2026-07-31",
    "cardType": "MasterCard",
    "accountHolderName": null,
    "maskedBankAccountNumber": null,
    "ifscCode": null
}
```

#### II. Updating a Debit Card

**Request:**
```
PUT /api/users/payments/debitcard HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
    "expiryDate": "2026-10-15",
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
    "expiryDate": "2026-10-15",
    "cardType": "MasterCard",
    "accountHolderName": null,
    "maskedBankAccountNumber": null,
    "ifscCode": null
}
```

#### III. Updating a UPI Payment Method

**Request:**
```
PUT /api/users/payments/upi HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
    "upiId": "john.new@upi"
}
```

**Response:**
```json
{
    "paymentType": "upi",
    "maskedCardNumber": null,
    "upiId": "john.new@upi",
    "nameOnCard": null,
    "expiryDate": null,
    "cardType": null,
    "accountHolderName": null,
    "maskedBankAccountNumber": null,
    "ifscCode": null
}
```

#### IV. Updating a Bank Transfer

**Request:**
```
PUT /api/users/payments/banktransfer HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
    "accountHolderName": "John D.",
    "ifscCode": "WXYZ9876543"
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
    "accountHolderName": "John D.",
    "maskedBankAccountNumber": "**7890",
    "ifscCode": "WXYZ9876543"
}
```

#### Response Codes for PUT Requests

- **200 OK** – Payment method updated successfully.
- **400 Bad Request** – Invalid request format.
- **401 Unauthorized** – Invalid or missing token.
- **404 Not Found** – Payment method not found.

---

### 4.Delete a Payment Method

#### Endpoint

```http
DELETE  /api/users/payments/{paymentType}
```

#### Description

Deletes a saved payment method based on its type.

#### Request Example

```
DELETE /api/users/payments/upi HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

#### Response Codes

- **200 OK** – Payment method deleted successfully.
- **401 Unauthorized** – Invalid or missing token.
- **404 Not Found** – Payment method not found.


#### Notes

- Each user can store one payment method per type.
- `POST` and `PUT` requests must only include applicable fields; missing fields will be `null` in the response.
- Masked details (card numbers, bank accounts) ensure security.
- Bearer token authentication is mandatory for all requests.

Here is the "Get User Addresses" section in the same format as your existing documentation:

---

## Address APIs

The Address API allows users to store, retrieve, update, and remove addresses associated with their account. It provides a structured way to handle user address data, ensuring consistency and security across various applications such as delivery services, billing, and user profiles.

Users can add new addresses, fetch their saved addresses, modify existing ones, or delete them when no longer needed. Each operation requires authentication via a Bearer token, and the API returns appropriate HTTP status codes to indicate the success or failure of the request.


### Authentication
All requests require authentication via a **Bearer Token** in the request header. Unauthorized requests will be denied.

### Authorization Header Example
```http
Authorization: Bearer <token>
```

## Request Parameters
| Parameter       | Type   | Required | Description |
|---------------|--------|----------|-------------|
| `addressLabel` | String | Yes      | Unique label for the address (e.g., Home, Work). |
| `addressLine1` | String | Yes      | Primary address line. |
| `addressLine2` | String | No       | Secondary address line (optional). |
| `city`         | String | Yes      | City name. |
| `state`        | String | Yes      | State name. |
| `zipCode`      | String | Yes      | Postal code. |
| `landmark`     | String | No       | Nearby landmark for easy identification (optional). |

---
## Endpoints

### 1. Get All Addresses
**Endpoint:**
```
GET /api/users/addresses
```
**Description:** Retrieves a list of all saved addresses for the authenticated user.

**Request Example:**
```
GET /api/users/addresses HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

**Response Example:**
```json
[
    {
        "addressLabel": "Home",
        "addressLine1": "123 Main St",
        "addressLine2": "Apt 4B",   
        "city": "New York",
        "state": "NY",
        "zipCode": "10001",
        "landmark": "Near Central Park" 
    }
]
```

### 2. Add a New Address
**Endpoint:**
```
POST /api/users/addresses
```
**Description:** Adds a new address for the authenticated user. All fields except `addressLine2` and `landmark` are required.

**Request Example:**
```
POST /api/users/addresses/home HTTP/1.1
Host:localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```
**Request Body:**
```json
{
    "addressLabel": "Home",
    "addressLine1": "123 Main St",
    "addressLine2": "Apt 4B",   
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "landmark": "Near Central Park" 
}
```

**Response Example:**
```json
{
    "addressLabel": "Home",
    "addressLine1": "123 Main St",
    "addressLine2": "Apt 4B",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "landmark": "Near Central Park"
}
```

### 3. Update an Address
**Endpoint:**
```
PUT /api/users/addresses/{addressLabel}
```
**Description:** Updates an existing address identified by `addressLabel`. All fields are optional; only include the fields that need to be updated.

**Request Example:**
```
PUT /api/users/addresses/home HTTP/1.1
Host:localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```
**Request Body (Example - Updating City and Landmark Only):**
```json
{
    "city": "Hyderabad",
    "landmark": "Near Central Vista"
}
```

**Response Example:**
```json
{
    "addressLabel": "Home",
    "addressLine1": "123 Main St",
    "addressLine2": "Apt 4B",
    "city": "Hyderabad",
    "state": "NY",
    "zipCode": "10001",
    "landmark": "Near Central Vista"
}
```

### 4. Delete an Address
**Endpoint:**
```
DELETE /api/users/addresses/{addressLabel}
```
**Description:** Deletes an address associated with the given `addressLabel`.

**Request Example:**
```
DELETE /api/users/addresses/Home HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

**Response:**
- **Status:** `200 OK`
- **Body:** No response body.



## Notes
- All requests require authentication using a Bearer Token.
- For updating an address, only include fields that need to be changed.
- `addressLine2` and `landmark` are optional fields across all requests.
- `addressLabel` should be unique per user.

---
## Health Conditions API

The Health Conditions API provides endpoints to manage user health records, including retrieving, adding, updating, and deleting health conditions.

### Authentication
All requests require authentication via a **Bearer Token** in the request header. Unauthorized requests will be denied.

### Authorization Header Example
```http
Authorization: Bearer <token>
```

## Request Parameters
| Parameter         | Type   | Required | Description |
|------------------|--------|----------|-------------|
| `recordNo`       | Integer | Yes      | Unique identifier for the health record. |
| `healthCondition` | String | Yes      | Name of the health condition. |
| `description`     | String | No       | Additional details about the condition (optional). |


## Endpoints

### 1. Get All Health Records
**Endpoint:**
```
GET /api/users/health-records
```
**Description:** Retrieves a list of all saved health records for the authenticated user.

**Request Example:**
```
GET /api/users/health-records HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

**Response Example:**
```json
[
    {
        "recordNo": 1,
        "healthCondition": "Diabetes Type 2",
        "description": "Diagnosed in 2020, under medication"
    }
]
```

### 2. Add a New Health Record
**Endpoint:**
```
POST /api/users/health-records
```
**Description:** Adds a new health record for the authenticated user. The `description` field is optional.

**Request Example:**
```
POST /api/users/health-records HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```
**Request Body:**
```json
{
    "healthCondition": "Diabetes Type 2",
    "description": "Diagnosed in 2020, under medication"
}
```

**Response Example:**
```json
{
    "recordNo": 1,
    "healthCondition": "Diabetes Type 2",
    "description": "Diagnosed in 2020, under medication"
}
```

### 3. Update a Health Record
**Endpoint:**
```
PUT /api/users/health-records/{recordNo}
```
**Description:** Updates an existing health record identified by `recordNo`. All fields are optional; only include the fields that need to be updated.

**Request Example:**
```
PUT /api/users/health-records/1  HTTP/1.1
Host: localhost:8080 
Authorization: Bearer <token>
Content-Type: application/json
```
**Request Body (Example - Updating Health Condition and Description):**
```json
{
    "healthCondition": "Type 1 Diabetes",
    "description": "Type 1 diabetes with regular medication"
}
```

**Response Example:**
```json
{
    "recordNo": 1,
    "healthCondition": "Type 1 Diabetes",
    "description": "Type 1 diabetes with regular medication"
}
```

### 4. Delete a Health Record
**Endpoint:**
```
DELETE /api/users/health-records/{recordNo}
```
**Description:** Deletes a health record associated with the given `recordNo`.

**Request Example:**
```
DELETE /api/users/health-records/1 HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

**Response:**
- **Status:** `200 OK`
- **Body:** No response body.



## Notes
- All requests require authentication using a Bearer Token.
- For updating a health record, only include fields that need to be changed.
- The `description` field is optional across all requests.

---
## Allergy Records APIs
The Allergy Records API provides endpoints to manage user allergy records, including retrieving, adding, updating, and deleting records.

### Authentication
All requests require authentication via a **Bearer Token** in the request header. Unauthorized requests will be denied.

### Authorization Header Example
```http
Authorization: Bearer <token>
```

## Request Parameters
| Parameter       | Type   | Required | Description |
|---------------|--------|----------|-------------|
| `recordNo`    | Integer | Yes      | Unique identifier for the allergy record. |
| `allergies`   | String  | Yes      | The name(s) of the allergens. |
| `description` | String  | No       | Additional details about the allergy (optional). |

## Endpoints

### 1. Get All Allergy Records
**Endpoint:**
```
GET /api/users/allergy-records
```
**Description:** Retrieves a list of all recorded allergies for the authenticated user.

**Request Example:**
```
GET /api/users/allergy-records HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

**Response Example:**
```json
[
    {
        "recordNo": 1,
        "allergies": "Peanuts",
        "description": "Severe allergic reaction, carries EpiPen" 
    }
]
```

---

### 2. Add a New Allergy Record
**Endpoint:**
```
POST /api/users/allergy-records
```
**Description:** Adds a new allergy record for the authenticated user. The `description` field is optional.

**Request Example:**
```
POST /api/users/allergy-records HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```
**Request Body:**
```json
{
    "allergies": "Peanuts",
    "description": "Severe allergic reaction, carries EpiPen" 
}
```

**Response Example:**
```json
{
    "recordNo": 1,
    "allergies": "Peanuts",
    "description": "Severe allergic reaction, carries EpiPen"
}
```

### 3. Update an Allergy Record
**Endpoint:**
```
PUT /api/users/allergy-records/{recordNo}
```
**Description:** Updates an existing allergy record identified by `recordNo`. All fields are optional; only include the fields that need to be updated.

**Request Example:**
```
PUT /api/users/allergy-records/1 HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```
**Request Body (Example - Updating Allergy Information Only):**
```json
{
    "allergies": "Peanuts, Shellfish"
}
```

**Response Example:**
```json
{
    "recordNo": 1,
    "allergies": "Peanuts, Shellfish",
    "description": "Severe allergic reactions, carries EpiPen"
}
```

### 4. Delete an Allergy Record
**Endpoint:**
```
DELETE /api/users/allergy-records/{recordNo}
```
**Description:** Deletes an allergy record associated with the given `recordNo`.

**Request Example:**
```
DELETE /api/users/allergy-records/1 HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

**Response:**
- **Status:** `200 OK`
- **Body:** No response body.



## Notes
- All requests require authentication using a Bearer Token.
- For updating an allergy record, only include fields that need to be changed.
- The `description` field is optional across all requests.


## Dependents APIs

The Dependents API provides endpoints to manage user dependents, including retrieving, adding, updating, and deleting records.

### Authentication
All requests require authentication via a **Bearer Token** in the request header. Unauthorized requests will be denied.

### Authorization Header Example
```http
Authorization: Bearer <token>
```

## Request Parameters
| Parameter       | Type   | Required | Description |
|---------------|--------|----------|-------------|
| `fullName`    | String  | Yes      | Full name of the dependent. If it contains spaces, replace them with `%20` in the URL. |
| `relation`    | String  | Yes      | Relationship of the dependent to the user. |
| `dob`         | String  | Yes      | Date of birth of the dependent in `YYYY-MM-DD` format. |
| `mobileNumber` | String  | No       | Mobile number of the dependent (optional). |
| `emailAddress` | String  | No       | Email address of the dependent (optional). |
| `emergencySosContact` | Boolean | No | Indicates if this dependent is an emergency contact (optional). |


## Endpoints

### 1. Get All Dependents
**Endpoint:**
```
GET /api/users/dependents
```
**Description:** Retrieves a list of all dependents for the authenticated user.

**Request Example:**
```
GET /api/users/dependents HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

**Response Example:**
```json
[
    {
        "fullName": "Jane Doe",
        "relation": "Spouse",
        "dob": "1992-05-15",
        "mobileNumber": "9876543210",
        "emailAddress": "jane.doe@example.com",
        "emergencySosContact": true
    }
]
```


### 2. Add a New Dependent
**Endpoint:**
```
POST /api/users/dependents
```
**Description:** Adds a new dependent for the authenticated user. 

**Request Example:**
```
POST /api/users/dependents HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```
**Request Body:**
```json
{
    "fullName": "Jane Doe",
    "relation": "Spouse",
    "dob": "1992-05-15",
    "mobileNumber": "9876543210",
    "emailAddress": "jane.doe@example.com",
    "emergencySosContact": true
}
```

**Response Example:**
```json
{
    "fullName": "Jane Doe",
    "relation": "Spouse",
    "dob": "1992-05-15",
    "mobileNumber": "9876543210",
    "emailAddress": "jane.doe@example.com",
    "emergencySosContact": true
}
```

### 3. Update a Dependent
**Endpoint:**
```
PUT /api/users/dependents/{fullName}
```
**Description:** Updates an existing dependent identified by `fullName`. All fields are optional; only include the fields that need to be updated.

**Note:** If `fullName` contains spaces, it should be replaced with `%20` in the request URL. This is because spaces are not allowed in URLs and `%20` represents a space in URL encoding.

**Request Example:**
```
PUT /api/users/dependents/Jane%20Doe HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json
```
**Request Body (Example - Updating Name Only):**
```json
{
    "fullName": "Rahul"
}
```

**Response Example:**
```json
{
    "fullName": "Rahul",
    "relation": "Spouse",
    "dob": "1992-05-15",
    "mobileNumber": "9876543210",
    "emailAddress": "jane.doe@example.com",
    "emergencySosContact": true
}
```

### 4. Delete a Dependent
**Endpoint:**
```
DELETE /api/users/dependents/{fullName}
```
**Description:** Deletes a dependent associated with the given `fullName`.

**Note:** If `fullName` contains spaces, use `%20` in the request URL to replace spaces.

**Request Example:**
```
DELETE /api/users/dependents/Jane%20Doe HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

**Response:**
- **Status:** `200 OK`
- **Body:** No response body.


## Notes
- All requests require authentication using a Bearer Token.
- For updating a dependent, only include fields that need to be changed.
- Spaces in `fullName` should be replaced with `%20` in the request URL to ensure proper encoding.

---






