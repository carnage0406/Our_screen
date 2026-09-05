package com.example.data

import com.example.model.Movie

object SampleMovies {
  val movieList = listOf(
    Movie(
      id = "movie_sci_fi",
      title = "Cosmos: Voyage to Andromeda",
      genre = "Sci-Fi / Space Odyssey",
      duration = "1h 48m",
      durationMs = 596000L, // 596s for standard video loop / testing
      description = "Deep space explorers journey past the gravitational horizon to find a habitable exoplanet before Earth's solar storm arrives.",
      videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
      primaryColorHex = 0xFF4338CA,
      secondaryColorHex = 0xFF06B6D4,
      rating = "9.1",
      year = "2025",
      tag = "4K Dolby Atmos"
    ),
    Movie(
      id = "movie_cyberpunk",
      title = "Neon District: City 2099",
      genre = "Action / Cyberpunk Thriller",
      duration = "2h 05m",
      durationMs = 735000L,
      description = "In a neon-drenched megacity, two rogue netrunners unite to expose a rogue synthetic intelligence hijacking the city grid.",
      videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
      primaryColorHex = 0xFF831843,
      secondaryColorHex = 0xFF0284C7,
      rating = "8.8",
      year = "2024",
      tag = "HDR10+ • 60fps"
    ),
    Movie(
      id = "movie_fantasy",
      title = "The Ancient Guardian Realm",
      genre = "Fantasy / Mythic Adventure",
      duration = "1h 52m",
      durationMs = 650000L,
      description = "When the celestial crystal awakens, two estranged guardians must traverse enchanted valleys to seal the abyss forever.",
      videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
      primaryColorHex = 0xFF14532D,
      secondaryColorHex = 0xFFCA8A04,
      rating = "9.3",
      year = "2025",
      tag = "IMAX Enhanced"
    ),
    Movie(
      id = "movie_nature",
      title = "Wild Horizons: Frozen Frontier",
      genre = "Nature Documentary / Cinema",
      duration = "1h 35m",
      durationMs = 520000L,
      description = "Breathtaking cinematography across the Arctic circle capturing auroras, polar wildlife migrations, and majestic glacial fjords.",
      videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
      primaryColorHex = 0xFF0E7490,
      secondaryColorHex = 0xFF3B82F6,
      rating = "9.5",
      year = "2024",
      tag = "8K Ultra Remaster"
    ),
    Movie(
      id = "movie_screen_share",
      title = "Alex's Live Screen Share Stream",
      genre = "Live Screen Broadcast",
      duration = "Live",
      durationMs = 3600000L,
      description = "Broadcasting your phone/computer screen, Netflix/YouTube tab, or local media directly to Jordan with real-time audio pass-through.",
      videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
      primaryColorHex = 0xFFBE123C,
      secondaryColorHex = 0xFF6366F1,
      rating = "LIVE",
      year = "NOW",
      tag = "Real-time • 1080p 60fps"
    )
  )
}
