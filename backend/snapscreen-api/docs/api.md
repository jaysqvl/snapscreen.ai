# Resume Screening Platform API Documentation (MVP)

This document outlines the essential API endpoints for the Resume Screening Platform MVP backend implemented in Spring Boot.

## Base URL

All API endpoints are relative to:

```
https://api.snapscreen.ai/api
```

## Authentication

The application uses Firebase Authentication. Authentication is handled on the client side, and the resulting Firebase ID token should be included in the Authorization header for authenticated requests:

```
Authorization: Bearer <firebase_id_token>
```

The backend will verify this token with Firebase and extract the user ID and roles. Users can have the following roles:
- `ROLE_USER`: Basic user role (all authenticated users)
- `ROLE_ADMIN`: Administrative privileges

## Error Handling

All endpoints follow a standard error response format:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid input parameters",
  "timestamp": "2023-07-01T12:00:00Z"
}
```

## Endpoints

### Authentication

Authentication is handled client-side using Firebase. The backend doesn't provide traditional authentication endpoints.

#### Authentication Health Check

```
GET /api/public/auth/health
```

Response:
```json
{
  "status": "active",
  "message": "Firebase authentication is being handled client-side. Use ID tokens from Firebase in the Authorization header for authenticated requests."
}
```

### User Management

#### Get Current User

Retrieves information about the currently authenticated user.

```
GET /api/users/me
```

Response:
```json
{
  "uid": "firebase-user-id",
  "email": "user@example.com",
  "displayName": "John Doe",
  "phoneNumber": "+15551234567",
  "emailVerified": true,
  "disabled": false,
  "metadata": {
    "creationTime": "2023-07-01T12:00:00Z",
    "lastSignInTime": "2023-07-10T15:30:00Z"
  }
}
```

#### Get User by ID (Admin only)

```
GET /api/users/{uid}
```

Response: Same format as Get Current User

#### List All Users (Admin only)

```
GET /api/users?maxResults=1000
```

Parameters:
- `maxResults` (optional): Maximum number of users to return (default 1000)

Response:
```json
[
  {
    "uid": "user-id-1",
    "email": "user1@example.com",
    "displayName": "User One",
    // Other user properties...
  },
  {
    "uid": "user-id-2",
    "email": "user2@example.com",
    "displayName": "User Two",
    // Other user properties...
  }
]
```

#### Set Admin Role (Admin only)

```
PUT /api/users/{uid}/admin?isAdmin=true
```

Parameters:
- `isAdmin`: Boolean flag to grant (true) or revoke (false) admin privileges

Response: 200 OK if successful

#### Set Custom Claims (Admin only)

```
PUT /api/users/{uid}/claims
```

Request:
```json
{
  "premium": true,
  "accessLevel": 2,
  "customField": "value"
}
```

Response: 200 OK if successful

### Resume Management

#### Upload Resume

```
POST /api/resumes/upload
Content-Type: multipart/form-data

file: [binary file data]
```

Response:
```json
{
  "objectKey": "resumes/user-id/filename.pdf",
  "url": "https://presigned-s3-url-to-access-the-file",
  "filename": "filename.pdf"
}
```

#### Get User's Resume

```
GET /api/resumes
```

Response:
```json
{
  "objectKey": "resumes/user-id/filename.pdf",
  "url": "https://presigned-s3-url-to-access-the-file",
  "filename": "filename.pdf"
}
```

#### Check if User Has Resume

```
GET /api/resumes/exists
```

Response:
```json
{
  "hasResume": true
}
```

#### Get Pre-signed URL for Resume

```
GET /api/resumes/url?objectKey=resumes/user-id/filename.pdf
```

Response:
```json
{
  "url": "https://presigned-s3-url-to-access-the-file"
}
```

#### Delete User's Resume

```
DELETE /api/resumes
```

Response: 200 OK if successful

## Data Models

### User (Firebase UserRecord)
- uid: string
- email: string
- displayName: string
- phoneNumber: string (optional)
- photoURL: string (optional)
- emailVerified: boolean
- disabled: boolean
- metadata: object
  - creationTime: string (ISO datetime)
  - lastSignInTime: string (ISO datetime)
- customClaims: object (additional user properties)

### Resume File
- objectKey: string (S3 object key)
- url: string (pre-signed URL for access)
- filename: string (original filename)

## Rate Limiting

API requests are limited to:
- 100 requests per minute for authenticated users
- 20 requests per minute for unauthenticated endpoints
