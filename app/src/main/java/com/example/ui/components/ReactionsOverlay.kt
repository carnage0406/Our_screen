package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FloatingReaction

@Composable
fun ReactionsOverlay(
  reactions: List<FloatingReaction>,
  onReactionFinished: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier.fillMaxSize()) {
    reactions.forEach { reaction ->
      FloatingReactionItem(
        reaction = reaction,
        onFinished = { onReactionFinished(reaction.id) }
      )
    }
  }
}

@Composable
private fun FloatingReactionItem(
  reaction: FloatingReaction,
  onFinished: () -> Unit
) {
  val yAnim = remember { Animatable(0f) }
  val alphaAnim = remember { Animatable(1f) }

  LaunchedEffect(reaction.id) {
    // Float upwards by 250dp while fading out
    yAnim.animateTo(
      targetValue = -320f,
      animationSpec = tween(durationMillis = 2400)
    )
    alphaAnim.animateTo(
      targetValue = 0f,
      animationSpec = tween(durationMillis = 600)
    )
    onFinished()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .offset {
        IntOffset(
          x = (reaction.xOffsetFraction * 700).toInt(),
          y = (600 + yAnim.value).toInt()
        )
      }
      .alpha(alphaAnim.value)
  ) {
    Text(
      text = reaction.emoji,
      fontSize = 32.sp
    )
  }
}
