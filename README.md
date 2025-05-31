# URL Shortener Service

## Features

- URL shortening with 6-character codes
- Custom URL validation
- Collision detection and handling
- RESTful API with Swagger documentation
- H2 in-memory database
- Spring Boot Actuator for monitoring
- Docker support

## Prerequisites

- Docker & Docker Compose
- JDK 21 (for local development)
- Gradle 8.x (optional, wrapper included)

## Quick Start

1. Clone the repository
```bash
git clone <repository-url>
cd urlshortener
```

2. Build and start with Docker
```bash
docker-compose up --build
```

3. Access Services

- Main API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console
- Actuator Endpoints: http://localhost:8080/actuator

## API Usage

### Shorten URL
```bash
curl -X POST http://localhost:8080/v1/urls/shorten \
  -H "Content-Type: application/json" \
  -d '{"originalUrl":"https://www.example.com"}'
```

### Visit Shortened URL
```bash
curl -L http://localhost:8080/v1/urls/visit/{shortCode}
```

### Get URL Info
```bash
curl http://localhost:8080/v1/urls/info/{shortCode}
```

## Development

### Local Setup
```bash
./gradlew bootRun
```

### Running Tests
```bash
./gradlew test
```

### H2 Database Access
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:urlsdb
- Username: sa
- Password: password

## Future Improvements

1. Rate limiting for API endpoints
2. Redis caching layer for shortened URLs
3. Layered architecture
   - API Gateway
   - Microservices
   - Redis Cache
   - Persistent Database
4. Circuit breaker pattern for retries
5. Security features
   - API Authentication
6. Monitoring & Observability
   - Prometheus metrics
   - Grafana dashboards
   - Distributed tracing