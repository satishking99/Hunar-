import { Router } from 'express';
import { AuthController } from '../controllers/authController.js';
import { authenticateToken } from '../middleware/auth.js';
import { validateRegister, validateLogin } from '../middleware/validator.js';
import { authRateLimiter } from '../middleware/rateLimiter.js';

const router = Router();

router.post('/register', authRateLimiter, validateRegister, AuthController.register);
router.post('/login', authRateLimiter, validateLogin, AuthController.login);
router.post('/refresh', authRateLimiter, AuthController.refresh);
router.post('/logout', authenticateToken, AuthController.logout);
router.post('/password-recovery', authRateLimiter, AuthController.passwordRecovery);
router.get('/me', authenticateToken, AuthController.me);

export default router;
