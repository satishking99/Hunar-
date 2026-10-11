# Hunar Social Media Platform & AI Robot Assistant — Backend

Production-ready, privacy-first backend and AI Robot Assistant service for **HUNAR** ("Talent ki pehchaan, privacy ke saath").

---

## 🏛️ Architecture Overview

- **Runtime**: Node.js v22 (ES Modules)
- **Framework**: Express.js with modular REST routing and centralized error handling
- **Database**: SQLite 3 with Write-Ahead Logging (WAL) and strict foreign key constraints
- **Authentication**: Stateless JSON Web Tokens (short-lived 15m access tokens + rotating 7-day refresh tokens stored in database)
- **Password Security**: Salted Bcrypt (cost factor 10)
- **File Storage**: Multer with strict MIME verification (`image/jpeg`, `image/png`, `image/webp`, `video/mp4`) and 50MB file size limits
- **Rate Limiting**: In-memory sliding window rate limiter (120 req/min general, 10 req/min auth)
- **Security & Privacy**: Zero public display of likes, views, comments, shares, or follower lists. Strict account-owner authorization on Creator Analytics.

---

## 🚀 Beginner Quick-Start Guide

### 1. Prerequisites
Ensure Node.js (version 20+ or 22+) is installed:
```bash
node -v
npm -v
```

### 2. Installation
Navigate to the `backend` directory and install dependencies:
```bash
cd backend
npm install
```

### 3. Environment Configuration
Copy the template configuration to create your `.env` file:
```bash
cp .env.example .env
```
*(In production, generate secure random secrets for `JWT_ACCESS_SECRET` and `JWT_REFRESH_SECRET` using `openssl rand -base64 64`)*

### 4. Database Initialization
Run the database initialization script to apply the schema and seed initial verified talent creators:
```bash
npm run init-db
```
This creates `backend/data/hunar.sqlite` with WAL mode enabled.

### 5. Running the Backend Server
- **Development (with hot-reload)**:
  ```bash
  npm run dev
  ```
- **Production**:
  ```bash
  npm start
  ```
The server will start at `http://localhost:5000`.

### 6. Health Check Verification
Visit in your browser or run:
```bash
curl http://localhost:5000/api/health
```
Response:
```json
{
  "status": "healthy",
  "database": "connected",
  "service": "Hunar Social & AI Robot Assistant Backend",
  "version": "1.0.0"
}
```

---

## 🧪 Automated Testing

Execute the comprehensive test suite verifying authentication, privacy enforcement, posts, saved items, history, AI robot commands, and analytics:
```bash
npm test
```
All 15 critical user journey tests run against the local database in < 1 second.

---

## 🤖 AI Robot Assistant Features

The backend powers the on-screen AI Robot Assistant:
1. **Natural Language Command Processing** (`POST /api/v1/robot/command`):
   - Parsed intent detection for navigation, sports discovery, and contact messaging.
2. **Contact Satish Messaging Assistance**:
   - Prepares message `"Hi"` for contact **Satish Kumar** (`+91 98765 43210`, `@satish_creator`).
   - Requires explicit approval via `POST /api/v1/robot/confirm-action` before execution.
3. **Sports Masterclass Discovery** (`GET /api/v1/robot/sports-videos`):
   - Search across cricket bowling mechanics, football freestyle, pro kabaddi, sprint athletics, and kushti wrestling.
4. **Emergency Stop & Settings Control** (`GET / PUT /api/v1/robot/settings`):
   - Configures exact 5-minute lap (300 seconds), walking speed, and disengages touch lock.

---

## 🛡️ Security & Privacy Enforcement

1. **No Public Numerical Stats**:
   Public endpoints (`/api/v1/posts`, `/api/v1/users/profile/:id`) never return numerical counts for likes, views, comments, or shares.
2. **Hidden Friend & Follower Lists**:
   User networks and mutual connections are hidden behind privacy enforcement.
3. **Owner-Gated Creator Dashboard**:
   Analytics (`/api/v1/analytics/dashboard`) are strictly locked behind JWT authorization matching the creator's ID.
4. **Zero Secret Leakage**:
   Logs never print passwords, tokens, or API keys.

---

## 🚢 Production Deployment Instructions

### Option A: Docker Container
```dockerfile
FROM node:22-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
ENV NODE_ENV=production
EXPOSE 5000
CMD ["node", "src/server.js"]
```

### Option B: Cloud Run / VM / PM2
```bash
npm install -g pm2
pm2 start src/server.js --name "hunar-backend" -i max
pm2 save
```
Ensure persistent storage is mounted for `./data` and `./uploads`.
