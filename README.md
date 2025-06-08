# Auction System Application

## Overview

The **Auction System Application** is a web-based platform for managing auctions. It includes user authentication with role segregation, allowing **users** to add "lots" for auction and **admins** to manage users, lots, and roles. The application is developed using **Spring Boot**, **Thymeleaf**, **Spring Security**, and **Spring Data JPA**, and it integrates with a **MySQL database**.

---

## Features

### Authentication and User Roles
- **Login and Registration:** Users can register and log in to the application.
- **Roles:**
    - **Admin**: Full management access (users, lots, roles).
    - **Registered User**: Can add and view their lots.
    - **Guest**: Limited access (e.g., browsing lots, no creation or management permissions).

### Lots Management
- Users can:
    - Add new auction lots.
    - View lots added by them.
- Admins can:
    - Delete lots.
    - Manage ownership for lots.

### Admin Panel
- **Roles Assignment**: Admins can assign roles to users.
- **User Management**: Admins can delete users.
- **Lot Management**: Admins can monitor and delete lots.

---

## Technologies Used

- **Backend:**
    - Spring Boot 3.x
    - Spring Security
    - Spring Data JPA
    - Lombok
- **Frontend:**
    - Thymeleaf templates
    - HTML/CSS (static resources)
- **Database:**
    - MySQL (database connection via Spring Data JPA)
- **Testing:**
    - Spring Boot Test
    - Spring Security Test
- **Build Tool:**
    - Maven
- **Tools:**
    - OpenAPI/Swagger integration for API testing and documentation (Springdoc)

---

## Installation Instructions

### Prerequisites
1. **Java Development Kit (JDK):** Ensure you have JDK 21 installed.
2. **Maven:** Make sure Maven is installed to build the project.
3. **MySQL Database:** Set up a MySQL database.

### Clone the Repository

```bash 
git clone git@github.com:Dastya/PI-223-2-1.git
```


### Configure Database
1. Open the `src/main/resources/application.properties` file.
2. Update the following properties with your database details:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/auction_db spring.datasource.username=<your_username> spring.datasource.password=<your_password> spring.jpa.hibernate.ddl-auto=update
```


- Replace `<your_username>` and `<your_password>` with your MySQL credentials.
- Ensure the database `auction_db` exists or create it manually.

### Run the Project
1. Build the project using Maven:
   ```bash
   mvn clean install
   ```
2. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
3. Access the application in your browser at `http://localhost:8080`.

---

## Project Structure

Here’s an overview of the application's structure:
```
src/ ├── main/ │ ├── java/ │ │ └── com.auction/ # Main application source code │ │ ├── config/ # Spring Security configuration │ │ ├── controller/ # REST and Thymeleaf controllers │ │ ├── dto/ # Data Transfer Objects (DTOs) │ │ ├── exception/ # Custom exceptions │ │ ├── model/ # JPA entities (User, Lot, etc.) │ │ ├── repository/ # Repositories for database access │ │ └── service/ # Service layer for business logic │ ├── resources/ │ │ ├── static/ # Static files (CSS, JS, images) │ │ ├── templates/ # Thymeleaf templates (HTML files) │ │ └── application.properties # Configuration file ├── test/ │ └── java/ # Unit and integration tests
```


---

## Available Endpoints

### Public Endpoints
| Method | Endpoint      | Description          |
|--------|---------------|----------------------|
| GET    | `/login`      | Login page           |
| GET    | `/register`   | Registration page    |

### Protected Endpoints
| Role       | Method | Endpoint                     | Description                           |
|------------|--------|------------------------------|---------------------------------------|
| Registered | GET    | `/lots`                     | View and manage user's lots.          |
| Admin      | GET    | `/admin`                    | Admin panel for managing lots, users. |

### API Endpoints (via REST)
Refer to the Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

---

## Example Accounts

To simplify testing, you can use these preconfigured accounts:

### Admin User
- **Username:** `admin`
- **Password:** `admin123`

### Registered User
- **Username:** `user`
- **Password:** `user123`

You can create more users via the **/register** endpoint or by updating the database directly.

---

## How to Contribute

1. Fork the repository.
2. Create a feature branch.
3. Commit your changes.
4. Push and create a pull request.

Contributions are welcome!

---

## License

This project is licensed under the **MIT License**. See the `LICENSE` file for more details.

---

## Contact

For any issues or inquiries, please feel free to contact:

- **Author:** Daniil Lypenets
- **Email:** daniillipenec@gmail.com
