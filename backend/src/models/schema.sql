-- ==============================================================================
-- HUNAR SOCIAL PLATFORM - DATABASE SCHEMA
-- Engine: SQLite 3 / WAL Mode with Foreign Keys
-- Privacy Principle: No public likes, views, comments, shares, or follower lists.
-- ==============================================================================

PRAGMA foreign_keys = ON;

-- 1. USERS & PROFILES TABLE
CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    name TEXT NOT NULL,
    handle TEXT UNIQUE NOT NULL,
    avatar_emoji TEXT DEFAULT '🇮🇳',
    profile_picture_url TEXT,
    bio TEXT DEFAULT 'Skill over looks. Real hard work & merit.',
    verified_skill_title TEXT DEFAULT 'Traditional Craft & Tech Creator',
    talent_score INTEGER DEFAULT 88,
    category TEXT DEFAULT 'TECH',
    role TEXT DEFAULT 'creator', -- 'creator', 'user', 'moderator', 'admin'
    is_verified INTEGER DEFAULT 1,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_handle ON users(handle);

-- 2. REFRESH TOKENS (Secure token rotation)
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash TEXT NOT NULL,
    expires_at INTEGER NOT NULL,
    created_at INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user ON refresh_tokens(user_id);

-- 3. POSTS, PHOTOS, VIDEOS & REELS TABLE
CREATE TABLE IF NOT EXISTS posts (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    description TEXT,
    content_type TEXT NOT NULL, -- 'photo', 'video', 'reel'
    media_url TEXT,
    thumbnail_url TEXT,
    category TEXT NOT NULL, -- 'TECH', 'ART', 'DANCE', 'MUSIC', 'COOKING', 'SPORTS', 'CRAFT'
    duration TEXT,
    talent_merit_score INTEGER DEFAULT 90,
    is_private INTEGER DEFAULT 0,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_posts_user ON posts(user_id);
CREATE INDEX IF NOT EXISTS idx_posts_category ON posts(category);
CREATE INDEX IF NOT EXISTS idx_posts_created ON posts(created_at DESC);

-- 4. SAVED POSTS (Private bookmarking only — never public)
CREATE TABLE IF NOT EXISTS saved_posts (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    post_id TEXT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    created_at INTEGER NOT NULL,
    UNIQUE(user_id, post_id)
);

CREATE INDEX IF NOT EXISTS idx_saved_user ON saved_posts(user_id);

-- 5. VIDEO & PHOTO WATCH HISTORY (Private to viewer)
CREATE TABLE IF NOT EXISTS view_history (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    post_id TEXT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    media_type TEXT NOT NULL,
    watch_time_seconds INTEGER DEFAULT 0,
    created_at INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_history_user ON view_history(user_id, created_at DESC);

-- 6. PRIVATE CONVERSATIONS
CREATE TABLE IF NOT EXISTS conversations (
    id TEXT PRIMARY KEY,
    user1_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user2_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    last_message TEXT,
    last_message_at INTEGER NOT NULL,
    created_at INTEGER NOT NULL,
    UNIQUE(user1_id, user2_id)
);

CREATE INDEX IF NOT EXISTS idx_conv_users ON conversations(user1_id, user2_id);

-- 7. PRIVATE MESSAGES
CREATE TABLE IF NOT EXISTS messages (
    id TEXT PRIMARY KEY,
    conversation_id TEXT NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    recipient_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    is_read INTEGER DEFAULT 0,
    created_at INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_messages_conv ON messages(conversation_id, created_at ASC);
CREATE INDEX IF NOT EXISTS idx_messages_recipient ON messages(recipient_id, is_read);

-- 8. NOTIFICATIONS
CREATE TABLE IF NOT EXISTS notifications (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    body TEXT NOT NULL,
    type TEXT NOT NULL, -- 'tip', 'save', 'merit', 'system', 'message'
    is_read INTEGER DEFAULT 0,
    created_at INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_notif_user ON notifications(user_id, is_read, created_at DESC);

-- 9. AI ROBOT ASSISTANT SETTINGS
CREATE TABLE IF NOT EXISTS robot_settings (
    user_id TEXT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    is_robot_mode_on INTEGER DEFAULT 0,
    voice_commands_enabled INTEGER DEFAULT 1,
    lap_duration_seconds REAL DEFAULT 300.0,
    speed_multiplier REAL DEFAULT 1.0,
    is_clockwise INTEGER DEFAULT 1,
    screen_navigation_allowed INTEGER DEFAULT 1,
    messaging_permission_granted INTEGER DEFAULT 1,
    app_opening_permission_granted INTEGER DEFAULT 1,
    touch_interception_active INTEGER DEFAULT 0,
    updated_at INTEGER NOT NULL
);

-- 10. AI ROBOT COMMANDS AUDIT HISTORY
CREATE TABLE IF NOT EXISTS robot_commands_history (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    raw_query TEXT NOT NULL,
    intent_type TEXT NOT NULL, -- 'MESSAGE_CONTACT', 'SPORTS_SEARCH', 'OPEN_APP', 'NAVIGATE', 'EMERGENCY_STOP', 'SPEED_CHANGE'
    action_target TEXT,
    was_confirmed INTEGER DEFAULT 0,
    status TEXT NOT NULL, -- 'executed', 'pending', 'rejected', 'canceled'
    created_at INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_robot_history_user ON robot_commands_history(user_id, created_at DESC);

-- 11. USER PRIVACY SETTINGS
CREATE TABLE IF NOT EXISTS user_privacy_settings (
    user_id TEXT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    hide_public_stats INTEGER DEFAULT 1,
    hide_friends_and_following INTEGER DEFAULT 1,
    allow_direct_messages INTEGER DEFAULT 1,
    screenshot_protection_enabled INTEGER DEFAULT 1,
    biometric_lock_required INTEGER DEFAULT 0,
    updated_at INTEGER NOT NULL
);

-- 12. PRIVATE CREATOR ANALYTICS & EARNINGS (Strictly accessible by account owner only)
CREATE TABLE IF NOT EXISTS creator_analytics (
    user_id TEXT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    total_likes INTEGER DEFAULT 0,
    total_views INTEGER DEFAULT 0,
    total_comments INTEGER DEFAULT 0,
    total_shares INTEGER DEFAULT 0,
    total_saves INTEGER DEFAULT 0,
    total_watch_time_minutes REAL DEFAULT 0.0,
    avg_watch_duration_seconds REAL DEFAULT 0.0,
    follower_growth_percent REAL DEFAULT 0.0,
    estimated_earnings REAL DEFAULT 0.0,
    withdrawable_balance REAL DEFAULT 0.0,
    total_paid_out REAL DEFAULT 0.0,
    updated_at INTEGER NOT NULL
);

-- 13. CREATOR PAYMENT & UPI PAYOUT HISTORY
CREATE TABLE IF NOT EXISTS creator_payment_history (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount REAL NOT NULL,
    upi_id TEXT NOT NULL,
    status TEXT NOT NULL, -- 'completed', 'pending', 'processing', 'rejected'
    reference_id TEXT NOT NULL,
    created_at INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_payments_user ON creator_payment_history(user_id, created_at DESC);

-- 14. MODERATION REPORTS
CREATE TABLE IF NOT EXISTS moderation_reports (
    id TEXT PRIMARY KEY,
    reporter_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reported_user_id TEXT REFERENCES users(id) ON DELETE CASCADE,
    reported_post_id TEXT REFERENCES posts(id) ON DELETE CASCADE,
    reason TEXT NOT NULL,
    details TEXT,
    status TEXT DEFAULT 'pending', -- 'pending', 'resolved', 'dismissed'
    created_at INTEGER NOT NULL
);

-- 15. BLOCKED USERS
CREATE TABLE IF NOT EXISTS blocked_users (
    id TEXT PRIMARY KEY,
    blocker_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    blocked_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at INTEGER NOT NULL,
    UNIQUE(blocker_id, blocked_id)
);

CREATE INDEX IF NOT EXISTS idx_blocks ON blocked_users(blocker_id, blocked_id);
