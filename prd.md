# Project Overview
A free resume screening platform where useres can upload their resumes, upload or paste a job description and we give the user insights.

## 1. Core Functionalities & Roadmap

### 1.1. User Authentication & Account Setup
- **Google OAuth** for secure sign-up and login.
- Basic user profile: track name, email, and unique user ID in the database.
- Potential future expansions: password login, account management pages.

### 1.2. Dashboard & Navigation
- **Notion-style** UI with a sidebar listing the most recent resume scans.
- A “New Scan” button that creates a prompt to:
  - Upload/select a resume file (PDF/DOCX).
  - Paste a full job description (text + job title).
- Each new job scan appears as a separate item in the sidebar.

### 1.3. Resume & Job Description Storage
- **File Storage** in AWS S3 for resumes.
- **Database**: PostgreSQL (via AWS RDS) to store:
  - Basic resume metadata (filename, S3 path).
  - Job description text/title.
  - Associations between user, resume, and job posting.

### 1.4. ATS Scoring & Analysis (MVP)
- **Contact Info Check**: verify presence of email, phone, name.
- **Section Headings**: ensure Education, Experience, Skills, etc.
- **Job Title Match**: look for exact or close match of job title.
- **Date Formatting** consistency in work/education history.
- **Education Check**: confirm presence of relevant degree or credentials.
- **File Type** check/guidance (PDF vs. DOCX).
- **Hard & Soft Skills**: parse job description for keywords (e.g., “Java,” “teamwork”) and verify presence in resume text.
- **Formatting Quick Checks**: basic layout, margin, or font consistency.

### 1.5. Display of Scan Results
- Generate an overall match **score** or percentage.
- Detail each check with a pass/fail or needs-attention tag.
- Show missing keywords in highlight form.

### 1.6. Payment & Monetization (Future)
- **Stripe Integration** for paid features or subscriptions.
- User plan management: free tier vs. premium plan with advanced features.
- Store payment history, invoices, and subscription status.

### 1.7. Advanced Features (Post-MVP)
- **AI-Powered Suggestions**: auto-recommending phrases or skill keywords.
- **Version Control**: compare changes between multiple resume uploads.
- **Team/Recruiter Access**: multi-user accounts for HR teams.
- **Analytics & Reporting**: usage metrics, average match scores, etc.

## 2. Proposed Tech Stack

### 2.1. Frontend
- **React + TypeScript**
  - Type safety and maintainability.
  - Modern ecosystem, widely adopted.
- **UI Framework**
  -  Shadcn/UI 
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


# Doc


# Current File Structure
