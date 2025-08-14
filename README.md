# wbs-backend

Backend service for WBS system, built with **Java 21** and **Spring Boot 3**.

---

## 🚀 Tech Stack

- **Java 21**
- **Spring Boot 3**
- **Spring Web**
- **Spring Validation**
- **Spring Data JPA** (planned)
- **Spring Security** (planned, JWT/OAuth2)
- **Kafka integration** (planned)
- **Docker / Kubernetes** (deployment)
- **JUnit 5 + Testcontainers** (testing)

---

## 📦 Project Package Structure

```text
io.github.dmitrypasichko.wbs
├── app                             # Application layer: use-cases/services orchestrating domain
│   ├── command                     # Commands (inputs to use-cases)
│   ├── query                       # Queries / read models
│   ├── service                     # @Service application services (transaction scripts)
│   ├── dto                         # App-level DTOs (inputs/outputs of services)
│   └── mapper                      # Mappers between DTOs and domain models
├── domain                          # Pure business domain
│   ├── model                       # Entities & value objects
│   ├── repository                  # Repository interfaces (ports)
│   ├── event                       # Domain events
│   └── service                     # Domain services (business rules)
├── web                             # API layer (adapters for HTTP)
│   ├── controller                  # @RestController classes
│   ├── dto                         # Request/response DTOs (API contracts)
│   ├── advice                      # @ControllerAdvice (exception mapping)
│   ├── validator                   # Custom validators
│   └── BookingAppApplication.java  # Custom validators
├── persistence                     # Persistence adapters (DB implementations)
│   ├── jpa                         # JPA repositories & entities
│   └── config                      # DB and JPA configuration
├── security                        # Authentication & authorization
│   ├── config                      # Spring Security configuration
│   ├── jwt                         # JWT-related classes
│   └── oauth                       # OAuth2 / OIDC integration
├── config                          # Application-wide configuration
│   ├── props                       # @ConfigurationProperties classes
│   └── startup                     # Application runners / initializers
└── common                          # Cross-cutting concerns
    ├── exception                   # Custom exception hierarchy
    ├── util                        # Utility classes
    └── logging                     # Logging & tracing helpers
