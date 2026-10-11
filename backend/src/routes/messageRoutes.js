import { Router } from 'express';
import { MessageController } from '../controllers/messageController.js';
import { authenticateToken } from '../middleware/auth.js';
import { validateMessage } from '../middleware/validator.js';

const router = Router();

router.get('/conversations', authenticateToken, MessageController.getConversations);
router.get('/:conversationId', authenticateToken, MessageController.getMessages);
router.post('/', authenticateToken, validateMessage, MessageController.sendMessage);

export default router;
