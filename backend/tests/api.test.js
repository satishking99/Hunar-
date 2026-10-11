import { test, describe, before } from 'node:test';
import assert from 'node:assert/strict';
import request from 'supertest';
import { createApp } from '../src/app.js';
import { getDatabase } from '../src/config/database.js';

describe('Hunar Backend & AI Robot Assistant API Test Suite', () => {
  let app;
  let testUserToken = '';
  let testUserId = '';
  let testRefreshToken = '';
  let createdPostId = '';

  before(() => {
    // Ensure DB is initialized
    getDatabase();
    app = createApp();
  });

  // 1. SYSTEM HEALTH CHECK
  test('GET /api/health should return 200 and healthy database status', async () => {
    const res = await request(app).get('/api/health');
    assert.equal(res.status, 200);
    assert.equal(res.body.status, 'healthy');
    assert.equal(res.body.database, 'connected');
    assert.equal(res.body.service, 'Hunar Social & AI Robot Assistant Backend');
  });

  // 2. AUTHENTICATION: REGISTER
  test('POST /api/v1/auth/register should register a new creator account', async () => {
    const uniqueEmail = `creator_${Date.now()}@hunar.app`;
    const uniqueHandle = `@craft_${Date.now().toString().slice(-6)}`;

    const res = await request(app)
      .post('/api/v1/auth/register')
      .send({
        email: uniqueEmail,
        password: 'SecurePassword123!',
        name: 'Kavita Verma',
        handle: uniqueHandle,
        category: 'CRAFT',
        bio: 'Handmade jute crafts and sustainable weaving.'
      });

    assert.equal(res.status, 201);
    assert.equal(res.body.success, true);
    assert.ok(res.body.data.tokens.accessToken);
    assert.ok(res.body.data.tokens.refreshToken);
    assert.equal(res.body.data.user.name, 'Kavita Verma');

    testUserToken = res.body.data.tokens.accessToken;
    testRefreshToken = res.body.data.tokens.refreshToken;
    testUserId = res.body.data.user.id;
  });

  // 3. AUTHENTICATION: LOGIN
  test('POST /api/v1/auth/login should authenticate with verified seed creator', async () => {
    const res = await request(app)
      .post('/api/v1/auth/login')
      .send({
        identifier: 'satishhp2000@gmail.com',
        password: 'Hunar@2026'
      });

    assert.equal(res.status, 200);
    assert.equal(res.body.success, true);
    assert.equal(res.body.data.user.handle, '@satish_creator');
    assert.ok(res.body.data.tokens.accessToken);
  });

  // 4. AUTHENTICATION: REFRESH TOKEN ROTATION
  test('POST /api/v1/auth/refresh should rotate and return fresh tokens', async () => {
    const res = await request(app)
      .post('/api/v1/auth/refresh')
      .send({ refreshToken: testRefreshToken });

    assert.equal(res.status, 200);
    assert.equal(res.body.success, true);
    assert.ok(res.body.data.tokens.accessToken);
    assert.notEqual(res.body.data.tokens.refreshToken, testRefreshToken); // Rotated token

    testUserToken = res.body.data.tokens.accessToken;
    testRefreshToken = res.body.data.tokens.refreshToken;
  });

  // 5. POSTS & PRIVACY COMPLIANCE
  test('GET /api/v1/posts should strictly hide numerical likes/views/comments', async () => {
    const res = await request(app).get('/api/v1/posts');
    assert.equal(res.status, 200);
    assert.equal(res.body.success, true);
    assert.ok(Array.isArray(res.body.data));
    assert.ok(res.body.data.length > 0);

    const firstPost = res.body.data[0];
    assert.ok(firstPost.id);
    assert.ok(firstPost.title);
    assert.ok(firstPost.creator);

    // CRITICAL PRIVACY CHECK: Ensure public numerical statistics are completely absent
    assert.equal(firstPost.likes, undefined, 'Public like count must NOT be exposed');
    assert.equal(firstPost.views, undefined, 'Public view count must NOT be exposed');
    assert.equal(firstPost.comments, undefined, 'Public comments count must NOT be exposed');
    assert.equal(firstPost.shares, undefined, 'Public share count must NOT be exposed');

    // Only private Save action should be present
    assert.equal(typeof firstPost.isSaved, 'boolean');
  });

  // 6. CREATE POST
  test('POST /api/v1/posts should create post with authenticated creator', async () => {
    const res = await request(app)
      .post('/api/v1/posts')
      .set('Authorization', `Bearer ${testUserToken}`)
      .send({
        title: 'Traditional Handloom Cotton Sarees Weaving',
        description: 'Loom shuttle speed and thread tension calibration masterclass.',
        category: 'CRAFT',
        contentType: 'video',
        duration: '0:55',
        mediaUrl: 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=600'
      });

    assert.equal(res.status, 201);
    assert.equal(res.body.success, true);
    assert.ok(res.body.data.id);
    createdPostId = res.body.data.id;
  });

  // 7. SAVED POSTS (PRIVATE BOOKMARKING)
  test('POST /api/v1/saved/:postId should privately save post', async () => {
    const res = await request(app)
      .post(`/api/v1/saved/${createdPostId}`)
      .set('Authorization', `Bearer ${testUserToken}`);

    assert.equal(res.status, 200);
    assert.equal(res.body.success, true);
    assert.equal(res.body.isSaved, true);
  });

  test('GET /api/v1/saved should return only authenticated user saved collection', async () => {
    const res = await request(app)
      .get('/api/v1/saved')
      .set('Authorization', `Bearer ${testUserToken}`);

    assert.equal(res.status, 200);
    assert.equal(res.body.success, true);
    assert.ok(res.body.data.some(p => p.id === createdPostId));
  });

  // 8. WATCH HISTORY
  test('POST /api/v1/history should record watch event privately', async () => {
    const res = await request(app)
      .post('/api/v1/history')
      .set('Authorization', `Bearer ${testUserToken}`)
      .send({
        postId: createdPostId,
        mediaType: 'video',
        watchTimeSeconds: 45
      });

    assert.equal(res.status, 201);
    assert.equal(res.body.success, true);
  });

  // 9. MESSAGING & CONVERSATIONS
  test('POST /api/v1/messages should send private message to contact', async () => {
    const res = await request(app)
      .post('/api/v1/messages')
      .set('Authorization', `Bearer ${testUserToken}`)
      .send({
        recipient_id: 'u_satish',
        content: 'Hi Satish! Loved your yorker delivery breakdown.'
      });

    assert.equal(res.status, 201);
    assert.equal(res.body.success, true);
    assert.ok(res.body.data.conversationId);
  });

  // 10. AI ROBOT ASSISTANT: MESSAGE SATISH PREPARATION
  test('POST /api/v1/robot/command should prepare message to Satish with confirmation required', async () => {
    const res = await request(app)
      .post('/api/v1/robot/command')
      .set('Authorization', `Bearer ${testUserToken}`)
      .send({ query: 'Send message to Satish' });

    assert.equal(res.status, 200);
    assert.equal(res.body.success, true);
    assert.equal(res.body.data.intent, 'MESSAGE_CONTACT');
    assert.equal(res.body.data.requiresConfirmation, true);
    assert.equal(res.body.data.actionPayload.recipientName, 'Satish Kumar');
    assert.equal(res.body.data.actionPayload.message, 'Hi');

    // Confirm the action
    const confirmRes = await request(app)
      .post('/api/v1/robot/confirm-action')
      .set('Authorization', `Bearer ${testUserToken}`)
      .send({
        commandId: res.body.data.commandId,
        approved: true
      });

    assert.equal(confirmRes.status, 200);
    assert.equal(confirmRes.body.data.success, true);
  });

  // 11. AI ROBOT ASSISTANT: SPORTS VIDEO SEARCH
  test('GET /api/v1/robot/sports-videos should return cricket and sports masterclasses', async () => {
    const res = await request(app)
      .get('/api/v1/robot/sports-videos?query=cricket')
      .set('Authorization', `Bearer ${testUserToken}`);

    assert.equal(res.status, 200);
    assert.equal(res.body.success, true);
    assert.ok(res.body.data.length > 0);
    assert.equal(res.body.data[0].sportCategory, 'Cricket');
  });

  // 12. AI ROBOT ASSISTANT: EMERGENCY STOP
  test('POST /api/v1/robot/command with "stop robot" should trigger EMERGENCY_STOP', async () => {
    const res = await request(app)
      .post('/api/v1/robot/command')
      .set('Authorization', `Bearer ${testUserToken}`)
      .send({ query: 'Emergency STOP robot' });

    assert.equal(res.status, 200);
    assert.equal(res.body.data.intent, 'EMERGENCY_STOP');
    assert.equal(res.body.data.actionPayload.touchInterceptionActive, false);
  });

  // 13. PRIVATE CREATOR ANALYTICS DASHBOARD
  test('GET /api/v1/analytics/dashboard should return private creator metrics', async () => {
    const res = await request(app)
      .get('/api/v1/analytics/dashboard')
      .set('Authorization', `Bearer ${testUserToken}`);

    assert.equal(res.status, 200);
    assert.equal(res.body.success, true);
    assert.ok(res.body.data.totalViews !== undefined);
    assert.ok(res.body.data.totalWatchTimeMinutes !== undefined);
    assert.ok(res.body.data.withdrawableBalance !== undefined);
  });

  // 14. MODERATION: REPORT AND BLOCK
  test('POST /api/v1/moderation/report should submit a content report', async () => {
    const res = await request(app)
      .post('/api/v1/moderation/report')
      .set('Authorization', `Bearer ${testUserToken}`)
      .send({
        reported_post_id: createdPostId,
        reason: 'Low effort or copyright violation',
        details: 'Testing Hunar merit rules enforcement'
      });

    assert.equal(res.status, 201);
    assert.equal(res.body.success, true);
  });
});
