import { Router } from 'express';
import { SavedController } from '../controllers/savedController.js';
import { authenticateToken } from '../middleware/auth.js';

const router = Router();

router.get('/', authenticateToken, SavedController.getSavedPosts);
router.post('/:postId', authenticateToken, SavedController.savePost);
router.delete('/:postId', authenticateToken, SavedController.unsavePost);

export default router;
