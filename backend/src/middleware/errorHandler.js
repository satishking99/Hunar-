import { config } from '../config/env.js';

/**
 * Privacy-Aware Logger: Strips passwords, auth tokens, and sensitive headers
 */
export function privacyLogger(req, res, next) {
  const start = Date.now();
  res.on('finish', () => {
    const duration = Date.now() - start;
    const sanitizedPath = req.originalUrl.split('?')[0]; // Strip query tokens
    const status = res.statusCode;
    const logLine = `[${new Date().toISOString()}] ${req.method} ${sanitizedPath} ${status} - ${duration}ms`;
    if (status >= 500) {
      console.error(logLine);
    } else if (status >= 400) {
      console.warn(logLine);
    } else {
      console.log(logLine);
    }
  });
  next();
}

/**
 * Centralized Error Handler Middleware
 */
export function errorHandler(err, req, res, next) {
  // Avoid leaking internal traces or secret keys
  const status = err.status || err.statusCode || 500;
  const isInternal = status >= 500;

  if (isInternal) {
    console.error(`[InternalError] ${err.message}`);
  }

  res.status(status).json({
    success: false,
    error: err.name || 'InternalServerError',
    message: isInternal && config.isProd
      ? 'An unexpected error occurred. Please try again later.'
      : err.message || 'Operation failed'
  });
}
