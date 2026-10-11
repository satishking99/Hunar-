import path from 'path';

export class UploadController {
  static async uploadMedia(req, res, next) {
    try {
      if (!req.file) {
        return res.status(400).json({ success: false, message: 'No file uploaded or file rejected by MIME filter' });
      }

      // Safe relative URL
      const relativeUrl = `/uploads/${path.basename(req.file.path)}`;

      res.status(201).json({
        success: true,
        message: 'File uploaded successfully',
        data: {
          filename: req.file.filename,
          originalName: req.file.originalname,
          mimetype: req.file.mimetype,
          size: req.file.size,
          url: relativeUrl
        }
      });
    } catch (err) {
      next(err);
    }
  }
}
