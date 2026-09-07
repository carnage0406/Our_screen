package com.example.model

data class Movie(
  val id: String,
  val title: String,
  val genre: String,
  val duration: String,
  val durationMs: Long,
  val description: String,
  val videoUrl: String,
  val primaryColorHex: Long = 0xFF6366F1,
  val secondaryColorHex: Long = 0xFF0284C7,
  val rating: String = "8.9",
  val year: String = "2025",
  val tag: String = "4K HDR • 5.1 Audio",
  val isCustom: Boolean = false
)

data class ScreenShareInfo(
  val isSharing: Boolean = false,
  val sourceTitle: String = "Movie Tab (Chrome)",
  val resolution: String = "1080p 60fps",
  val bitrate: String = "6.5 Mbps",
  val latencyMs: Int = 18,
  val sharedAudio: Boolean = true,
  val presenterName: String = "You"
)

data class Participant(
  val id: String,
  val name: String,
  val isMe: Boolean = false,
  val isConnected: Boolean = true,
  val isCameraOn: Boolean = true,
  val isMicMuted: Boolean = false,
  val isScreenSharing: Boolean = false,
  val isFrontCamera: Boolean = true,
  val pingMs: Int = 24
)

data class FriendParticipant(
  val name: String = "Jordan",
  val isConnected: Boolean = false,
  val isCameraOn: Boolean = true,
  val isMicMuted: Boolean = false,
  val isSpeaking: Boolean = false,
  val reactionEmoji: String? = null,
  val pingMs: Int = 24,
  val currentMood: String = "Watching! 🍿"
)

data class ChatMessage(
  val id: String,
  val senderName: String,
  val message: String,
  val timestamp: String,
  val isFromMe: Boolean,
  val attachedEmoji: String? = null
)

data class FloatingReaction(
  val id: String,
  val emoji: String,
  val senderName: String,
  val xOffsetFraction: Float,
  val createdAt: Long = System.currentTimeMillis()
)

enum class VideoViewMode {
  THEATER,      // Main video dominant with floating camera PiPs
  SPLIT_SCREEN, // Video on top half, side-by-side user & friend cams on bottom
  FULLSCREEN    // Full video screen with transparent overlay controls
}

enum class PartyTab {
  WATCH,
  MOVIES,
  SCREEN_SHARE,
  CHAT
}
