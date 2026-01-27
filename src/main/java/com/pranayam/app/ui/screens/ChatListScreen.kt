package com.pranayam.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pranayam.app.data.model.Conversation
import com.pranayam.app.data.model.Match
import com.pranayam.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    matches: List<Match>,
    conversations: List<Conversation>,
    onChatClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Matches", style = PranayamTypography.H3) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // New Matches Carousel
            if (matches.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            text = "New Matches",
                            style = PranayamTypography.LabelLarge,
                            modifier = Modifier.padding(Spacing.M)
                        )
                        
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = Spacing.M),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.M)
                        ) {
                            items(matches) { match ->
                                NewMatchItem(
                                    match = match,
                                    onClick = { onChatClick(match.id) }
                                )
                            }
                        }
                        
                        HorizontalDivider(
                            modifier = Modifier.padding(
                                horizontal = Spacing.M,
                                vertical = Spacing.M
                            ),
                            color = PranayamColors.DividerLight
                        )
                    }
                }
            }
            
            // Conversations Header
            item {
                Text(
                    text = "All Conversations",
                    style = PranayamTypography.LabelLarge,
                    modifier = Modifier.padding(
                        horizontal = Spacing.M,
                        bottom = Spacing.S
                    )
                )
            }
            
            // Conversations List
            items(
                items = conversations,
                key = { it.id }
            ) { conversation ->
                ConversationItem(
                    conversation = conversation,
                    onClick = { onChatClick(conversation.id) }
                )
            }
        }
    }
}

@Composable
fun NewMatchItem(
    match: Match,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box {
            AsyncImage(
                model = match.photoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            // Red dot for unread
            if (match.isUnread) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(PranayamColors.Error)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
        }
        
        Spacer(Modifier.height(Spacing.XXS))
        
        Text(
            text = match.name,
            style = PranayamTypography.LabelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 80.dp)
        )
    }
}

@Composable
fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Note: SwipeToDismiss is a bit complex to implement perfectly without state,
    // so here's a simplified version of the UI.
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(Spacing.M),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Photo
            Box {
                AsyncImage(
                    model = conversation.photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                // Online indicator
                if (conversation.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(PranayamColors.Success)
                            .border(2.dp, Color.White, CircleShape)
                    )
                }
            }
            
            Spacer(Modifier.width(Spacing.M))
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Name + Verified
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${conversation.name}, ${conversation.age}",
                            style = PranayamTypography.LabelLarge,
                            maxLines = 1
                        )
                        if (conversation.isVerified) {
                            Spacer(Modifier.width(Spacing.XXS))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                modifier = Modifier.size(16.dp),
                                tint = PranayamColors.Info
                            )
                        }
                    }
                    
                    // Timestamp
                    Text(
                        text = conversation.timestamp,
                        style = PranayamTypography.LabelSmall,
                        color = if (conversation.unreadCount > 0) 
                            PranayamColors.RosePrimary 
                        else 
                            PranayamColors.TextTertiaryLight
                    )
                }
                
                Spacer(Modifier.height(Spacing.XXS))
                
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Last message preview
                    Text(
                        text = conversation.lastMessage,
                        style = PranayamTypography.BodyMedium,
                        color = if (conversation.unreadCount > 0) 
                            PranayamColors.TextPrimaryLight 
                        else 
                            PranayamColors.TextSecondaryLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        fontWeight = if (conversation.unreadCount > 0) 
                            FontWeight.SemiBold 
                        else 
                            FontWeight.Normal
                    )
                    
                    // Unread badge
                    if (conversation.unreadCount > 0) {
                        Spacer(Modifier.width(Spacing.S))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(PranayamColors.RosePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = conversation.unreadCount.toString(),
                                style = PranayamTypography.LabelSmall,
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
