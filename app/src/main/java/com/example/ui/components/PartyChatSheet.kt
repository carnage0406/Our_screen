package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaPrimary
import com.example.ui.theme.CinemaSecondary
import com.example.ui.theme.CinemaSurfaceHigh
import com.example.ui.theme.CinemaSurfaceVariant

@Composable
fun PartyChatSheet(
  messages: List<ChatMessage>,
  onSendMessage: (String) -> Unit,
  onSendReaction: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var textInput by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  val reactionOptions = listOf("🍿", "😂", "🔥", "😱", "❤️", "👏")

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CinemaBackground)
  ) {
    // Quick Floating Reaction Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(CinemaSurfaceVariant)
        .padding(horizontal = 12.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      reactionOptions.forEach { emoji ->
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .clickable { onSendReaction(emoji) }
            .testTag("quick_reaction_$emoji"),
          contentAlignment = Alignment.Center
        ) {
          Text(text = emoji, fontSize = 20.sp)
        }
      }
    }

    // Message List
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(messages, key = { it.id }) { msg ->
        ChatMessageItem(message = msg)
      }
    }

    // Input Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(CinemaSurfaceVariant)
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = textInput,
        onValueChange = { textInput = it },
        placeholder = {
          Text(
            text = "Chat with friend...",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 13.sp
          )
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = CinemaSecondary,
          unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          cursorColor = CinemaSecondary
        ),
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
        modifier = Modifier
          .weight(1f)
          .testTag("chat_message_input")
      )

      Spacer(modifier = Modifier.width(8.dp))

      IconButton(
        onClick = {
          if (textInput.isNotBlank()) {
            onSendMessage(textInput.trim())
            textInput = ""
          }
        },
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(CinemaPrimary)
          .testTag("chat_send_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Send,
          contentDescription = "Send",
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
  val isMe = message.isFromMe

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
  ) {
    Column(
      horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
      modifier = Modifier.fillMaxWidth(0.85f)
    ) {
      // Sender Name & Timestamp
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
      ) {
        Text(
          text = message.senderName,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = if (isMe) CinemaSecondary else Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = message.timestamp,
          fontSize = 9.sp,
          color = Color.White.copy(alpha = 0.4f)
        )
      }

      // Bubble
      Box(
        modifier = Modifier
          .clip(
            RoundedCornerShape(
              topStart = 16.dp,
              topEnd = 16.dp,
              bottomStart = if (isMe) 16.dp else 4.dp,
              bottomEnd = if (isMe) 4.dp else 16.dp
            )
          )
          .background(
            if (isMe) CinemaPrimary else CinemaSurfaceHigh
          )
          .padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = message.message,
            color = Color.White,
            fontSize = 13.sp,
            lineHeight = 18.sp
          )
          if (message.attachedEmoji != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = message.attachedEmoji, fontSize = 16.sp)
          }
        }
      }
    }
  }
}
