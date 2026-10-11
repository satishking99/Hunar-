package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.derivedStateOf

/**
 * PAGE 1 — HOME 🏠 (Full-Screen Infinite Scroll Feed)
 * Feed where users can:
 * - Watch photos and videos in full-screen infinite scroll
 * - Like, comment and share
 * - Follow creators
 * - Upload photos and videos (with Quick Upload trigger)
 * - Create posts
 * - Start LIVE streaming & watch LIVE content
 * - See recommended content & stories
 */
@Composable
fun HomeScreen(
    posts: List<Post>,
    stories: List<Story>,
    selectedCategory: SkillCategory,
    strikeStatus: StrikeStatus,
    isLoadingMore: Boolean = false,
    onLoadMore: () -> Unit = {},
    onCategorySelected: (SkillCategory) -> Unit,
    onSavePost: (String) -> Unit = {},
    onViewRulesModal: () -> Unit,
    onOpenStory: (Story) -> Unit,
    onOpenCreatePost: () -> Unit,
    onOpenLiveStreaming: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    val filteredPosts = remember(posts, selectedCategory) {
        if (selectedCategory == SkillCategory.ALL) posts
        else posts.filter { it.skillCategory == selectedCategory }
    }

    // Infinite scroll detection: triggers loadMore when user scrolls near the end
    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 3 && lastVisibleItemIndex >= totalItems - 2
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoadingMore) {
            onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen_feed"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Strike Alert Banner if active
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                StrikeAlertBanner(
                    strikeStatus = strikeStatus,
                    onViewRulesClick = onViewRulesModal
                )
            }
        }

        // Quick Creation & Go LIVE Bar
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("quick_create_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onOpenCreatePost)
                    ) {
                        Text(text = "✍️", fontSize = 22.sp)
                        Text(
                            text = "Share your talent or project...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(
                            onClick = onOpenCreatePost,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = HunarOrange.copy(alpha = 0.15f),
                                contentColor = HunarOrange
                            )
                        ) {
                            Text(text = "＋ Post", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onOpenLiveStreaming,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HunarError,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "🔴 LIVE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Stories Carousel (Including live streamers)
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
        if (filteredPosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🎯", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No content in ${selectedCategory.displayName} yet",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Be the first to share your hard work and talent!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(filteredPosts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    onSaveClick = { onSavePost(post.id) },
                    onViewRulesModal = onViewRulesModal
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Infinite Scroll Loader Footer
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp)
                        .testTag("infinite_scroll_footer"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoadingMore) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = HunarOrange
                            )
                            Text(
                                text = "Loading more hard work & talent...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = "✨ Pure talent, infinite discovery",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}
