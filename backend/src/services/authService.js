import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import crypto from 'crypto';
import { config } from '../config/env.js';
import { getDatabase } from '../config/database.js';

export class AuthService {
  static hashPassword(password) {
    return bcrypt.hashSync(password, config.bcryptSaltRounds);
  }

  static comparePassword(password, hash) {
    return bcrypt.compareSync(password, hash);
  }

  static generateAccessToken(user) {
    return jwt.sign(
      {
        sub: user.id,
        email: user.email,
        handle: user.handle,
        role: user.role
      },
      config.jwtAccessSecret,
      { expiresIn: config.jwtAccessExpiry }
    );
  }

  static generateRefreshToken(user) {
    const db = getDatabase();
    const token = crypto.randomBytes(40).toString('hex');
    const tokenHash = crypto.createHash('sha256').update(token).digest('hex');
    const id = `ref_${crypto.randomUUID()}`;
    const expiresAt = Date.now() + (7 * 24 * 60 * 60 * 1000); // 7 days

    db.prepare(`
      INSERT INTO refresh_tokens (id, user_id, token_hash, expires_at, created_at)
      VALUES (?, ?, ?, ?, ?)
    `).run(id, user.id, tokenHash, expiresAt, Date.now());

    return token;
  }

  static verifyAndRotateRefreshToken(token) {
    const db = getDatabase();
    const tokenHash = crypto.createHash('sha256').update(token).digest('hex');

    const row = db.prepare(`
      SELECT r.*, u.id as user_id, u.email, u.handle, u.role
      FROM refresh_tokens r
      JOIN users u ON r.user_id = u.id
      WHERE r.token_hash = ? AND r.expires_at > ?
    `).get(tokenHash, Date.now());

    if (!row) {
      return null;
    }

    // Revoke used token (rotation)
    db.prepare('DELETE FROM refresh_tokens WHERE id = ?').run(row.id);

    // Issue new pair
    const user = { id: row.user_id, email: row.email, handle: row.handle, role: row.role };
    const newAccessToken = this.generateAccessToken(user);
    const newRefreshToken = this.generateRefreshToken(user);

    return { user, accessToken: newAccessToken, refreshToken: newRefreshToken };
  }

  static revokeRefreshToken(token) {
    const db = getDatabase();
    const tokenHash = crypto.createHash('sha256').update(token).digest('hex');
    db.prepare('DELETE FROM refresh_tokens WHERE token_hash = ?').run(tokenHash);
  }
}
