# Cafheg - Family Allowance Management System

## Prerequisites

- **Java 25**
- **IntelliJ IDEA** (Community or Ultimate)
- **Docker Desktop** (or Docker + Docker Compose)
- **Maven 3.8+** (bundled with the project as `mvnw`)

## Getting Started with IntelliJ

### 1. Open the Project

1. Launch IntelliJ IDEA
2. Select **File** → **Open** and choose the `cafheg` folder
3. IntelliJ will recognize the Maven project automatically

### 2. Configure Java Version

1. Go to **File** → **Project Structure** → **Project**
2. Set **SDK** to Java 25
    - If Java 25 is not available, click the dropdown and select **Download JDK**
    - Choose **Version 25** and a vendor (e.g., Eclipse Temurin)
    - Click **Download** and wait for completion
3. Set **Language level** to 25
4. Click **Apply** and **OK**

### 3. PostgreSQL Database

The application is configured to automatically start PostgreSQL via Docker Compose on startup thanks to the
`spring-boot-docker-compose` dependency.

**No manual setup required** - PostgreSQL will start automatically when you run the application.

#### Manual Database Startup (Optional)

If you prefer to manage the database separately, run:

```bash
docker compose up -d
```

This will start a PostgreSQL 18.1 container with:

- Database: `cafheg`
- User: `cafheg`
- Password: `secret`
- Published port: `5432`

### 4. Run the Application

#### Option A: From IntelliJ

1. Open `CafhegApplication.java` (in `src/main/java/ch/hearc/cafheg/`)
2. Click the green **Run** button next to the `main` method
3. The application will start on `http://localhost:8080/api`

#### Option B: From Terminal

```bash
./mvnw clean spring-boot:run
```

Or on Windows:

```bash
mvnw.cmd clean spring-boot:run
```

### 5. Access the Application

- **API Documentation**: http://localhost:8080/api/swagger-ui/index.html
- **API Base URL**: http://localhost:8080/api

## Database Migrations

Database schema and migrations are managed by Flyway and located in:

- `src/main/resources/db/migration/ddl/` - Schema definitions
- `src/main/resources/db/migration/dml/` - Data initialization

Migrations run automatically on application startup.

## Troubleshooting

- **Port 8080 already in use**: Change `server.port` in `application.yaml`
- **Database connection failed**: Ensure PostgreSQL is running via Docker Compose and environment variables are set
  correctly
- **Maven sync issues**: Right-click on `pom.xml` and select **Maven** → **Reload projects**


