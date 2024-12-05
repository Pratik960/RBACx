# Role Based Access Control System (RBACx)

RBACx is a secure Role-Based Access Control (RBAC) system built with React, Spring Boot, Spring Security, JWT tokens, MySQL, Webpack, and Swagger. The system implements authentication, authorization, and role-based access for different user types with varying permissions.

This project is aimed at demonstrating the principles of Authentication, Authorization, and Role-Based Access Control (RBAC) by allowing users to register, log in, and access resources based on their assigned roles.

## Deployment URL

The application is deployed and can be accessed at the following URL:

https://rbac-deployment.onrender.com


## Note

> This application is hosted on a **free server**, which may result in slightly slower (2-3 minutes) initial loading times due to server cold starts or resource limitations. Please be patient while the website loads.



## Security Features

There are several security features that I have added to secure this **RBACx** system.

### 1. Spring Security Integration
- In this system, **Spring Security** is used to handle all authentication and authorization processes.
- It takes care of essential security concerns in the backend, including user login, password validation, and securing API endpoints.

### 2. Custom Authentication Filter
- A **custom authentication filter** was developed to handle authentication for every incoming request to the backend.
- This filter intercepts requests, extracts the **JWT token** from the request header, and validates it to ensure the request is from an authorized user.

### 3. Custom JWT Token Implementation
- **Custom JWT tokens** were created for secure user authentication and authorization.
- These tokens are **signed and encrypted**, ensuring that only authorized users can interact with the system.
- After a successful login, both a JWT token and a refresh token are issued. They are used for authenticating subsequent requests and generating a new JWT token when the current one expires.

### 4. Refresh Token System
- **Refresh tokens** were implemented to extend user sessions and reduce the need for frequent re-authentication.
- Each time a user logs in, both an **access token** (JWT) and a **refresh token** are issued.
- When the access token expires, the refresh token can be used to generate a new access token, allowing users to stay logged in without having to re-enter their credentials.
- The refresh token is securely stored and transmitted to prevent unauthorized access.

### 5. Role-Based Access Control (RBAC)
- The application enforces **role-based access control (RBAC)** for different types of users.
- Users are assigned roles like **Admin**, **User**, **Employee**, and each role has its own access rights to various resources.
- **Spring Security** is used to manage authorization, ensuring that only users with the correct role can access certain endpoints and actions.

### 6. Rate Limiting
- To protect against **Distributed Denial of Service (DDoS)** attacks, the app implements **rate limiting**.
- Rate limiting restricts the number of requests a user can make to any given API endpoint within a set amount of time.
- This helps prevent the backend from being overwhelmed by too many requests and ensures that the system remains available and responsive.

### 7. Frontend Protected Routes
- Some routes in the frontend are **protected** and require the user to be authenticated before accessing them, as well as having specific authorization (i.e., roles).
- If the user is not logged in, or their session has expired, they are automatically redirected to the login page.
- Protected routes rely on the **role of current user** stored securely in the frontend (e.g., in **localStorage**) to verify the user’s identity before granting access.

### 8. Email Verification on User Registration
- **Email verification** is implemented as part of the user registration process to ensure the validity of the user's email address.
- After a user registers, a verification email containing a token is sent to the provided email address.
- The user must click on the verification link to confirm their email address before gaining access to certain features of the application.
- This step prevents fake or invalid email addresses from being used and adds an extra layer of security to the user registration process.

---




## User Roles and Permissions

| **Role**       | **Permissions**                                                                                                                                                                                                                                                                                                  |
|----------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **ROLE_USER**  | - Edit own profile<br>- Mark assigned tasks as completed<br>- Register<br>- Log in<br>- Log out                                                                                                                                                                                                                  |
| **ROLE_EMP**   | - Assign tasks to active users<br>- View pending tasks and their assignments<br>- View assigned tasks<br>- View completed tasks and who completed them<br>- Register<br>- Log in<br>- Log out                                                                                                               |
| **ROLE_ADMIN** | - Create new tasks<br>- Delete users from the system<br>- Delete employees from the system<br>- Register<br>- Log in<br>- Log out                                                                                             |



## Installation & Usage

### 1. Clone the Repository
Clone the repository to your local machine:

```bash
  git clone https://github.com/Pratik960/VRV-Security-Backend-Assignment-RBACx.git
```

### 2. Database Setup (MySQL)

 2.1 Create a MySQL database:
```bash
CREATE DATABASE rbac;
```
2.2 Set up the database connection and email sender service configuration in the application-local.properties of the Spring Boot backend:
```bash
app.email=youremail@gmail.com

spring.mail.username=youremail@gmail.com
spring.mail.password=your_email_app_password

spring.datasource.username=your_username
spring.datasource.password=your_password
```
2.3 Set the active profile as local in application.properties file
```bash
spring.profiles.active=local
```

### 3. Backend Setup (Spring Boot)
Navigate to cloned repository

```bash
  cd VRV-Security-Backend-Assignment-RBACx
  mvn clean install
  mvn spring-boot:run
```

### 4. Open web browser and visit this url

```bash
  http://localhost:5001
```

### 5. To access documentation of APIs

```bash
  http://localhost:5001/swagger-ui/index.html#/
```

## Screenshots

### Registration Page

![Registration_Page](https://github.com/user-attachments/assets/553800a3-32df-4a65-a64f-1d045e55258d)



---

### Login Page

![Login_Page](https://github.com/user-attachments/assets/b9daf665-1ff8-4b06-862e-625e0175efd8)



---

### User Dashboard

![User_Dashboard](https://github.com/user-attachments/assets/11492f0e-1ea7-43a9-a838-0543e5367140)
)


---

### Employee Dashboard

![Employee_Dashboard](https://github.com/user-attachments/assets/f771f798-2349-41a8-895d-6ad6f0b38452)


---

### Admin Dashboard

![Admin_Dashboard1](https://github.com/user-attachments/assets/126930c3-8102-4fc4-a5e3-acb7c3476c54)
![Admin_Dashboard2](https://github.com/user-attachments/assets/56dd0502-adf3-4882-b0ac-599273383dde)




## Notes

> ⚠️ You might notice some **design inconsistencies**—I like to call it "character." 😅 Enjoy the charm!
> 
> We do not have to start frontend as it is packaged with the spring boot jar file.
> 
> User with ADMIN role will be pre-populated in the database to ensure only one admin exists at a time
