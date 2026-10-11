import crypto from 'crypto';
import { getDatabase } from '../config/database.js';

export class PostController {
  static async getPosts(req, res, next) {
    try {
      const db = getDatabase();
      const { category, type, search, limit = 20, offset = 0 } = req.query;
      const currentUserId = req.user?.id || null;

      let sql = `
        SELECT
          p.id, p.title, p.description, p.content_type, p.media_url, p.thumbnail_url,
          p.category, p.duration, p.talent_merit_score, p.created_at,
          u.id as creator_id, u.name as creator_name, u.handle as creator_handle,
          u.avatar_emoji as creator_avatar, u.verified_skill_title as creator_skill,
          u.talent_score as creator_score
        FROM posts p
        JOIN users u ON p.user_id = u.id
        WHERE p.is_private = 0
      `;
      const params = [];

      if (category && category !== 'ALL') {
        sql += ` AND p.category = ?`;
        params.push(category.toUpperCase());
      }
      if (type) {
        sql += ` AND p.content_type = ?`;
        params.push(type.toLowerCase());
      }
      if (search) {
        sql += ` AND (p.title LIKE ? OR p.description LIKE ?)`;
        params.push(`%${search}%`, `%${search}%`);
      }

      sql += ` ORDER BY p.created_at DESC LIMIT ? OFFSET ?`;
      params.push(parseInt(limit, 10), parseInt(offset, 10));

      const rows = db.prepare(sql).all(...params);

      // Check if current user saved any of these
      let savedSet = new Set();
      if (currentUserId && rows.length > 0) {
        const postIds = rows.map(r => r.id);
        const placeholders = postIds.map(() => '?').join(',');
        const savedRows = db.prepare(`
          SELECT post_id FROM saved_posts WHERE user_id = ? AND post_id IN (${placeholders})
        `).all(currentUserId, ...postIds);
        savedSet = new Set(savedRows.map(s => s.post_id));
      }

      // Map to privacy-compliant response: NO public like/view/comment counts
      const posts = rows.map(r => ({
        id: r.id,
        title: r.title,
        description: r.description,
        contentType: r.content_type,
        mediaUrl: r.media_url,
        thumbnailUrl: r.thumbnail_url,
        category: r.category,
        duration: r.duration,
        talentMeritScore: r.talent_merit_score,
        createdAt: r.created_at,
        creator: {
          id: r.creator_id,
          name: r.creator_name,
          handle: r.creator_handle,
          avatarEmoji: r.creator_avatar,
          verifiedSkillTitle: r.creator_skill,
          talentScore: r.creator_score
        },
        // Engagement action allowed: Save Privately
        isSaved: savedSet.has(r.id)
      }));

      res.json({
        success: true,
        data: posts,
        meta: {
          count: posts.length,
          limit: parseInt(limit, 10),
          offset: parseInt(offset, 10)
        }
      });
    } catch (err) {
      next(err);
    }
  }

  static async getPostById(req, res, next) {
    try {
      const db = getDatabase();
      const { id } = req.params;
      const currentUserId = req.user?.id || null;

      const r = db.prepare(`
        SELECT
          p.id, p.title, p.description, p.content_type, p.media_url, p.thumbnail_url,
          p.category, p.duration, p.talent_merit_score, p.created_at,
          u.id as creator_id, u.name as creator_name, u.handle as creator_handle,
          u.avatar_emoji as creator_avatar, u.verified_skill_title as creator_skill,
          u.talent_score as creator_score
        FROM posts p
        JOIN users u ON p.user_id = u.id
        WHERE p.id = ?
      `).get(id);

      if (!r) {
        return res.status(404).json({ success: false, message: 'Post not found' });
      }

      let isSaved = false;
      if (currentUserId) {
        const saved = db.prepare('SELECT id FROM saved_posts WHERE user_id = ? AND post_id = ?').get(currentUserId, id);
        isSaved = !!saved;
      }

      res.json({
        success: true,
        data: {
          id: r.id,
          title: r.title,
          description: r.description,
          contentType: r.content_type,
          mediaUrl: r.media_url,
          thumbnailUrl: r.thumbnail_url,
          category: r.category,
          duration: r.duration,
          talentMeritScore: r.talent_merit_score,
          createdAt: r.created_at,
          creator: {
            id: r.creator_id,
            name: r.creator_name,
            handle: r.creator_handle,
            avatarEmoji: r.creator_avatar,
            verifiedSkillTitle: r.creator_skill,
            talentScore: r.creator_score
          },
          isSaved
        }
      });
    } catch (err) {
      next(err);
    }
  }

  static async createPost(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;
      const {
        title,
        description = '',
        category,
        contentType = 'video',
        mediaUrl = '',
        thumbnailUrl = '',
        duration = '0:45'
      } = req.body;

      const postId = `p_${crypto.randomUUID()}`;
      const now = Date.now();

      db.prepare(`
        INSERT INTO posts (id, user_id, title, description, content_type, media_url, thumbnail_url, category, duration, talent_merit_score, is_private, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 90, 0, ?, ?)
      `).run(postId, userId, title, description, contentType.toLowerCase(), mediaUrl, thumbnailUrl, category.toUpperCase(), duration, now, now);

      res.status(201).json({
        success: true,
        message: 'Post created and verified on Hunar merit feed',
        data: { id: postId, title, category, contentType, createdAt: now }
      });
    } catch (err) {
      next(err);
    }
  }

  static async updatePost(req, res, next) {
    try {
      const db = getDatabase();
      const { id } = req.params;
      const userId = req.user.id;
      const { title, description } = req.body;

      const post = db.prepare('SELECT * FROM posts WHERE id = ?').get(id);
      if (!post) {
        return res.status(404).json({ success: false, message: 'Post not found' });
      }
      if (post.user_id !== userId && req.user.role !== 'admin') {
        return res.status(403).json({ success: false, message: 'Unauthorized to edit this post' });
      }

      db.prepare(`
        UPDATE posts
        SET title = COALESCE(?, title), description = COALESCE(?, description), updated_at = ?
        WHERE id = ?
      `).run(title, description, Date.now(), id);

      res.json({ success: true, message: 'Post updated successfully' });
    } catch (err) {
      next(err);
    }
  }

  static async deletePost(req, res, next) {
    try {
      const db = getDatabase();
      const { id } = req.params;
      const userId = req.user.id;

      const post = db.prepare('SELECT * FROM posts WHERE id = ?').get(id);
      if (!post) {
        return res.status(404).json({ success: false, message: 'Post not found' });
      }
      if (post.user_id !== userId && req.user.role !== 'admin') {
        return res.status(403).json({ success: false, message: 'Unauthorized to delete this post' });
      }

      db.prepare('DELETE FROM posts WHERE id = ?').run(id);

      res.json({ success: true, message: 'Post removed successfully' });
    } catch (err) {
      next(err);
    }
  }
}
