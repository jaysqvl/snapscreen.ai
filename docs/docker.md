# Docker Deployment

This repo now has a base Docker stack for local/home-server deployment:

- `compose.yaml` runs the Next.js frontend and Spring Boot API.
- `compose.dependencies.yaml` adds PostgreSQL and Redis when the app is ready to use them.
- `.env.docker.example` lists the variables Portainer can use for stack configuration.

## Base Stack

Build and run the current app without PostgreSQL or Redis:

```bash
docker compose up --build
```

To use the example environment file directly:

```bash
docker compose --env-file .env.docker.example up --build
```

Frontend: `http://localhost:3000`

API health: `http://localhost:8080/actuator/health`

The API runs with `SPRING_PROFILES_ACTIVE=docker`, which disables datasource/JPA auto-configuration so the container can boot before database wiring exists. Firebase Admin is also disabled by default in this profile with `FIREBASE_ENABLED=false`.

## With PostgreSQL and Redis

When backend persistence or caching is wired into the app, run:

```bash
docker compose -f compose.yaml -f compose.dependencies.yaml up --build
```

With the example environment file:

```bash
docker compose --env-file .env.docker.example -f compose.yaml -f compose.dependencies.yaml up --build
```

This switches the API to the `prod` profile by default and provides:

- PostgreSQL at `postgres:5432`
- Redis at `redis:6379`
- Persistent Docker volumes for both services

## Portainer Notes

Use `compose.yaml` for the first stack deployment. Add the variables from `.env.docker.example` in Portainer's environment editor.

When deploying behind a reverse proxy, set `NEXT_PUBLIC_API_URL` to the browser-facing API URL, not the internal Docker service name. Next.js embeds `NEXT_PUBLIC_*` values at image build time, so rebuild the frontend image after changing Firebase or API public values.

To enable Firebase Admin in the API, set:

```env
FIREBASE_ENABLED=true
FIREBASE_SERVICE_ACCOUNT_PATH=/host/path/to/firebase-service-account.json
FIREBASE_SDK_PATH=/run/secrets/firebase-service-account.json
```

For S3-backed resume upload endpoints, replace the local placeholder AWS values with real credentials and bucket settings.
