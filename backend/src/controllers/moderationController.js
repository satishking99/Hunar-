import crypto from 'crypto';
import { getDatabase } from '../config/database.js';

export class ModerationController {
  static async submitReport(req, res, next) {
    try {
      const db = getDatabase();
      const reporterId = req.user.id;
      const { reported_user_id, reported_post_id, reason, details } = req.body;

      if (!reason) {
        return res.status(400).json({ success: false, message: 'Reason for report is required' });
      }

      const reportId = `rep_${crypto.randomUUID()}`;
      db.prepare(`
        INSERT INTO moderation_reports (id, reporter_id, reported_user_id, reported_post_id, reason, details, status, created_at)
        VALUES (?, ?, ?, ?, ?, ?, 'pending', ?)
      `).run(reportId, reporterId, reported_user_id || null, reported_post_id || null, reason, details || '', Date.now());

      res.status(201).json({
        success: true,
        message: 'Report submitted. Our safety team will review it according to the 4 Golden Rules.'
      });
    } catch (err) {
      next(err);
    }
  }

  static async getBlockedUsers(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;

      const rows = db.prepare(`
        SELECT b.id, b.created_at as blocked_at, u.id as user_id, u.name, u.handle, u.avatar_emoji
        FROM blocked_users b
        JOIN users u ON b.blocked_id = u.id
        WHERE b.blocker_id = ?
        ORDER BY b.created_at DESC
      `).all(userId);

      res.json({ success: true, data: rows });
    } catch (err) {
      next(err);
    }
  }

  static async blockUser(req, res, next) {
    try {
      const db = getDatabase();
      const blockerId = req.user.id;
      const { userId } = req.params;

      if (blockerId === userId) {
        return res.status(400).json({ success: false, message: 'You cannot block yourself' });
      }

      const id = `blk_${crypto.randomUUID()}`;
      try {
        db.prepare(`
          INSERT INTO blocked_users (id, blocker_id, blocked_id, created_at)
          VALUES (?, ?, ?, ?)
        `).run(id, blockerId, userId, Date.now());
      } catch (e) {
        // Already blocked
      }

      res.json({ success: true, message: 'User blocked' });
    } catch (err) {
      next(err);
    }
  }

  static async unblockUser(req, res, next) {
    try {
      const db = getDatabase();
      const blockerId = req.user.id;
      const { userId } = req.params;

      db.prepare('DELETE FROM blocked_users WHERE blocker_id = ? AND blocked_id = ?').run(blockerId, userId);

      res.json({ success: true, message: 'User unblocked' });
    } catch (err) {
      next(err);
    }
  }
}
