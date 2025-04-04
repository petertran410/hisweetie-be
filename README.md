# HiSweetie - Java Backend Project

Welcome to **HiSweetie**, the Java backend project for managing and serving data for HiSweetie applications. This project provides RESTful APIs and backend services for domains hosted at [lermao.com](https://lermao.com/) and [diep.com](https://diep.com/).

## Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)

---

## Project Overview

The **HiSweetie** backend project is a Java Spring Boot application designed to handle business logic, data processing, and API endpoints for the HiSweetie application suite. It supports scalable and efficient interactions for various client applications under the domains [lermao.com](https://lermao.com/) and [diep.com](https://diep.com/).

## Features

- **User Management**: Register, authenticate, and manage user profiles.
- **Content Management**: CRUD operations for articles, posts, and other content types.
- **Image Handling**: Upload, retrieve, and manage images.
- **Role-Based Access Control**: Secured endpoints with role-based access for `ROLE_ADMIN`, `ROLE_SUPER_ADMIN`, and standard users.
- **Rate Limiting**: Prevents abuse by limiting the number of requests per user.
- **Notifications**: Support for sending notifications and updates to users.

## Technologies Used

- **Java 17**
- **Spring Boot**
- **Spring Security** with JWT for authentication and authorization
- **Hibernate/JPA** for data persistence
- **PostgreSQL** as the database
- **Redis** for caching and rate-limiting
- **Swagger** for API documentation
- **Maven** for dependency management

## Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/hisweetie.git
   cd hisweetie

# HiSweetie - Java Backend Project

Welcome to **HiSweetie**, the Java backend project for managing and serving data for HiSweetie applications. This project provides RESTful APIs and backend services for domains hosted at [lermao.com](https://lermao.com/) and [diep.com](https://diep.com/).

## Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)

---

## Project Overview

The **HiSweetie** backend project is a Java Spring Boot application designed to handle business logic, data processing, and API endpoints for the HiSweetie application suite. It supports scalable and efficient interactions for various client applications under the domains [lermao.com](https://lermao.com/) and [diep.com](https://diep.com/).

## Features

- **User Management**: Register, authenticate, and manage user profiles.
- **Content Management**: CRUD operations for articles, posts, and other content types.
- **Image Handling**: Upload, retrieve, and manage images.
- **Role-Based Access Control**: Secured endpoints with role-based access for `ROLE_ADMIN`, `ROLE_SUPER_ADMIN`, and standard users.
- **Rate Limiting**: Prevents abuse by limiting the number of requests per user.
- **Notifications**: Support for sending notifications and updates to users.

## Technologies Used

- **Java 17**
- **Spring Boot**
- **Spring Security** with JWT for authentication and authorization
- **Hibernate/JPA** for data persistence
- **PostgreSQL** as the database
- **Redis** for caching and rate-limiting
- **Swagger** for API documentation
- **Maven** for dependency management

## Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/hisweetie.git
   cd hisweetie
# HiSweetie - Java Backend Project

Welcome to **HiSweetie**, the Java backend project for managing and serving data for HiSweetie applications. This project provides RESTful APIs and backend services for domains hosted at [lermao.com](https://lermao.com/) and [diep.com](https://diep.com/).

## Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)

---

## Project Overview

The **HiSweetie** backend project is a Java Spring Boot application designed to handle business logic, data processing, and API endpoints for the HiSweetie application suite. It supports scalable and efficient interactions for various client applications under the domains [lermao.com](https://lermao.com/) and [diep.com](https://diep.com/).

## Features

- **User Management**: Register, authenticate, and manage user profiles.
- **Content Management**: CRUD operations for articles, posts, and other content types.
- **Image Handling**: Upload, retrieve, and manage images.
- **Role-Based Access Control**: Secured endpoints with role-based access for `ROLE_ADMIN`, `ROLE_SUPER_ADMIN`, and standard users.
- **Rate Limiting**: Prevents abuse by limiting the number of requests per user.
- **Notifications**: Support for sending notifications and updates to users.

## Technologies Used

- **Java 17**
- **Spring Boot**
- **Spring Security** with JWT for authentication and authorization
- **Hibernate/JPA** for data persistence
- **PostgreSQL** as the database
- **Redis** for caching and rate-limiting
- **Swagger** for API documentation
- **Maven** for dependency management

## Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/hisweetie.git
   cd hisweetie
   
2. **Build the project:**:
   ```bash
   mvn clean install
   
3. **Run the application:**:
   ```bash
   mvn spring-boot:run

The server should start on http://localhost:8080.

Configuration
Application Properties:

Rename application.properties.example to application.properties and update the following configurations:
Database settings (spring.datasource.url, spring.datasource.username, spring.datasource.password).
JWT secret key and expiration settings.
Redis connection settings if Redis is used.
Environment Variables:

Set environment variables for sensitive information (e.g., database credentials, JWT secret).
Domains:

Confirm that the allowedOrigins in CORS settings include https://lermao.com and https://diep.com.
Usage
After starting the application, you can access the APIs by navigating to:

http://localhost:8080/api for the main API endpoints.
http://localhost:8080/api/swagger-ui/ for API documentation.
API Documentation
This project uses Swagger for API documentation. Once the server is running, you can access the documentation at:

Swagger UI
Contributing
Contributions are welcome! To contribute:

Fork this repository.
Create a feature branch (git checkout -b feature-branch-name).
Commit your changes (git commit -m 'Add new feature').
Push to the branch (git push origin feature-branch-name).
Create a Pull Request.
License
This project is licensed under the MIT License. See the LICENSE file for more details.

Contact
For any questions or support, please reach out to the project maintainers or create an issue on this repository.

Happy coding!


### Explanation

This `README.md` provides a structured overview of the **HiSweetie** project, including installation steps, configuration details, and usage instructions. Let me know if you'd like to customize any specific section further!