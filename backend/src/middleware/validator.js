/**
 * Input validation and sanitization middleware
 */

export function sanitizeString(str) {
  if (typeof str !== 'string') return '';
  return str
    .replace(/[<>]/g, '') // Strip basic HTML tags
    .trim();
}

export function validateRegister(req, res, next) {
  const { email, password, name, handle } = req.body;

  const errors = [];
  if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    errors.push('A valid email address is required');
  }
  if (!password || password.length < 8) {
    errors.push('Password must be at least 8 characters long');
  }
  if (!name || name.trim().length < 2) {
    errors.push('Full name must be at least 2 characters long');
  }
  if (!handle || !/^@[a-zA-Z0-9_]{3,30}$/.test(handle)) {
    errors.push('Handle must begin with @ and contain 3-30 alphanumeric characters or underscores');
  }

  if (errors.length > 0) {
    return res.status(400).json({
      success: false,
      error: 'ValidationError',
      errors
    });
  }

  req.body.name = sanitizeString(name);
  req.body.email = email.toLowerCase().trim();
  req.body.handle = handle.toLowerCase().trim();

  next();
}

export function validateLogin(req, res, next) {
  const { identifier, password } = req.body;
  if (!identifier || !password) {
    return res.status(400).json({
      success: false,
      error: 'ValidationError',
      message: 'Email/handle and password are required'
    });
  }
  next();
}

export function validatePost(req, res, next) {
  const { title, category, content_type } = req.body;
  const errors = [];

  if (!title || title.trim().length < 3) {
    errors.push('Post title must be at least 3 characters');
  }
  const allowedCategories = ['TECH', 'ART', 'DANCE', 'MUSIC', 'COOKING', 'SPORTS', 'CRAFT'];
  if (!category || !allowedCategories.includes(category.toUpperCase())) {
    errors.push(`Category must be one of: ${allowedCategories.join(', ')}`);
  }
  const allowedTypes = ['photo', 'video', 'reel'];
  if (content_type && !allowedTypes.includes(content_type.toLowerCase())) {
    errors.push(`Content type must be one of: ${allowedTypes.join(', ')}`);
  }

  if (errors.length > 0) {
    return res.status(400).json({
      success: false,
      error: 'ValidationError',
      errors
    });
  }

  req.body.title = sanitizeString(title);
  if (req.body.description) {
    req.body.description = sanitizeString(req.body.description);
  }
  req.body.category = category.toUpperCase();

  next();
}

export function validateMessage(req, res, next) {
  const { recipient_id, content } = req.body;
  if (!recipient_id || !content || content.trim().length === 0) {
    return res.status(400).json({
      success: false,
      error: 'ValidationError',
      message: 'recipient_id and non-empty content are required'
    });
  }
  req.body.content = sanitizeString(content);
  next();
}
