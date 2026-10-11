import { config } from '../config/env.js';

class SlidingWindowRateLimiter {
  constructor(windowMs, maxRequests) {
    this.windowMs = windowMs;
    this.maxRequests = maxRequests;
    this.requests = new Map();

    // Clean up expired buckets periodically
    const timer = setInterval(() => {
      const now = Date.now();
      for (const [key, timestamps] of this.requests.entries()) {
        const valid = timestamps.filter(t => now - t < this.windowMs);
        if (valid.length === 0) {
          this.requests.delete(key);
        } else {
          this.requests.set(key, valid);
        }
      }
    }, Math.max(this.windowMs, 30000));
    if (timer.unref) timer.unref();
  }

  middleware() {
    return (req, res, next) => {
      const clientIp = req.headers['x-forwarded-for'] || req.socket.remoteAddress || '127.0.0.1';
      const key = `${clientIp}:${req.baseUrl || req.path}`;
      const now = Date.now();

      const timestamps = this.requests.get(key) || [];
      const recent = timestamps.filter(t => now - t < this.windowMs);

      if (recent.length >= this.maxRequests) {
        const oldest = recent[0];
        const retryAfterSeconds = Math.ceil((this.windowMs - (now - oldest)) / 1000);
        res.setHeader('Retry-After', retryAfterSeconds);
        return res.status(429).json({
          success: false,
          error: 'RateLimitExceeded',
          message: `Too many requests. Please slow down and try again in ${retryAfterSeconds} seconds.`,
          retryAfterSeconds
        });
      }

      recent.push(now);
      this.requests.set(key, recent);

      res.setHeader('X-RateLimit-Limit', this.maxRequests);
      res.setHeader('X-RateLimit-Remaining', Math.max(0, this.maxRequests - recent.length));

      next();
    };
  }
}

export const generalRateLimiter = new SlidingWindowRateLimiter(
  config.rateLimitWindowMs,
  config.rateLimitMaxRequests
).middleware();

export const authRateLimiter = new SlidingWindowRateLimiter(
  config.rateLimitWindowMs,
  config.authRateLimitMaxRequests
).middleware();
