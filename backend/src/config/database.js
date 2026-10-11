import { DatabaseSync } from 'node:sqlite';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import bcrypt from 'bcryptjs';
import { config } from './env.js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

let dbInstance = null;

export function getDatabase() {
  if (dbInstance) return dbInstance;

  const dataDir = path.dirname(config.dbPath);
  if (!fs.existsSync(dataDir)) {
    fs.mkdirSync(dataDir, { recursive: true });
  }

  dbInstance = new DatabaseSync(config.dbPath);

  // Performance and safety pragmas
  dbInstance.exec('PRAGMA foreign_keys = ON;');
  dbInstance.exec('PRAGMA journal_mode = WAL;');
  dbInstance.exec('PRAGMA synchronous = NORMAL;');

  // Run schema
  const schemaPath = path.resolve(__dirname, '../models/schema.sql');
  if (fs.existsSync(schemaPath)) {
    const schemaSql = fs.readFileSync(schemaPath, 'utf8');
    dbInstance.exec(schemaSql);
  }

  seedInitialData(dbInstance);

  return dbInstance;
}

function seedInitialData(db) {
  // Check if users already seeded
  const userCount = db.prepare('SELECT COUNT(*) as count FROM users').get().count;
  if (userCount > 0) return;

  console.log('[Database] Seeding initial Hunar verified talent creators...');

  const now = Date.now();
  const defaultHash = bcrypt.hashSync('Hunar@2026', config.bcryptSaltRounds);

  const insertUser = db.prepare(`
    INSERT INTO users (id, email, password_hash, name, handle, avatar_emoji, profile_picture_url, bio, verified_skill_title, talent_score, category, role, is_verified, created_at, updated_at)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
  `);

  const insertAnalytics = db.prepare(`
    INSERT INTO creator_analytics (user_id, total_likes, total_views, total_comments, total_shares, total_saves, total_watch_time_minutes, avg_watch_duration_seconds, follower_growth_percent, estimated_earnings, withdrawable_balance, total_paid_out, updated_at)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
  `);

  const insertRobotSettings = db.prepare(`
    INSERT INTO robot_settings (user_id, is_robot_mode_on, voice_commands_enabled, lap_duration_seconds, speed_multiplier, is_clockwise, screen_navigation_allowed, messaging_permission_granted, app_opening_permission_granted, touch_interception_active, updated_at)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
  `);

  const insertPrivacy = db.prepare(`
    INSERT INTO user_privacy_settings (user_id, hide_public_stats, hide_friends_and_following, allow_direct_messages, screenshot_protection_enabled, biometric_lock_required, updated_at)
    VALUES (?, ?, ?, ?, ?, ?, ?)
  `);

  const seedCreators = [
    {
      id: 'u_satish',
      email: 'satishhp2000@gmail.com',
      name: 'Satish Kumar',
      handle: '@satish_creator',
      avatarEmoji: '🏏',
      bio: 'Professional fast bowler & athletic training mentor. Pure pace & discipline.',
      skill: 'Fast Bowling & Athletic Conditioning',
      score: 98,
      category: 'SPORTS',
      likes: 14200,
      views: 185000,
      comments: 3200,
      shares: 4100,
      saves: 8900,
      watchMins: 4200.5,
      balance: 14850.0
    },
    {
      id: 'u_aarav',
      email: 'aarav@hunar.app',
      name: 'Aarav Sharma',
      handle: '@aarav_claycraft',
      avatarEmoji: '🏺',
      bio: 'Handmade terracotta pottery without electric wheels. Keeping heritage alive.',
      skill: 'Terracotta Hand Pottery',
      score: 95,
      category: 'CRAFT',
      likes: 8500,
      views: 92000,
      comments: 1400,
      shares: 2100,
      saves: 5400,
      watchMins: 2100.0,
      balance: 8200.0
    },
    {
      id: 'u_priya',
      email: 'priya@hunar.app',
      name: 'Priya Nair',
      handle: '@priya_mudra',
      avatarEmoji: '💃',
      bio: 'Bharatnatyam Varnam practitioner and classical rhythm instructor.',
      skill: 'Classical Indian Dance',
      score: 96,
      category: 'DANCE',
      likes: 11000,
      views: 130000,
      comments: 2100,
      shares: 3800,
      saves: 7200,
      watchMins: 3100.0,
      balance: 11500.0
    },
    {
      id: 'u_vikram',
      email: 'vikram@hunar.app',
      name: 'Vikram Rathore',
      handle: '@vikram_botics',
      avatarEmoji: '🤖',
      bio: 'Autonomous robotics engineer building SLAM navigation & robotic quadrupeds.',
      skill: 'Autonomous Robotics & IoT',
      score: 97,
      category: 'TECH',
      likes: 9800,
      views: 110000,
      comments: 1800,
      shares: 2900,
      saves: 6100,
      watchMins: 2800.0,
      balance: 9750.0
    }
  ];

  for (const c of seedCreators) {
    insertUser.run(
      c.id, c.email, defaultHash, c.name, c.handle, c.avatarEmoji, null,
      c.bio, c.skill, c.score, c.category, 'creator', 1, now, now
    );

    insertAnalytics.run(
      c.id, c.likes, c.views, c.comments, c.shares, c.saves,
      c.watchMins, 45.2, 12.8, c.balance + 3500.0, c.balance, 3500.0, now
    );

    insertRobotSettings.run(
      c.id, 0, 1, 300.0, 1.0, 1, 1, 1, 1, 0, now
    );

    insertPrivacy.run(
      c.id, 1, 1, 1, 1, 0, now
    );
  }

  // Seed sample posts
  const insertPost = db.prepare(`
    INSERT INTO posts (id, user_id, title, description, content_type, media_url, thumbnail_url, category, duration, talent_merit_score, is_private, created_at, updated_at)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
  `);

  const seedPosts = [
    {
      id: 'p_1',
      userId: 'u_satish',
      title: 'Fast Bowling Yorker Technique & Seam Position',
      desc: 'Complete grip and seam release mechanics breakdown to hit the base of off-stump consistently.',
      type: 'video',
      category: 'SPORTS',
      duration: '0:45',
      score: 98,
      thumb: 'https://images.unsplash.com/photo-1531415074968-036ba1b575da?w=600'
    },
    {
      id: 'p_2',
      userId: 'u_aarav',
      title: 'Centering 5kg Clay by Hand Without Machine',
      desc: 'Traditional hand kneading, thumb balance and wheel speed control for high-walled terracotta vases.',
      type: 'video',
      category: 'CRAFT',
      duration: '0:58',
      score: 95,
      thumb: 'https://images.unsplash.com/photo-1565193566173-7a0ee3dbe261?w=600'
    },
    {
      id: 'p_3',
      userId: 'u_priya',
      title: 'Padabhedam Footwork & Trikona Bhangi Drills',
      desc: 'Detailed tala alignment, aramandi posture perfection and adavu rhythm sequence.',
      type: 'video',
      category: 'DANCE',
      duration: '0:50',
      score: 96,
      thumb: 'https://images.unsplash.com/photo-1547153760-18fc86324498?w=600'
    },
    {
      id: 'p_4',
      userId: 'u_vikram',
      title: 'Robotics SLAM Lidar Navigation on ROS2',
      desc: 'Real-time 2D grid map generation and autonomous obstacle evasion path planning.',
      type: 'video',
      category: 'TECH',
      duration: '1:15',
      score: 97,
      thumb: 'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=600'
    }
  ];

  for (const p of seedPosts) {
    insertPost.run(
      p.id, p.userId, p.title, p.desc, p.type, p.thumb, p.thumb,
      p.category, p.duration, p.score, 0, now, now
    );
  }

  // Seed sample notifications
  const insertNotif = db.prepare(`
    INSERT INTO notifications (id, user_id, title, body, type, is_read, created_at)
    VALUES (?, ?, ?, ?, ?, ?, ?)
  `);

  insertNotif.run('n_1', 'u_satish', '₹500 Shabaashi Tip Received!', 'Viewer supported your yorker bowling masterclass via UPI.', 'tip', 0, now);
  insertNotif.run('n_2', 'u_satish', 'Talent Merit Score Upgraded', 'Your cricket technique verified at 98/100 by the board.', 'merit', 1, now - 3600000);

  console.log('[Database] Initial seeding completed successfully.');
}
