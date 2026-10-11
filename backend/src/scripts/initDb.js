import { getDatabase } from '../config/database.js';
import { validateEnv } from '../config/env.js';

console.log('[InitDb] Initializing Hunar Database...');
validateEnv();
const db = getDatabase();

const users = db.prepare('SELECT COUNT(*) as count FROM users').get();
const posts = db.prepare('SELECT COUNT(*) as count FROM posts').get();
const analytics = db.prepare('SELECT COUNT(*) as count FROM creator_analytics').get();
const robot = db.prepare('SELECT COUNT(*) as count FROM robot_settings').get();

console.log(`[InitDb] Initialization complete!`);
console.log(` - Users count: ${users.count}`);
console.log(` - Posts count: ${posts.count}`);
console.log(` - Analytics records: ${analytics.count}`);
console.log(` - Robot settings records: ${robot.count}`);
process.exit(0);
