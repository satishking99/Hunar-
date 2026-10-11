import { Router } from 'express';
import { NotificationController } from '../controllers/notificationController.js';
import { authenticateToken } from '../middleware/auth.js';

const router = Router();

router.get('/', authenticateToken, NotificationController.getNotifications);
router.put('/read-all', authenticateToken, NotificationController.markAllRead);
router.put('/:id/read', authenticateToken, NotificationController.markRead);

export default router;
