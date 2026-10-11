import { createApp } from './app.js';
import { config, validateEnv } from './config/env.js';
import { getDatabase } from './config/database.js';

// 1. Validate environment configuration
validateEnv();

// 2. Initialize database schema & seed data
getDatabase();

// 3. Instantiate Express App
const app = createApp();

// 4. Start HTTP Server
const server = app.listen(config.port, config.host, () => {
  console.log(`================================================================`);
  console.log(`🚀 Hunar Social & AI Robot Assistant Backend Active`);
  console.log(`📡 URL: http://${config.host}:${config.port}`);
  console.log(`🏥 Health Check: http://${config.host}:${config.port}/api/health`);
  console.log(`🔒 Environment: ${config.env}`);
  console.log(`================================================================`);
});

// Graceful shutdown handling
function handleShutdown(signal) {
  console.log(`[Server] Received ${signal}. Shutting down gracefully...`);
  server.close(() => {
    console.log('[Server] HTTP connections closed.');
    process.exit(0);
  });
}

process.on('SIGTERM', () => handleShutdown('SIGTERM'));
process.on('SIGINT', () => handleShutdown('SIGINT'));
