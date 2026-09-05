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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import com.example.model.Movie
import com.example.ui.theme.CinemaAmber
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaPrimary
import com.example.ui.theme.CinemaSecondary
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceHigh
import com.example.ui.theme.CinemaSurfaceVariant

@Composable
fun MovieCatalogView(
  movies: List<Movie>,
  currentMovieId: String,
  onSelectMovie: (Movie) -> Unit,
  onOpenAddMovieDialog: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CinemaBackground)
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    // Header & Add Button
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Movie Library",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Text(
          text = "Pick a movie to stream together",
          style = MaterialTheme.typography.labelSmall,
          color = Color.White.copy(alpha = 0.6f)
        )
      }

      Button(
        onClick = onOpenAddMovieDialog,
        colors = ButtonDefaults.buttonColors(containerColor = CinemaSurfaceHigh),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("add_custom_movie_btn")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          tint = CinemaSecondary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Add Link", color = Color.White, fontSize = 12.sp)
      }
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(movies, key = { it.id }) { movie ->
        val isCurrent = movie.id == currentMovieId
        MovieCardItem(
          movie = movie,
          isCurrent = isCurrent,
          onClick = { onSelectMovie(movie) }
        )
      }
    }
  }
}

@Composable
fun MovieCardItem(
  movie: Movie,
  isCurrent: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(CinemaSurfaceVariant)
      .border(
        width = if (isCurrent) 2.dp else 1.dp,
        color = if (isCurrent) CinemaSecondary else Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(16.dp)
      )
      .clickable { onClick() }
      .padding(12.dp)
      .testTag("movie_card_${movie.id}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Artistic Poster Block
      Box(
        modifier = Modifier
          .size(width = 80.dp, height = 100.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(
            Brush.linearGradient(
              listOf(Color(movie.primaryColorHex), Color(movie.secondaryColorHex))
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Movie,
          contentDescription = null,
          tint = Color.White.copy(alpha = 0.8f),
          modifier = Modifier.size(36.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      // Movie Details
      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = movie.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          if (isCurrent) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(CinemaSecondary.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Currently Playing",
                tint = CinemaSecondary,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Playing",
                color = CinemaSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            tint = CinemaAmber,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = movie.rating,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "${movie.genre} • ${movie.duration}",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.6f)
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = movie.description,
          fontSize = 11.sp,
          color = Color.White.copy(alpha = 0.7f),
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          lineHeight = 15.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color.White.copy(alpha = 0.1f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = movie.tag,
              fontSize = 9.sp,
              color = CinemaSecondary,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }
  }
}

@Composable
fun AddCustomMovieDialog(
  onDismiss: () -> Unit,
  onAddMovie: (title: String, url: String, genre: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var url by remember { mutableStateOf("") }
  var genre by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .border(1.dp, CinemaSecondary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
        .testTag("add_custom_movie_dialog"),
      color = CinemaSurface
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = "Add Video / Movie Stream",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Text(
          text = "Enter a direct video URL (MP4 / HLS / WebM)",
          style = MaterialTheme.typography.labelSmall,
          color = Color.White.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Movie / Video Title") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CinemaSecondary,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = url,
          onValueChange = { url = it },
          label = { Text("Direct Video URL") },
          placeholder = { Text("https://example.com/stream.mp4") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CinemaSecondary,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = genre,
          onValueChange = { genre = it },
          label = { Text("Genre (e.g. Action, Comedy, Anime)") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CinemaSecondary,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
          ) {
            Text("Cancel", color = Color.White.copy(alpha = 0.7f))
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = {
              if (title.isNotBlank() && url.isNotBlank()) {
                onAddMovie(title.trim(), url.trim(), genre.trim())
              }
            },
            enabled = title.isNotBlank() && url.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = CinemaPrimary),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Add to Party", color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
