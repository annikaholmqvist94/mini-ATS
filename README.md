# Mini-ATS - Applicant Tracking System

## 📋 Översikt

Ett mini-ATS (Applicant Tracking System) byggt med Java Spring Boot och Supabase som backend. Systemet stöder multi-tenant arkitektur där olika organisationer kan hantera sina rekryteringsprocesser isolerat.

## 🏗️ Arkitektur

### Design Patterns
- **Builder Pattern**: Alla domain entities använder immutable builders
- **Adapter Pattern**: Repository-lager separerar affärslogik från datakälla
- **DTO Pattern**: Immutable Data Transfer Objects för API-kommunikation
- **Dependency Injection**: Spring Boot IoC för loose coupling

### Teknisk Stack
- **Backend**: Java 17, Spring Boot 3.2.x
- **Database**: PostgreSQL via Supabase
- **Auth**: Supabase Authentication + JWT
- **Build Tool**: Maven

## 🚀 Setup

### 1. Förutsättningar
```bash
- Java 21+
- Maven 3.8+
- Supabase-konto
- Git
```

### 2. Klona & Konfigurera

```bash
# Klona projektet
git clone <repo-url>
cd mini-ats

# Kopiera och fyll i .env-filen
cp .env .env.local
```

### 3. Hämta Supabase-credentials

Gå till din Supabase-dashboard:
1. **Project Settings** → **API**
2. Kopiera:
    - `Project URL` → `SUPABASE_URL`
    - `anon/public key` → `SUPABASE_ANON_KEY`
    - `service_role key` → `SUPABASE_SERVICE_ROLE_KEY`

3. **Project Settings** → **Database**
4. Kopiera:
    - Connection string → `DB_URL`
    - Password → `DB_PASSWORD`

### 4. Uppdatera .env

```env
SUPABASE_URL=https://xxxxx.supabase.co
SUPABASE_ANON_KEY=eyJhbGc...
SUPABASE_SERVICE_ROLE_KEY=eyJhbGc...

DB_URL=jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=din-databas-password

JWT_SECRET=din-hemliga-nyckel-minst-256-bitar
```

### 5. Bygg & Kör

```bash
# Installera dependencies
mvn clean install

# Kör applikationen
mvn spring-boot:run
```

Applikationen startar på: `http://localhost:8080/api`

## 📊 Databasschema

### Entiteter

#### Organizations
- Root entity för multi-tenant
- Isolerar data mellan kunder

#### Users
- Kan vara ADMIN eller USER
- Kopplade till en Organization
- Admins kan impersonate

#### Jobs
- Tillhör en Organization
- Status: ACTIVE, CLOSED, DRAFT

#### Candidates
- Tillhör en Organization
- Innehåller LinkedIn-länk, CV, etc.

#### Applications
- Kopplar Candidate till Job (many-to-many)
- Kanban-status: NEW → SCREENING → INTERVIEW → OFFER / REJECTED

## 🔒 Säkerhet

### Row Level Security (RLS)
- All data är isolerad per Organization
- Policies kontrollerar åtkomst baserat på user's org
- Admins har full åtkomst via service role key

### Authentication Flow
1. Frontend loggar in via Supabase Auth
2. JWT token innehåller user email
3. Backend verifierar token och hämtar user från DB
4. RLS policies filtrerar data baserat på organization_id

## 🎯 Core Features (Status)

- [x] Databasschema i Supabase
- [x] RLS policies
- [x] Seed data
- [x] Domain models (immutable med Builder)
- [x] Enums (UserRole, JobStatus, ApplicationStatus)
- [ ] DTOs (nästa steg)
- [ ] Repository interfaces
- [ ] Supabase repository implementations
- [ ] Service layer
- [ ] REST Controllers
- [ ] Admin impersonation
- [ ] Frontend integration

## 📁 Projektstruktur

```
src/main/java/com/miniats/
├── domain/
│   ├── model/          # Immutable entities med Builder
│   └── enums/          # Status enumerations
├── dto/                # Data Transfer Objects
├── repository/         # Interface + Supabase implementations
├── service/            # Business logic
├── controller/         # REST endpoints
├── config/             # Spring configuration
└── exception/          # Error handling
```

## 🧪 Test Data

Admin user:
- Email: `admin@acme.com`
- Org: `Acme Corp`

Test job:
- Title: `Senior Java Developer`

Test kandidater:
- Erik Svensson
- Anna Andersson

## 📝 Nästa Steg

1. ✅ Skapa DTOs
2. ✅ Implementera Repository layer
3. ✅ Skapa Service layer
4. ✅ REST API Controllers
5. ⬜ Frontend i Lovable/React
6. ⬜ Deployment

## 🤝 Kontakt

Vid frågor eller problem, kontakta utvecklaren.