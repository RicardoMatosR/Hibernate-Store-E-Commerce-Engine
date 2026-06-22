# Hibernate E-Commerce Store

A robust, console-based e-commerce management system built with **Java** and **Hibernate ORM**. This project was developed to demonstrate enterprise-level backend architecture, focusing on secure transactions, data persistence, and clean code principles.

## 🚀 Key Features

*   **Role-Based Access Control (RBAC)**: Distinct permissions and dashboards for `Admin` and `Customer` profiles.
*   **Atomic Transactions (ACID)**: Secure purchasing flow that guarantees data integrity when deducting wallet balances and updating product stock simultaneously.
*   **External API Integration**: Automatically fetches and populates the database with real-time product data using the DummyJSON REST API via `HttpClient` and `Gson`.
*   **DAO Pattern Architecture**: Strict separation of concerns between the database layer (`UserDAO`, `ProductDAO`), business logic (`TiendaController`), and the console view (`Main`).
*   **Automated Seeding**: Automatically initializes default roles and a Super Admin account upon the first execution if the database is empty.
*   **Resource Management**: Safe and leak-free session handling using Java `try-with-resources`.

## 🛠️ Technology Stack

*   **Language**: Java (JDK 11+)
*   **ORM**: Hibernate 7
*   **Database**: MySQL
*   **Dependencies**: 
    *   Lombok (for boilerplate reduction)
    *   Gson (for JSON parsing)

## 🏗️ Architecture

The project strictly adheres to the **MVC (Model-View-Controller)** and **DAO (Data Access Object)** patterns:
*   `model/`: Contains the Hibernate Entities (`User`, `Product`, `Profile`) mapping directly to the MySQL database tables.
*   `dao/`: Contains the Data Access Objects managing Hibernate sessions and SQL queries.
*   `controller/`: The `TiendaController` acts as the bridge orchestrating data between the DAOs and the User Interface.
*   `Main.java`: The entry point and interactive console interface.

## 💻 How to Run

1. Clone the repository.
2. Ensure you have a local MySQL server running.
3. Configure your database credentials in `hibernate.cfg.xml`.
4. Compile and run `Main.java`. The application will automatically create the necessary tables and seed the initial `Admin` user (`admin@ces.com` / `admin123`).
=======
# Hibernate-Store-E-Commerce-Engine
E-commerce backend engine built with Java and Hibernate ORM. Includes ACID transactions, RBAC, and secure session management.
