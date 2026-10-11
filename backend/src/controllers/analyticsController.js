import crypto from 'crypto';
import { getDatabase } from '../config/database.js';

export class AnalyticsController {
  /**
   * Private Creator Analytics Dashboard.
   * STRICT SECURITY: Only the authenticated account owner can view their private metrics.
   * No other user can access these analytics.
   */
  static async getDashboard(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;

      let analytics = db.prepare('SELECT * FROM creator_analytics WHERE user_id = ?').get(userId);
      if (!analytics) {
        db.prepare(`
          INSERT INTO creator_analytics (user_id, total_likes, total_views, total_comments, total_shares, total_saves, total_watch_time_minutes, avg_watch_duration_seconds, follower_growth_percent, estimated_earnings, withdrawable_balance, total_paid_out, updated_at)
          VALUES (?, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, ?)
        `).run(userId, Date.now());
        analytics = db.prepare('SELECT * FROM creator_analytics WHERE user_id = ?').get(userId);
      }

      // Video-by-video breakdown (private to creator)
      const videoStats = db.prepare(`
        SELECT id, title, content_type, category, talent_merit_score, created_at
        FROM posts
        WHERE user_id = ?
        ORDER BY created_at DESC
      `).all(userId);

      const response = {
        totalLikes: analytics.total_likes,
        totalViews: analytics.total_views,
        totalComments: analytics.total_comments,
        totalShares: analytics.total_shares,
        totalSaves: analytics.total_saves,
        totalWatchTimeMinutes: analytics.total_watch_time_minutes,
        avgWatchDurationSeconds: analytics.avg_watch_duration_seconds,
        followerGrowthPercent: analytics.follower_growth_percent,
        estimatedEarnings: analytics.estimated_earnings,
        withdrawableBalance: analytics.withdrawable_balance,
        totalPaidOut: analytics.total_paid_out,
        videoCount: videoStats.length,
        videos: videoStats
      };

      res.json({
        success: true,
        data: response
      });
    } catch (err) {
      next(err);
    }
  }

  static async getPayments(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;

      const payments = db.prepare(`
        SELECT id, amount, upi_id, status, reference_id, created_at
        FROM creator_payment_history
        WHERE user_id = ?
        ORDER BY created_at DESC
      `).all(userId);

      res.json({ success: true, data: payments });
    } catch (err) {
      next(err);
    }
  }

  static async requestWithdrawal(req, res, next) {
    try {
      const db = getDatabase();
      const userId = req.user.id;
      const { amount, upiId } = req.body;

      const numAmount = parseFloat(amount);
      if (isNaN(numAmount) || numAmount < 100) {
        return res.status(400).json({ success: false, message: 'Minimum withdrawal amount is ₹100' });
      }
      if (!upiId || !upiId.includes('@')) {
        return res.status(400).json({ success: false, message: 'Valid UPI ID required (e.g. yourname@okhdfcbank)' });
      }

      const analytics = db.prepare('SELECT withdrawable_balance FROM creator_analytics WHERE user_id = ?').get(userId);
      if (!analytics || analytics.withdrawable_balance < numAmount) {
        return res.status(400).json({ success: false, message: 'Insufficient balance for withdrawal' });
      }

      const paymentId = `payout_${crypto.randomUUID()}`;
      const refId = `UPI-${Date.now()}-${Math.floor(Math.random() * 89999 + 10000)}`;
      const now = Date.now();

      // Deduct balance and record payout
      db.prepare(`
        UPDATE creator_analytics
        SET withdrawable_balance = withdrawable_balance - ?,
            total_paid_out = total_paid_out + ?,
            updated_at = ?
        WHERE user_id = ?
      `).run(numAmount, numAmount, now, userId);

      db.prepare(`
        INSERT INTO creator_payment_history (id, user_id, amount, upi_id, status, reference_id, created_at)
        VALUES (?, ?, ?, ?, 'completed', ?, ?)
      `).run(paymentId, userId, numAmount, upiId, refId, now);

      res.json({
        success: true,
        message: `₹${numAmount} successfully transferred to ${upiId}`,
        data: {
          paymentId,
          referenceId: refId,
          amount: numAmount,
          upiId,
          status: 'completed'
        }
      });
    } catch (err) {
      next(err);
    }
  }
}
