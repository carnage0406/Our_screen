package com.example.ui.player

import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.StopScreenShare
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.Movie
import com.example.model.ScreenShareInfo
import com.example.ui.theme.CinemaAmber
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaLiveRed
import com.example.ui.theme.CinemaPrimary
import com.example.ui.theme.CinemaSecondary
import com.example.ui.theme.CinemaSuccess
import kotlinx.coroutines.delay

@Composable
fun MoviePlayerView(
  movie: Movie,
  isPlaying: Boolean,
  currentPositionMs: Long,
  durationMs: Long,
  screenShareInfo: ScreenShareInfo,
  isFullscreen: Boolean,
  onPlayPauseToggle: () -> Unit,
  onSeekTo: (Long) -> Unit,
  onToggleFullscreen: () -> Unit,
  onToggleScreenShare: () -> Unit,
  onResync: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showControls by remember { mutableStateOf(true) }
  var isVideoLoaded by remember { mutableStateOf(false) }
  var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
  val interactionSource = remember { MutableInteractionSource() }

  // Auto-hide controls after 4 seconds of inactivity when playing
  LaunchedEffect(showControls, isPlaying) {
    if (showControls && isPlaying) {
      delay(4000)
      showControls = false
    }
  }

  // Synchronize native VideoView playback state
  LaunchedEffect(isPlaying, videoViewRef) {
    videoViewRef?.let { vv ->
      if (isPlaying) {
        if (!vv.isPlaying) {
          vv.start()
        }
      } else {
        if (vv.isPlaying) {
          vv.pause()
        }
      }
    }
  }

  // Handle seeking if difference is > 1000ms
  LaunchedEffect(currentPositionMs) {
    videoViewRef?.let { vv ->
      if (kotlin.math.abs(vv.currentPosition - currentPositionMs) > 1500) {
        vv.seekTo(currentPositionMs.toInt())
      }
    }
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(if (isFullscreen) 0.dp else 16.dp))
      .background(Color.Black)
      .clickable(
        interactionSource = interactionSource,
        indication = null
      ) {
        showControls = !showControls
      }
      .testTag("movie_player_container")
  ) {
    // 1. Video Player Surface
    AndroidView(
      factory = { ctx ->
        VideoView(ctx).apply {
          layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )
          setOnErrorListener { _, what, extra ->
            Log.w("MoviePlayerView", "MediaPlayer error: what=$what, extra=$extra. Suppressed.")
            isVideoLoaded = true
            true
          }
          val uri = Uri.parse(movie.videoUrl)
          val headers = mapOf("User-Agent" to "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/120.0 Mobile Safari/537.36")
          try {
            setVideoURI(uri, headers)
          } catch (_: Exception) {
            setVideoURI(uri)
          }
          setOnPreparedListener { mp ->
            mp.isLooping = true
            isVideoLoaded = true
            if (currentPositionMs > 0) {
              seekTo(currentPositionMs.toInt())
            }
            if (isPlaying) {
              start()
            }
          }
          videoViewRef = this
        }
      },
      update = { vv ->
        videoViewRef = vv
      },
      modifier = Modifier
        .fillMaxSize()
        .testTag("native_video_view")
    )

    // Cinematic Poster Backdrop Gradient (in case video stream is connecting or screen share is streaming)
    if (screenShareInfo.isSharing) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.radialGradient(
              colors = listOf(
                Color(movie.primaryColorHex).copy(alpha = 0.6f),
                CinemaBackground.copy(alpha = 0.95f)
              )
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.padding(24.dp)
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(CinemaLiveRed.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.ScreenShare,
              contentDescription = "Screen Share Active",
              tint = CinemaLiveRed,
              modifier = Modifier.size(36.dp)
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Broadcasting Your Screen",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "${screenShareInfo.sourceTitle} • ${screenShareInfo.resolution}",
            style = MaterialTheme.typography.bodySmall,
            color = CinemaSecondary
          )
        }
      }
    }

    // 2. Top Bar (Overlay)
    AnimatedVisibility(
      visible = showControls,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.align(Alignment.TopCenter)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
            )
          )
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (screenShareInfo.isSharing) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(CinemaLiveRed)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "SCREEN SHARE",
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
              text = if (screenShareInfo.isSharing) screenShareInfo.sourceTitle else movie.title,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          Text(
            text = "${movie.genre} • ${movie.tag}",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp
          )
        }

        // Live Sync Status Pill
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .clickable { onResync() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("resync_button")
        ) {
          Icon(
            imageVector = Icons.Default.Sync,
            contentDescription = "Sync",
            tint = CinemaSuccess,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Synced",
            color = CinemaSuccess,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }

    // 3. Center Big Play/Pause & Scrub 10s Overlay
    AnimatedVisibility(
      visible = showControls,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.align(Alignment.Center)
    ) {
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        // Rewind -10s
        IconButton(
          onClick = {
            val newPos = (currentPositionMs - 10000L).coerceAtLeast(0L)
            onSeekTo(newPos)
          },
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .testTag("rewind_10s_button")
        ) {
          Icon(
            imageVector = Icons.Default.FastRewind,
            contentDescription = "Rewind 10 seconds",
            tint = Color.White,
            modifier = Modifier.size(26.dp)
          )
        }

        Spacer(modifier = Modifier.width(28.dp))

        // Big Play/Pause
        IconButton(
          onClick = onPlayPauseToggle,
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(CinemaPrimary, CinemaPrimary.copy(alpha = 0.8f))
              )
            )
            .testTag("main_play_pause_button")
        ) {
          Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = Color.White,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.width(28.dp))

        // Forward +10s
        IconButton(
          onClick = {
            val newPos = (currentPositionMs + 10000L).coerceAtMost(durationMs)
            onSeekTo(newPos)
          },
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .testTag("forward_10s_button")
        ) {
          Icon(
            imageVector = Icons.Default.FastForward,
            contentDescription = "Forward 10 seconds",
            tint = Color.White,
            modifier = Modifier.size(26.dp)
          )
        }
      }
    }

    // 4. Bottom Scrubber & Time Bar
    AnimatedVisibility(
      visible = showControls,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.align(Alignment.BottomCenter)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
            )
          )
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        // Slider
        Slider(
          value = if (durationMs > 0) currentPositionMs.toFloat() / durationMs.toFloat() else 0f,
          onValueChange = { fraction ->
            val targetMs = (fraction * durationMs).toLong()
            onSeekTo(targetMs)
          },
          colors = SliderDefaults.colors(
            thumbColor = CinemaSecondary,
            activeTrackColor = CinemaSecondary,
            inactiveTrackColor = Color.White.copy(alpha = 0.25f)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .testTag("playback_progress_slider")
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Time text
          Text(
            text = "${formatDuration(currentPositionMs)} / ${formatDuration(durationMs)}",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp
          )

          Row(verticalAlignment = Alignment.CenterVertically) {
            // Screen Share Toggle Button
            IconButton(
              onClick = onToggleScreenShare,
              modifier = Modifier
                .size(36.dp)
                .testTag("toggle_screen_share_button")
            ) {
              Icon(
                imageVector = if (screenShareInfo.isSharing) Icons.Default.StopScreenShare else Icons.Default.ScreenShare,
                contentDescription = "Toggle Screen Share",
                tint = if (screenShareInfo.isSharing) CinemaLiveRed else Color.White,
                modifier = Modifier.size(20.dp)
              )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Fullscreen Toggle
            IconButton(
              onClick = onToggleFullscreen,
              modifier = Modifier
                .size(36.dp)
                .testTag("toggle_fullscreen_button")
            ) {
              Icon(
                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                contentDescription = "Toggle Fullscreen",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }
      }
    }
  }
}

fun formatDuration(ms: Long): String {
  val totalSec = ms / 1000
  val hours = totalSec / 3600
  val minutes = (totalSec % 3600) / 60
  val seconds = totalSec % 60
  return if (hours > 0) {
    String.format("%d:%02d:%02d", hours, minutes, seconds)
  } else {
    String.format("%02d:%02d", minutes, seconds)
  }
}
