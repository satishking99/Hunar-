import { Router } from 'express';
import { RobotController } from '../controllers/robotController.js';
import { authenticateToken } from '../middleware/auth.js';

const router = Router();

// Command execution (Text or Voice)
router.post('/command', authenticateToken, RobotController.processCommand);

// Sports video search discovery
router.get('/sports-videos', authenticateToken, RobotController.getSportsVideos);

// Robot assistant settings & permissions
router.get('/settings', authenticateToken, RobotController.getSettings);
router.put('/settings', authenticateToken, RobotController.updateSettings);

// Sensitive action confirmation (e.g. Satish messaging)
router.post('/confirm-action', authenticateToken, RobotController.confirmAction);

// Robot commands audit history
router.get('/history', authenticateToken, RobotController.getHistory);

export default router;
