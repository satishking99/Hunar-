import express from 'express';
import cors from 'cors';
import path from 'path';
import { config } from './config/env.js';
import { getDatabase } from './config/database.js';
import { privacyLogger, errorHandler } from './middleware/errorHandler.js';
import { generalRateLimiter } from './middleware/rateLimiter.js';

// Route imports
import authRoutes from './routes/authRoutes.js';
import userRoutes from './routes/userRoutes.js';
import postRoutes from './routes/postRoutes.js';
import savedRoutes from './routes/savedRoutes.js';
import historyRoutes from './routes/historyRoutes.js';
import messageRoutes from './routes/messageRoutes.js';
import notificationRoutes from './routes/notificationRoutes.js';
import robotRoutes from './routes/robotRoutes.js';
import analyticsRoutes from './routes/analyticsRoutes.js';
import moderationRoutes from './routes/moderationRoutes.js';
import uploadRoutes from './routes/uploadRoutes.js';

export function createApp() {
  const app = express();

  // Basic security & CORS
  app.use(cors({ origin: config.corsOrigin }));
  app.use(express.json({ limit: '10mb' }));
  app.use(express.urlencoded({ extended: true, limit: '10mb' }));

  // Privacy-preserving logger
  app.use(privacyLogger);

  // Serve static uploaded assets securely
  app.use('/uploads', express.static(config.uploadDir));

  // Health-check endpoint
  app.get('/api/health', (req, res) => {
    try {
      const db = getDatabase();
      const check = db.prepare('SELECT 1 as ok').get();
      res.json({
        status: 'healthy',
        timestamp: new Date().toISOString(),
        uptimeSeconds: Math.floor(process.uptime()),
        environment: config.env,
        database: check.ok === 1 ? 'connected' : 'unhealthy',
        service: 'Hunar Social & AI Robot Assistant Backend',
        version: '1.0.0'
      });
    } catch (e) {
      res.status(503).json({
        status: 'unhealthy',
        error: e.message
      });
    }
  });

  // Apply general rate limiter to API routes
  app.use(config.apiPrefix, generalRateLimiter);

  // Mount API modules
  app.use(`${config.apiPrefix}/auth`, authRoutes);
  app.use(`${config.apiPrefix}/users`, userRoutes);
  app.use(`${config.apiPrefix}/posts`, postRoutes);
  app.use(`${config.apiPrefix}/saved`, savedRoutes);
  app.use(`${config.apiPrefix}/history`, historyRoutes);
  app.use(`${config.apiPrefix}/messages`, messageRoutes);
  app.use(`${config.apiPrefix}/notifications`, notificationRoutes);
  app.use(`${config.apiPrefix}/robot`, robotRoutes);
  app.use(`${config.apiPrefix}/analytics`, analyticsRoutes);
  app.use(`${config.apiPrefix}/moderation`, moderationRoutes);
  app.use(`${config.apiPrefix}/uploads`, uploadRoutes);

  // 404 Route Handler
  app.use((req, res) => {
    res.status(404).json({
      success: false,
      error: 'NotFound',
      message: `Endpoint ${req.method} ${req.originalUrl} not found`
    });
  });

  // Centralized Error Handler
  app.use(errorHandler);

  return app;
}
