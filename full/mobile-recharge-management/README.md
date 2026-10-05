# Mobile Recharge Management System

A complete Java Full Stack project using:

- Java 17
- Spring Boot
- Spring JDBC / REST API
- MySQL 8+
- HTML5 + CSS3 + Vanilla JavaScript
- Maven

## Required database features

This project explicitly implements:

1. JOIN query: recharge details with customer and plan information.
2. Subquery: plans used more than average.
3. Stored procedure: `perform_recharge`.
4. Stored function: `calculate_recharge_validity`.
5. Trigger: `trg_recharges_after_insert` updates mobile validity/status.
6. REST API.
7. Frontend dashboard and management screens.

## Extra features

- Dashboard statistics
- Customer search
- Mobile-number search
- Active/expired status
- Recharge history
- Plan listing
- Most-used / above-average plans
- Payment method and transaction reference
- Client-side and server-side validation
- Responsive UI
- Error handling
- API health endpoint

## 1. Prerequisites

Install:

- JDK 17
- Maven 3.9+
- MySQL 8.0+

Check:

```bash
java -version
mvn -version
mysql --version
```

## 2. Create the database

Open MySQL Workbench and run:

`database/schema.sql`

Then run:

`database/sample_data.sql`

The schema creates:

- CUSTOMERS
- MOBILE_NUMBERS
- PLANS
- RECHARGES

and the required function, procedure, trigger, views and indexes.

## 3. Configure MySQL

Open:

`src/main/resources/application.properties`

Change:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

The default database is:

`mobile_recharge_db`

## 4. Run backend

From the project folder:

```bash
mvn clean spring-boot:run
```

Backend:

http://localhost:8080

The frontend is served by Spring Boot at:

http://localhost:8080/

## 5. Main API endpoints

### Dashboard

GET `/api/dashboard`

### Customers

GET `/api/customers`

GET `/api/customers/{id}`

POST `/api/customers`

### Mobile numbers

GET `/api/mobiles`

GET `/api/mobiles/search?keyword=987`

POST `/api/mobiles`

### Plans

GET `/api/plans`

GET `/api/plans/above-average-usage`

### Recharges

GET `/api/recharges`

GET `/api/recharges/{id}`

POST `/api/recharges`

Example:

```json
{
  "mobileId": 1,
  "planId": 3,
  "paymentMethod": "UPI"
}
```

The backend calls the MySQL stored procedure. The procedure inserts the recharge and the trigger updates mobile validity/status.

## 6. Important SQL requirement mapping

### JOIN

`RechargeRepository.findRechargeDetails()` executes a JOIN between:

RECHARGES -> MOBILE_NUMBERS -> CUSTOMERS -> PLANS

### Subquery

`PlanRepository.findAboveAverageUsage()` finds plans whose recharge count is greater than the average recharge count across plans.

### Procedure

`perform_recharge(...)`

### Function

`calculate_recharge_validity(...)`

### Trigger

`trg_recharges_after_insert`

## 7. Default sample login

This project intentionally does NOT add a fake authentication system because the requested requirements are recharge/database/REST/frontend features. Authentication can be added later using Spring Security + JWT.

## 8. Project structure

```text
mobile-recharge-management/
├── database/
│   ├── schema.sql
│   └── sample_data.sql
├── src/
│   ├── main/
│   │   ├── java/com/monika/recharge/
│   │   │   ├── MobileRechargeApplication.java
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           ├── index.html
│   │           ├── css/style.css
│   │           └── js/app.js
├── pom.xml
└── README.md
```

## 9. Docker later

The project is kept Docker-friendly. You can later add:

- MySQL container
- Spring Boot container
- Optional Nginx container

without changing the core application.

## 10. Troubleshooting

### Access denied for MySQL

Check username/password in `application.properties`.

### Unknown database

Run `database/schema.sql`.

### Stored procedure not found

Run `database/schema.sql` again after selecting `mobile_recharge_db`.

### Port 8080 already in use

Change:

```properties
server.port=8081
```

Then open:

http://localhost:8081/

## Academic requirement

This project is suitable for demonstrating:

- DBMS JOIN
- DBMS subquery
- Stored procedure
- Stored function
- Trigger
- Java JDBC
- REST API
- Full-stack integration
- CRUD
- MySQL database design
