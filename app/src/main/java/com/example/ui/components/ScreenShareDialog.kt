package com.example.ui.components

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
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.StopScreenShare
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.model.ScreenShareInfo
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaLiveRed
import com.example.ui.theme.CinemaPrimary
import com.example.ui.theme.CinemaSecondary
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceHigh
import com.example.ui.theme.CinemaSurfaceVariant

@Composable
fun ScreenShareDialog(
  currentInfo: ScreenShareInfo,
  onDismiss: () -> Unit,
  onStartScreenShare: (sourceTitle: String, customUrl: String?) -> Unit,
  onStopScreenShare: () -> Unit
) {
  val sources = listOf(
    Pair("Movie Tab (Chrome / Browser)", Icons.Default.Tab),
    Pair("Entire Phone Screen", Icons.Default.PhoneAndroid),
    Pair("Stream URL / Online Video", Icons.Default.Link)
  )

  var selectedSource by remember { mutableStateOf(currentInfo.sourceTitle) }
  var customStreamUrl by remember { mutableStateOf("") }
  var shareAudio by remember { mutableStateOf(currentInfo.sharedAudio) }
  var selectedResolution by remember { mutableStateOf("1080p 60fps") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .border(1.dp, CinemaSecondary.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
        .testTag("screen_share_dialog"),
      color = CinemaSurface,
      tonalElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CinemaPrimary.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.ScreenShare,
                contentDescription = null,
                tint = CinemaSecondary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Screen Sharing",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Stream movies & tabs directly to Jordan",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f)
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color.White.copy(alpha = 0.7f),
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Choose Source
        Text(
          text = "Select Broadcast Source",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
          color = CinemaSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))

        sources.forEach { (title, icon) ->
          val isSelected = selectedSource == title || (title.startsWith("Movie Tab") && selectedSource.startsWith("Movie Tab"))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) CinemaSurfaceHigh else CinemaSurfaceVariant)
              .border(
                width = 1.dp,
                color = if (isSelected) CinemaSecondary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
              )
              .clickable { selectedSource = title }
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = if (isSelected) CinemaSecondary else Color.White.copy(alpha = 0.6f),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = title,
              style = MaterialTheme.typography.bodyMedium,
              color = Color.White,
              modifier = Modifier.weight(1f)
            )
            if (isSelected) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = CinemaSecondary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        // Optional Custom URL field
        if (selectedSource.contains("Stream URL")) {
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = customStreamUrl,
            onValueChange = { customStreamUrl = it },
            placeholder = { Text("https://example.com/movie.mp4", fontSize = 12.sp) },
            label = { Text("Video / Screen Stream Link") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = CinemaSecondary,
              unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("custom_stream_url_input"),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Audio Sharing Switch
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CinemaSurfaceVariant)
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.VolumeUp,
              contentDescription = null,
              tint = CinemaSecondary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Share Internal Movie Audio",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
              )
              Text(
                text = "Send synced sound to friend",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp
              )
            }
          }

          Switch(
            checked = shareAudio,
            onCheckedChange = { shareAudio = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = CinemaPrimary
            )
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        if (currentInfo.isSharing) {
          Button(
            onClick = {
              onStopScreenShare()
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(containerColor = CinemaLiveRed),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("stop_screen_share_dialog_btn")
          ) {
            Icon(
              imageVector = Icons.Default.StopScreenShare,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Stop Screen Sharing",
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        } else {
          Button(
            onClick = {
              onStartScreenShare(
                selectedSource,
                if (selectedSource.contains("Stream URL") && customStreamUrl.isNotBlank()) customStreamUrl else null
              )
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(containerColor = CinemaPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("start_screen_share_dialog_btn")
          ) {
            Icon(
              imageVector = Icons.Default.ScreenShare,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Start Sharing Screen",
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }
  }
}
