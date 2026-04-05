# 🔄 Skill Swap Platform (Spring Boot REST API)

## 📌 Project Overview

The **Skill Swap Platform** is a backend REST API built using Spring Boot that allows users to exchange skills with each other. Users can register, add skills, send swap requests, accept/reject requests, and provide reviews after successful exchanges.

This project follows a **real-world workflow-based design** instead of simple CRUD operations.

---

## 🎯 Features

### 👤 User Management

* Register new users
* Secure password storage (BCrypt)
* Role-based access (USER / ADMIN)

### 🧠 Skill Management

* Add skills
* Update skills (owner/admin only)
* Delete skills (owner/admin only)
* Prevent duplicate skills per user

### 🔄 Swap Request System

* Send swap requests
* Accept / Reject requests
* Workflow-based state management:

  * `PENDING → ACCEPTED / REJECTED`
* Prevent duplicate pending requests

### ⭐ Review System

* Add review after accepted swap
* Prevent duplicate reviews
* Only participants can review
* Update & delete reviews

---

## 🏗️ Architecture

The project follows **Layered Architecture**:

```text
Controller → Service → Repository → Database
```

* **Controller** → Handles HTTP requests
* **Service** → Business logic & validation
* **Repository** → Database interaction (JPA)

---

## 🔐 Security

* Authentication: **HTTP Basic Auth**
* Password Encryption: **BCrypt**
* Authorization: **Role-Based Access Control**

### Access Rules:

| Endpoint Type          | Access              |
| ---------------------- | ------------------- |
| Register               | Public              |
| Read / Create / Update | Authenticated Users |
| Delete                 | ADMIN only          |

---

## 🧩 Entities

* **User**
* **Skill**
* **SwapRequest**
* **Review**

### Relationships:

* User → Skills (One-to-Many)
* User → SwapRequests (Sender/Receiver)
* SwapRequest → Review

---

## 🔄 API Endpoints

### User

* `POST /users/register`
* `GET /users/{id}`
* `PUT /users/{id}`
* `DELETE /users/{id}` (ADMIN)

### Skill

* `POST /skills`
* `GET /skills`
* `PUT /skills/{id}`
* `DELETE /skills/{id}` (ADMIN/Owner)

### Swap Request

* `POST /swap-requests`
* `GET /swap-requests/{id}`
* `PUT /swap-requests/{id}/accept`
* `PUT /swap-requests/{id}/reject`

### Review

* `POST /reviews`
* `GET /reviews/user/{userId}`
* `PUT /reviews/{id}`
* `DELETE /reviews/{id}` (ADMIN)

---

## 🧪 Data Initialization

The project uses a **DataInitializer** to preload data:

### Default Users:

* **admin / admin123**
* **alice / alice123**
* **bob / bob123**

### Sample Skills:

* Java Programming
* React.js
* UI/UX Design
* SQL & Database Design

👉 This makes the system **demo-ready and testable**

---

## ⚠️ Validations Implemented

* Duplicate username/email prevention
* Duplicate skill prevention per user
* Duplicate swap request prevention (PENDING only)
* Review allowed only after accepted swap
* Authorization checks (owner/admin)

---

## 🚀 How to Run

1. Clone the repository:

```bash
git clone https://github.com/your-username/skill-swap-platform.git
```

2. Navigate to project:

```bash
cd skill-swap-platform
```

3. Run application:

```bash
mvn spring-boot:run
```

4. Test APIs using:

* Postman

---

## 📌 Technologies Used

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* H2 / MySQL
* Lombok

---

## 💡 Key Highlights

* Real-world workflow design (not just CRUD)
* Clean layered architecture
* Role-based security
* Data validation & consistency
* Duplicate prevention logic

---

## 🎯 Future Improvements

* JWT Authentication
* Frontend Integration
* Learning-only requests (without skill exchange)
* Notification system

---

## 👨‍💻 Author

**Krish Patel**

---

## ⭐ Final Note

This project is designed to demonstrate **practical backend development concepts**, including REST API design, security, and real-world business logic.
