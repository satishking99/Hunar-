import { Router } from 'express';
import { ModerationController } from '../controllers/moderationController.js';
import { authenticateToken } from '../middleware/auth.js';

const router = Router();

router.post('/report', authenticateToken, ModerationController.submitReport);
router.get('/blocks', authenticateToken, ModerationController.getBlockedUsers);
router.post('/block/:userId', authenticateToken, ModerationController.blockUser);
router.delete('/block/:userId', authenticateToken, ModerationController.unblockUser);

export default router;
