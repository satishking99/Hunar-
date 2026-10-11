import { Router } from 'express';
import { PostController } from '../controllers/postController.js';
import { authenticateToken, optionalAuth } from '../middleware/auth.js';
import { validatePost } from '../middleware/validator.js';

const router = Router();

router.get('/', optionalAuth, PostController.getPosts);
router.get('/:id', optionalAuth, PostController.getPostById);
router.post('/', authenticateToken, validatePost, PostController.createPost);
router.put('/:id', authenticateToken, PostController.updatePost);
router.delete('/:id', authenticateToken, PostController.deletePost);

export default router;
