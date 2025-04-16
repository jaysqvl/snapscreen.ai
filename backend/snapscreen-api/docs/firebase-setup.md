# Firebase Authentication Setup

This project uses Firebase Authentication for user management. Follow these steps to set up Firebase with this application:

## Create a Firebase Project

1. Go to the [Firebase Console](https://console.firebase.google.com/)
2. Click "Add Project" and follow the steps to create a new project
3. Enable Authentication in the Firebase Console:
   - Navigate to "Authentication" in the left sidebar
   - Click "Get Started"
   - Enable the sign-in methods you want to use (Email/Password, Google, etc.)

## Generate Service Account Credentials

1. In the Firebase Console, go to Project Settings (gear icon) > Service accounts
2. Click "Generate new private key" to download a JSON file with your service account credentials
3. Rename this file to `firebase-service-account.json`

## Configure the Application

### Option 1: Place the file in the resources directory

1. Place the `firebase-service-account.json` file in the `src/main/resources` directory
2. This file is already in `.gitignore` so it won't be committed to version control

### Option 2: Set an environment variable

1. Set the `FIREBASE_SDK_PATH` environment variable to the absolute path of your service account JSON file
2. You can add this to your `.env` file:
   ```
   FIREBASE_SDK_PATH=/path/to/your/firebase-service-account.json
   ```

## Using Firebase Authentication

Once configured, the application will automatically initialize Firebase when it starts. You can use the `UserService` and `UserController` to manage users:

- `GET /api/users/{uid}` - Get a user by their Firebase UID
- `GET /api/users` - List all users (paginated)
- `PUT /api/users/{uid}/admin?isAdmin=true|false` - Set admin role for a user
- `PUT /api/users/{uid}/claims` - Set custom claims for a user (POST with JSON body)

## Additional Firebase Resources

- [Firebase Admin SDK for Java](https://firebase.google.com/docs/admin/setup)
- [Firebase Authentication Documentation](https://firebase.google.com/docs/auth)
- [Managing Users with the Firebase Admin SDK](https://firebase.google.com/docs/auth/admin/manage-users) 