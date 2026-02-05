# Mini-ATS REST API Documentation

Base URL: `http://localhost:8080/api`

## Response Format

All API responses follow this standard format:

```json
{
  "success": true,
  "data": { ... },
  "error": null,
  "timestamp": "2026-02-05T13:45:30.123Z"
}
```

Error responses:
```json
{
  "success": false,
  "data": null,
  "error": "Error message here",
  "timestamp": "2026-02-05T13:45:30.123Z"
}
```

---

## 🏢 Organizations

### Get All Organizations
```http
GET /api/organizations
```

### Get Organization by ID
```http
GET /api/organizations/{id}
```

### Create Organization
```http
POST /api/organizations
Content-Type: application/json

{
  "name": "Tech Corp"
}
```

### Update Organization
```http
PUT /api/organizations/{id}
Content-Type: application/json

{
  "name": "Tech Corp Updated"
}
```

### Delete Organization
```http
DELETE /api/organizations/{id}
```

---

## 👥 Users

### Get All Users
```http
GET /api/users
```

### Get User by Email
```http
GET /api/users/email/{email}
```

### Get Users by Organization
```http
GET /api/users/organization/{organizationId}
```

### Create User
```http
POST /api/users
Content-Type: application/json

{
  "organizationId": "11111111-1111-1111-1111-111111111111",
  "email": "user@example.com",
  "role": "USER",
  "fullName": "John Doe"
}
```

### Create Admin User
```http
POST /api/users/admin
Content-Type: application/json

{
  "organizationId": "11111111-1111-1111-1111-111111111111",
  "email": "admin@example.com",
  "fullName": "Admin User"
}
```

---

## 💼 Jobs

### Get All Jobs for Organization
```http
GET /api/jobs/organization/{organizationId}
```

### Get Active Jobs
```http
GET /api/jobs/organization/{organizationId}/active
```

### Search Jobs by Title
```http
GET /api/jobs/organization/{organizationId}/search?title=developer
```

### Create Job
```http
POST /api/jobs
Content-Type: application/json

{
  "organizationId": "11111111-1111-1111-1111-111111111111",
  "title": "Senior Java Developer",
  "description": "We are looking for...",
  "department": "Engineering",
  "location": "Stockholm",
  "status": "ACTIVE",
  "createdBy": "22222222-2222-2222-2222-222222222222"
}
```

### Update Job
```http
PUT /api/jobs/{id}
Content-Type: application/json

{
  "organizationId": "11111111-1111-1111-1111-111111111111",
  "title": "Senior Java Developer (Updated)",
  "description": "Updated description",
  "department": "Engineering",
  "location": "Stockholm",
  "status": "ACTIVE"
}
```

### Close Job
```http
PATCH /api/jobs/{id}/close
```

### Get Job Statistics
```http
GET /api/jobs/organization/{organizationId}/count
GET /api/jobs/organization/{organizationId}/active/count
```

---

## 👤 Candidates

### Get All Candidates for Organization
```http
GET /api/candidates/organization/{organizationId}
```

### Search Candidates by Name
```http
GET /api/candidates/organization/{organizationId}/search?name=erik
```

### Search by LinkedIn
```http
GET /api/candidates/organization/{organizationId}/linkedin?keyword=linkedin.com/in/eriksvensson
```

### Create Candidate
```http
POST /api/candidates
Content-Type: application/json

{
  "organizationId": "11111111-1111-1111-1111-111111111111",
  "fullName": "Anna Andersson",
  "email": "anna@example.com",
  "phone": "+46701234567",
  "linkedinUrl": "https://linkedin.com/in/annaandersson",
  "notes": "Great candidate with 5 years experience"
}
```

### Update Candidate
```http
PUT /api/candidates/{id}
Content-Type: application/json

{
  "organizationId": "11111111-1111-1111-1111-111111111111",
  "fullName": "Anna Andersson",
  "email": "anna.new@example.com",
  "phone": "+46701234567",
  "linkedinUrl": "https://linkedin.com/in/annaandersson",
  "notes": "Updated notes"
}
```

---

## 📊 Applications (Kanban Board)

### Get Applications for Organization (Full Kanban)
```http
GET /api/applications/organization/{organizationId}
```

### Get Applications for Job (Job-specific Kanban)
```http
GET /api/applications/job/{jobId}
```

### Get Applications by Status (Kanban Column)
```http
GET /api/applications/organization/{organizationId}/status/NEW
GET /api/applications/organization/{organizationId}/status/SCREENING
GET /api/applications/organization/{organizationId}/status/INTERVIEW
GET /api/applications/organization/{organizationId}/status/OFFER
GET /api/applications/organization/{organizationId}/status/REJECTED
```

### Search Applications by Candidate Name
```http
GET /api/applications/organization/{organizationId}/search?candidateName=anna
```

### Create Application (Add Candidate to Job)
```http
POST /api/applications
Content-Type: application/json

{
  "jobId": "33333333-3333-3333-3333-333333333333",
  "candidateId": "44444444-4444-4444-4444-444444444444",
  "status": "NEW",
  "notes": "Applied via website"
}
```

### Update Application Status (Move in Kanban)
```http
PATCH /api/applications/{id}/status
Content-Type: application/json

{
  "status": "SCREENING",
  "notes": "Moved to screening after initial review"
}
```

### Advance Application to Next Stage
```http
PATCH /api/applications/{id}/advance
```

### Reject Application
```http
PATCH /api/applications/{id}/reject
Content-Type: application/json

{
  "reason": "Not enough experience with Java"
}
```

### Make Offer
```http
PATCH /api/applications/{id}/offer
Content-Type: application/json

{
  "notes": "Offering 650,000 SEK base salary"
}
```

### Get Kanban Statistics for Job
```http
GET /api/applications/job/{jobId}/stats
```

Response:
```json
{
  "success": true,
  "data": {
    "newCount": 5,
    "screeningCount": 3,
    "interviewCount": 2,
    "offerCount": 1,
    "rejectedCount": 4
  },
  "error": null,
  "timestamp": "2026-02-05T13:45:30.123Z"
}
```

---

## 🏥 Health & Monitoring

### Basic Health Check
```http
GET /api/health
```

### Detailed Status
```http
GET /api/health/status
```

### Readiness Probe
```http
GET /api/health/ready
```

### Liveness Probe
```http
GET /api/health/live
```

---

## 🎯 Kanban Filtering Examples

### Full Organization Kanban Board
```http
GET /api/applications/organization/11111111-1111-1111-1111-111111111111
```
Returns all applications across all jobs in the organization.

### Single Job Kanban
```http
GET /api/applications/job/33333333-3333-3333-3333-333333333333
```
Returns all applications for a specific job.

### Filter by Job within Organization
```http
GET /api/applications/organization/11111111-1111-1111-1111-111111111111/job/33333333-3333-3333-3333-333333333333
```
Returns applications for a specific job, verified to belong to the organization.

### Get Specific Kanban Column
```http
GET /api/applications/job/33333333-3333-3333-3333-333333333333/status/INTERVIEW
```
Returns only applications in INTERVIEW stage for a job.

---

## 📝 Application Status Flow

```
NEW → SCREENING → INTERVIEW → OFFER
  ↓       ↓           ↓         ↓
         REJECTED (terminal)
```

Status values:
- `NEW` - Initial stage
- `SCREENING` - Under review
- `INTERVIEW` - In interview process
- `OFFER` - Offer extended
- `REJECTED` - Application rejected (terminal)

---

## 🔧 Testing with cURL

### Create Organization
```bash
curl -X POST http://localhost:8080/api/organizations \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Company"}'
```

### Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "organizationId": "11111111-1111-1111-1111-111111111111",
    "email": "test@example.com",
    "role": "USER",
    "fullName": "Test User"
  }'
```

### Create Job
```bash
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "organizationId": "11111111-1111-1111-1111-111111111111",
    "title": "Backend Developer",
    "description": "Looking for a backend developer",
    "department": "Engineering",
    "location": "Remote",
    "status": "ACTIVE"
  }'
```

### Create Candidate
```bash
curl -X POST http://localhost:8080/api/candidates \
  -H "Content-Type: application/json" \
  -d '{
    "organizationId": "11111111-1111-1111-1111-111111111111",
    "fullName": "Test Candidate",
    "email": "candidate@example.com",
    "linkedinUrl": "https://linkedin.com/in/testcandidate"
  }'
```

### Create Application
```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -d '{
    "jobId": "33333333-3333-3333-3333-333333333333",
    "candidateId": "44444444-4444-4444-4444-444444444444",
    "status": "NEW",
    "notes": "Applied through website"
  }'
```