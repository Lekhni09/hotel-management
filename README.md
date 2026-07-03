# 🏨 Hotel Management System

A desktop Hotel Management System built with **Core Java**, **JavaFX**, **JDBC**, and **MySQL**, following **SOLID principles** and a clean layered architecture.

---

## 📸 Screenshots

> *(Add screenshots of your app here after pushing)*

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 17+ | Core language |
| JavaFX 21 | Desktop UI framework |
| JDBC | Database connectivity |
| MySQL | Relational database |
| Maven | Dependency management |

---

## ✨ Features

- 📊 **Dashboard** — Live stats: total rooms, available, occupied, revenue
- 🚪 **Room Management** — Add, view, and delete rooms (Single / Double / Suite)
- 📅 **Booking System** — Create bookings with auto-calculated total amount
- ✅ **Check-in / Check-out** — Automatically updates room availability
- ❌ **Cancel Booking** — Frees the room instantly
- 👥 **Customer Management** — View all customers, search by phone number
- 🧾 **Billing** — Generate invoice for any booking by ID

---

## 🏗️ Project Architecture

```
com.hotel/
├── config/          → Database connection (DatabaseConfig.java)
├── model/           → Plain Java models (Room, Customer, Booking)
├── dao/             → DAO interfaces (RoomDAO, CustomerDAO, BookingDAO)
├── dao/impl/        → JDBC implementations
├── service/         → Business logic (RoomService, BookingService)
└── ui/
    ├── MainLayout   → Sidebar navigation
    └── controllers/ → JavaFX screens (Dashboard, Rooms, Bookings, Customers, Billing)
```

---

## 🔄 Request Flow

```
JavaFX UI → Service Layer → DAO Layer → MySQL Database
```

---

## ⚙️ SOLID Principles Applied

- **S** — Each class has one responsibility (DAO only talks to DB, Service only has business logic)
- **O** — New room types can be added without modifying existing code
- **L** — DAO implementations can be swapped without breaking anything
- **I** — Separate interfaces for each entity (RoomDAO, CustomerDAO, BookingDAO)
- **D** — Service layer depends on DAO interfaces, not concrete implementations

---

## 🚀 How to Run

### Prerequisites
- Java 17+
- MySQL 8+
- Maven

### Setup

1. Clone the repository:
```bash
git clone https://github.com/yourusername/hotel-management-system.git
```

2. Create the database — run `database/setup.sql` in MySQL Workbench

3. Update DB credentials in `src/main/java/com/hotel/config/DatabaseConfig.java`:
```java
private static final String USER     = "root";
private static final String PASSWORD = "your_password";
```

4. Run the app:
```bash
mvn javafx:run
```

---

## 📂 Database Schema

```sql
rooms     (id, room_number, type, price_per_night, is_available)
customers (id, name, phone, email, id_proof)
bookings  (id, room_id, customer_id, check_in, check_out, total_amount, status)
```

---

## 👩‍💻 Author

Built by [Your Name] — Java Backend Developer
