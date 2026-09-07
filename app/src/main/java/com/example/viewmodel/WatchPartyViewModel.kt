package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SampleMovies
import com.example.model.ChatMessage
import com.example.model.FloatingReaction
import com.example.model.FriendParticipant
import com.example.model.Movie
import com.example.model.Participant
import com.example.model.PartyTab
import com.example.model.ScreenShareInfo
import com.example.model.VideoViewMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class WatchPartyUiState(
  val roomCode: String = "PARTY-7829",
  val userName: String = "Alex",
  val currentMovie: Movie = SampleMovies.movieList.first(),
  val movies: List<Movie> = SampleMovies.movieList,
  val isPlaying: Boolean = true,
  val currentPositionMs: Long = 42000L,
  val durationMs: Long = SampleMovies.movieList.first().durationMs,
  val screenShareInfo: ScreenShareInfo = ScreenShareInfo(),
  val isMyCameraOn: Boolean = true,
  val isMyMicMuted: Boolean = false,
  val isFrontCamera: Boolean = true,
  // Other joined friends in the party. Default is empty (alone) until someone joins!
  val participants: List<Participant> = emptyList(),
  val chatMessages: List<ChatMessage> = listOf(
    ChatMessage(
      id = "1",
      senderName = "System",
      message = "🎉 Welcome to your WatchParty room! Share the code to invite friends.",
      timestamp = "Now",
      isFromMe = false
    )
  ),
  val reactions: List<FloatingReaction> = emptyList(),
  val currentTab: PartyTab = PartyTab.WATCH,
  val viewMode: VideoViewMode = VideoViewMode.THEATER,
  val isFullscreen: Boolean = false,
  val showScreenShareDialog: Boolean = false,
  val showInviteDialog: Boolean = false,
  val showAddCustomMovieDialog: Boolean = false
) {
  // Total members in party = You (1) + any other joined participants
  val totalMemberCount: Int
    get() = 1 + participants.count { it.isConnected }

  val isFriendJoined: Boolean
    get() = participants.any { it.isConnected }
}

class WatchPartyViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(WatchPartyUiState())
  val uiState: StateFlow<WatchPartyUiState> = _uiState.asStateFlow()

  private var playbackJob: Job? = null

  init {
    startPlaybackTicker()
  }

  private fun startPlaybackTicker() {
    playbackJob?.cancel()
    playbackJob = viewModelScope.launch {
      while (true) {
        delay(1000)
        if (_uiState.value.isPlaying) {
          _uiState.update { state ->
            val nextPos = (state.currentPositionMs + 1000L).coerceAtMost(state.durationMs)
            state.copy(currentPositionMs = nextPos)
          }
        }
      }
    }
  }

  fun togglePlayPause() {
    val willPlay = !_uiState.value.isPlaying
    _uiState.update { it.copy(isPlaying = willPlay) }
    // Add brief system notification in chat
    val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    val actionText = if (willPlay) "Alex resumed the movie" else "Alex paused the movie"
    addSystemChatNotice(actionText, time)
  }

  fun seekTo(positionMs: Long) {
    _uiState.update { it.copy(currentPositionMs = positionMs) }
  }

  fun selectMovie(movie: Movie) {
    _uiState.update {
      it.copy(
        currentMovie = movie,
        currentPositionMs = 0L,
        durationMs = movie.durationMs,
        isPlaying = true,
        currentTab = PartyTab.WATCH
      )
    }
    val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    addSystemChatNotice("Alex switched movie to \"${movie.title}\"", time)
  }

  fun startScreenShare(sourceTitle: String, customUrl: String? = null) {
    val movieToPlay = if (!customUrl.isNullOrBlank()) {
      Movie(
        id = "custom_${System.currentTimeMillis()}",
        title = "Custom Stream: $sourceTitle",
        genre = "Live Stream Broadcast",
        duration = "Live",
        durationMs = 3600000L,
        description = "Live streaming directly from $customUrl",
        videoUrl = customUrl,
        primaryColorHex = 0xFFBE123C,
        secondaryColorHex = 0xFF6366F1,
        rating = "LIVE",
        tag = "Custom Link",
        isCustom = true
      )
    } else {
      _uiState.value.currentMovie
    }

    _uiState.update {
      it.copy(
        screenShareInfo = ScreenShareInfo(
          isSharing = true,
          sourceTitle = sourceTitle,
          resolution = "1080p 60fps",
          bitrate = "6.5 Mbps",
          latencyMs = 18,
          sharedAudio = true,
          presenterName = it.userName
        ),
        currentMovie = movieToPlay,
        showScreenShareDialog = false
      )
    }

    val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    addSystemChatNotice("${_uiState.value.userName} started sharing screen: $sourceTitle (Video camera remains active)", time)
    sendReaction("💻")
  }

  fun stopScreenShare() {
    _uiState.update {
      it.copy(
        screenShareInfo = ScreenShareInfo(isSharing = false),
        showScreenShareDialog = false
      )
    }
    val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    addSystemChatNotice("Screen sharing stopped", time)
  }

  // Join a simulated friend (or remove when leaving)
  fun addFriendToRoom(name: String = "Jordan") {
    if (_uiState.value.participants.any { it.name == name && it.isConnected }) return
    val newFriend = Participant(
      id = UUID.randomUUID().toString(),
      name = name,
      isMe = false,
      isConnected = true,
      isCameraOn = true,
      isMicMuted = false,
      isScreenSharing = false,
      pingMs = (20..35).random()
    )
    _uiState.update {
      it.copy(participants = it.participants + newFriend)
    }
    val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    addSystemChatNotice("$name joined the party room 🎉", time)
  }

  fun removeParticipant(participantId: String) {
    val participant = _uiState.value.participants.find { it.id == participantId }
    _uiState.update {
      it.copy(participants = it.participants.filterNot { p -> p.id == participantId })
    }
    participant?.let {
      val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
      addSystemChatNotice("${it.name} left the room", time)
    }
  }

  fun toggleMyCamera() {
    _uiState.update { it.copy(isMyCameraOn = !it.isMyCameraOn) }
  }

  fun toggleMyMic() {
    _uiState.update { it.copy(isMyMicMuted = !it.isMyMicMuted) }
  }

  fun switchCameraLens() {
    _uiState.update { it.copy(isFrontCamera = !it.isFrontCamera) }
  }

  fun setViewMode(mode: VideoViewMode) {
    _uiState.update { it.copy(viewMode = mode) }
  }

  fun toggleFullscreen() {
    _uiState.update { it.copy(isFullscreen = !it.isFullscreen) }
  }

  fun setCurrentTab(tab: PartyTab) {
    _uiState.update { it.copy(currentTab = tab) }
  }

  fun showScreenShareDialog(show: Boolean) {
    _uiState.update { it.copy(showScreenShareDialog = show) }
  }

  fun showInviteDialog(show: Boolean) {
    _uiState.update { it.copy(showInviteDialog = show) }
  }

  fun showAddCustomMovieDialog(show: Boolean) {
    _uiState.update { it.copy(showAddCustomMovieDialog = show) }
  }

  fun resync() {
    // Snap position to sync
    val targetPos = _uiState.value.currentPositionMs
    _uiState.update {
      it.copy(
        currentPositionMs = targetPos,
        isPlaying = true
      )
    }
    sendReaction("⚡")
  }

  fun sendMessage(text: String) {
    val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    val newMsg = ChatMessage(
      id = UUID.randomUUID().toString(),
      senderName = _uiState.value.userName,
      message = text,
      timestamp = time,
      isFromMe = true
    )
    _uiState.update { it.copy(chatMessages = it.chatMessages + newMsg) }

    // Only generate friend reply if someone has joined the room
    val joinedFriend = _uiState.value.participants.firstOrNull { it.isConnected }
    if (joinedFriend != null) {
      viewModelScope.launch {
        delay(2000)
        val friendReplies = listOf(
          "Totally agree! 😄",
          "Haha that scene was crazy! 🍿",
          "Wait pause in 5 mins, grabbing water!",
          "Screen quality looks great on my side! 👍",
          "Look at that cinematography! 🔥"
        )
        val reply = friendReplies.random()
        val friendTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val friendMsg = ChatMessage(
          id = UUID.randomUUID().toString(),
          senderName = joinedFriend.name,
          message = reply,
          timestamp = friendTime,
          isFromMe = false
        )
        _uiState.update { it.copy(chatMessages = it.chatMessages + friendMsg) }
      }
    }
  }

  fun sendReaction(emoji: String) {
    val reaction = FloatingReaction(
      id = UUID.randomUUID().toString(),
      emoji = emoji,
      senderName = _uiState.value.userName,
      xOffsetFraction = (0.2f + Math.random().toFloat() * 0.6f)
    )
    _uiState.update { it.copy(reactions = it.reactions + reaction) }

    val joinedFriend = _uiState.value.participants.firstOrNull { it.isConnected }
    if (joinedFriend != null) {
      viewModelScope.launch {
        delay(1200)
        val friendReaction = FloatingReaction(
          id = UUID.randomUUID().toString(),
          emoji = emoji,
          senderName = joinedFriend.name,
          xOffsetFraction = (0.2f + Math.random().toFloat() * 0.6f)
        )
        _uiState.update { it.copy(reactions = it.reactions + friendReaction) }
      }
    }
  }

  fun removeReaction(id: String) {
    _uiState.update { state ->
      state.copy(reactions = state.reactions.filterNot { it.id == id })
    }
  }

  fun addCustomMovie(title: String, url: String, genre: String) {
    val newMovie = Movie(
      id = "custom_${System.currentTimeMillis()}",
      title = title,
      genre = genre.ifBlank { "Custom Video" },
      duration = "Stream",
      durationMs = 3600000L,
      description = "Custom streamed movie source provided by host.",
      videoUrl = url,
      primaryColorHex = 0xFF7C3AED,
      secondaryColorHex = 0xFF06B6D4,
      tag = "User Stream",
      isCustom = true
    )
    _uiState.update {
      it.copy(
        movies = listOf(newMovie) + it.movies,
        showAddCustomMovieDialog = false
      )
    }
    selectMovie(newMovie)
  }

  private fun addSystemChatNotice(text: String, time: String) {
    val msg = ChatMessage(
      id = UUID.randomUUID().toString(),
      senderName = "Party",
      message = "📢 $text",
      timestamp = time,
      isFromMe = false
    )
    _uiState.update { it.copy(chatMessages = it.chatMessages + msg) }
  }
}
