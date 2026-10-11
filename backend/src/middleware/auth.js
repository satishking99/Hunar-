import jwt from 'jsonwebtoken';
import { config } from '../config/env.js';

export function authenticateToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({
      success: false,
      error: 'AuthenticationRequired',
      message: 'Access token is missing or malformed'
    });
  }

  const token = authHeader.split(' ')[1];

  try {
    const payload = jwt.verify(token, config.jwtAccessSecret);
    req.user = {
      id: payload.sub,
      email: payload.email,
      handle: payload.handle,
      role: payload.role || 'creator'
    };
    next();
  } catch (err) {
    if (err.name === 'TokenExpiredError') {
      return res.status(401).json({
        success: false,
        error: 'TokenExpired',
        message: 'Access token has expired. Please refresh your session.'
      });
    }
    return res.status(403).json({
      success: false,
      error: 'InvalidToken',
      message: 'Access token is invalid or signature mismatch'
    });
  }
}

export function optionalAuth(req, res, next) {
  const authHeader = req.headers['authorization'];
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    req.user = null;
    return next();
  }

  const token = authHeader.split(' ')[1];
  try {
    const payload = jwt.verify(token, config.jwtAccessSecret);
    req.user = {
      id: payload.sub,
      email: payload.email,
      handle: payload.handle,
      role: payload.role || 'creator'
    };
  } catch (e) {
    req.user = null;
  }
  next();
}

export function requireRole(...allowedRoles) {
  return (req, res, next) => {
    if (!req.user) {
      return res.status(401).json({ success: false, error: 'Unauthorized' });
    }
    if (!allowedRoles.includes(req.user.role)) {
      return res.status(403).json({
        success: false,
        error: 'Forbidden',
        message: 'You do not have the required permissions for this action'
      });
    }
    next();
  };
}
