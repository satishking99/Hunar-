import { Router } from 'express';
import { AnalyticsController } from '../controllers/analyticsController.js';
import { authenticateToken } from '../middleware/auth.js';

const router = Router();

// Private dashboard (Strictly restricted to account owner)
router.get('/dashboard', authenticateToken, AnalyticsController.getDashboard);
router.get('/payments', authenticateToken, AnalyticsController.getPayments);
router.post('/withdraw', authenticateToken, AnalyticsController.requestWithdrawal);

export default router;
