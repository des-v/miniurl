# MiniURL - REST API MVP for URL Shortening

A minimal REST API MVP for URL shortening built with Java, Spring Boot, and PostgreSQL.  
**No user registration or authentication/security is included.**  
Easily run locally or in Docker, with ready-to-use Postman/manual testing support.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Features](#features)
3. [Tech Stack & Tools](#tech-stack--tools)
4. [Getting Started](#getting-started)
    - [Environment Configuration](#environment-configuration)
    - [Database Schema](#database-schema)
    - [Working with Docker Compose](#working-with-docker-compose)
    - [Working with Docker](#working-with-docker)
    - [Manual Local Run](#manual-local-run)
5. [API Endpoints](#api-endpoints)
6. [Testing](#testing)
7. [Project Structure](#project-structure)
8. [Future Improvements](#future-improvements)

---

## Project Overview

MiniURL is a simple, production-ready URL shortener REST API MVP.  
It allows you to create, retrieve, update, and delete shortened URLs (mini-URLs).  
**This MVP does not implement user registration, authentication, or advanced security.**

---

## Features

- Shorten long URLs to unique mini-keys.
- Retrieve original URLs by mini-key.
- Update or delete existing mini-URLs.
- List all mini-URLs.
- Asynchronous (CompletableFuture-based) service operations.
- Input validation and error handling.
- Configurable random key generator (alphabet & length).
- Ready for local development and production Docker environments.

---

## Tech Stack & Tools

- **Java 21**
- **Spring Boot 3.4**
- **PostgreSQL**
- **Maven** (build tool)
- **JUnit 5 & Mockito** (testing)
- **IntelliJ IDEA** (recommended IDE)
- **Docker & Docker Compose** (containerization)
- **Postman** (manual API testing)
- **pgAdmin** (for easy PostgreSQL database management)

### Main Dependencies

- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-validation`
- `postgresql`
- `commons-validator`
- `spring-boot-starter-test`, `junit-jupiter`, `mockito-core` (test)

---

## Getting Started

### Environment Configuration

1. **Clone the repository** and create the appropriate `.env` files in the root directory.
    - Use `.env-example`, `.env-dev-example`, or `.env-prod-example` as templates.

2. **Create the PostgreSQL database manually** (using `psql`, `pgAdmin`, or another tool) before running the application.  
   The database name should match `DB_NAME` in your `.env` files.

3. **Configure database credentials** in your `.env` files:
    ```
    DB_NAME=miniurl
    DB_USER=your_db_user
    DB_PASS=your_db_password
    ```

4. **Random Key Generator** (optional):
    - Customize `MINIURL_CUSTOM_ALPHABET` and `MINIURL_KEY_LENGTH` in your `.env` file if needed.

---

### Database Schema

**For development**, the schema can be auto-updated by Spring Boot (see `application-dev.properties`).  
**For production**, you must create the schema manually before running the app.  
SQL scripts are provided in `src/main/resources/db/dev/v1/v1__init_schema_dev.sql`
and `src/main/resources/db/prod/v1/v1__init_schema_prod.sql`.

<details>
<summary>Click to expand SQL schema</summary>

```sql
    CREATE TABLE IF NOT EXISTS mini_urls (
    mini_key VARCHAR(32) PRIMARY KEY,
    full_url TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
    
    CREATE INDEX IF NOT EXISTS idx_mini_urls_full_url ON mini_urls(full_url);
```
</details>

---

### Working with Docker Compose

#### **Production Build**

> Before running your Docker container, double-check which DB_URL is being used in your .env.prod file. For reference check the .env-prod-example file
* To build it and run it, use:
    ```shell
    docker-compose -f docker-compose.prod.yml up --build
    ```
    - Exposes API at `localhost:8080`
    - PostgreSQL at `localhost:5433`

* To stop and remove containers, networks, and volumes:
    ```shell
    docker-compose -f docker-compose.prod.yml down
    ```

---

#### **Development Build**

> Before running your Docker container, double-check which DB_URL is being used in your .env.dev file. For reference check the .env-dev-example file
* To build it and run it, use:
    ```shell
    docker-compose -f docker-compose.dev.yml up --build
    ```
  - Exposes API at `localhost:8080`
  - PostgreSQL at `localhost:5434`
  
* To stop and remove containers, networks, and volumes:
    ```shell
    docker-compose -f docker-compose.dev.yml down
    ```

---

### Working with Docker

#### **Production Build**
* To build it, use:
    ```shell
    docker build -t miniurl:prod -f Dockerfile .
    ```

> Before running your Docker container, double-check which DB_URL is being used in your .env.prod file. For reference check the .env-prod-example file
* To run it, use:
    ```shell
    docker run -p 8080:8080 --env-file .env.prod miniurl:prod
    ```
* To stop your running Docker container, first identify the container name or ID using:

    ```shell
    docker ps
    ```
* Then stop the container with:

    ```shell
    docker stop <container_name_or_id>
    ```
    >Replace <container_name_or_id> with the actual name or ID from the docker ps outpu

---

#### **Development Build**
* To build it, use:
    ```shell
    docker build -t miniurl:dev -f Dockerfile.dev .
    ```
  
> Before running your Docker container, double-check which DB_URL is being used in your .env.dev file. For reference check the .env-dev-example file
* To run it, use:
    ```shell
    docker run -p 8080:8080 --env-file .env.dev miniurl:dev
    ```
* To stop your running Docker container, first identify the container name or ID using:

    ```shell
    docker ps
    ```
* Then stop the container with:

    ```shell
    docker stop <container_name_or_id>
    ```
  >Replace <container_name_or_id> with the actual name or ID from the docker ps output

---

## Manual Local Run

1. Start PostgreSQL locally and **create the database manually** (using `psql`, `pgAdmin`, etc.).
2. Set up `.env` or use application properties.
3. Build and run:
    ```shell
    mvn clean package -DskipTests
    ```
   - Before running your JAR, set the variables in your shell,
   please replace before running the values with your own ones:
    ```
    export DB_URL=jdbc:postgresql://localhost:5432/miniurl
    export DB_USER=your_db_user
    export DB_PASS=your_db_password
    export SPRING_PROFILES_ACTIVE=dev
    ```

    ```shell
    java -jar target/miniurl-0.0.1-SNAPSHOT.jar
    ```

   - Or, you can do it in a single line, please replace before running the values with your own ones:
    ```
    DB_URL=jdbc:postgresql://localhost:5432/miniurl DB_USER=your_db_user DB_PASS=your_db_password SPRING_PROFILES_ACTIVE=dev java -jar target/miniurl-0.0.1-SNAPSHOT.jar
    ```
   
---

## API Endpoints

All endpoints are under `/api/v1/miniurls`.

| Method | Endpoint                      | Description                  | Body/Params                |
|--------|-------------------------------|------------------------------|----------------------------|
| POST   | `/api/v1/miniurls`            | Create or get mini-URL       | `{ "fullUrl": "<url>" }`   |
| GET    | `/api/v1/miniurls/{miniKey}`  | Get mini-URL by key          | Path: `miniKey`            |
| PUT    | `/api/v1/miniurls/{miniKey}`  | Update mini-URL              | `{ "fullUrl": "<url>" }`   |
| DELETE | `/api/v1/miniurls/{miniKey}`  | Delete mini-URL              | Path: `miniKey`            |
| GET    | `/api/v1/miniurls`            | List all mini-URLs           |                            |

**Request/Response examples and test payloads can be found in `src/test/resources/json/`.**

---

## Testing

- **Unit and integration tests**: 

| Test Class              | Type                | What it Covers                                 |
    |-------------------------|---------------------|------------------------------------------------|
| MiniUrlServiceTest      | Unit test           | Service logic with mocks                       |
| CustomUrlValidatorTest  | Unit test           | Utility class logic                            |
| RandomKeyGeneratorTest  | Unit test           | Utility class logic                            |
| MiniUrlControllerTest   | Integration (slice) | Web layer + Spring MVC, mocks service layer    |

- Running the test
    - Before running `mvn test`, export the required variables,
      please replace before running the values with your own ones:
    ```
    export DB_URL=jdbc:postgresql://localhost:5432/miniurl
    export DB_USER=your_db_user
    export DB_PASS=your_db_password
    export SPRING_PROFILES_ACTIVE=dev
    ```
    ```shell
    mvn test
    ```
    - Or, you can do it in a single line, please replace before running the values with your own ones:

    ```
    DB_URL=jdbc:postgresql://localhost:5432/miniurl DB_USER=your_db_user DB_PASS=your_db_password SPRING_PROFILES_ACTIVE=dev mvn test
    ```
- **Manual API testing**: Use Postman or curl.  
  Example payloads are in `src/test/resources/json/`.

---

## Project Structure


    src/
    ├── main/
    │ ├── java/com/desireevaldes/miniurl/
    │ │ ├── api/v1/ # Controllers
    │ │ ├── dto/ # Request/Response DTOs
    │ │ ├── exceptions/ # Exception handlers
    │ │ ├── models/ # JPA Entities
    │ │ ├── repositories/ # Spring Data JPA Repos
    │ │ ├── services/ # Service layer
    │ │ └── utils/ # Utilities (validation, keygen)
    │ └── resources/
    │ ├── application.properties
    │ ├── application-dev.properties
    │ ├── application-prod.properties
    │ └── db/
    │ ├── dev/v1/v1__init_schema_dev.sql
    │ └── prod/v1/v1__init_schema_prod.sql
    └── test/
    ├── java/com/desireevaldes/miniurl/
    │ ├── controllers/ # Controller tests
    │ ├── services/ # Service tests
    │ └── utils/ # Utils tests
    └── resources/json/ # Test JSON payloads
    

---

## Future Improvements Ideas

- **User authentication and authorization**
- **Rate limiting and abuse prevention**
- **Custom mini-key support**
- **Expiration for mini-URLs**
- **HTTPS and security best practices**
- **Analytics and click tracking**
- **CI/CD pipeline integration**

---

**Happy URL minimizing! 🚀**  
For questions or contributions, feel free to open an issue or PR
