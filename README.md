# Mini-ATS - Applicant Tracking System Backend

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-blue.svg)](https://supabase.com/)

> A modern, multi-tenant Applicant Tracking System built with Spring Boot and Supabase. Features a Kanban-style pipeline for managing recruitment workflows.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Deployment](#deployment)

---

## 🎯 Overview

Mini-ATS is a recruitment management platform for organizations to track job candidates through a visual Kanban pipeline. The system supports multi-tenancy with complete data isolation and role-based access control.

### Key Capabilities

- **Multi-tenant Architecture**: Organization-level data isolation
- **Kanban Pipeline**: Visual tracking (NEW → SCREENING → INTERVIEW → OFFER / REJECTED)
- **Admin Features**: Create organizations, manage users across tenants
- **RESTful API**: 63 endpoints for full CRUD operations
- **Supabase Integration**: PostgreSQL with Row Level Security

---

## ✨ Features

### User Management
✅ Admin and User roles  
✅ Organization-based access control  
✅ Admin can manage multiple organizations

### Job Management
✅ Create and manage job postings  
✅ Filter by status (ACTIVE/CLOSED/DRAFT)  
✅ Search by title and department

### Candidate Management
✅ Add candidates with LinkedIn profiles  
✅ Store contact information and notes  
✅ Search and filter capabilities

### Kanban Pipeline
✅ Visual tracking across 5 stages  
✅ Drag-and-drop status updates  
✅ Enriched data (candidate + job info)  
✅ Filter by job or candidate name  
✅ Pipeline statistics and metrics

---

## 🏗️ Architecture

### Design Patterns

- **Builder Pattern**: Immutable domain entities
- **Adapter Pattern**: Repository layer abstractions
- **DTO Pattern**: Immutable records for API

### Layers

```
┌─────────────────────────┐
│  REST Controllers (63)  │
├─────────────────────────┤
│    Service Layer (5)    │
├─────────────────────────┤
│   Repository Layer (5)  │
├─────────────────────────┤
│  Supabase REST Client   │
└─────────────────────────┘
```

---

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.2.2
- **Language**: Java 17
- **Build**: Maven 3.8+
- **Database**: PostgreSQL (Supabase)
- **Data Access**: Supabase REST API

---

## 🚀 Getting Started

### Prerequisites

```bash
java -version    # Java 17+
mvn -version     # Maven 3.8+
```

### Quick Start

1. **Clone repository**
```bash
git clone <repo-url>
cd mini-ats
```

2. **Configure environment** (create `.env` file)
```bash
SUPABASE_URL=https://xxxxx.supabase.co
SUPABASE_ANON_KEY=your-key
SUPABASE_SERVICE_ROLE_KEY=your-key
DB_URL=jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=your-password
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

3. **Build and run**
```bash
mvn clean install
mvn spring-boot:run
```

API available at: `http://localhost:8080/api`

### Verify

```bash
curl http://localhost:8080/api/health
# Expected: {"success":true,"data":"OK",...}
```

---

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Response Format
```json
{
  "success": true,
  "data": { ... },
  "error": null,
  "timestamp": "2026-02-05T..."
}
```

### Endpoints Summary

| Resource | Count | Description |
|----------|-------|-------------|
| Organizations | 6 | CRUD operations |
| Users | 10 | User management |
| Jobs | 14 | Job postings |
| Candidates | 9 | Candidate profiles |
| Applications | 20 | Kanban pipeline |
| Health | 4 | Monitoring |

**Total**: 63 REST endpoints

### Key Endpoints

```bash
# Kanban board
GET  /api/applications/organization/{orgId}
POST /api/applications
PATCH /api/applications/{id}/status

# Jobs
GET  /api/jobs/organization/{orgId}
POST /api/jobs

# Candidates
GET  /api/candidates/organization/{orgId}
POST /api/candidates
```

**Full documentation**: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

---

## 📁 Project Structure

```
mini-ats/
├── src/main/java/com/miniats/
│   ├── MiniAtsApplication.java
│   ├── config/                  # Security, Supabase
│   ├── controller/              # 6 REST controllers
│   ├── service/                 # 5 business logic services
│   ├── repository/              # Data access layer
│   │   ├── (5 interfaces)
│   │   └── impl/                # Supabase implementations
│   ├── domain/
│   │   ├── model/               # 5 immutable entities
│   │   └── enums/               # 3 enums
│   ├── dto/                     # 5 DTOs (records)
│   └── exception/               # Error handling
├── src/main/resources/
│   └── application.yml
├── .env                         # Environment config
├── pom.xml                      # Maven dependencies
└── README.md
```

---

## ⚙️ Configuration

### Environment Variables

```bash
# Supabase
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key
SUPABASE_SERVICE_ROLE_KEY=your-service-key

# Database
DB_URL=jdbc:postgresql://db.your-project.supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=your-password

# CORS (add frontend URLs)
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://your-frontend.com

# JWT (for future auth)
JWT_SECRET=your-secret-key
```

---

## 🚢 Deployment

### Build

```bash
mvn clean package
java -jar target/mini-ats-1.0.0.jar
```

### Platforms

- **Railway**: One-click deploy
- **Heroku**: `git push heroku main`
- **AWS**: Upload JAR to Elastic Beanstalk

### Checklist

- [ ] Update CORS with production URL
- [ ] Set production Supabase credentials
- [ ] Configure strong JWT secret
- [ ] Test all endpoints
- [ ] Enable HTTPS only

---

## 🔒 Security

**Current** (Development):
- CORS enabled
- No authentication (`.permitAll()`)
- RLS in database

**Production** (See [JWT_AUTH_GUIDE.md](JWT_AUTH_GUIDE.md)):
- JWT authentication
- Role-based access
- Rate limiting

---

## 📖 Documentation

- **[API_DOCUMENTATION.md](API_DOCUMENTATION.md)** - Complete API reference
- **[QUICK_START.md](QUICK_START.md)** - Setup guide
- **[INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)** - Frontend integration
- **[JWT_AUTH_GUIDE.md](JWT_AUTH_GUIDE.md)** - Authentication setup

---

## 📄 License

MIT License

---

**Built with ❤️ using Spring Boot and Supabase**
