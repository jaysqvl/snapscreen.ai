# Resume Screening Platform Database Schema

This document outlines the SQL schema for the Resume Screening Platform's PostgreSQL database.

## Core Tables

### Users
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### Scans
```sql
CREATE TABLE scans (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    job_title VARCHAR(255),
    company VARCHAR(255),
    job_description TEXT NOT NULL,
    resume_text TEXT,
    resume_url VARCHAR(512),
    status VARCHAR(50) NOT NULL CHECK (status IN ('PROCESSING', 'COMPLETED', 'FAILED')),
    score INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Skills
```sql
CREATE TABLE skills (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('HARD', 'SOFT')),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (name, type)
);
```

### ScanSkills
```sql
CREATE TABLE scan_skills (
    id UUID PRIMARY KEY,
    scan_id UUID NOT NULL,
    skill_id UUID NOT NULL,
    in_resume BOOLEAN NOT NULL DEFAULT FALSE,
    in_job_description BOOLEAN NOT NULL DEFAULT FALSE,
    resume_count INTEGER NOT NULL DEFAULT 0,
    job_description_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_scan FOREIGN KEY (scan_id) REFERENCES scans(id) ON DELETE CASCADE,
    CONSTRAINT fk_skill FOREIGN KEY (skill_id) REFERENCES skills(id),
    UNIQUE (scan_id, skill_id)
);
```

### ScanChecks
```sql
CREATE TABLE scan_checks (
    id UUID PRIMARY KEY,
    scan_id UUID NOT NULL,
    check_type VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PASS', 'FAIL', 'NEEDS_ATTENTION')),
    score INTEGER NOT NULL,
    details JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_scan FOREIGN KEY (scan_id) REFERENCES scans(id) ON DELETE CASCADE,
    UNIQUE (scan_id, check_type)
);
```

### ScanRecommendations
```sql
CREATE TABLE scan_recommendations (
    id UUID PRIMARY KEY,
    scan_id UUID NOT NULL,
    recommendation TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_scan FOREIGN KEY (scan_id) REFERENCES scans(id) ON DELETE CASCADE
);
```

## Analytics Tables

### ScanActivity
```sql
CREATE TABLE scan_activity (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    date DATE NOT NULL,
    scan_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (user_id, date)
);
```

### CompanyApplications
```sql
CREATE TABLE company_applications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    company VARCHAR(255) NOT NULL,
    application_count INTEGER NOT NULL DEFAULT 0,
    average_score DECIMAL(5,2),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (user_id, company)
);
```

## Indexes

```sql
-- User lookup indexes
CREATE INDEX idx_users_email ON users(email);

-- Scan lookup indexes
CREATE INDEX idx_scans_user_id ON scans(user_id);
CREATE INDEX idx_scans_created_at ON scans(created_at);
CREATE INDEX idx_scans_status ON scans(status);
CREATE INDEX idx_scans_company ON scans(company);

-- Skill lookup indexes
CREATE INDEX idx_skills_name ON skills(name);
CREATE INDEX idx_skills_type ON skills(type);

-- Scan-skill relationship indexes
CREATE INDEX idx_scan_skills_scan_id ON scan_skills(scan_id);
CREATE INDEX idx_scan_skills_skill_id ON scan_skills(skill_id);

-- Check indexes
CREATE INDEX idx_scan_checks_scan_id ON scan_checks(scan_id);
CREATE INDEX idx_scan_checks_check_type ON scan_checks(check_type);

-- Analytics indexes
CREATE INDEX idx_scan_activity_user_id ON scan_activity(user_id);
CREATE INDEX idx_scan_activity_date ON scan_activity(date);
CREATE INDEX idx_company_applications_user_id ON company_applications(user_id);
CREATE INDEX idx_company_applications_company ON company_applications(company);
```

## Triggers and Functions

```sql
-- Function to update timestamp columns
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Add trigger for users table
CREATE TRIGGER update_users_timestamp
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

-- Add trigger for scans table
CREATE TRIGGER update_scans_timestamp
BEFORE UPDATE ON scans
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

-- Function to update scan activity when a new scan is added
CREATE OR REPLACE FUNCTION update_scan_activity()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO scan_activity (id, user_id, date, scan_count)
    VALUES (gen_random_uuid(), NEW.user_id, CURRENT_DATE, 1)
    ON CONFLICT (user_id, date) 
    DO UPDATE SET scan_count = scan_activity.scan_count + 1,
                  updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Add trigger for updating scan activity
CREATE TRIGGER update_scan_activity_on_insert
AFTER INSERT ON scans
FOR EACH ROW
EXECUTE FUNCTION update_scan_activity();

-- Function to update company applications when a new scan is added
CREATE OR REPLACE FUNCTION update_company_applications()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.company IS NOT NULL THEN
        INSERT INTO company_applications (id, user_id, company, application_count, average_score)
        VALUES (
            gen_random_uuid(), 
            NEW.user_id, 
            NEW.company, 
            1, 
            COALESCE(NEW.score, 0)
        )
        ON CONFLICT (user_id, company) 
        DO UPDATE SET 
            application_count = company_applications.application_count + 1,
            average_score = (company_applications.average_score * company_applications.application_count + COALESCE(NEW.score, 0)) / (company_applications.application_count + 1),
            updated_at = NOW();
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Add trigger for updating company applications
CREATE TRIGGER update_company_applications_on_insert
AFTER INSERT ON scans
FOR EACH ROW
EXECUTE FUNCTION update_company_applications();
```

## Example Data Structures

### ScanChecks Details Field

The `details` JSONB field in the `scan_checks` table will contain minimal required information specific to each check:

```json
// Contact Info Check
{
  "email": true,
  "phone": false,
  "address": false
}

// Date Formatting Check
{
  "consistent": true,
  "format": "MM/YYYY"
}

// Hard Skills Check (summary data - details in ScanSkills table)
{
  "total_required": 7,
  "total_found": 5
}
```

## API Response Example 

The API would return checks in a simplified format with hardcoded messages applied by the backend:

```json
{
  "id": "scan-123",
  "name": "Software Engineer - Google",
  "score": 85,
  "createdAt": "2023-07-01T12:00:00Z",
  "status": "COMPLETED",
  "overall": {
    "passed": 16,
    "failed": 3,
    "needsAttention": 2,
    "total": 21
  },
  "checks": [
    {
      "name": "Email",
      "checkType": "EMAIL",
      "status": "PASS"
    },
    {
      "name": "Phone Number",
      "checkType": "PHONE",
      "status": "FAIL"
    },
    // ... other standard checks
  ],
  "hardSkills": {
    "totalRequired": 7,
    "totalFound": 5,
    "skills": [
      {
        "name": "JavaScript",
        "inResume": true,
        "inJobDescription": true
      },
      {
        "name": "Docker",
        "inResume": false,
        "inJobDescription": true
      }
      // ... other skills
    ]
  },
  "softSkills": {
    "totalRequired": 4,
    "totalFound": 3,
    "skills": [
      // ... soft skills
    ]
  }
}
```

## Implementation Notes

1. **Skills Matching:**
   - Dynamic skills are stored in the `skills` table
   - For each scan, the system will extract skills from both resume and job description
   - The `scan_skills` table tracks which skills appear in each document and how many times
   - This allows for detailed analytics on skill gaps and matches

2. **Static Checks:**
   - Standard checks are stored simply with their status and minimal details
   - UI messages and explanations are handled by the frontend based on check_type
   - No need to store categories as these are static and can be hardcoded in the frontend

3. **Performance Considerations:**
   - Indexes are defined for frequently queried columns
   - The database uses triggers to maintain analytics tables, reducing application code complexity
   - JSONB type for complex nested data allows flexibility while maintaining query capabilities 