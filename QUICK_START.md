# 🚀 Quick Start Guide - Mini-ATS

## Prerequisites Checklist

- [x] Java 17 installed
- [x] Maven 3.8+ installed
- [ ] Supabase project created
- [ ] Database schema created in Supabase
- [ ] Seed data inserted in Supabase
- [ ] .env file configured with Supabase credentials

---

## Step 1: Configure Environment

Edit the `.env` file in the project root:

```bash
# Copy your values from Supabase dashboard
SUPABASE_URL=https://your-project-id.supabase.co
SUPABASE_ANON_KEY=eyJhbGc...your-anon-key
SUPABASE_SERVICE_ROLE_KEY=eyJhbGc...your-service-role-key

DB_URL=jdbc:postgresql://db.your-project-id.supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=your-database-password

JWT_SECRET=change-this-to-a-secure-random-string-at-least-256-bits
```

---

## Step 2: Build the Project

```bash
cd mini-ats
mvn clean install
```

If you see errors about dependencies, try:
```bash
mvn clean install -U
```

---

## Step 3: Start the Application

```bash
mvn spring-boot:run
```

You should see output like:
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.2)

2026-02-05 14:30:00 - INFO  - Starting MiniAtsApplication...
2026-02-05 14:30:02 - INFO  - Tomcat started on port(s): 8080 (http)
2026-02-05 14:30:02 - INFO  - Started MiniAtsApplication in 2.5 seconds
```

---

## Step 4: Test the API

### Option A: Using cURL

**Test Health Endpoint:**
```bash
curl http://localhost:8080/api/health
```

Expected response:
```json
{
  "success": true,
  "data": "OK",
  "error": null,
  "timestamp": "2026-02-05T14:30:00.123Z"
}
```

**Get Test Organization:**
```bash
curl http://localhost:8080/api/organizations/11111111-1111-1111-1111-111111111111
```

**Get Test User:**
```bash
curl http://localhost:8080/api/users/email/admin@acme.com
```

**Get Test Jobs:**
```bash
curl http://localhost:8080/api/jobs/organization/11111111-1111-1111-1111-111111111111
```

**Get Test Candidates:**
```bash
curl http://localhost:8080/api/candidates/organization/11111111-1111-1111-1111-111111111111
```

**Get Test Applications (Kanban):**
```bash
curl http://localhost:8080/api/applications/organization/11111111-1111-1111-1111-111111111111
```

### Option B: Using Browser

Open your browser and visit:
- http://localhost:8080/api/health
- http://localhost:8080/api/health/status
- http://localhost:8080/api/organizations

---

## Step 5: Test Kanban Functionality

### Get Kanban Board for Test Job
```bash
curl http://localhost:8080/api/applications/job/33333333-3333-3333-3333-333333333333
```

### Get Kanban Statistics
```bash
curl http://localhost:8080/api/applications/job/33333333-3333-3333-3333-333333333333/stats
```

### Move Application to Next Stage
```bash
curl -X PATCH http://localhost:8080/api/applications/{application-id}/advance
```

### Filter Kanban by Status
```bash
curl http://localhost:8080/api/applications/organization/11111111-1111-1111-1111-111111111111/status/NEW
```

---

## Step 6: Create Your Own Data

### 1. Create a New Organization
```bash
curl -X POST http://localhost:8080/api/organizations \
  -H "Content-Type: application/json" \
  -d '{"name": "My Company"}'
```

Save the returned `id` - you'll need it!

### 2. Create a User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "organizationId": "YOUR-ORG-ID-HERE",
    "email": "you@yourcompany.com",
    "role": "USER",
    "fullName": "Your Name"
  }'
```

### 3. Create a Job
```bash
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "organizationId": "YOUR-ORG-ID-HERE",
    "title": "Software Engineer",
    "description": "Join our team!",
    "department": "Engineering",
    "location": "Stockholm",
    "status": "ACTIVE"
  }'
```

Save the job `id`!

### 4. Add a Candidate
```bash
curl -X POST http://localhost:8080/api/candidates \
  -H "Content-Type: application/json" \
  -d '{
    "organizationId": "YOUR-ORG-ID-HERE",
    "fullName": "John Doe",
    "email": "john@example.com",
    "phone": "+46701234567",
    "linkedinUrl": "https://linkedin.com/in/johndoe",
    "notes": "Great developer"
  }'
```

Save the candidate `id`!

### 5. Create an Application (Add to Kanban)
```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -d '{
    "jobId": "YOUR-JOB-ID-HERE",
    "candidateId": "YOUR-CANDIDATE-ID-HERE",
    "status": "NEW",
    "notes": "First application!"
  }'
```

### 6. View Your Kanban Board
```bash
curl http://localhost:8080/api/applications/organization/YOUR-ORG-ID-HERE
```

---

## Common Issues & Solutions

### Issue: Port 8080 already in use
**Solution:** Change the port in `application.yml`:
```yaml
server:
  port: 8081
```

### Issue: Cannot connect to Supabase
**Solution:**
1. Check your `.env` file has correct values
2. Verify Supabase project is running
3. Check network connectivity

### Issue: Database connection errors
**Solution:**
1. Verify `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` in `.env`
2. Check Supabase database is active
3. Verify RLS policies are created

### Issue: Maven build fails
**Solution:**
```bash
# Clean and rebuild
mvn clean install -U

# Skip tests if needed
mvn clean install -DskipTests
```

---

## Next Steps

1. ✅ Test all API endpoints using the examples above
2. 📱 Build Frontend in Lovable (React/Next.js)
3. 🔐 Add JWT authentication
4. 📊 Add more Kanban features (drag & drop, filters)
5. 🚀 Deploy to production

---

## API Documentation

Full API documentation is available in `API_DOCUMENTATION.md`

---

## Need Help?

Check the logs:
```bash
tail -f logs/mini-ats.log
```

Enable debug logging in `application.yml`:
```yaml
logging:
  level:
    com.miniats: DEBUG
```

---

## 🎉 Success!

If you can:
- ✅ Start the application without errors
- ✅ Access http://localhost:8080/api/health
- ✅ Get test data from the API
- ✅ Create new organizations, users, jobs, candidates
- ✅ View Kanban board with applications

**You're ready to build the frontend!** 🚀