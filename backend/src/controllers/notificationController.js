import { getDatabase } from '../config/database.js';

export class NotificationController {
  static async getNotifications(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;

      const rows = db.prepare(`
        SELECT id, title, body, type, is_read, created_at
        FROM notifications
        WHERE user_id = ?
        ORDER BY created_at DESC
        LIMIT 50
      `).all(userId);

      const unreadCount = rows.filter(r => !r.is_read).length;

      res.json({
        success: true,
        data: rows,
        unreadCount
      });
    } catch (err) {
      next(err);
    }
  }

  static async markAllRead(req, res, next) {
    try {
      const db = getDatabase();
      db.prepare('UPDATE notifications SET is_read = 1 WHERE user_id = ?').run(req.user.id);
      res.json({ success: true, message: 'All notifications marked as read' });
    } catch (err) {
      next(err);
    }
  }

  static async markRead(req, res, next) {
    try {
      const db = getDatabase();
      const { id } = req.params;
      db.prepare('UPDATE notifications SET is_read = 1 WHERE id = ? AND user_id = ?').run(id, req.user.id);
      res.json({ success: true, message: 'Notification marked as read' });
    } catch (err) {
      next(err);
    }
  }
}
