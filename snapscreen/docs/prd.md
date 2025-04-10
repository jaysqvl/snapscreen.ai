# Project Overview
A free resume screening platform where useres can upload their resumes, upload or paste a job description and we give the user insights.

Core Technologies in this project are, React, Typescript, Next.js, TailwindCSS, ShadCN/UI, Java Spring Boot, PostgresSQL, AWS, Docker

## 1. Core Functionalities & Roadmap

### 1.1. User Authentication & Account Setup
- I already set up a firebase project and enabled email/password and google login
  - We just need to build out the front-end of the auth page
  - connect it to the project
  - fully set up the signing in without google (email/password)
  - fully set up the signing in with google
  - Ensure this will work with the later postgres database for when we'll be tying user accounts to the resume scan history they have and their settings
  - After this basic setup we'll add github auth

### 1.2. Dashboard & Navigation (Expanded)
- **Top Navigation Bar**  
  - Could use **Navigation Menu** or **Menubar** from shadcn/ui.  
  - Displays project name/logo on the left, optional “global actions” (e.g., “Download”), and a user **Avatar** (with a **Dropdown Menu** for account settings/logout) on the right.
  - Typically remains fixed at the top.

- **Sidebar**  
  - Use the **Sidebar** component to display a vertical list of existing resume scans.
  - A **New Scan** button (prominently placed at the top or bottom) triggers a **Dialog** or **Sheet** (slide-over) for creating a new scan.
  - If you expect many scans, wrap the list in a **Scroll Area** to keep things tidy.

- **Creating a New Scan**  
  - When the user clicks **New Scan**, open a **Dialog** or **Sheet**.
  - Show a **Form** with:  
    1. **Input**: file upload (PDF/DOCX).  
    2. **Textarea** (or **Input**): job description text.  
    3. Optional **Input**: job title.  
  - On submit, store the new scan’s info in the database, upload the file to AWS S3, and add it to the sidebar list.

- **Main Content Area**  
  - Displays details of the selected scan. If no scan is selected, you can show a welcome or instructions message.
  - Optionally use **Tabs** for sub-sections (e.g., “Overview,” “Detailed Analysis,” etc.).
  - Components like **Card**, **Table**, or **Accordion** can help organize results.

- **UI Components & Utilities**  
  - **Toast** or **Alert** for success/error messages on scan creation.  
  - **Popover** or **Hover Card** for small tips or explanations.  
  - **Skeleton** for loading states while fetching scan data.

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
  -  Shadcn/UI in tailwindcss
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
The components you have to work with from shadcn are

Components
Accordion
Alert
Alert Dialog
Aspect Ratio
Avatar
Badge
Breadcrumb
Button
Calendar
Card
Carousel
Chart
Checkbox
Collapsible
Combobox
Command
Context Menu
Data Table
Date Picker
Dialog
Drawer
Dropdown Menu
Form
Hover Card
Input
Input OTP
Label
Menubar
Navigation Menu
Pagination
Popover
Progress
Radio Group
Resizable
Scroll Area
Select
Separator
Sheet
Sidebar
Skeleton
Slider
Sonner
Switch
Table
Tabs
Textarea
Toast
Toggle
Toggle Group
Tooltip

If you want to look at the documentation for any of these components
you can go to
https://ui.shadcn.com/docs/components/<component-name>

for example, https://ui.shadcn.com/docs/components/accordion

usually you can add them via

npx shadcn@latest add accordion

Then usage for atleast this example is

```
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion"

<Accordion type="single" collapsible>
  <AccordionItem value="item-1">
    <AccordionTrigger>Is it accessible?</AccordionTrigger>
    <AccordionContent>
      Yes. It adheres to the WAI-ARIA design pattern.
    </AccordionContent>
  </AccordionItem>
</Accordion>
```

# Current File Structure
