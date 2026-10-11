import crypto from 'crypto';
import { getDatabase } from '../config/database.js';

export class HistoryController {
  static async getHistory(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;
      const { type } = req.query;

      let sql = `
        SELECT
          h.id as history_id, h.media_type, h.watch_time_seconds, h.created_at as viewed_at,
          p.id, p.title, p.description, p.thumbnail_url, p.media_url, p.category, p.duration,
          u.name as creator_name, u.handle as creator_handle
        FROM view_history h
        JOIN posts p ON h.post_id = p.id
        JOIN users u ON p.user_id = u.id
        WHERE h.user_id = ?
      `;
      const params = [userId];

      if (type) {
        sql += ' AND h.media_type = ?';
        params.push(type.toLowerCase());
      }

      sql += ' ORDER BY h.created_at DESC LIMIT 50';

      const rows = db.prepare(sql).all(...params);

      res.json({ success: true, data: rows });
    } catch (err) {
      next(err);
    }
  }

  static async recordHistory(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;
      const { postId, mediaType = 'video', watchTimeSeconds = 30 } = req.body;

      if (!postId) {
        return res.status(400).json({ success: false, message: 'postId is required' });
      }

      const id = `hist_${crypto.randomUUID()}`;
      const now = Date.now();

      db.prepare(`
        INSERT INTO view_history (id, user_id, post_id, media_type, watch_time_seconds, created_at)
        VALUES (?, ?, ?, ?, ?, ?)
      `).run(id, userId, postId, mediaType.toLowerCase(), watchTimeSeconds, now);

      // Increment creator's views & watch time privately
      const post = db.prepare('SELECT user_id FROM posts WHERE id = ?').get(postId);
      if (post) {
        const watchMins = (watchTimeSeconds || 30) / 60.0;
        db.prepare(`
          UPDATE creator_analytics
          SET total_views = total_views + 1,
              total_watch_time_minutes = total_watch_time_minutes + ?
          WHERE user_id = ?
        `).run(watchMins, post.user_id);
      }

      res.status(201).json({ success: true, message: 'Watch event recorded' });
    } catch (err) {
      next(err);
    }
  }

  static async clearHistory(req, res, next) {
    try {
      const db = getDatabase();
      db.prepare('DELETE FROM view_history WHERE user_id = ?').run(req.user.id);
      res.json({ success: true, message: 'Watch history cleared' });
    } catch (err) {
      next(err);
    }
  }
}
