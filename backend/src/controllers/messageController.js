import crypto from 'crypto';
import { getDatabase } from '../config/database.js';

export class MessageController {
  static async getConversations(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;

      const rows = db.prepare(`
        SELECT
          c.id, c.last_message, c.last_message_at,
          CASE WHEN c.user1_id = ? THEN c.user2_id ELSE c.user1_id END as other_user_id
        FROM conversations c
        WHERE c.user1_id = ? OR c.user2_id = ?
        ORDER BY c.last_message_at DESC
      `).all(userId, userId, userId);

      const conversations = rows.map(r => {
        const otherUser = db.prepare(`
          SELECT id, name, handle, avatar_emoji, verified_skill_title
          FROM users WHERE id = ?
        `).get(r.other_user_id) || { id: r.other_user_id, name: 'User', handle: '@user', avatar_emoji: '👤' };

        const unreadCount = db.prepare(`
          SELECT COUNT(*) as count FROM messages
          WHERE conversation_id = ? AND recipient_id = ? AND is_read = 0
        `).get(r.id, userId).count;

        return {
          id: r.id,
          otherUser,
          lastMessage: r.last_message,
          lastMessageAt: r.last_message_at,
          unreadCount
        };
      });

      res.json({ success: true, data: conversations });
    } catch (err) {
      next(err);
    }
  }

  static async getMessages(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;
      const { conversationId } = req.params;

      const conv = db.prepare(`
        SELECT * FROM conversations
        WHERE id = ? AND (user1_id = ? OR user2_id = ?)
      `).get(conversationId, userId, userId);

      if (!conv) {
        return res.status(404).json({ success: false, message: 'Conversation not found or unauthorized' });
      }

      const messages = db.prepare(`
        SELECT id, conversation_id, sender_id, recipient_id, content, is_read, created_at
        FROM messages
        WHERE conversation_id = ?
        ORDER BY created_at ASC
      `).all(conversationId);

      // Auto mark as read
      db.prepare(`
        UPDATE messages SET is_read = 1
        WHERE conversation_id = ? AND recipient_id = ?
      `).run(conversationId, userId);

      res.json({ success: true, data: messages });
    } catch (err) {
      next(err);
    }
  }

  static async sendMessage(req, res, next) {
    try {
      const db = getDatabase();
      const senderId = req.user.id;
      const { recipient_id, content } = req.body;

      if (!recipient_id || !content) {
        return res.status(400).json({ success: false, message: 'recipient_id and content are required' });
      }

      // Check if recipient has blocked sender
      const blocked = db.prepare(`
        SELECT id FROM blocked_users
        WHERE (blocker_id = ? AND blocked_id = ?) OR (blocker_id = ? AND blocked_id = ?)
      `).get(recipient_id, senderId, senderId, recipient_id);

      if (blocked) {
        return res.status(403).json({ success: false, message: 'Cannot send message to this user' });
      }

      // Find or create conversation
      const [u1, u2] = [senderId, recipient_id].sort();
      let conv = db.prepare('SELECT id FROM conversations WHERE user1_id = ? AND user2_id = ?').get(u1, u2);
      const now = Date.now();

      if (!conv) {
        const convId = `conv_${crypto.randomUUID()}`;
        db.prepare(`
          INSERT INTO conversations (id, user1_id, user2_id, last_message, last_message_at, created_at)
          VALUES (?, ?, ?, ?, ?, ?)
        `).run(convId, u1, u2, content, now, now);
        conv = { id: convId };
      } else {
        db.prepare('UPDATE conversations SET last_message = ?, last_message_at = ? WHERE id = ?').run(content, now, conv.id);
      }

      const msgId = `msg_${crypto.randomUUID()}`;
      db.prepare(`
        INSERT INTO messages (id, conversation_id, sender_id, recipient_id, content, is_read, created_at)
        VALUES (?, ?, ?, ?, ?, 0, ?)
      `).run(msgId, conv.id, senderId, recipient_id, content, now);

      res.status(201).json({
        success: true,
        data: {
          id: msgId,
          conversationId: conv.id,
          senderId,
          recipientId: recipient_id,
          content,
          createdAt: now
        }
      });
    } catch (err) {
      next(err);
    }
  }
}
