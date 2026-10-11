import crypto from 'crypto';
import { getDatabase } from '../config/database.js';
import { config } from '../config/env.js';

export class RobotService {
  /**
   * Pre-configured contact Satish Kumar
   */
  static getContactSatish() {
    return {
      name: 'Satish Kumar',
      phoneNumber: '+91 98765 43210',
      email: 'satishhp2000@gmail.com',
      handle: '@satish_creator',
      avatarEmoji: '🏏'
    };
  }

  /**
   * Curated sports video collection
   */
  static getSportsVideosCatalog(query = '') {
    const catalog = [
      {
        id: 'sp_1',
        title: 'Fast Bowling Yorker Technique & Seam Position',
        sportCategory: 'Cricket',
        creatorName: 'Satish Kumar (Fast Bowler)',
        duration: '0:45',
        description: 'Detailed slow-motion release analysis showing wrist snap, seam alignment, and toe-crusher trajectory.',
        talentScore: 98,
        thumbnailUrl: 'https://images.unsplash.com/photo-1531415074968-036ba1b575da?w=600'
      },
      {
        id: 'sp_2',
        title: 'Freestyle Football: Around the World & Ground Moves',
        sportCategory: 'Football',
        creatorName: 'Sameer Akhtar',
        duration: '0:52',
        description: 'Street freestyle combo executed on gravel ground without expensive gear. Pure ball control mastery.',
        talentScore: 95,
        thumbnailUrl: 'https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=600'
      },
      {
        id: 'sp_3',
        title: 'Pro Kabaddi Ankle Hold & Thigh Block Drills',
        sportCategory: 'Kabaddi',
        creatorName: 'Raju Pehelwan',
        duration: '0:38',
        description: 'Traditional clay akhada training for explosive defensive dives and balance lock against raiders.',
        talentScore: 96,
        thumbnailUrl: 'https://images.unsplash.com/photo-1517649763962-0c623266ddc0?w=600'
      },
      {
        id: 'sp_4',
        title: 'Sprint Acceleration & 100m Block Start Mechanics',
        sportCategory: 'Athletics',
        creatorName: 'Pooja Verma (State Sprinter)',
        duration: '0:42',
        description: 'Biomechanics breakdown of the first 15 steps drive phase with explosive hip extension.',
        talentScore: 94,
        thumbnailUrl: 'https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=600'
      },
      {
        id: 'sp_5',
        title: 'Badminton Backhand Clear & Jump Smash Timing',
        sportCategory: 'Badminton',
        creatorName: 'Aditya Sen',
        duration: '0:49',
        description: 'Pronation technique drills to hit deep baseline clears from difficult backhand corners.',
        talentScore: 92,
        thumbnailUrl: 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=600'
      },
      {
        id: 'sp_6',
        title: 'Kushti Dhobi Pachhad Throw & Hip Twist',
        sportCategory: 'Wrestling',
        creatorName: 'Sultan Akhada',
        duration: '0:40',
        description: 'Authentic Indian mud wrestling technique with leverage and safe landing execution.',
        talentScore: 97,
        thumbnailUrl: 'https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=600'
      }
    ];

    const filter = (query || '').toLowerCase().trim();
    if (!filter || filter === 'sports' || filter === 'find sports') {
      return catalog;
    }

    return catalog.filter(item =>
      item.title.toLowerCase().includes(filter) ||
      item.sportCategory.toLowerCase().includes(filter) ||
      item.description.toLowerCase().includes(filter) ||
      item.creatorName.toLowerCase().includes(filter)
    );
  }

  /**
   * Process Natural Language Command
   */
  static async processCommand(userId, rawQuery) {
    const db = getDatabase();
    const query = (rawQuery || '').toLowerCase().trim();

    // Fetch user robot settings to verify permissions
    const settings = db.prepare('SELECT * FROM robot_settings WHERE user_id = ?').get(userId) || {
      is_robot_mode_on: 0,
      voice_commands_enabled: 1,
      screen_navigation_allowed: 1,
      messaging_permission_granted: 1,
      app_opening_permission_granted: 1,
      speed_multiplier: 1.0,
      lap_duration_seconds: 300.0
    };

    let intent = 'UNKNOWN';
    let responseText = '';
    let requiresConfirmation = false;
    let actionPayload = null;
    let status = 'executed';

    // 1. Emergency Stop
    if (query.includes('stop') || query.includes('emergency') || query.includes('freeze')) {
      intent = 'EMERGENCY_STOP';
      responseText = 'Emergency STOP engaged! Touch control restored immediately 🛑';
      status = 'executed';
      actionPayload = { action: 'RESTORE_TOUCH', touchInterceptionActive: false };

      db.prepare('UPDATE robot_settings SET touch_interception_active = 0 WHERE user_id = ?').run(userId);
    }
    // 2. Messaging Satish Kumar
    else if (query.includes('satish') || (query.includes('message') && query.includes('hi')) || query.includes('text satish')) {
      intent = 'MESSAGE_CONTACT';
      if (!settings.messaging_permission_granted) {
        responseText = 'Messaging permission is disabled in your Robot Settings. Please enable it first.';
        status = 'rejected';
      } else {
        requiresConfirmation = true;
        status = 'pending';
        const contact = this.getContactSatish();
        responseText = `Prepared message 'Hi' for ${contact.name} (${contact.phoneNumber}). User confirmation required.`;
        actionPayload = {
          recipientName: contact.name,
          recipientPhone: contact.phoneNumber,
          recipientHandle: contact.handle,
          message: 'Hi',
          supportedIntents: ['ACTION_SENDTO', 'smsto:+919876543210']
        };
      }
    }
    // 3. Sports Video Search
    else if (query.includes('sport') || query.includes('cricket') || query.includes('football') ||
             query.includes('kabaddi') || query.includes('athletic') || query.includes('wrestling') || query.includes('badminton')) {
      intent = 'SPORTS_SEARCH';
      const results = this.getSportsVideosCatalog(query);
      responseText = `Found ${results.length} sports masterclass videos matching your search.`;
      status = 'executed';
      actionPayload = {
        resultsCount: results.length,
        sportsVideos: results
      };
    }
    // 4. Open External Application
    else if (query.startsWith('open ') || query.startsWith('launch ') || query.includes('calculator') || query.includes('youtube') || query.includes('whatsapp')) {
      intent = 'OPEN_APP';
      if (!settings.app_opening_permission_granted) {
        responseText = 'App-opening permission is disabled in your Robot Settings.';
        status = 'rejected';
      } else {
        requiresConfirmation = true;
        status = 'pending';
        let targetApp = 'Generic';
        let packageName = '';
        if (query.includes('youtube')) { targetApp = 'YouTube'; packageName = 'com.google.android.youtube'; }
        else if (query.includes('whatsapp')) { targetApp = 'WhatsApp'; packageName = 'com.whatsapp'; }
        else if (query.includes('maps')) { targetApp = 'Google Maps'; packageName = 'com.google.android.apps.maps'; }
        else if (query.includes('camera')) { targetApp = 'Camera'; packageName = 'camera'; }
        else if (query.includes('calculator')) { targetApp = 'Calculator'; packageName = 'calculator'; }
        else if (query.includes('settings')) { targetApp = 'Settings'; packageName = 'settings'; }

        responseText = `Prepared to open ${targetApp}. User approval required before launching.`;
        actionPayload = { targetApp, packageName };
      }
    }
    // 5. Navigate Hunar Screens
    else if (query.includes('home') || query.includes('feed') || query.includes('reel') || query.includes('discover') ||
             query.includes('message') || query.includes('profile') || query.includes('settings')) {
      intent = 'NAVIGATE_SCREEN';
      if (!settings.screen_navigation_allowed) {
        responseText = 'Screen navigation permission is disabled in your Robot Settings.';
        status = 'rejected';
      } else {
        let destination = 'HOME';
        if (query.includes('reel') || query.includes('discover')) destination = 'REELS_SEARCH';
        else if (query.includes('message') || query.includes('chat')) destination = 'MESSAGES';
        else if (query.includes('profile') || query.includes('dashboard')) destination = 'PROFILE';
        else if (query.includes('setting')) destination = 'SETTINGS';

        responseText = `Navigating to ${destination}.`;
        status = 'executed';
        actionPayload = { destination };
      }
    }
    // 6. Speed & Lap Settings
    else if (query.includes('fast') || query.includes('speed') || query.includes('5 min') || query.includes('slow')) {
      intent = 'SPEED_CHANGE';
      let speed = 1.0;
      let lapSeconds = 300.0;
      if (query.includes('fast') || query.includes('speed up')) {
        speed = 2.0;
        lapSeconds = 150.0;
      } else if (query.includes('turbo')) {
        speed = 4.0;
        lapSeconds = 75.0;
      }
      db.prepare('UPDATE robot_settings SET speed_multiplier = ?, lap_duration_seconds = ? WHERE user_id = ?')
        .run(speed, lapSeconds, userId);

      responseText = `Robot speed updated to ${speed}x (${lapSeconds}s full perimeter lap).`;
      status = 'executed';
      actionPayload = { speedMultiplier: speed, lapDurationSeconds: lapSeconds };
    }
    // Default fallback
    else {
      intent = 'CONVERSATIONAL';
      responseText = `I understood: "${rawQuery}". Try asking me to "Message Satish", "Find sports videos", "Open YouTube", or "Go to Reels"!`;
      status = 'executed';
    }

    // Record in history audit table
    const commandId = `cmd_${crypto.randomUUID()}`;
    db.prepare(`
      INSERT INTO robot_commands_history (id, user_id, raw_query, intent_type, action_target, was_confirmed, status, created_at)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    `).run(commandId, userId, rawQuery, intent, JSON.stringify(actionPayload), requiresConfirmation ? 0 : 1, status, Date.now());

    return {
      commandId,
      intent,
      responseText,
      requiresConfirmation,
      status,
      actionPayload
    };
  }

  /**
   * Confirm and authorize sensitive action (e.g., messaging Satish or launching app)
   */
  static confirmAction(userId, commandId, approved) {
    const db = getDatabase();
    const command = db.prepare('SELECT * FROM robot_commands_history WHERE id = ? AND user_id = ?').get(commandId, userId);

    if (!command) {
      return { success: false, message: 'Command not found' };
    }

    if (!approved) {
      db.prepare('UPDATE robot_commands_history SET status = ? WHERE id = ?').run('canceled', commandId);
      return { success: true, message: 'Action canceled safely' };
    }

    db.prepare('UPDATE robot_commands_history SET was_confirmed = 1, status = ? WHERE id = ?').run('executed', commandId);
    return {
      success: true,
      message: 'Action authorized and logged',
      payload: JSON.parse(command.action_target || '{}')
    };
  }
}
