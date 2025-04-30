# SnapScreen.AI

SnapScreen.AI is a resume screening platform designed to empower students and job seekers in the competitive job market.

## Overview

SnapScreen.AI helps users optimize their resumes for specific job applications by:
- Analyzing resumes against job descriptions
- Providing ATS (Applicant Tracking System) compatibility scoring
- Offering keyword and skills matching recommendations
- Suggesting formatting and structure improvements

## Features

### Implemented Features ✅
- **User Authentication**
  - Email/password login
  - Google OAuth sign-in
  - Authentication flow with Firebase
- **Dashboard & Navigation**
  - Top navigation bar with user avatar and theme toggle
  - Sidebar with scan list and "New Scan" functionality
  - Main content area with scan results display
- **UI Components**
  - Complete scan analysis interface
  - Resume upload functionality
  - Job description input
  - Results visualization
- **Technical Process Documentation**
  - Tech stack overview
  - Resume parser explanation
  - API architecture documentation
  - Scoring algorithms breakdown

### Pending Implementation ⚠️
- **Backend Processing**
  - Resume parsing and analysis
  - Skills extraction and matching
  - ATS compatibility checks
  - Score calculation
- **Database Integration**
  - PostgreSQL implementation
  - User data storage
  - Scan history and results
- **Storage Solutions**
  - AWS S3 for resume file storage
- **Advanced Features** (Post-MVP)
  - AI-powered suggestions
  - Version control
  - Team/recruiter access
  - Analytics and reporting
  - Payment and monetization (Stripe)

## Core Functionality

### Resume Analysis
The platform performs various checks on resumes including:
- **Contact Information**: Verifies presence of email, phone, name
- **Section Headings**: Ensures Education, Experience, Skills sections exist
- **Job Title Match**: Looks for exact or close match of job title
- **Date Formatting**: Checks consistency in work/education history
- **Education Verification**: Confirms presence of relevant degree or credentials
- **File Type Guidance**: Provides recommendations for optimal file formats
- **Skills Assessment**: Parses job description for keywords and verifies presence
- **Formatting Analysis**: Checks basic layout, margin, and font consistency

## Tech Stack

### Frontend
- **Framework**: Next.js 15.x with App Router
- **UI**: React 19.x with Tailwind CSS 4.x
- **Component Library**: Custom UI components built with Radix UI primitives (shadcn/ui)
- **Authentication**: NextAuth.js with Firebase Authentication
- **Styling**: TailwindCSS with class-variance-authority and clsx

### Backend
- **Framework**: Spring Boot 3.4.x (Java 21)
- **Authentication**: Firebase Admin SDK for token verification
- **Storage**: AWS S3 for resume storage
- **Database**: PostgreSQL with Spring Data JPA
- **Security**: Spring Security
- **Build Tool**: Maven

### DevOps & Deployment
- **Containerization**: Docker
- **Cloud Services**: AWS (S3, RDS, ECS/EKS)
- **CI/CD**: Future implementation with GitHub Actions
- **Infrastructure**: Planned AWS CloudFront, IAM roles, Auto Scaling

## Database Schema

The application uses a PostgreSQL database with the following core tables:
- **Users**: User account information
- **Scans**: Resume analysis sessions
- **Skills**: Hard and soft skills database
- **ScanSkills**: Mapping of skills found in resumes and job descriptions
- **ScanChecks**: Results of format and content checks
- **ScanRecommendations**: Improvement suggestions

Additional analytics tables:
- **ScanActivity**: User engagement metrics
- **CompanyApplications**: Statistics on company-specific applications

## API Structure

All API endpoints are available at `https://api.snapscreen.ai/api` with the following main routes:

- **Authentication**: Firebase-based authentication
- **User Management**: User information and role management
- **Resume Management**: Upload, retrieve, and delete resumes

## Project Structure

```
snapscreen.ai/
├── snapscreen/                 # Frontend Next.js application
│   ├── app/                    # App Router pages and layouts
│   │   ├── auth/               # Authentication-related pages
│   │   ├── dashboard/          # User dashboard
│   │   └── process/            # Resume processing workflow
│   ├── components/             # Reusable UI components
│   ├── hooks/                  # Custom React hooks
│   ├── lib/                    # Utility functions and configurations
│   └── public/                 # Static assets
│
└── backend/                    # Backend services
    └── snapscreen-api/         # Spring Boot REST API
        ├── src/main/java/com/snapscreen/snapscreen_api/
        │   ├── controller/     # REST API endpoints
        │   ├── service/        # Business logic
        │   ├── config/         # Application configuration
        │   ├── security/       # Authentication and authorization
        │   └── util/           # Utility classes
        └── src/main/resources/ # Configuration files
```

## Getting Started

### Prerequisites
- Node.js 20.x or higher
- Java 21 JDK
- PostgreSQL database
- AWS account (for S3 storage)
- Firebase project (for authentication)

### Frontend Setup
1. Navigate to the frontend directory:
   ```
   cd snapscreen
   ```
2. Install dependencies:
   ```
   npm install
   ```
3. Create a `.env.local` file with your environment variables:
   ```
   NEXT_PUBLIC_API_URL=http://localhost:8080
   NEXT_PUBLIC_FIREBASE_API_KEY=your_firebase_api_key
   NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=your_firebase_auth_domain
   # Add other Firebase config values
   ```
4. Start the development server:
   ```
   npm run dev
   ```

### Backend Setup
1. Navigate to the backend directory:
   ```
   cd backend/snapscreen-api
   ```
2. Create an `application.properties` file in `src/main/resources` with your configuration:
   ```
   # Database configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/snapscreen
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # AWS S3 configuration
   aws.s3.bucket=your_bucket_name
   aws.s3.region=your_aws_region
   
   # Firebase configuration
   firebase.credentials.path=/path/to/firebase-credentials.json
   ```
3. Build and run the application:
   ```
   ./mvnw spring-boot:run
   ```

## Firebase Setup
1. Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/)
2. Enable Authentication with Email/Password and Google providers
3. Generate service account credentials and save the JSON file
4. Configure the backend with the service account file path

## AWS S3 Setup
1. Create an S3 bucket for resume storage
2. Configure bucket permissions and CORS settings
3. Create an IAM user with appropriate permissions
4. Add AWS credentials to the backend configuration

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Resources

To help with development, here are some useful references:

- [OpenResume Parser](https://www.open-resume.com/resume-parser) - An excellent reference for resume parsing algorithms that provides insights into how to extract structured data from PDF resumes
- For UI components, refer to the Shadcn UI documentation in the project docs

Please reach out directly if you have any questions about the project direction or what needs to be worked on. Your contributions are valuable to making this platform better for everyone.

## License

This project is licensed under the [LICENSE](LICENSE) file in the repository.
