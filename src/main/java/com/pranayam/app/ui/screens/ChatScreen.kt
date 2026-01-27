package com.pranayam.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pranayam.app.data.model.*
import com.pranayam.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    chatInfo: Conversation,
    messages: List<Message>,
    messageText: String,
    isRecording: Boolean,
    recordingDuration: Int,
    remoteUserStatus: String = "Offline",
    remoteUserTyping: Boolean = false,
    onMessageChange: (String, String) -> Unit,
    onSendClick: () -> Unit,
    onBack: () -> Unit,
    onProfileClick: () -> Unit,
    onVoiceRecordStart: () -> Unit,
    onVoiceRecordStop: () -> Unit,
    onVoiceRecordCancel: () -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    // Auto-scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    Scaffold(
        topBar = {
            ChatTopBar(
                name = chatInfo.name,
                age = chatInfo.age,
                photoUrl = chatInfo.photoUrl,
                isOnline = remoteUserStatus == "Online",
                lastSeen = if (remoteUserStatus == "Online") "Active now" else "Offline",
                isVerified = chatInfo.isVerified,
                isTyping = remoteUserTyping,
                onBackClick = onBack,
                onProfileClick = onProfileClick,
                onVideoCall = { /* TODO */ },
                onMoreClick = { /* Show report/block options */ }
            )
        },
        bottomBar = {
            ChatInputBar(
                messageText = messageText,
                onMessageChange = { onMessageChange(it, conversationId) },
                onSendClick = onSendClick,
                onAttachClick = { /* Show media picker */ },
                onVoiceRecordStart = onVoiceRecordStart,
                onVoiceRecordStop = onVoiceRecordStop,
                onVoiceRecordCancel = onVoiceRecordCancel,
                isRecording = isRecording,
                recordingDuration = recordingDuration
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PranayamColors.ChatBackground)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    horizontal = Spacing.XS,
                    vertical = Spacing.S
                )
            ) {
                items(
                    items = messages,
                    key = { it.id }
                ) { message ->
                    when (message.type) {
                        MessageType.DATE_SEPARATOR -> {
                            DateSeparator(date = message.text)
                        }
                        MessageType.SYSTEM -> {
                            SystemMessage(text = message.text)
                        }
                        else -> {
                            ChatBubble(
                                message = message
                            )
                        }
                    }
                }
            }
            
            // Scroll to bottom FAB
            if (listState.firstVisibleItemIndex > 5) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(Spacing.M)
                        .size(40.dp),
                    containerColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = Elevation.Medium
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Scroll to bottom",
                        tint = PranayamColors.TextSecondaryLight
                    )
                }
            }
        }
    }
}

@Composable
fun ChatTopBar(
    name: String,
    age: Int,
    photoUrl: String,
    isOnline: Boolean,
    lastSeen: String,
    isVerified: Boolean,
    isTyping: Boolean = false,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onVideoCall: () -> Unit,
    onMoreClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = Elevation.Small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = Spacing.XXS),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onProfileClick)
                    .padding(horizontal = Spacing.S),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    if (isOnline) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(PranayamColors.Success)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }
                }
                
                Spacer(Modifier.width(Spacing.M))
                
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$name, $age",
                            style = PranayamTypography.LabelLarge
                        )
                        if (isVerified) {
                            Spacer(Modifier.width(Spacing.XXS))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                modifier = Modifier.size(14.dp),
                                tint = PranayamColors.Info
                            )
                        }
                    }
                    
                    Text(
                        text = if (isTyping) "Typing..." else if (isOnline) "Active now" else "Active $lastSeen",
                        style = PranayamTypography.LabelSmall,
                        color = if (isTyping || isOnline) 
                            PranayamColors.Success 
                        else 
                            PranayamColors.TextTertiaryLight
                    )
                }
            }
            
            IconButton(onClick = onVideoCall) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Video call",
                    tint = PranayamColors.PurplePrimary
                )
            }
            
            IconButton(onClick = onMoreClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options"
                )
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: Message,
    modifier: Modifier = Modifier
) {
    val isSent = message.isSent
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = Spacing.XS,
                vertical = Spacing.XXS
            ),
        horizontalAlignment = if (isSent) Alignment.End else Alignment.Start
    ) {
        when (message.contentType) {
            ContentType.TEXT -> TextBubble(message, isSent)
            ContentType.IMAGE -> ImageBubble(message, isSent)
            ContentType.VOICE -> VoiceBubble(message, isSent)
            else -> {}
        }
    }
}

@Composable
fun TextBubble(message: Message, isSent: Boolean) {
    Surface(
        shape = RoundedCornerShape(
            topStart = CornerRadius.M,
            topEnd = CornerRadius.M,
            bottomStart = if (isSent) CornerRadius.M else CornerRadius.XS,
            bottomEnd = if (isSent) CornerRadius.XS else CornerRadius.M
        ),
        color = if (isSent) PranayamColors.ChatBubbleSent else PranayamColors.ChatBubbleReceived,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .padding(
                    horizontal = Spacing.M,
                    vertical = Spacing.S
                )
        ) {
            Text(
                text = message.text,
                style = PranayamTypography.BodyMedium,
                color = PranayamColors.TextPrimaryLight
            )
            
            Spacer(Modifier.height(Spacing.XXS))
            
            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.XXS)
            ) {
                Text(
                    text = message.timestamp,
                    style = PranayamTypography.ChatMessageTime,
                    color = Color(0xFF667781)
                )
                
                if (isSent) {
                    Icon(
                        imageVector = when (message.status) {
                            MessageStatus.SENT -> Icons.Default.Done
                            MessageStatus.DELIVERED -> Icons.Default.DoneAll
                            MessageStatus.READ -> Icons.Default.DoneAll
                            else -> Icons.Default.Schedule
                        },
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (message.status == MessageStatus.READ) 
                            PranayamColors.Info 
                        else 
                            Color(0xFF667781)
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceBubble(message: Message, isSent: Boolean) {
    Surface(
        shape = RoundedCornerShape(
            topStart = CornerRadius.M,
            topEnd = CornerRadius.M,
            bottomStart = if (isSent) CornerRadius.M else CornerRadius.XS,
            bottomEnd = if (isSent) CornerRadius.XS else CornerRadius.M
        ),
        color = if (isSent) PranayamColors.ChatBubbleSent else PranayamColors.ChatBubbleReceived,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .widthIn(min = 200.dp, max = 280.dp)
                .padding(Spacing.S),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Toggle play */ },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = PranayamColors.RosePrimary
                )
            }
            
            Spacer(Modifier.width(Spacing.S))
            
            // Placeholder for waveform
            Box(modifier = Modifier.weight(1f).height(24.dp).background(Color.LightGray.copy(alpha = 0.3f)))
            
            Spacer(Modifier.width(Spacing.S))
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = message.duration ?: "0:00",
                    style = PranayamTypography.LabelSmall,
                    color = PranayamColors.TextSecondaryLight
                )
                
                Text(
                    text = message.timestamp,
                    style = PranayamTypography.ChatMessageTime,
                    color = Color(0xFF667781)
                )
            }
        }
    }
}

@Composable
fun ImageBubble(message: Message, isSent: Boolean) {
    Surface(
        shape = RoundedCornerShape(CornerRadius.M),
        shadowElevation = 1.dp
    ) {
        Box {
            AsyncImage(
                model = message.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .heightIn(max = 400.dp)
                    .clip(RoundedCornerShape(CornerRadius.M)),
                contentScale = ContentScale.Crop
            )
            
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Spacing.S),
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(Spacing.S)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = Spacing.S,
                        vertical = Spacing.XXS
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.XXS)
                ) {
                    Text(
                        text = message.timestamp,
                        style = PranayamTypography.ChatMessageTime,
                        color = Color.White
                    )
                    
                    if (isSent) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (message.status == MessageStatus.READ) 
                                PranayamColors.Info 
                            else 
                                Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateSeparator(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.M),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xFFE1F3FB),
            shape = RoundedCornerShape(Spacing.S)
        ) {
            Text(
                text = date,
                style = PranayamTypography.LabelSmall,
                color = Color(0xFF54656F),
                modifier = Modifier.padding(
                    horizontal = Spacing.M,
                    vertical = Spacing.XXS
                )
            )
        }
    }
}

@Composable
fun SystemMessage(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.S),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xFFFFF9C4),
            shape = RoundedCornerShape(Spacing.S)
        ) {
            Text(
                text = text,
                style = PranayamTypography.BodySmall,
                color = Color(0xFF5F5F5F),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    horizontal = Spacing.M,
                    vertical = Spacing.S
                )
            )
        }
    }
}

@Composable
fun ChatInputBar(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onAttachClick: () -> Unit,
    onVoiceRecordStart: () -> Unit,
    onVoiceRecordStop: () -> Unit,
    onVoiceRecordCancel: () -> Unit,
    isRecording: Boolean,
    recordingDuration: Int
) {
    Surface(
        color = Color.White,
        shadowElevation = Elevation.Medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Spacing.S,
                    vertical = Spacing.S
                )
        ) {
            if (!isRecording) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onAttachClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Attach media",
                            tint = PranayamColors.TextSecondaryLight
                        )
                    }
                    
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(CornerRadius.XXL),
                        color = PranayamColors.ChatInputBg
                    ) {
                        BasicTextField(
                            value = messageText,
                            onValueChange = onMessageChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = Spacing.M,
                                    vertical = Spacing.M
                                ),
                            textStyle = PranayamTypography.BodyMedium.copy(
                                color = PranayamColors.TextPrimaryLight
                            ),
                            maxLines = 6,
                            decorationBox = { innerTextField ->
                                if (messageText.isEmpty()) {
                                    Text(
                                        text = "Type message...",
                                        style = PranayamTypography.BodyMedium,
                                        color = PranayamColors.TextTertiaryLight
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                    
                    Spacer(Modifier.width(Spacing.S))
                    
                    if (messageText.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PrimaryGradient)
                                .clickable(onClick = onSendClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PranayamColors.ChatInputBg)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            onVoiceRecordStart()
                                            tryAwaitRelease()
                                            onVoiceRecordStop()
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice message",
                                tint = PranayamColors.TextSecondaryLight
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onVoiceRecordCancel,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = PranayamColors.Error
                        )
                    }
                    
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(PranayamColors.Error)
                        )
                        
                        Spacer(Modifier.width(Spacing.S))
                        
                        Text(
                            text = formatDuration(recordingDuration),
                            style = PranayamTypography.LabelMedium,
                            color = PranayamColors.Error
                        )
                        
                        Spacer(Modifier.width(Spacing.M))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = PranayamColors.TextTertiaryLight
                            )
                            Text(
                                text = "Slide to cancel",
                                style = PranayamTypography.LabelSmall,
                                color = PranayamColors.TextTertiaryLight
                            )
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PrimaryGradient)
                            .clickable(onClick = onVoiceRecordStop),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send voice",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

fun formatDuration(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", mins, secs)
}
