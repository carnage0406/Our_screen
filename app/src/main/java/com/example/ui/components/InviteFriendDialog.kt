package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Participant
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaLiveRed
import com.example.ui.theme.CinemaPrimary
import com.example.ui.theme.CinemaSecondary
import com.example.ui.theme.CinemaSuccess
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceVariant

@Composable
fun InviteFriendDialog(
  roomCode: String,
  participants: List<Participant>,
  onAddFriend: (String) -> Unit,
  onRemoveFriend: (String) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val shareUrl = "https://watchparty.live/join/$roomCode"

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .border(1.dp, CinemaSecondary.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
        .testTag("invite_friend_dialog"),
      color = CinemaSurface,
      tonalElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(22.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Watch Party Room",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = if (participants.isEmpty()) "You are alone in this room" else "${participants.size + 1} people in room",
              style = MaterialTheme.typography.labelSmall,
              color = if (participants.isEmpty()) Color.White.copy(alpha = 0.6f) else CinemaSuccess
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color.White.copy(alpha = 0.6f),
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Room Code Container
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CinemaBackground)
            .border(1.dp, CinemaPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "ROOM CODE",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = CinemaSecondary,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = roomCode,
              fontSize = 28.sp,
              fontWeight = FontWeight.Black,
              color = Color.White,
              letterSpacing = 2.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Current Connected Participants
        Text(
          text = "Joined Participants (${participants.size + 1})",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = CinemaSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))

        // You (Always present)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CinemaSurfaceVariant)
            .padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(CinemaPrimary),
            contentAlignment = Alignment.Center
          ) {
            Text("Y", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(text = "You (Host)", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 12.sp)
            Text(text = "Online • Sharing controls", color = CinemaSuccess, fontSize = 10.sp)
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Dynamic other participants
        if (participants.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(CinemaBackground.copy(alpha = 0.5f))
              .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
              .padding(12.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "No one else has joined yet",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
              )
              Spacer(modifier = Modifier.height(6.dp))
              Button(
                onClick = { onAddFriend("Jordan") },
                colors = ButtonDefaults.buttonColors(containerColor = CinemaSecondary.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(32.dp)
              ) {
                Text("+ Simulate Friend Joining", color = CinemaSecondary, fontSize = 11.sp)
              }
            }
          }
        } else {
          participants.forEach { p ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CinemaSurfaceVariant)
                .padding(horizontal = 10.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(CinemaSecondary),
                contentAlignment = Alignment.Center
              ) {
                Text(p.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(text = p.name, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 12.sp)
                Text(text = "Connected (${p.pingMs}ms)", color = CinemaSuccess, fontSize = 10.sp)
              }
              IconButton(
                onClick = { onRemoveFriend(p.id) },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Remove",
                  tint = CinemaLiveRed,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("WatchParty Room Code", roomCode)
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "Room code copied to clipboard!", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("copy_room_code_btn")
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = null,
              tint = CinemaSecondary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Copy Code", color = Color.White, fontSize = 13.sp)
          }

          Button(
            onClick = {
              val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Join my WatchParty! Watch movies together with screen sharing and camera: $shareUrl (Code: $roomCode)")
                type = "text/plain"
              }
              val shareIntent = Intent.createChooser(sendIntent, "Invite friend to WatchParty")
              context.startActivity(shareIntent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = CinemaPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("share_invite_link_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Share Link", color = Color.White, fontSize = 13.sp)
          }
        }
      }
    }
  }
}
