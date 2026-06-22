# Hibernate-Store-E-Commerce-Engine 🛍️

A robust backend management system developed in Java that utilizes Hibernate ORM for data persistence. This project implements an enterprise-level architecture, focusing on transactional integrity, role-based security, and efficient database session management.

##  Key Features

*   **RBAC (Role-Based Access Control)**: Authentication system with differentiated dashboards for Admin and Customer profiles.
*   **Atomic Transactions (ACID)**: Bulletproof purchasing logic: if anything fails during the checkout process (e.g., out of stock or insufficient funds), the database automatically reverts any changes (Rollback).
*   **External API Integration**: Automatic stock synchronization by consuming the DummyJSON API using `HttpClient` and mapping responses with `Gson`.
*   **DAO Architecture**: Strict separation of concerns between the data access layer (DAO), business logic layer (Controller), and the user interface (Main).
*   **Secure Session Management**: Implementation of `try-with-resources` to guarantee connection closures and prevent memory leaks.
*   **Automated Seeding**: Smart initialization of default roles and a "Super Admin" account upon system boot if the database is empty.

##  Technology Stack

*   **Language**: Java (JDK 17+)
*   **ORM**: Hibernate 7
*   **Database**: MySQL
*   **Project Manager**: Maven
*   **Optimization**: Lombok (Automated boilerplate code generation)
*   **Parsers**: Gson, org.json

## ⚙️ Installation and Setup

**Prerequisites:**
*   An active MySQL server running locally.
*   JDK 17 or higher installed.

**1. Clone the repository:**
```bash
git clone https://github.com/RicardoMatosR/Hibernate-Store-E-Commerce-Engine.git
cd Hibernate-Store-E-Commerce-Engine
```

**2. Database Configuration:**
Modify the `src/main/resources/hibernate.cfg.xml` file with your local MySQL server credentials:
```xml
<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/tienda_ces</property>
<property name="hibernate.connection.username">YOUR_USERNAME</property>
<property name="hibernate.connection.password">YOUR_PASSWORD</property>
```

**3. Compilation and Execution:**
If you are using Maven, simply run:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="Main"
```

## 📁 Project Structure

```text
Hibernate-Store-E-Commerce-Engine/
│
├── src/main/java/
│   ├── controller/      # Business logic and orchestration
│   ├── dao/             # Data access (Transactional CRUD operations)
│   ├── model/           # Hibernate Entities (JPA)
│   ├── database/        # HibernateUtil configuration
│   └── Main.java        # Console interface and system startup
│
├── src/main/resources/  # hibernate.cfg.xml
└── pom.xml              # Maven dependencies
```
