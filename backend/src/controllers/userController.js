import { getDatabase } from '../config/database.js';

export class UserController {
  /**
   * Public profile — strictly protects creator privacy.
   * Never displays follower numbers, follower lists, or public like counts.
   */
  static async getProfile(req, res, next) {
    try {
      const db = getDatabase();
      const { id } = req.params;

      const user = db.prepare(`
        SELECT id, name, handle, avatar_emoji, profile_picture_url, bio, verified_skill_title, talent_score, category, is_verified, created_at
        FROM users
        WHERE id = ? OR handle = ?
      `).get(id, id.startsWith('@') ? id : `@${id}`);

      if (!user) {
        return res.status(404).json({ success: false, message: 'Creator profile not found' });
      }

      // Count only verified talent works (posts created by user)
      const worksCount = db.prepare('SELECT COUNT(*) as count FROM posts WHERE user_id = ? AND is_private = 0').get(user.id).count;

      res.json({
        success: true,
        data: {
          ...user,
          worksCount,
          // Explicit Hunar privacy enforcement:
          followers: 'Private 🔒',
          following: 'Private 🔒',
          friends: 'Hidden 🔒'
        }
      });
    } catch (err) {
      next(err);
    }
  }

  static async updateProfile(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;
      const { name, bio, avatarEmoji, verifiedSkillTitle } = req.body;

      const user = db.prepare('SELECT * FROM users WHERE id = ?').get(userId);
      if (!user) {
        return res.status(404).json({ success: false, message: 'User not found' });
      }

      const updatedName = name || user.name;
      const updatedBio = bio !== undefined ? bio : user.bio;
      const updatedEmoji = avatarEmoji || user.avatar_emoji;
      const updatedTitle = verifiedSkillTitle || user.verified_skill_title;

      db.prepare(`
        UPDATE users
        SET name = ?, bio = ?, avatar_emoji = ?, verified_skill_title = ?, updated_at = ?
        WHERE id = ?
      `).run(updatedName, updatedBio, updatedEmoji, updatedTitle, Date.now(), userId);

      res.json({
        success: true,
        message: 'Profile updated successfully',
        data: {
          id: userId,
          name: updatedName,
          bio: updatedBio,
          avatarEmoji: updatedEmoji,
          verifiedSkillTitle: updatedTitle
        }
      });
    } catch (err) {
      next(err);
    }
  }

  static async getPrivacySettings(req, res, next) {
    try {
      const db = getDatabase();
      const settings = db.prepare('SELECT * FROM user_privacy_settings WHERE user_id = ?').get(req.user.id);
      res.json({
        success: true,
        data: settings || {
          hide_public_stats: 1,
          hide_friends_and_following: 1,
          allow_direct_messages: 1,
          screenshot_protection_enabled: 1,
          biometric_lock_required: 0
        }
      });
    } catch (err) {
      next(err);
    }
  }

  static async updatePrivacySettings(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;
      const {
        hide_public_stats = 1,
        hide_friends_and_following = 1,
        allow_direct_messages = 1,
        screenshot_protection_enabled = 1,
        biometric_lock_required = 0
      } = req.body;

      db.prepare(`
        INSERT INTO user_privacy_settings (user_id, hide_public_stats, hide_friends_and_following, allow_direct_messages, screenshot_protection_enabled, biometric_lock_required, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT(user_id) DO UPDATE SET
          hide_public_stats = excluded.hide_public_stats,
          hide_friends_and_following = excluded.hide_friends_and_following,
          allow_direct_messages = excluded.allow_direct_messages,
          screenshot_protection_enabled = excluded.screenshot_protection_enabled,
          biometric_lock_required = excluded.biometric_lock_required,
          updated_at = excluded.updated_at
      `).run(userId, hide_public_stats ? 1 : 0, hide_friends_and_following ? 1 : 0, allow_direct_messages ? 1 : 0, screenshot_protection_enabled ? 1 : 0, biometric_lock_required ? 1 : 0, Date.now());

      res.json({ success: true, message: 'Privacy preferences saved' });
    } catch (err) {
      next(err);
    }
  }

  static async deleteAccount(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;

      // Cascading delete triggered via SQLite foreign keys
      db.prepare('DELETE FROM users WHERE id = ?').run(userId);

      res.json({
        success: true,
        message: 'Your account and all associated personal data have been completely deleted.'
      });
    } catch (err) {
      next(err);
    }
  }
}
