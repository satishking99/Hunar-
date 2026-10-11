import crypto from 'crypto';
import { getDatabase } from '../config/database.js';

export class SavedController {
  /**
   * Get authenticated user's private saved posts collection.
   * Strictly accessible only by the account owner.
   */
  static async getSavedPosts(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;

      const rows = db.prepare(`
        SELECT
          s.id as saved_id, s.created_at as saved_at,
          p.id, p.title, p.description, p.content_type, p.media_url, p.thumbnail_url,
          p.category, p.duration, p.talent_merit_score, p.created_at,
          u.id as creator_id, u.name as creator_name, u.handle as creator_handle,
          u.avatar_emoji as creator_avatar, u.verified_skill_title as creator_skill
        FROM saved_posts s
        JOIN posts p ON s.post_id = p.id
        JOIN users u ON p.user_id = u.id
        WHERE s.user_id = ?
        ORDER BY s.created_at DESC
      `).all(userId);

      const saved = rows.map(r => ({
        id: r.id,
        savedAt: r.saved_at,
        title: r.title,
        description: r.description,
        contentType: r.content_type,
        mediaUrl: r.media_url,
        thumbnailUrl: r.thumbnail_url,
        category: r.category,
        duration: r.duration,
        talentMeritScore: r.talent_merit_score,
        creator: {
          id: r.creator_id,
          name: r.creator_name,
          handle: r.creator_handle,
          avatarEmoji: r.creator_avatar,
          verifiedSkillTitle: r.creator_skill
        },
        isSaved: true
      }));

      res.json({
        success: true,
        data: saved,
        count: saved.length
      });
    } catch (err) {
      next(err);
    }
  }

  static async savePost(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;
      const { postId } = req.params;

      const post = db.prepare('SELECT id, user_id FROM posts WHERE id = ?').get(postId);
      if (!post) {
        return res.status(404).json({ success: false, message: 'Post not found' });
      }

      const id = `save_${crypto.randomUUID()}`;
      try {
        db.prepare(`
          INSERT INTO saved_posts (id, user_id, post_id, created_at)
          VALUES (?, ?, ?, ?)
        `).run(id, userId, postId, Date.now());

        // Increment private saves in creator's analytics (author only)
        db.prepare('UPDATE creator_analytics SET total_saves = total_saves + 1 WHERE user_id = ?').run(post.user_id);
      } catch (e) {
        // Already saved (unique constraint)
      }

      res.json({ success: true, message: 'Post saved privately to your collection', isSaved: true });
    } catch (err) {
      next(err);
    }
  }

  static async unsavePost(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;
      const { postId } = req.params;

      const result = db.prepare('DELETE FROM saved_posts WHERE user_id = ? AND post_id = ?').run(userId, postId);

      if (result.changes > 0) {
        const post = db.prepare('SELECT user_id FROM posts WHERE id = ?').get(postId);
        if (post) {
          db.prepare('UPDATE creator_analytics SET total_saves = MAX(0, total_saves - 1) WHERE user_id = ?').run(post.user_id);
        }
      }

      res.json({ success: true, message: 'Post removed from your saved collection', isSaved: false });
    } catch (err) {
      next(err);
    }
  }
}
