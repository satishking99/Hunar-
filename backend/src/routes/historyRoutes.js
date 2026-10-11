import { Router } from 'express';
import { HistoryController } from '../controllers/historyController.js';
import { authenticateToken } from '../middleware/auth.js';

const router = Router();

router.get('/', authenticateToken, HistoryController.getHistory);
router.post('/', authenticateToken, HistoryController.recordHistory);
router.delete('/', authenticateToken, HistoryController.clearHistory);

export default router;
