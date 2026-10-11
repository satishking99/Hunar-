# Hunar Social Platform & AI Robot Assistant — REST API Documentation

**Base URL**: `http://localhost:5000/api/v1`  
**Authentication**: Bearer Token in `Authorization: Bearer <accessToken>` header.  
**Core Privacy Philosophy**: Public surfaces (Home, Reels, Profiles) **never** expose numerical counts for likes, views, comments, shares, saves, or follower/following lists. Creators access their personal metrics exclusively via their private Creator Dashboard.

---

## 1. System Health Check

### `GET /api/health`
Checks backend and database connectivity status.
- **Auth**: None
- **Response**:
```json
{
  "status": "healthy",
  "timestamp": "2026-10-10T00:41:00.000Z",
  "uptimeSeconds": 142,
  "environment": "development",
  "database": "connected",
  "service": "Hunar Social & AI Robot Assistant Backend",
  "version": "1.0.0"
}
```

---

## 2. Authentication & Authorization

### `POST /api/v1/auth/register`
Creates a new creator account.
- **Rate Limit**: 10 req/min
- **Body**:
```json
{
  "email": "creator@hunar.app",
  "password": "Password123!",
  "name": "Kavita Verma",
  "handle": "@kavita_crafts",
  "category": "CRAFT",
  "bio": "Traditional handloom weaving."
}
```
- **Response (201 Created)**:
```json
{
  "success": true,
  "message": "Account created successfully",
  "data": {
    "user": {
      "id": "u_abc123",
      "email": "creator@hunar.app",
      "name": "Kavita Verma",
      "handle": "@kavita_crafts",
      "role": "creator",
      "category": "CRAFT"
    },
    "tokens": {
      "accessToken": "eyJhbGciOi...",
      "refreshToken": "4a8c9e...",
      "tokenType": "Bearer"
    }
  }
}
```

### `POST /api/v1/auth/login`
Authenticates a user with email/handle and password.
- **Body**:
```json
{
  "identifier": "satishhp2000@gmail.com",
  "password": "Hunar@2026"
}
```
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Logged in successfully",
  "data": {
    "user": {
      "id": "u_satish",
      "email": "satishhp2000@gmail.com",
      "name": "Satish Kumar",
      "handle": "@satish_creator",
      "avatarEmoji": "🏏",
      "verifiedSkillTitle": "Fast Bowling & Athletic Conditioning",
      "talentScore": 98,
      "role": "creator",
      "category": "SPORTS"
    },
    "tokens": {
      "accessToken": "eyJhbGciOi...",
      "refreshToken": "7f21a4...",
      "tokenType": "Bearer"
    }
  }
}
```

### `POST /api/v1/auth/refresh`
Rotates refresh token and issues a new access token.
- **Body**:
```json
{
  "refreshToken": "7f21a4..."
}
```

### `POST /api/v1/auth/logout`
Revokes the refresh token and terminates the session.
- **Auth**: Bearer Token
- **Body**: `{ "refreshToken": "..." }`

---

## 3. Profiles & Privacy

### `GET /api/v1/users/profile/:id`
Retrieves a public creator profile with privacy enforcement.
- **Response**:
```json
{
  "success": true,
  "data": {
    "id": "u_satish",
    "name": "Satish Kumar",
    "handle": "@satish_creator",
    "avatar_emoji": "🏏",
    "bio": "Professional fast bowler.",
    "verified_skill_title": "Fast Bowling & Athletic Conditioning",
    "talent_score": 98,
    "worksCount": 8,
    "followers": "Private 🔒",
    "following": "Private 🔒",
    "friends": "Hidden 🔒"
  }
}
```

### `PUT /api/v1/users/profile`
Updates authenticated creator profile.
- **Auth**: Bearer Token
- **Body**: `{ "name": "...", "bio": "...", "avatarEmoji": "🏏", "verifiedSkillTitle": "..." }`

### `GET /api/v1/users/privacy` & `PUT /api/v1/users/privacy`
Manages privacy switches: `hide_public_stats`, `hide_friends_and_following`, `screenshot_protection_enabled`.

---

## 4. Posts, Videos & Reels

### `GET /api/v1/posts`
Feed of talent works with category and type filtering.
- **Query Params**: `category=SPORTS`, `type=video`, `search=yorker`, `limit=20`, `offset=0`
- **Response**:
```json
{
  "success": true,
  "data": [
    {
      "id": "p_1",
      "title": "Fast Bowling Yorker Technique & Seam Position",
      "description": "Release mechanics breakdown.",
      "contentType": "video",
      "category": "SPORTS",
      "duration": "0:45",
      "talentMeritScore": 98,
      "createdAt": 1728518400000,
      "creator": {
        "id": "u_satish",
        "name": "Satish Kumar",
        "handle": "@satish_creator",
        "avatarEmoji": "🏏",
        "talentScore": 98
      },
      "isSaved": false
    }
  ]
}
```

### `POST /api/v1/posts`
Publishes verified skill work.
- **Auth**: Bearer Token
- **Body**:
```json
{
  "title": "Handmade Terracotta Pottery",
  "description": "Manual clay centering without machines.",
  "category": "CRAFT",
  "contentType": "video",
  "duration": "0:52",
  "mediaUrl": "https://..."
}
```

---

## 5. Private Saves & History

### `GET /api/v1/saved`
Retrieves authenticated user's private saved videos.
- **Auth**: Bearer Token

### `POST /api/v1/saved/:postId` & `DELETE /api/v1/saved/:postId`
Privately bookmarks or removes post from user collection.

### `GET /api/v1/history` & `POST /api/v1/history`
Private watch history for photos, videos, and reels.

---

## 6. Private Messaging

### `GET /api/v1/messages/conversations`
List user's active private conversations with unread counts.

### `POST /api/v1/messages`
Sends end-to-end authorized private message.
- **Body**: `{ "recipient_id": "u_satish", "content": "Hi Satish!" }`

---

## 7. AI Robot Assistant Endpoints

### `POST /api/v1/robot/command`
Processes user text or voice command.
- **Auth**: Bearer Token
- **Body**:
```json
{
  "query": "Send message to Satish"
}
```
- **Response**:
```json
{
  "success": true,
  "data": {
    "commandId": "cmd_812a...",
    "intent": "MESSAGE_CONTACT",
    "responseText": "Prepared message 'Hi' for Satish Kumar (+91 98765 43210). User confirmation required.",
    "requiresConfirmation": true,
    "status": "pending",
    "actionPayload": {
      "recipientName": "Satish Kumar",
      "recipientPhone": "+91 98765 43210",
      "recipientHandle": "@satish_creator",
      "message": "Hi",
      "supportedIntents": ["ACTION_SENDTO", "smsto:+919876543210"]
    }
  }
}
```

### `POST /api/v1/robot/confirm-action`
Explicit user authorization for sensitive actions.
- **Body**: `{ "commandId": "cmd_812a...", "approved": true }`

### `GET /api/v1/robot/sports-videos`
Curated sports video catalog query.
- **Query Params**: `query=cricket`

### `GET /api/v1/robot/settings` & `PUT /api/v1/robot/settings`
Fetches and updates assistant configuration:
- `is_robot_mode_on`: `0 | 1`
- `lap_duration_seconds`: `300.0` (Exact 5-minute lap)
- `speed_multiplier`: `1.0`
- `voice_commands_enabled`: `0 | 1`
- `messaging_permission_granted`: `0 | 1`
- `app_opening_permission_granted`: `0 | 1`
- `screen_navigation_allowed`: `0 | 1`

---

## 8. Private Creator Dashboard & Earnings

### `GET /api/v1/analytics/dashboard`
Protected endpoint: only account owner can view their private metrics.
- **Auth**: Bearer Token
- **Response**:
```json
{
  "success": true,
  "data": {
    "totalLikes": 14200,
    "totalViews": 185000,
    "totalComments": 3200,
    "totalShares": 4100,
    "totalSaves": 8900,
    "totalWatchTimeMinutes": 4200.5,
    "avgWatchDurationSeconds": 45.2,
    "followerGrowthPercent": 12.8,
    "estimatedEarnings": 18350.0,
    "withdrawableBalance": 14850.0,
    "totalPaidOut": 3500.0,
    "videoCount": 4
  }
}
```

### `POST /api/v1/analytics/withdraw`
Requests UPI payout.
- **Body**: `{ "amount": 1000, "upiId": "satish@upi" }`
