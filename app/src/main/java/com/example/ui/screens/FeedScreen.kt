package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun FeedScreen(
    posts: List<Post>,
    stories: List<Story>,
    selectedCategory: SkillCategory,
    strikeStatus: StrikeStatus,
    onCategorySelected: (SkillCategory) -> Unit,
    onSavePost: (String) -> Unit,
    onViewRulesModal: () -> Unit,
    onOpenStory: (Story) -> Unit
) {
    val filteredPosts = remember(posts, selectedCategory) {
        if (selectedCategory == SkillCategory.ALL) posts
        else posts.filter { it.skillCategory == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("feed_screen_column")
    ) {
        // Strike Alert Banner (Golden Rules Reminder)
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                StrikeAlertBanner(
                    strikeStatus = strikeStatus,
                    onViewRulesClick = onViewRulesModal
                )
            }
        }

        // Stories Bar
        item {
            StoriesSection(
                stories = stories,
                onStoryClick = onOpenStory
            )
        }

        // Skill Categories Chips with Orange Filter
        item {
            SkillCategoryChips(
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Feed Posts
        items(filteredPosts, key = { it.id }) { post ->
            PostCard(
                post = post,
                onSaveClick = { onSavePost(post.id) },
                onViewRulesModal = onViewRulesModal
            )
        }
    }
}

@Composable
fun StoriesSection(
    stories: List<Story>,
    onStoryClick: (Story) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "Talent Stories & Live Creators",
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(stories) { story ->
                StoryAvatarItem(story = story, onClick = { onStoryClick(story) })
            }
        }
    }
}

@Composable
fun StoryAvatarItem(
    story: Story,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("story_${story.id}")
    ) {
        val borderBrush = if (story.isLive) {
            Brush.sweepGradient(listOf(HunarError, HunarOrange, HunarError))
        } else if (!story.isViewed) {
            Brush.sweepGradient(listOf(HunarOrange, HunarAmber, HunarOrange))
        } else {
            Brush.sweepGradient(listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)))
        }

        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .border(2.5.dp, borderBrush, CircleShape)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            if (story.creator.avatarDrawableRes != null) {
                Image(
                    painter = painterResource(id = story.creator.avatarDrawableRes),
                    contentDescription = story.creator.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = story.creator.avatarEmoji, fontSize = 24.sp)
                }
            }

            // LIVE indicator badge
            if (story.isLive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 2.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(HunarError)
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = story.creator.name.take(9),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
        Text(
            text = story.skillTag,
            color = HunarOrange,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
fun PostCard(
    post: Post,
    onSaveClick: () -> Unit,
    onViewRulesModal: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("post_card_${post.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Avatar + Creator Name • Category + Time (No public views count)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Circular Avatar
                if (post.creator.avatarDrawableRes != null) {
                    Image(
                        painter = painterResource(id = post.creator.avatarDrawableRes),
                        contentDescription = post.creator.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = post.creator.avatarEmoji, fontSize = 18.sp)
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${post.creator.name} • ${post.skillCategory.displayName}",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${post.timeAgo} • Skill Verified",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                // Privacy Indicator Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Private",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "Private Stats",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Video/Image Preview Container (16:9 widescreen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .testTag("post_media_${post.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (post.drawableRes != null) {
                    Image(
                        painter = painterResource(id = post.drawableRes),
                        contentDescription = post.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = post.skillCategory.icon, fontSize = 42.sp)
                        Text(
                            text = post.title,
                            color = HunarOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Caption
            Text(
                text = post.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = post.caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ACTION BAR: REQUIREMENT 2 — ONLY SHOW THE SAVE OPTION
            // - Users can save videos and photos privately.
            // - Only the account owner can access their saved content.
            // - Never show who saved a post or the number of saves.
            // - Do not show public Like, Comment or Share buttons.
            // - Do not show public comment sections or lists of users who liked a post.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSaveClick() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("save_btn_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (post.isSaved) "Saved" else "Save Privately",
                        tint = if (post.isSaved) HunarOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = if (post.isSaved) "Saved to Collection" else "Save Privately",
                        color = if (post.isSaved) HunarOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "🔒 No Public Counts",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}
