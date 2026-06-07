# Local Development

Use this flow for day-to-day development. Docker is intended for deployment and Portainer stack validation.

## Frontend

Use the frontend's pinned toolchain:

```bash
cd snapscreen
nvm use
npm install
```

The project currently targets Node `24.16.0` for fresh local shells and Docker builds, with Node `22.22.0` or newer still allowed by `package.json` for compatibility.

```bash
cd snapscreen
npm run dev
```

The frontend dev server runs at:

```text
http://localhost:3000
```

Next.js hot reload will pick up UI changes without rebuilding Docker images.

Useful local checks:

```bash
npm run build
npm run lint
npm run audit
npm run outdated
```

## Backend

The backend requires Java 21.

To boot the API without PostgreSQL or Firebase Admin while developing:

```bash
cd backend/snapscreen-api
SPRING_PROFILES_ACTIVE=docker ./mvnw spring-boot:run
```

The API runs at:

```text
http://localhost:8080
```

Health endpoint:

```text
http://localhost:8080/actuator/health
```

When database-backed features are wired in, run a local PostgreSQL service and use the `prod` profile with `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`.
