# URL shortener service

## How to start in local

### Prerequisites
- Docker
- Docker Compose

### Steps to Run

1. Clone the repository
```bash
git clone <repository-url>
cd urlshortener
```
2. Build and start the application
```
docker-compose up --build
```
3. The application will be available at:
Main API: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui.html
H2 Console: http://localhost:8080/h2-console
Actuator Endpoints:
  - Health: http://localhost:8080/actuator/health
  - Info: http://localhost:8080/actuator/info
  - Metrics: http://localhost:8080/actuator/metrics
  - Prometheus: http://localhost:8080/actuator/prometheus

4. Stopping the application
```
docker-compose down
```