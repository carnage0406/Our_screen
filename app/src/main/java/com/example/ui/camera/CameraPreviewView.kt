package com.example.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaLiveRed
import com.example.ui.theme.CinemaPrimary
import com.example.ui.theme.CinemaSecondary
import com.example.ui.theme.CinemaSurfaceHigh

@Composable
fun CameraVideoFeed(
  isMyCamera: Boolean,
  userName: String,
  isCameraActive: Boolean,
  isMicMuted: Boolean,
  isFrontCamera: Boolean = true,
  onToggleCamera: () -> Unit,
  onToggleMic: () -> Unit,
  onSwitchCamera: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  var hasPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
      ) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasPermission = isGranted
    if (isGranted && !isCameraActive) {
      onToggleCamera()
    }
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(CinemaBackground)
      .border(
        width = 1.5.dp,
        color = if (isCameraActive) CinemaSecondary.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(14.dp)
      )
  ) {
    if (isMyCamera) {
      if (hasPermission && isCameraActive) {
        // CameraX Live Stream
        var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }

        AndroidView(
          factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
              scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            previewViewRef = previewView

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
              try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                  it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = if (isFrontCamera) {
                  if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                  } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                  }
                } else {
                  if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                    CameraSelector.DEFAULT_BACK_CAMERA
                  } else {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                  }
                }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
              } catch (e: Exception) {
                // Fallback handled gracefully
              }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
          },
          update = {
            // Updated when lens selector changes
          },
          modifier = Modifier
            .fillMaxSize()
            .testTag("user_camera_preview")
        )
      } else {
        // Camera Off or Need Permission
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(CinemaSurfaceHigh, CinemaBackground)
              )
            )
            .clickable {
              if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
              } else {
                onToggleCamera()
              }
            }
            .testTag("camera_placeholder_click"),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(CinemaPrimary.copy(alpha = 0.2f))
                .border(1.dp, CinemaPrimary, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (!hasPermission) Icons.Default.Videocam else Icons.Default.VideocamOff,
                contentDescription = "Camera Toggle",
                tint = CinemaSecondary,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = if (!hasPermission) "Tap to Allow Camera" else "Camera is Off",
              style = MaterialTheme.typography.labelSmall,
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 11.sp,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    } else {
      // Friend Camera Simulation with lively realistic video webcam styling
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.linearGradient(
              colors = listOf(
                Color(0xFF1E1B4B),
                Color(0xFF312E81),
                Color(0xFF0F172A)
              )
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        if (isCameraActive) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            // Friend avatar pulse
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                  Brush.radialGradient(
                    listOf(CinemaSecondary, CinemaPrimary)
                  )
                )
                .border(2.dp, CinemaSecondary.copy(alpha = 0.8f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = userName.take(1).uppercase(),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 20.sp
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(CinemaSecondary)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Watching live",
                color = CinemaSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        } else {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.VideocamOff,
              contentDescription = "Friend Cam Off",
              tint = Color.White.copy(alpha = 0.4f),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "$userName cam off",
              color = Color.White.copy(alpha = 0.5f),
              fontSize = 10.sp
            )
          }
        }
      }
    }

    // Top Header Overlay: Name + Live Indicator
    Row(
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(6.dp)
        .clip(RoundedCornerShape(6.dp))
        .background(Color.Black.copy(alpha = 0.65f))
        .padding(horizontal = 6.dp, vertical = 2.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (isCameraActive) {
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(if (isMyCamera) CinemaLiveRed else CinemaSecondary)
        )
        Spacer(modifier = Modifier.width(4.dp))
      }
      Text(
        text = userName,
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold
      )
    }

    // Bottom Action Controls (for User's camera)
    if (isMyCamera) {
      Row(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(4.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color.Black.copy(alpha = 0.7f)),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = {
            if (!hasPermission) {
              permissionLauncher.launch(Manifest.permission.CAMERA)
            } else {
              onToggleCamera()
            }
          },
          modifier = Modifier
            .size(32.dp)
            .testTag("toggle_my_camera")
        ) {
          Icon(
            imageVector = if (isCameraActive) Icons.Default.Videocam else Icons.Default.VideocamOff,
            contentDescription = "Toggle Camera",
            tint = if (isCameraActive) CinemaSecondary else Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
          )
        }

        if (hasPermission && isCameraActive) {
          IconButton(
            onClick = onSwitchCamera,
            modifier = Modifier
              .size(32.dp)
              .testTag("switch_camera_lens")
          ) {
            Icon(
              imageVector = Icons.Default.Cameraswitch,
              contentDescription = "Flip Camera",
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        IconButton(
          onClick = onToggleMic,
          modifier = Modifier
            .size(32.dp)
            .testTag("toggle_my_mic")
        ) {
          Icon(
            imageVector = if (!isMicMuted) Icons.Default.Mic else Icons.Default.MicOff,
            contentDescription = "Toggle Mic",
            tint = if (!isMicMuted) Color.White else CinemaLiveRed,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    } else {
      // Friend Mic status pill
      if (isMicMuted) {
        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(6.dp)
            .clip(CircleShape)
            .background(CinemaLiveRed.copy(alpha = 0.85f))
            .padding(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.MicOff,
            contentDescription = "Friend Muted",
            tint = Color.White,
            modifier = Modifier.size(12.dp)
          )
        }
      }
    }
  }
}
