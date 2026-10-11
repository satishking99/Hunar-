import { getDatabase } from '../config/database.js';
import { RobotService } from '../services/robotService.js';

export class RobotController {
  static async processCommand(req, res, next) {
    try {
      const userId = req.user.id;
      const { query } = req.body;

      if (!query || typeof query !== 'string') {
        return res.status(400).json({ success: false, message: 'query string is required' });
      }

      const result = await RobotService.processCommand(userId, query);

      res.json({
        success: true,
        data: result
      });
    } catch (err) {
      next(err);
    }
  }

  static async getSportsVideos(req, res, next) {
    try {
      const { query } = req.query;
      const videos = RobotService.getSportsVideosCatalog(query);
      res.json({
        success: true,
        data: videos,
        count: videos.length
      });
    } catch (err) {
      next(err);
    }
  }

  static async getSettings(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;

      let settings = db.prepare('SELECT * FROM robot_settings WHERE user_id = ?').get(userId);
      if (!settings) {
        db.prepare(`
          INSERT INTO robot_settings (user_id, is_robot_mode_on, voice_commands_enabled, lap_duration_seconds, speed_multiplier, is_clockwise, screen_navigation_allowed, messaging_permission_granted, app_opening_permission_granted, touch_interception_active, updated_at)
          VALUES (?, 0, 1, 300.0, 1.0, 1, 1, 1, 1, 0, ?)
        `).run(userId, Date.now());
        settings = db.prepare('SELECT * FROM robot_settings WHERE user_id = ?').get(userId);
      }

      res.json({ success: true, data: settings });
    } catch (err) {
      next(err);
    }
  }

  static async updateSettings(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;
      const {
        is_robot_mode_on,
        voice_commands_enabled,
        lap_duration_seconds,
        speed_multiplier,
        is_clockwise,
        screen_navigation_allowed,
        messaging_permission_granted,
        app_opening_permission_granted,
        touch_interception_active
      } = req.body;

      const current = db.prepare('SELECT * FROM robot_settings WHERE user_id = ?').get(userId) || {};

      db.prepare(`
        INSERT INTO robot_settings (user_id, is_robot_mode_on, voice_commands_enabled, lap_duration_seconds, speed_multiplier, is_clockwise, screen_navigation_allowed, messaging_permission_granted, app_opening_permission_granted, touch_interception_active, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT(user_id) DO UPDATE SET
          is_robot_mode_on = excluded.is_robot_mode_on,
          voice_commands_enabled = excluded.voice_commands_enabled,
          lap_duration_seconds = excluded.lap_duration_seconds,
          speed_multiplier = excluded.speed_multiplier,
          is_clockwise = excluded.is_clockwise,
          screen_navigation_allowed = excluded.screen_navigation_allowed,
          messaging_permission_granted = excluded.messaging_permission_granted,
          app_opening_permission_granted = excluded.app_opening_permission_granted,
          touch_interception_active = excluded.touch_interception_active,
          updated_at = excluded.updated_at
      `).run(
        userId,
        is_robot_mode_on !== undefined ? (is_robot_mode_on ? 1 : 0) : (current.is_robot_mode_on ?? 0),
        voice_commands_enabled !== undefined ? (voice_commands_enabled ? 1 : 0) : (current.voice_commands_enabled ?? 1),
        lap_duration_seconds !== undefined ? parseFloat(lap_duration_seconds) : (current.lap_duration_seconds ?? 300.0),
        speed_multiplier !== undefined ? parseFloat(speed_multiplier) : (current.speed_multiplier ?? 1.0),
        is_clockwise !== undefined ? (is_clockwise ? 1 : 0) : (current.is_clockwise ?? 1),
        screen_navigation_allowed !== undefined ? (screen_navigation_allowed ? 1 : 0) : (current.screen_navigation_allowed ?? 1),
        messaging_permission_granted !== undefined ? (messaging_permission_granted ? 1 : 0) : (current.messaging_permission_granted ?? 1),
        app_opening_permission_granted !== undefined ? (app_opening_permission_granted ? 1 : 0) : (current.app_opening_permission_granted ?? 1),
        touch_interception_active !== undefined ? (touch_interception_active ? 1 : 0) : (current.touch_interception_active ?? 0),
        Date.now()
      );

      const updated = db.prepare('SELECT * FROM robot_settings WHERE user_id = ?').get(userId);
      res.json({ success: true, message: 'Robot settings updated', data: updated });
    } catch (err) {
      next(err);
    }
  }

  static async confirmAction(req, res, next) {
    try {
      const userId = req.user.id;
      const { commandId, approved } = req.body;

      if (!commandId) {
        return res.status(400).json({ success: false, message: 'commandId is required' });
      }

      const result = RobotService.confirmAction(userId, commandId, !!approved);
      res.json({ success: true, data: result });
    } catch (err) {
      next(err);
    }
  }

  static async getHistory(req, res, next) {
    try {
      const db = getDatabase();
      const rows = db.prepare(`
        SELECT id, raw_query, intent_type, action_target, was_confirmed, status, created_at
        FROM robot_commands_history
        WHERE user_id = ?
        ORDER BY created_at DESC
        LIMIT 50
      `).all(req.user.id);

      const history = rows.map(r => ({
        id: r.id,
        rawQuery: r.raw_query,
        intentType: r.intent_type,
        actionTarget: r.action_target ? JSON.parse(r.action_target) : null,
        wasConfirmed: !!r.was_confirmed,
        status: r.status,
        createdAt: r.created_at
      }));

      res.json({ success: true, data: history });
    } catch (err) {
      next(err);
    }
  }
}
