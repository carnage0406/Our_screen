package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.PartyTab
import com.example.model.VideoViewMode
import com.example.ui.camera.CameraVideoFeed
import com.example.ui.components.AddCustomMovieDialog
import com.example.ui.components.InviteFriendDialog
import com.example.ui.components.MovieCatalogView
import com.example.ui.components.PartyChatSheet
import com.example.ui.components.ReactionsOverlay
import com.example.ui.components.ScreenShareDialog
import com.example.ui.player.MoviePlayerView
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaLiveRed
import com.example.ui.theme.CinemaPrimary
import com.example.ui.theme.CinemaSecondary
import com.example.ui.theme.CinemaSuccess
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceHigh
import com.example.ui.theme.CinemaSurfaceVariant
import com.example.viewmodel.WatchPartyViewModel

@Composable
fun WatchPartyApp(
  viewModel: WatchPartyViewModel = viewModel()
) {
  val uiState by viewModel.uiState.collectAsState()

  Scaffold(
    containerColor = CinemaBackground,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    bottomBar = {
      if (!uiState.isFullscreen) {
        NavigationBar(
          containerColor = CinemaSurface,
          modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_navigation_bar")
        ) {
          NavigationBarItem(
            selected = uiState.currentTab == PartyTab.WATCH,
            onClick = { viewModel.setCurrentTab(PartyTab.WATCH) },
            icon = { Icon(Icons.Default.Tv, contentDescription = "Watch") },
            label = { Text("Watch", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = CinemaSecondary,
              selectedTextColor = CinemaSecondary,
              indicatorColor = CinemaSurfaceHigh,
              unselectedIconColor = Color.White.copy(alpha = 0.6f),
              unselectedTextColor = Color.White.copy(alpha = 0.6f)
            ),
            modifier = Modifier.testTag("nav_item_watch")
          )

          NavigationBarItem(
            selected = uiState.currentTab == PartyTab.MOVIES,
            onClick = { viewModel.setCurrentTab(PartyTab.MOVIES) },
            icon = { Icon(Icons.Default.Movie, contentDescription = "Movies") },
            label = { Text("Movies", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = CinemaSecondary,
              selectedTextColor = CinemaSecondary,
              indicatorColor = CinemaSurfaceHigh,
              unselectedIconColor = Color.White.copy(alpha = 0.6f),
              unselectedTextColor = Color.White.copy(alpha = 0.6f)
            ),
            modifier = Modifier.testTag("nav_item_movies")
          )

          NavigationBarItem(
            selected = uiState.currentTab == PartyTab.SCREEN_SHARE,
            onClick = { viewModel.showScreenShareDialog(true) },
            icon = {
              if (uiState.screenShareInfo.isSharing) {
                BadgedBox(badge = { Badge { Text("LIVE") } }) {
                  Icon(Icons.Default.ScreenShare, contentDescription = "Screen Share")
                }
              } else {
                Icon(Icons.Default.ScreenShare, contentDescription = "Screen Share")
              }
            },
            label = { Text("Share Screen", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = CinemaLiveRed,
              selectedTextColor = CinemaLiveRed,
              indicatorColor = CinemaSurfaceHigh,
              unselectedIconColor = Color.White.copy(alpha = 0.6f),
              unselectedTextColor = Color.White.copy(alpha = 0.6f)
            ),
            modifier = Modifier.testTag("nav_item_screen_share")
          )

          NavigationBarItem(
            selected = uiState.currentTab == PartyTab.CHAT,
            onClick = { viewModel.setCurrentTab(PartyTab.CHAT) },
            icon = {
              BadgedBox(badge = { Badge { Text("${uiState.chatMessages.size}") } }) {
                Icon(Icons.Default.Chat, contentDescription = "Chat")
              }
            },
            label = { Text("Chat & React", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = CinemaSecondary,
              selectedTextColor = CinemaSecondary,
              indicatorColor = CinemaSurfaceHigh,
              unselectedIconColor = Color.White.copy(alpha = 0.6f),
              unselectedTextColor = Color.White.copy(alpha = 0.6f)
            ),
            modifier = Modifier.testTag("nav_item_chat")
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = if (!uiState.isFullscreen) innerPadding.calculateBottomPadding() else 0.dp)
        .background(CinemaBackground)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        if (!uiState.isFullscreen) {
          TopPartyHeader(
            roomCode = uiState.roomCode,
            isScreenSharing = uiState.screenShareInfo.isSharing,
            viewMode = uiState.viewMode,
            onOpenInvite = { viewModel.showInviteDialog(true) },
            onToggleScreenShare = { viewModel.showScreenShareDialog(true) },
            onToggleViewMode = {
              val nextMode = when (uiState.viewMode) {
                VideoViewMode.THEATER -> VideoViewMode.SPLIT_SCREEN
                VideoViewMode.SPLIT_SCREEN -> VideoViewMode.THEATER
                VideoViewMode.FULLSCREEN -> VideoViewMode.THEATER
              }
              viewModel.setViewMode(nextMode)
            }
          )
        }

        // Main Tab Content
        when (uiState.currentTab) {
          PartyTab.WATCH, PartyTab.SCREEN_SHARE -> {
            WatchTheaterContent(
              uiState = uiState,
              viewModel = viewModel,
              modifier = Modifier.weight(1f)
            )
          }

          PartyTab.MOVIES -> {
            MovieCatalogView(
              movies = uiState.movies,
              currentMovieId = uiState.currentMovie.id,
              onSelectMovie = { movie -> viewModel.selectMovie(movie) },
              onOpenAddMovieDialog = { viewModel.showAddCustomMovieDialog(true) },
              modifier = Modifier.weight(1f)
            )
          }

          PartyTab.CHAT -> {
            Column(modifier = Modifier.weight(1f)) {
              // Mini Player at top when chatting
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(180.dp)
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                MoviePlayerView(
                  movie = uiState.currentMovie,
                  isPlaying = uiState.isPlaying,
                  currentPositionMs = uiState.currentPositionMs,
                  durationMs = uiState.durationMs,
                  screenShareInfo = uiState.screenShareInfo,
                  isFullscreen = false,
                  onPlayPauseToggle = { viewModel.togglePlayPause() },
                  onSeekTo = { pos -> viewModel.seekTo(pos) },
                  onToggleFullscreen = { viewModel.toggleFullscreen() },
                  onToggleScreenShare = { viewModel.showScreenShareDialog(true) },
                  onResync = { viewModel.resync() },
                  modifier = Modifier.fillMaxSize()
                )
              }

              PartyChatSheet(
                messages = uiState.chatMessages,
                onSendMessage = { text -> viewModel.sendMessage(text) },
                onSendReaction = { emoji -> viewModel.sendReaction(emoji) },
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }

      // Floating Reactions Layer (over the entire screen)
      ReactionsOverlay(
        reactions = uiState.reactions,
        onReactionFinished = { id -> viewModel.removeReaction(id) }
      )

      // Dialogs
      if (uiState.showScreenShareDialog) {
        ScreenShareDialog(
          currentInfo = uiState.screenShareInfo,
          onDismiss = { viewModel.showScreenShareDialog(false) },
          onStartScreenShare = { source, url -> viewModel.startScreenShare(source, url) },
          onStopScreenShare = { viewModel.stopScreenShare() }
        )
      }

      if (uiState.showInviteDialog) {
        InviteFriendDialog(
          roomCode = uiState.roomCode,
          friend = uiState.friend,
          onDismiss = { viewModel.showInviteDialog(false) }
        )
      }

      if (uiState.showAddCustomMovieDialog) {
        AddCustomMovieDialog(
          onDismiss = { viewModel.showAddCustomMovieDialog(false) },
          onAddMovie = { title, url, genre ->
            viewModel.addCustomMovie(title, url, genre)
          }
        )
      }
    }
  }
}

@Composable
fun TopPartyHeader(
  roomCode: String,
  isScreenSharing: Boolean,
  viewMode: VideoViewMode,
  onOpenInvite: () -> Unit,
  onToggleScreenShare: () -> Unit,
  onToggleViewMode: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .windowInsetsPadding(WindowInsets.statusBars)
      .background(CinemaSurface)
      .padding(horizontal = 14.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Brand & Live Friend Status
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(CinemaPrimary),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Tv,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = "WatchParty",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(CinemaSuccess)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Jordan • Watching live",
            fontSize = 10.sp,
            color = CinemaSuccess,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Action Pills: Screen Share Badge + View Mode + Room Code
    Row(verticalAlignment = Alignment.CenterVertically) {
      if (isScreenSharing) {
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CinemaLiveRed.copy(alpha = 0.2f))
            .border(1.dp, CinemaLiveRed, RoundedCornerShape(12.dp))
            .clickable { onToggleScreenShare() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.ScreenShare,
            contentDescription = null,
            tint = CinemaLiveRed,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "LIVE",
            color = CinemaLiveRed,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
      }

      // Switch View Mode (PiP vs Split)
      IconButton(
        onClick = onToggleViewMode,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(CinemaSurfaceHigh)
          .testTag("toggle_view_mode_btn")
      ) {
        Icon(
          imageVector = if (viewMode == VideoViewMode.THEATER) Icons.Default.ViewAgenda else Icons.Default.PictureInPictureAlt,
          contentDescription = "Toggle Layout",
          tint = CinemaSecondary,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.width(6.dp))

      // Room Code Pill
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(14.dp))
          .background(CinemaSurfaceHigh)
          .clickable { onOpenInvite() }
          .padding(horizontal = 8.dp, vertical = 6.dp)
          .testTag("header_room_code_btn"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.PersonAdd,
          contentDescription = null,
          tint = CinemaSecondary,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = roomCode,
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun WatchTheaterContent(
  uiState: com.example.viewmodel.WatchPartyUiState,
  viewModel: WatchPartyViewModel,
  modifier: Modifier = Modifier
) {
  if (uiState.viewMode == VideoViewMode.SPLIT_SCREEN) {
    // Split Screen Mode: Movie on top, side-by-side cams on bottom
    Column(modifier = modifier.fillMaxSize().padding(12.dp)) {
      // Movie Player (55% height)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(0.55f)
      ) {
        MoviePlayerView(
          movie = uiState.currentMovie,
          isPlaying = uiState.isPlaying,
          currentPositionMs = uiState.currentPositionMs,
          durationMs = uiState.durationMs,
          screenShareInfo = uiState.screenShareInfo,
          isFullscreen = uiState.isFullscreen,
          onPlayPauseToggle = { viewModel.togglePlayPause() },
          onSeekTo = { pos -> viewModel.seekTo(pos) },
          onToggleFullscreen = { viewModel.toggleFullscreen() },
          onToggleScreenShare = { viewModel.showScreenShareDialog(true) },
          onResync = { viewModel.resync() },
          modifier = Modifier.fillMaxSize()
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Dual Camera Windows (Side-by-side on bottom 45%)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .weight(0.45f),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // User's Camera
        CameraVideoFeed(
          isMyCamera = true,
          userName = "${uiState.userName} (You)",
          isCameraActive = uiState.isMyCameraOn,
          isMicMuted = uiState.isMyMicMuted,
          isFrontCamera = uiState.isFrontCamera,
          onToggleCamera = { viewModel.toggleMyCamera() },
          onToggleMic = { viewModel.toggleMyMic() },
          onSwitchCamera = { viewModel.switchCameraLens() },
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
        )

        // Friend's Camera
        CameraVideoFeed(
          isMyCamera = false,
          userName = uiState.friend.name,
          isCameraActive = uiState.friend.isCameraOn,
          isMicMuted = uiState.friend.isMicMuted,
          onToggleCamera = {},
          onToggleMic = {},
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
        )
      }
    }
  } else {
    // Theater Mode: Big Movie Display with floating Picture-in-Picture camera bubbles!
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 6.dp)) {
      // Main Movie Player Container with Floating PiP Video Chat
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
      ) {
        MoviePlayerView(
          movie = uiState.currentMovie,
          isPlaying = uiState.isPlaying,
          currentPositionMs = uiState.currentPositionMs,
          durationMs = uiState.durationMs,
          screenShareInfo = uiState.screenShareInfo,
          isFullscreen = uiState.isFullscreen,
          onPlayPauseToggle = { viewModel.togglePlayPause() },
          onSeekTo = { pos -> viewModel.seekTo(pos) },
          onToggleFullscreen = { viewModel.toggleFullscreen() },
          onToggleScreenShare = { viewModel.showScreenShareDialog(true) },
          onResync = { viewModel.resync() },
          modifier = Modifier.fillMaxSize()
        )

        // Floating Camera Window 1: User's Camera (Bottom-Left)
        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = 12.dp, bottom = 48.dp)
            .width(130.dp)
            .height(98.dp)
        ) {
          CameraVideoFeed(
            isMyCamera = true,
            userName = "You",
            isCameraActive = uiState.isMyCameraOn,
            isMicMuted = uiState.isMyMicMuted,
            isFrontCamera = uiState.isFrontCamera,
            onToggleCamera = { viewModel.toggleMyCamera() },
            onToggleMic = { viewModel.toggleMyMic() },
            onSwitchCamera = { viewModel.switchCameraLens() },
            modifier = Modifier.fillMaxSize()
          )
        }

        // Floating Camera Window 2: Friend's Camera (Bottom-Right)
        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 12.dp, bottom = 48.dp)
            .width(130.dp)
            .height(98.dp)
        ) {
          CameraVideoFeed(
            isMyCamera = false,
            userName = uiState.friend.name,
            isCameraActive = uiState.friend.isCameraOn,
            isMicMuted = uiState.friend.isMicMuted,
            onToggleCamera = {},
            onToggleMic = {},
            modifier = Modifier.fillMaxSize()
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Bottom Quick Bar for Reactions & Screen Share shortcut
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(CinemaSurfaceVariant)
          .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          listOf("🍿", "😂", "🔥", "❤️").forEach { emoji ->
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .clickable { viewModel.sendReaction(emoji) }
                .testTag("theater_reaction_$emoji"),
              contentAlignment = Alignment.Center
            ) {
              Text(text = emoji, fontSize = 18.sp)
            }
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Camera On/Off shortcut
          IconButton(
            onClick = { viewModel.toggleMyCamera() },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (uiState.isMyCameraOn) CinemaPrimary else CinemaSurfaceHigh)
              .testTag("shortcut_toggle_cam")
          ) {
            Icon(
              imageVector = Icons.Default.Videocam,
              contentDescription = "Camera",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Screen Share shortcut
          IconButton(
            onClick = { viewModel.showScreenShareDialog(true) },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (uiState.screenShareInfo.isSharing) CinemaLiveRed else CinemaSurfaceHigh)
              .testTag("shortcut_screen_share")
          ) {
            Icon(
              imageVector = Icons.Default.ScreenShare,
              contentDescription = "Screen Share",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}
