# AWS S3 Resume Storage Setup

This project uses AWS S3 for storing user resumes. Follow these steps to set up S3 with this application:

## Create an AWS S3 Bucket

1. Log in to the [AWS Management Console](https://aws.amazon.com/console/)
2. Navigate to the S3 service
3. Click "Create bucket"
4. Choose a unique bucket name (this will be part of the URL to your files)
5. Select the AWS Region closest to your users
6. Configure bucket settings:
   - Block all public access (recommended for security)
   - Enable versioning (optional, but useful for keeping history of file changes)
7. Create the bucket

## Create IAM User for API Access

1. Navigate to the IAM service in the AWS Console
2. Go to "Users" and click "Add user"
3. Create a user with programmatic access (Access key and Secret access key)
4. Attach the following policies to the user:
   - `AmazonS3FullAccess` (for development) OR
   - A custom policy with limited permissions to just your specific bucket (for production)
5. Complete the user creation
6. **Important**: Save the Access Key ID and Secret Access Key, as you'll only see them once

## Configure CORS for Browser Access (Optional)

If you'll be uploading files directly from a web browser:

1. Go to your S3 bucket
2. Go to the "Permissions" tab
3. Find the CORS configuration section and add a configuration like:

```json
[
    {
        "AllowedHeaders": ["*"],
        "AllowedMethods": ["GET", "PUT", "POST", "DELETE", "HEAD"],
        "AllowedOrigins": ["https://your-domain.com"],
        "ExposeHeaders": []
    }
]
```

Replace `https://your-domain.com` with your actual domain or use `*` for development.

## Configure the Application

Add your AWS credentials to your environment variables:

1. Add these variables to your `.env` file:
   ```
   AWS_REGION=us-east-1
   AWS_S3_BUCKET_NAME=your-bucket-name
   AWS_ACCESS_KEY=your-access-key
   AWS_SECRET_KEY=your-secret-key
   ```

2. Replace the values with your actual AWS region, bucket name, and credentials

## Using Resume Storage API

The application provides the following endpoints for managing resumes:

**Note: Each user can only have one resume at a time. Uploading a new resume will replace any existing one.**

- **Upload a resume**: `POST /api/resumes/{userId}/upload`
  - Request: Multipart file upload with parameter name "file"
  - Response: 
    ```json
    {
      "objectKey": "resumes/user123/resume.pdf",
      "url": "https://presigned-url-to-file",
      "filename": "original-file-name.pdf"
    }
    ```

- **Get a user's resume**: `GET /api/resumes/{userId}`
  - Response if resume exists:
    ```json
    {
      "objectKey": "resumes/user123/resume.pdf",
      "url": "https://presigned-url-to-file",
      "filename": "resume.pdf"
    }
    ```
  - Response if no resume: HTTP 404 Not Found

- **Check if user has a resume**: `GET /api/resumes/{userId}/exists`
  - Response:
    ```json
    {
      "hasResume": true
    }
    ```

- **Get a pre-signed URL**: `GET /api/resumes/url?objectKey=resumes/user123/resume.pdf`
  - Response:
    ```json
    {
      "url": "https://presigned-url-to-file"
    }
    ```

- **Delete a user's resume**: `DELETE /api/resumes/{userId}`

## Security Best Practices

- Use IAM roles with minimal required permissions
- Enable server-side encryption for the S3 bucket
- Use pre-signed URLs for temporary access to files
- Keep your AWS credentials secure and never commit them to Git
- Consider using AWS Secrets Manager for credential management in production 