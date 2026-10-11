import { Router } from 'express';
import { UserController } from '../controllers/userController.js';
import { authenticateToken } from '../middleware/auth.js';

const router = Router();

// Public profile (privacy-protected)
router.get('/profile/:id', UserController.getProfile);

// Authenticated user settings & privacy
router.put('/profile', authenticateToken, UserController.updateProfile);
router.get('/privacy', authenticateToken, UserController.getPrivacySettings);
router.put('/privacy', authenticateToken, UserController.updatePrivacySettings);
router.delete('/account', authenticateToken, UserController.deleteAccount);

export default router;
