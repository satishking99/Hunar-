import dotenv from 'dotenv';
import path from 'path';
import fs from 'fs';

// Load .env if present
const envPath = path.resolve(process.cwd(), '.env');
if (fs.existsSync(envPath)) {
  dotenv.config({ path: envPath });
} else {
  dotenv.config();
}

/**
 * Strict Environment Configuration & Startup Validator
 */
const NODE_ENV = process.env.NODE_ENV || 'development';
const isProd = NODE_ENV === 'production';

export const config = {
  env: NODE_ENV,
  isProd,
  port: parseInt(process.env.PORT || '5000', 10),
  host: process.env.HOST || '0.0.0.0',
  apiPrefix: process.env.API_PREFIX || '/api/v1',
  corsOrigin: process.env.CORS_ORIGIN || '*',

  // Database
  dbPath: process.env.DB_PATH || path.resolve(process.cwd(), 'data', 'hunar.sqlite'),

  // JWT & Security
  jwtAccessSecret: process.env.JWT_ACCESS_SECRET || (isProd ? null : 'dev_jwt_access_secret_for_hunar_local_only_replace_in_prod'),
  jwtAccessExpiry: process.env.JWT_ACCESS_EXPIRY || '15m',
  jwtRefreshSecret: process.env.JWT_REFRESH_SECRET || (isProd ? null : 'dev_jwt_refresh_secret_for_hunar_local_only_replace_in_prod'),
  jwtRefreshExpiry: process.env.JWT_REFRESH_EXPIRY || '7d',
  bcryptSaltRounds: parseInt(process.env.BCRYPT_SALT_ROUNDS || '10', 10),

  // File Storage
  uploadDir: process.env.UPLOAD_DIR || path.resolve(process.cwd(), 'uploads'),
  maxFileSizeBytes: parseInt(process.env.MAX_FILE_SIZE_BYTES || '52428800', 10), // 50MB
  allowedMimeTypes: (process.env.ALLOWED_MIME_TYPES || 'image/jpeg,image/png,image/webp,video/mp4,video/quicktime')
    .split(',')
    .map(t => t.trim()),

  // AI Robot Provider
  aiProvider: process.env.AI_PROVIDER || 'gemini',
  aiApiKey: process.env.AI_API_KEY || null,
  aiModelName: process.env.AI_MODEL_NAME || 'gemini-1.5-flash',

  // Rate Limiting
  rateLimitWindowMs: parseInt(process.env.RATE_LIMIT_WINDOW_MS || '60000', 10),
  rateLimitMaxRequests: parseInt(process.env.RATE_LIMIT_MAX_REQUESTS || '120', 10),
  authRateLimitMaxRequests: parseInt(process.env.AUTH_RATE_LIMIT_MAX_REQUESTS || '10', 10)
};

/**
 * Validates that all critical variables are configured.
 */
export function validateEnv() {
  const missing = [];

  if (isProd) {
    if (!process.env.JWT_ACCESS_SECRET || process.env.JWT_ACCESS_SECRET.length < 32) {
      missing.push('JWT_ACCESS_SECRET (must be >= 32 characters in production)');
    }
    if (!process.env.JWT_REFRESH_SECRET || process.env.JWT_REFRESH_SECRET.length < 32) {
      missing.push('JWT_REFRESH_SECRET (must be >= 32 characters in production)');
    }
  }

  if (missing.length > 0) {
    console.error('FATAL: Missing required environment variables in production:\n' + missing.map(m => ` - ${m}`).join('\n'));
    process.exit(1);
  }

  // Ensure directories exist
  const dataDir = path.dirname(config.dbPath);
  if (!fs.existsSync(dataDir)) {
    fs.mkdirSync(dataDir, { recursive: true });
  }
  if (!fs.existsSync(config.uploadDir)) {
    fs.mkdirSync(config.uploadDir, { recursive: true });
  }

  console.log(`[Config] Environment validated successfully (${config.env} mode).`);
}
