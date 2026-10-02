package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CaptionSegment
import com.example.ui.theme.VizoraAmber
import com.example.ui.theme.VizoraCyan
import com.example.ui.theme.VizoraMagenta
import com.example.ui.theme.VizoraObsidian
import com.example.ui.theme.VizoraViolet
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VizoraCinemaPlayer(
    prompt: String,
    style: String,
    cameraMovement: String,
    lighting: String,
    aspectRatioStr: String,
    quality: String,
    durationSec: Int,
    captions: List<CaptionSegment>,
    showCaptions: Boolean = true,
    wordByWordHighlight: Boolean = true,
    captionFontSizeSp: Int = 15,
    textOverlay: String = "",
    activeSticker: String = "",
    activeFilter: String = "Anamorphic Gold",
    rotationDegrees: Int = 0,
    playbackSpeed: Float = 1.0f,
    characterLockName: String = "",
    previewSeed: Int = 101,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(true) }

    val infiniteTransition = rememberInfiniteTransition(label = "cinema_motion")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = ((durationSec * 1000) / playbackSpeed.coerceAtLeast(0.25f)).toInt().coerceIn(3000, 14000),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "playhead"
    )

    val effectiveProgress = if (isPlaying) progress else 0.42f
    val currentSecond = effectiveProgress * durationSec

    // Choose background base plate based on prompt/style
    val drawableRes = remember(prompt, style, previewSeed) {
        val lower = (prompt + " " + style).lowercase()
        when {
            lower.contains("cyberpunk") || lower.contains("neon") || lower.contains("sci-fi") || previewSeed % 3 == 0 ->
                R.drawable.img_sample_cyberpunk_1790944386307
            lower.contains("pilot") || lower.contains("boy") || lower.contains("kilimanjaro") || lower.contains("story") || previewSeed % 3 == 2 ->
                R.drawable.img_sample_pilot_story_1790944396942
            lower.contains("dar es salaam") || lower.contains("african") || lower.contains("sunset") ->
                R.drawable.img_sample_afro_cinema_1790944375799
            else -> R.drawable.img_hero_banner_1790944365201
        }
    }

    // Compute camera movement transforms
    val camLower = cameraMovement.lowercase()
    val scaleFactor = when {
        camLower.contains("zoom in") || camLower.contains("dolly forward") -> 1.04f + effectiveProgress * 0.22f
        camLower.contains("zoom out") || camLower.contains("dolly backward") -> 1.26f - effectiveProgress * 0.20f
        camLower.contains("drone") || camLower.contains("crane") -> 1.12f + sin(effectiveProgress * 3.14f) * 0.12f
        else -> 1.10f
    }
    val translationX = when {
        camLower.contains("pan left") -> -36f * effectiveProgress
        camLower.contains("pan right") || camLower.contains("tracking") -> 36f * effectiveProgress - 18f
        camLower.contains("orbit") -> cos(effectiveProgress * 6.28f) * 24f
        camLower.contains("handheld") -> sin(effectiveProgress * 25f) * 6f
        else -> 0f
    }
    val translationY = when {
        camLower.contains("crane") || camLower.contains("drone") -> -22f * effectiveProgress + 10f
        camLower.contains("handheld") -> cos(effectiveProgress * 31f) * 5f
        else -> 0f
    }

    // Color LUT Filter Matrix
    val colorFilter = remember(activeFilter) {
        val matrix = ColorMatrix()
        when (activeFilter) {
            "Cyber Neon" -> matrix.setToScale(0.95f, 1.08f, 1.32f, 1f)
            "Kodak 2383" -> matrix.setToScale(1.18f, 1.05f, 0.90f, 1f)
            "Noir Contrast" -> matrix.setToSaturation(0.15f)
            "Afro Golden" -> matrix.setToScale(1.22f, 1.06f, 0.85f, 1f)
            else -> matrix.setToScale(1.10f, 1.04f, 0.96f, 1f)
        }
        ColorFilter.colorMatrix(matrix)
    }

    val targetAspect = when (aspectRatioStr) {
        "9:16" -> 16f / 11f // Compact container that frames vertical 9:16 with safe-zone sidebars
        "1:1" -> 1.35f
        "21:9" -> 2.1f
        else -> 16f / 9f
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(targetAspect)
            .clip(RoundedCornerShape(20.dp))
            .background(VizoraObsidian)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(VizoraCyan.copy(alpha = 0.6f), VizoraViolet.copy(alpha = 0.6f))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { isPlaying = !isPlaying }
            .testTag("cinema_video_player")
    ) {
        // Base animated cinema plate
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = "Generated AI Video Preview",
            contentScale = ContentScale.Crop,
            colorFilter = colorFilter,
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotationDegrees.toFloat())
                .graphicsLayer(
                    scaleX = scaleFactor,
                    scaleY = scaleFactor,
                    translationX = translationX,
                    translationY = translationY
                )
        )

        // Procedural Lighting & Atmospheric Particle Canvas Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Lighting atmosphere overlay
            val lightColor = when (lighting.lowercase()) {
                "sunset", "golden hour" -> Color(0x55F59E0B)
                "neon" -> Color(0x4400F0FF)
                "dramatic", "low light" -> Color(0x66070A12)
                "sunrise" -> Color(0x44EC4899)
                else -> Color(0x2200F0FF)
            }
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(lightColor, Color.Transparent),
                    center = Offset(
                        x = w * (0.25f + 0.5f * effectiveProgress),
                        y = h * 0.3f
                    ),
                    radius = w * 0.75f
                )
            )

            // Anamorphic horizontal lens streak
            val flareY = h * (0.38f + 0.06f * sin(effectiveProgress * 6.28f))
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        VizoraCyan.copy(alpha = 0.45f),
                        Color.White.copy(alpha = 0.65f),
                        VizoraViolet.copy(alpha = 0.45f),
                        Color.Transparent
                    )
                ),
                start = Offset(w * 0.08f, flareY),
                end = Offset(w * 0.92f, flareY),
                strokeWidth = 3.5f
            )

            // Floating volumetric dust motes
            for (i in 0 until 12) {
                val px = ((w * ((i * 0.083f + effectiveProgress * 0.25f) % 1f)))
                val py = ((h * ((i * 0.13f + sin(effectiveProgress * 4f + i) * 0.08f).coerceIn(0.1f, 0.9f))))
                drawCircle(
                    color = Color.White.copy(alpha = 0.35f),
                    radius = (2f + (i % 3) * 1.5f),
                    center = Offset(px, py)
                )
            }

            // Bottom gradient vignette for captions & HUD
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0xCC070A12)),
                    startY = h * 0.45f,
                    endY = h
                )
            )
        }

        // Top HUD Bar: Aspect Ratio, Quality, Camera Movement & Character Lock
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = VizoraObsidian.copy(alpha = 0.78f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HighQuality,
                            contentDescription = null,
                            tint = VizoraCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$quality • $aspectRatioStr",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    color = VizoraViolet.copy(alpha = 0.82f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$style • $cameraMovement",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (characterLockName.isNotBlank()) {
                Surface(
                    color = VizoraObsidian.copy(alpha = 0.82f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Character Locked",
                            tint = VizoraAmber,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = characterLockName.take(14),
                            style = MaterialTheme.typography.labelSmall,
                            color = VizoraAmber
                        )
                    }
                }
            }
        }

        // Optional Text Overlay & Sticker in center-top
        if (textOverlay.isNotBlank() || activeSticker.isNotBlank()) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 46.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (activeSticker.isNotBlank()) {
                    Surface(
                        color = VizoraMagenta.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = activeSticker,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (textOverlay.isNotBlank()) {
                    Text(
                        text = textOverlay,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Dynamic Word-by-Word Highlighted Subtitles Overlay
        val activeCaption = captions.firstOrNull {
            currentSecond >= it.startSec && currentSecond <= it.endSec
        } ?: captions.firstOrNull()

        if (showCaptions && activeCaption != null) {
            Surface(
                color = VizoraObsidian.copy(alpha = 0.82f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 44.dp, start = 16.dp, end = 16.dp)
            ) {
                val words = activeCaption.text.split(" ")
                val highlightedIdx = ((effectiveProgress * words.size * 2).toInt()) % words.size.coerceAtLeast(1)

                Text(
                    text = buildAnnotatedString {
                        words.forEachIndexed { index, word ->
                            val isHighlighted = wordByWordHighlight && (index == highlightedIdx || word == activeCaption.highlightWord)
                            withStyle(
                                style = SpanStyle(
                                    color = if (isHighlighted) VizoraCyan else Color.White,
                                    fontWeight = if (isHighlighted) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            ) {
                                append("$word ")
                            }
                        }
                    },
                    fontSize = captionFontSizeSp.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Bottom Playback Controls & Scrubber Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Animated Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(effectiveProgress.coerceIn(0.05f, 1f))
                        .height(4.dp)
                        .background(
                            Brush.horizontalGradient(listOf(VizoraCyan, VizoraViolet, VizoraMagenta))
                        )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause preview" else "Play preview",
                        tint = VizoraCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "00:${currentSecond.toInt().toString().padStart(2, '0')} / 00:${durationSec.toString().padStart(2, '0')} • ${playbackSpeed}x",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ClosedCaption,
                        contentDescription = "Subtitles active",
                        tint = if (showCaptions) VizoraCyan else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Spatial Audio",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
