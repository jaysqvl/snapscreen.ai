# Resume Screening Platform API Documentation (MVP)

This document outlines the essential API endpoints for the Resume Screening Platform MVP backend implemented in Spring Boot.

## Base URL

All API endpoints are relative to:

```
https://api.snapscreen.ai/v1
```

## Authentication

Most endpoints require authentication using JWT tokens. Include the token in the Authorization header:

```
Authorization: Bearer <jwt_token>
```

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

#### Register User

```
POST /auth/register
```

Request:
```json
{
  "email": "user@example.com",
  "password": "securePassword123",
  "name": "John Doe"
}
```

Response:
```json
{
  "userId": "12345",
  "email": "user@example.com",
  "name": "John Doe",
  "createdAt": "2023-07-01T12:00:00Z"
}
```

#### Login User

```
POST /auth/login
```

Request:
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "userId": "12345"
}
```

#### Google OAuth Login

```
POST /auth/google
```

Request:
```json
{
  "idToken": "google-id-token-from-frontend"
}
```

Response: Same as standard login

### Scans

#### Get All Scans

Retrieves a list of user's scans for the sidebar.

```
GET /scans
```

Parameters:
- `page` (optional): Page number (default 0)
- `size` (optional): Page size (default 10)
- `sort` (optional): Sort field (default "createdAt,desc")

Response:
```json
{
  "content": [
    {
      "id": "scan-123",
      "name": "Software Engineer - Google",
      "createdAt": "2023-07-01T12:00:00Z",
      "score": 85,
      "jobTitle": "Software Engineer",
      "company": "Google",
      "status": "COMPLETED"
    },
    {
      "id": "scan-124",
      "name": "Data Scientist - Microsoft",
      "createdAt": "2023-06-28T10:30:00Z",
      "score": 72,
      "jobTitle": "Data Scientist",
      "company": "Microsoft",
      "status": "COMPLETED"
    }
  ],
  "totalElements": 15,
  "totalPages": 2,
  "number": 0,
  "size": 10
}
```

#### Create New Scan

```
POST /scans
```

Request:
```json
{
  "name": "Software Engineer - Google",
  "jobDescription": "We are looking for a Software Engineer with experience in...",
  "resumeText": "Optional plain text of resume if directly pasted"
}
```

Alternatively, use multipart/form-data to upload a resume file:

```
POST /scans
Content-Type: multipart/form-data

name: Software Engineer - Google
jobDescription: We are looking for a Software Engineer with experience in...
resumeFile: [binary file data]
```

Response:
```json
{
  "id": "scan-125",
  "name": "Software Engineer - Google",
  "createdAt": "2023-07-01T12:00:00Z",
  "status": "PROCESSING",
  "estimatedTimeRemaining": 30
}
```

#### Get Scan Details

Retrieves detailed information about a specific scan for the main content area.

```
GET /scans/{scanId}
```

Response:
```json
{
  "id": "scan-123",
  "name": "Software Engineer - Google",
  "createdAt": "2023-07-01T12:00:00Z",
  "updatedAt": "2023-07-01T12:02:00Z",
  "status": "COMPLETED",
  "score": 85,
  "resumeUrl": "https://s3.amazonaws.com/snapscreen/resumes/user-12345/resume-123.pdf",
  "jobTitle": "Software Engineer",
  "company": "Google",
  "jobDescription": "We are looking for a Software Engineer with experience in...",
  "checks": {
    "contactInfo": {
      "passed": true,
      "score": 100,
      "details": {
        "name": true,
        "email": true,
        "phone": true,
        "location": true
      }
    },
    "sectionHeadings": {
      "passed": true,
      "score": 100,
      "details": {
        "education": true,
        "experience": true,
        "skills": true,
        "projects": false
      }
    },
    "jobTitleMatch": {
      "passed": true,
      "score": 90,
      "details": {
        "exactMatch": false,
        "closeMatch": true,
        "suggestedTitle": "Senior Software Engineer"
      }
    },
    "dateFormatting": {
      "passed": true,
      "score": 100,
      "details": {
        "consistent": true,
        "format": "MM/YYYY"
      }
    },
    "educationCheck": {
      "passed": true,
      "score": 100,
      "details": {
        "degreePresent": true,
        "relevantField": true
      }
    },
    "fileTypeCheck": {
      "passed": true,
      "score": 100,
      "details": {
        "format": "PDF",
        "isPreferred": true
      }
    },
    "skillsMatch": {
      "passed": false,
      "score": 75,
      "details": {
        "hardSkills": {
          "matched": ["Java", "Spring Boot", "AWS", "React"],
          "missing": ["Kubernetes", "GraphQL", "CI/CD"]
        },
        "softSkills": {
          "matched": ["Communication", "Teamwork"],
          "missing": ["Leadership", "Problem-solving"]
        }
      }
    },
    "formattingCheck": {
      "passed": true,
      "score": 90,
      "details": {
        "margins": "adequate",
        "font": "consistent",
        "layout": "clean"
      }
    }
  },
  "recommendations": [
    "Add Kubernetes experience to your skills section",
    "Include problem-solving examples in your work experience",
    "Add a projects section to highlight relevant work"
  ]
}
```

#### Delete Scan

```
DELETE /scans/{scanId}
```

Response:
```json
{
  "message": "Scan successfully deleted"
}
```

### User progress

#### Get User Dashboard Data

Provides basic overview data for the user dashboard/progress section.

```
GET /users/me/dashboard
```

Response:
```json
{
  "totalScans": 15,
  "avgScore": 78.5,
  "recentScans": [
    {
      "id": "scan-123",
      "name": "Software Engineer - Google",
      "score": 85,
      "createdAt": "2023-07-01T12:00:00Z" 
    },
    {
      "id": "scan-124",
      "name": "Data Scientist - Microsoft",
      "score": 72,
      "createdAt": "2023-06-28T10:30:00Z"
    }
  ],
  "skillsAnalysis": {
    "strongSkills": ["React", "Java", "TypeScript"],
    "weakSkills": ["AWS", "GraphQL", "Docker"]
  }
}
```

#### Get User Profile

```
GET /users/me
```

Response:
```json
{
  "id": "12345",
  "email": "user@example.com",
  "name": "John Doe",
  "createdAt": "2023-06-01T10:00:00Z",
  "totalScans": 15
}
```

## Data Models

### User
- id: string
- email: string
- name: string
- createdAt: datetime
- updatedAt: datetime

### Scan
- id: string
- userId: string
- name: string
- jobTitle: string
- company: string (optional)
- jobDescription: string
- resumeText: string (optional)
- resumeUrl: string (optional)
- status: enum (PROCESSING, COMPLETED, FAILED)
- score: integer
- checks: object (containing all check results)
- recommendations: array of strings
- createdAt: datetime
- updatedAt: datetime

## Rate Limiting

API requests are limited to:
- 100 requests per minute for authenticated users
- 20 requests per minute for unauthenticated endpoints
