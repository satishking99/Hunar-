import crypto from 'crypto';
import { getDatabase } from '../config/database.js';
import { AuthService } from '../services/authService.js';

export class AuthController {
  static async register(req, res, next) {
    try {
      const db = getDatabase();
      const { email, password, name, handle, category = 'TECH', bio = '' } = req.body;

      // Check for existing email or handle
      const existing = db.prepare('SELECT id, email, handle FROM users WHERE email = ? OR handle = ?').get(email, handle);
      if (existing) {
        if (existing.email === email) {
          return res.status(409).json({ success: false, error: 'Conflict', message: 'Email already registered' });
        }
        return res.status(409).json({ success: false, error: 'Conflict', message: 'Handle already taken' });
      }

      const userId = `u_${crypto.randomUUID()}`;
      const now = Date.now();
      const hash = AuthService.hashPassword(password);

      db.prepare(`
        INSERT INTO users (id, email, password_hash, name, handle, bio, category, role, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
      `).run(userId, email, hash, name, handle, bio, category.toUpperCase(), 'creator', now, now);

      // Create default settings & analytics records
      db.prepare(`
        INSERT INTO robot_settings (user_id, is_robot_mode_on, voice_commands_enabled, lap_duration_seconds, speed_multiplier, is_clockwise, screen_navigation_allowed, messaging_permission_granted, app_opening_permission_granted, touch_interception_active, updated_at)
        VALUES (?, 0, 1, 300.0, 1.0, 1, 1, 1, 1, 0, ?)
      `).run(userId, now);

      db.prepare(`
        INSERT INTO user_privacy_settings (user_id, hide_public_stats, hide_friends_and_following, allow_direct_messages, screenshot_protection_enabled, biometric_lock_required, updated_at)
        VALUES (?, 1, 1, 1, 1, 0, ?)
      `).run(userId, now);

      db.prepare(`
        INSERT INTO creator_analytics (user_id, total_likes, total_views, total_comments, total_shares, total_saves, total_watch_time_minutes, avg_watch_duration_seconds, follower_growth_percent, estimated_earnings, withdrawable_balance, total_paid_out, updated_at)
        VALUES (?, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, ?)
      `).run(userId, now);

      const user = { id: userId, email, name, handle, role: 'creator', category };
      const accessToken = AuthService.generateAccessToken(user);
      const refreshToken = AuthService.generateRefreshToken(user);

      res.status(201).json({
        success: true,
        message: 'Account created successfully',
        data: {
          user,
          tokens: {
            accessToken,
            refreshToken,
            tokenType: 'Bearer'
          }
        }
      });
    } catch (err) {
      next(err);
    }
  }

  static async login(req, res, next) {
    try {
      const db = getDatabase();
      const { identifier, password } = req.body;

      const userRow = db.prepare(`
        SELECT * FROM users
        WHERE email = ? OR handle = ?
      `).get(identifier.toLowerCase().trim(), identifier.trim());

      if (!userRow || !AuthService.comparePassword(password, userRow.password_hash)) {
        return res.status(401).json({
          success: false,
          error: 'InvalidCredentials',
          message: 'Invalid email/handle or password'
        });
      }

      const user = {
        id: userRow.id,
        email: userRow.email,
        name: userRow.name,
        handle: userRow.handle,
        avatarEmoji: userRow.avatar_emoji,
        verifiedSkillTitle: userRow.verified_skill_title,
        talentScore: userRow.talent_score,
        role: userRow.role,
        category: userRow.category
      };

      const accessToken = AuthService.generateAccessToken(user);
      const refreshToken = AuthService.generateRefreshToken(user);

      res.json({
        success: true,
        message: 'Logged in successfully',
        data: {
          user,
          tokens: {
            accessToken,
            refreshToken,
            tokenType: 'Bearer'
          }
        }
      });
    } catch (err) {
      next(err);
    }
  }

  static async refresh(req, res, next) {
    try {
      const { refreshToken } = req.body;
      if (!refreshToken) {
        return res.status(400).json({ success: false, message: 'refreshToken is required' });
      }

      const result = AuthService.verifyAndRotateRefreshToken(refreshToken);
      if (!result) {
        return res.status(401).json({
          success: false,
          error: 'InvalidRefreshToken',
          message: 'Refresh token is expired, revoked, or invalid'
        });
      }

      res.json({
        success: true,
        data: {
          user: result.user,
          tokens: {
            accessToken: result.accessToken,
            refreshToken: result.refreshToken,
            tokenType: 'Bearer'
          }
        }
      });
    } catch (err) {
      next(err);
    }
  }

  static async logout(req, res, next) {
    try {
      const { refreshToken } = req.body;
      if (refreshToken) {
        AuthService.revokeRefreshToken(refreshToken);
      }
      res.json({ success: true, message: 'Logged out successfully' });
    } catch (err) {
      next(err);
    }
  }

  static async passwordRecovery(req, res, next) {
    try {
      const { email } = req.body;
      if (!email) {
        return res.status(400).json({ success: false, message: 'Email is required' });
      }

      // Do not disclose whether email exists for user privacy
      res.json({
        success: true,
        message: 'If the email exists in our records, password reset instructions have been dispatched.'
      });
    } catch (err) {
      next(err);
    }
  }

  static async me(req, res, next) {
    try {
      const db = getDatabase();
      const user = db.prepare(`
        SELECT id, email, name, handle, avatar_emoji, profile_picture_url, bio, verified_skill_title, talent_score, category, role, is_verified, created_at
        FROM users WHERE id = ?
      `).get(req.user.id);

      if (!user) {
        return res.status(404).json({ success: false, message: 'User not found' });
      }

      res.json({ success: true, data: user });
    } catch (err) {
      next(err);
    }
  }
}
