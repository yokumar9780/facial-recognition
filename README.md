# Facial Recognition API

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Build](https://img.shields.io/badge/build-maven-blue)](https://maven.apache.org/)

## Overview

This project is a production-ready, extensible facial recognition REST API built with Spring Boot. It provides endpoints for user enrollment, face recognition, and verification using either a mock or OpenCV-based strategy. The application is designed for rapid prototyping, educational use, and as a foundation for more advanced biometric systems.

## Features & Use Cases

- **User Enrollment:** Register users with facial images and store their facial templates securely.
- **Face Recognition:** Identify users by matching uploaded images against enrolled templates.
- **Face Verification:** Verify if a given image matches a specific user.
- **Pluggable Recognition Strategies:** Switch between a mock (randomized) and OpenCV-based (deterministic) recognition engine.
- **In-memory H2 Database:** Fast prototyping and testing without external dependencies.
- **RESTful API:** Easy integration with web/mobile apps, kiosks, or backend systems.

**Use Cases:**
- Access control systems
- Attendance tracking
- User authentication
- Educational demos

## Prerequisites

- **Java 21** or higher
- **Maven 3.9+** (Maven Wrapper included)
- **Git** (for cloning the repository)
- **OpenCV/JavaCV** (automatically managed via Maven)
- (Optional) An IDE like IntelliJ IDEA or VS Code

## Project Setup

1. **Clone the repository:**
   ```powershell
   git clone https://github.com/yokumar9780/facial-recognition.git
   cd facial-recognition
   ```
2. **Build the project:**
   ```powershell
   .\mvnw clean install
   ```
3. **(Optional) Configure application settings:**
   - Edit `src/main/resources/application.yml` to adjust server port, database, or recognition strategy (`mock` or `opencv`).

## Running the Application Locally

Start the Spring Boot application:
```powershell
.\mvnw spring-boot:run
```
The API will be available at [http://localhost:8080](http://localhost:8080).

## API Endpoints

### 1. Enroll User
- **POST** `/api/v1/facial/enroll`
- **Form Data:**
  - `username` (string)
  - `file` (image file)
- **Response:** Success or error message

### 2. Recognize Face
- **POST** `/api/v1/facial/recognize`
- **Form Data:**
  - `file` (image file)
- **Response:** Username if matched, or "No match found."

### 3. Verify Face
- **POST** `/api/v1/facial/verify`
- **Form Data:**
  - `username` (string)
  - `file` (image file)
- **Response:** Verification result

#### Example (using `curl`):
```sh
curl -X POST http://localhost:8080/api/v1/facial/enroll \
  -F "username=alice" \
  -F "file=@/path/to/alice.jpg"

curl -X POST http://localhost:8080/api/v1/facial/recognize \
  -F "file=@/path/to/unknown.jpg"

curl -X POST http://localhost:8080/api/v1/facial/verify \
  -F "username=alice" \
  -F "file=@/path/to/test.jpg"
```

## Best Practices

- **AWS Credentials:** If integrating with AWS (e.g., S3 for image storage), use environment variables or AWS Secrets Manager. Never hardcode credentials.
- **Environment Variables:** Use `application.yml` for configuration, and override with environment variables for production.
- **Error Handling:** The API returns clear error messages and HTTP status codes. All exceptions are logged for debugging.
- **Security:**
  - Use HTTPS in production.
  - Validate and sanitize all user inputs.
  - Limit file upload size (configured in `application.yml`).
- **Extensibility:** Implement new recognition strategies by extending the `FacialRecognitionStrategy` interface.
- **Database:** For production, replace the in-memory H2 database with a persistent database (e.g., PostgreSQL, MySQL).

## Deployment

No Terraform or cloud infrastructure scripts are included. To deploy in production:
- Package the app: `.\mvnw clean package`
- Deploy the JAR to your server or cloud platform
- Configure environment variables and persistent storage


## License

This project is licensed under the [MIT License](LICENSE).

## Contributing

Contributions are welcome! Please open issues or submit pull requests for improvements, bug fixes, or new features.

1. Fork the repository
2. Create a new branch (`git checkout -b feature/your-feature`)
3. Commit your changes
4. Push to your fork and open a Pull Request

---

**Maintainer:** Yogesh Kumar

For questions or support, please open an issue on GitHub.
