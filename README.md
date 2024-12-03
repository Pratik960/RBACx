## Role Based Access Control System (RBACx)

RBACx is a secure Role-Based Access Control (RBAC) system built with React, Spring Boot, Spring Security, JWT tokens, MySQL, Webpack, and Swagger. The system implements authentication, authorization, and role-based access for different user types with varying permissions.

This project is aimed at demonstrating the principles of Authentication, Authorization, and Role-Based Access Control (RBAC) by allowing users to register, log in, and access resources based on their assigned roles.


### User Roles and Permissions

| **Role**       | **Permissions**                                                                                                                                                                                                                                                                                                  |
|----------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **ROLE_USER**  | - Edit own profile<br>- Mark assigned tasks as completed<br>- Register<br>- Log in<br>- Log out                                                                                                                                                                                                                  |
| **ROLE_EMP**   | - Assign tasks to active users<br>- View pending tasks and their assignments<br>- View assigned tasks<br>- View completed tasks and who completed them<br>- Register<br>- Log in<br>- Log out                                                                                                               |
| **ROLE_ADMIN** | - Create new tasks<br>- Delete users from the system<br>- Delete employees from the system<br>- Register<br>- Log in<br>- Log out                                                                                             |



## Installation & Usage

### 1. Clone the Repository
Clone the repository to your local machine:

```bash
  git clone https://github.com/yourusername/rbacx.git
```

### 2. Database Setup (MySQL)

 2.1 Create a MySQL database:
```bash
CREATE DATABASE rbacx;
```
2.2 Set up the database connection in the application.properties of the Spring Boot backend (replace your_username and your_password with your username and password):
```bash
spring.datasource.username=your_username
spring.datasource.password=your_password

```

### 3. Backend Setup (Spring Boot)
Navigate to cloned repository

```bash
  cd rbacx
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

**Note:**
- We do not have to start frontend as it is packaged with the spring boot jar file.
- User with ADMIN role will be Pre-populated in the database to ensure only one admin exists at a time
