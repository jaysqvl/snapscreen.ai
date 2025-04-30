# Project Overview
A free resume screening platform where useres can upload their resumes, upload or paste a job description and we give the user insights.

Core Technologies in this project are, React, Typescript, Next.js, TailwindCSS, ShadCN/UI, Java Spring Boot, PostgresSQL, AWS, Docker

## 1. Core Functionalities & Roadmap

### 1.1. User Authentication & Account Setup ✅ IMPLEMENTED
- Firebase authentication is set up with:
  - Email/password login implementation ✅
  - Google OAuth sign-in implementation ✅
  - GitHub sign-in UI added (not yet functional) ⚠️
  - Authentication redirects to dashboard when successful ✅
- Backend integration with PostgreSQL for user data storage ⚠️ NOT IMPLEMENTED

### 1.2. Dashboard & Navigation ✅ IMPLEMENTED
- **Top Navigation Bar** ✅
  - Implemented using custom components
  - Displays project name/logo, links to dashboard and process pages
  - Features user avatar and theme toggle
  - Sign-in button when user not authenticated

- **Sidebar** ✅
  - Implemented with SidebarProvider from shadcn/ui
  - Shows list of scans
  - "New Scan" button opens dialog

- **Creating a New Scan** ✅
  - Dialog opens from sidebar or empty state
  - Form with:
    - File upload option for resume
    - Text area for pasting resume
    - Text area for job description
  - UI implemented but backend processing not connected ⚠️

- **Main Content Area** ✅
  - Empty state with welcome message when no scan selected
  - ScanDisplay component renders when scan is selected
  - UI for displaying scan results implemented

### 1.3. Resume & Job Description Storage ⚠️ NOT IMPLEMENTED
- **File Storage** in AWS S3 for resumes is designed but not implemented
- **Database**: PostgreSQL (via AWS RDS) not yet connected

### 1.4. ATS Scoring & Analysis ✅ UI IMPLEMENTED (BACKEND PENDING)
- Frontend UI for displaying all the following checks is implemented:
  - **Contact Info Check**: verify presence of email, phone, name ✅
  - **Section Headings**: ensure Education, Experience, Skills, etc. ✅
  - **Job Title Match**: look for exact or close match of job title ✅
  - **Date Formatting** consistency in work/education history ✅
  - **Education Check**: confirm presence of relevant degree or credentials ✅
  - **File Type** check/guidance (PDF vs. DOCX) ✅
  - **Hard & Soft Skills**: parse job description for keywords and verify presence ✅
  - **Formatting Quick Checks**: basic layout, margin, or font consistency ✅
- Backend processing for these checks is not yet implemented ⚠️

### 1.5. Display of Scan Results ✅ UI IMPLEMENTED (BACKEND PENDING)
- UI for the following is implemented but not connected to backend:
  - Overall match score display ✅
  - Detailed breakdown of all checks with pass/fail status ✅
  - Missing keywords highlights ✅

### 1.6. Payment & Monetization (Future) ⚠️ NOT IMPLEMENTED
- **Stripe Integration** not started
- User plan management not implemented

### 1.7. Advanced Features (Post-MVP) ⚠️ NOT IMPLEMENTED
- **AI-Powered Suggestions**: not started
- **Version Control**: not started
- **Team/Recruiter Access**: not started
- **Analytics & Reporting**: not started

### 1.8. Technical Process Documentation Page ✅ IMPLEMENTED
- Dedicated page explaining the technical aspects of the platform:
  - Overview of the tech stack ✅
  - Resume parser explanation ✅
  - API architecture documentation ✅
  - Scoring algorithms breakdown ✅
- Implemented with tabs and accordion components for organization

## 2. Proposed Tech Stack

### 2.1. Frontend ✅ IMPLEMENTED
- **React + TypeScript** ✅
  - Type safety and maintainability
  - Modern ecosystem, widely adopted
- **UI Framework** ✅
  - Shadcn/UI in tailwindcss successfully implemented
  - Component library being used effectively
- **Deployment**: 
  - Build your React app and serve it via AWS S3 + CloudFront, or host on Vercel/Netlify (and link to your backend).

### 2.2. Backend
- **Java + Spring Boot**
  - Annotated controllers for REST endpoints (e.g., `@RestController`).
  - Spring Data JPA for straightforward database access to PostgreSQL.
  - Spring Security for OAuth2 if implementing Google login at the backend level.
- **Containerization**: Dockerize the Spring Boot app for consistent deployments.

### 2.3. Database
- **PostgreSQL** (AWS RDS)
  - Ideal for relational data (users, scans, job descriptions).
  - Widely used, strong résumé skill.

### 2.4. File Storage
- **AWS S3**
  - Store resume files (PDF/DOCX).
  - Maintain references (S3 URLs) in the PostgreSQL database.
  - Cost-effective and highly scalable.

### 2.5. Deployment & DevOps
- **Docker + AWS ECS/EKS**:
  - Run containerized Spring Boot services.
  - Scale horizontally if traffic grows.
- **CI/CD Pipeline** (GitHub Actions or GitLab CI):
  - Automated builds, tests, and push to AWS ECR (Elastic Container Registry).
  - Deployment triggers to ECS/EKS clusters.
- **Infrastructure Considerations**:
  - AWS CloudFront (CDN) or an Application Load Balancer for distributing requests.
  - IAM roles for secure resource access (S3, RDS).
  - Auto Scaling Groups to handle peak loads.