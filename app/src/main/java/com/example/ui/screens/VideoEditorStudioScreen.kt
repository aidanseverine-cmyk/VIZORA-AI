package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.CropRotate
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.VizoraCinemaPlayer
import com.example.ui.theme.VizoraAmber
import com.example.ui.theme.VizoraCyan
import com.example.ui.theme.VizoraEmerald
import com.example.ui.theme.VizoraMagenta
import com.example.ui.theme.VizoraObsidian
import com.example.ui.theme.VizoraViolet
import com.example.viewmodel.VizoraUiState
import com.example.viewmodel.VizoraViewModel

@Composable
fun VideoEditorStudioScreen(
    uiState: VizoraUiState,
    viewModel: VizoraViewModel,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        "Timeline & Cut" to Icons.Default.ViewTimeline,
        "AI Voiceover" to Icons.Default.RecordVoiceOver,
        "AI Music & SFX" to Icons.Default.MusicNote,
        "Subtitles" to Icons.Default.Subtitles,
        "Filters & Color" to Icons.Default.ColorLens
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("video_editor_studio_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Live Cinema Monitor with burned-in captions, LUT filters, rotation & speed
        item {
            VizoraCinemaPlayer(
                prompt = uiState.promptText,
                style = uiState.selectedStyle.label,
                cameraMovement = uiState.selectedCamera.label,
                lighting = uiState.selectedLighting.label,
                aspectRatioStr = uiState.selectedAspectRatio.ratio,
                quality = uiState.selectedQuality.label,
                durationSec = uiState.selectedDurationSec,
                captions = uiState.captions,
                showCaptions = uiState.burnInCaptions,
                wordByWordHighlight = uiState.wordByWordHighlight,
                captionFontSizeSp = uiState.captionFontSizeSp,
                textOverlay = uiState.textOverlay,
                activeSticker = uiState.activeSticker,
                activeFilter = uiState.activeFilter,
                rotationDegrees = uiState.rotationDegrees,
                playbackSpeed = uiState.playbackSpeed,
                previewSeed = uiState.activeProject?.previewSeed ?: 101
            )
        }

        // Multi-Track Timeline Visualizer (Video Track + Voiceover Track + Music/SFX Track + Captions Track)
        item {
            MultiTrackTimelineCard(uiState = uiState)
        }

        // Editor Mode Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = uiState.activeEditorTab,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clip(RoundedCornerShape(14.dp))
            ) {
                tabs.forEachIndexed { index, (title, icon) ->
                    Tab(
                        selected = uiState.activeEditorTab == index,
                        onClick = { viewModel.selectEditorTab(index) },
                        text = { Text(title, style = MaterialTheme.typography.labelLarge) },
                        icon = { Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }
        }

        // Tab Specific Controls
        item {
            when (uiState.activeEditorTab) {
                0 -> TimelineTrimSpeedPanel(uiState = uiState, viewModel = viewModel)
                1 -> AiVoiceoverGeneratorPanel(uiState = uiState, viewModel = viewModel)
                2 -> AiMusicAndSfxPanel(uiState = uiState, viewModel = viewModel)
                3 -> AutomaticSubtitlesPanel(uiState = uiState, viewModel = viewModel)
                4 -> FiltersAndColorGradingPanel(uiState = uiState, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun MultiTrackTimelineCard(uiState: VizoraUiState) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "NON-LINEAR MULTI-TRACK TIMELINE",
                    style = MaterialTheme.typography.labelSmall,
                    color = VizoraCyan
                )
                Text(
                    text = "In: ${"%.1f".format(uiState.trimStartSec)}s • Out: ${"%.1f".format(uiState.trimEndSec)}s • ${uiState.playbackSpeed}x",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(VizoraObsidian)
            ) {
                val w = size.width
                val h = size.height
                val rowH = h / 3f

                // Track 1: Video Clips
                drawRoundRect(
                    color = VizoraCyan.copy(alpha = 0.35f),
                    topLeft = Offset(w * 0.04f, 4f),
                    size = Size(w * 0.88f, rowH - 6f),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                // Track 2: Voiceover + Captions
                drawRoundRect(
                    color = VizoraViolet.copy(alpha = 0.40f),
                    topLeft = Offset(w * 0.08f, rowH + 3f),
                    size = Size(w * 0.78f, rowH - 6f),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                // Track 3: Afrobeat Music Waveform + SFX
                val barCount = 28
                val barW = (w * 0.86f) / barCount
                for (i in 0 until barCount) {
                    val amp = ((kotlin.math.sin(i * 0.7f) * 0.4f + 0.5f) * (rowH - 6f)).coerceAtLeast(4f)
                    drawRoundRect(
                        color = VizoraEmerald.copy(alpha = 0.75f),
                        topLeft = Offset(w * 0.05f + i * barW, rowH * 2f + (rowH - amp) / 2f),
                        size = Size(barW * 0.65f, amp),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                }

                // Playhead needle
                drawLine(
                    color = VizoraAmber,
                    start = Offset(w * 0.42f, 0f),
                    end = Offset(w * 0.42f, h),
                    strokeWidth = 3f
                )
            }
        }
    }
}

@Composable
private fun TimelineTrimSpeedPanel(
    uiState: VizoraUiState,
    viewModel: VizoraViewModel
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "6. AI Video Editor • Timeline, Trim, Split, Merge & Speed",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Quick Cut / Split / Merge / Rotate actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { viewModel.splitClipAtPlayhead() }) {
                    Icon(imageVector = Icons.Default.CallSplit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Split Clip")
                }
                OutlinedButton(onClick = { viewModel.mergeStoryScenesIntoTimeline() }) {
                    Icon(imageVector = Icons.Default.MergeType, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Merge Scenes")
                }
                OutlinedButton(
                    onClick = {
                        viewModel.updateEditorTimeline(rotation = (uiState.rotationDegrees + 90) % 360)
                    }
                ) {
                    Icon(imageVector = Icons.Default.CropRotate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rotate (${uiState.rotationDegrees}°)")
                }
                Button(onClick = { viewModel.exportAndDownloadVideo() }) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export Video")
                }
            }

            // Trim In / Out Sliders
            Text(
                text = "Trim Start: ${"%.1f".format(uiState.trimStartSec)}s — Trim End: ${"%.1f".format(uiState.trimEndSec)}s",
                style = MaterialTheme.typography.labelSmall
            )
            Slider(
                value = uiState.trimEndSec,
                onValueChange = { viewModel.updateEditorTimeline(trimEnd = it) },
                valueRange = 2f..uiState.selectedDurationSec.toFloat().coerceAtLeast(5f)
            )

            // Speed Control & Slow Motion
            Text(
                text = "Speed Control & Slow Motion (${uiState.playbackSpeed}x)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0.5f to "0.5x Slow-Mo", 1.0f to "1.0x Normal", 1.5f to "1.5x Fast", 2.0f to "2.0x Hyper").forEach { (spd, label) ->
                    FilterChip(
                        selected = uiState.playbackSpeed == spd,
                        onClick = { viewModel.updateEditorTimeline(speed = spd) },
                        label = { Text(label) }
                    )
                }
            }

            // Text Overlay & Stickers
            OutlinedTextField(
                value = uiState.textOverlay,
                onValueChange = { viewModel.updateEditorTimeline(overlayText = it) },
                label = { Text("Text Overlay Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Motion Stickers", style = MaterialTheme.typography.labelSmall)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("🎬 4K ANAMORPHIC", "🔥 TRENDING AI", "🌍 DAR ES SALAAM", "✨ VIZORA STUDIO", "").forEach { stk ->
                    FilterChip(
                        selected = uiState.activeSticker == stk,
                        onClick = { viewModel.updateEditorTimeline(sticker = stk) },
                        label = { Text(stk.ifBlank { "No Sticker" }) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AiVoiceoverGeneratorPanel(
    uiState: VizoraUiState,
    viewModel: VizoraViewModel
) {
    val supportedLanguages = listOf(
        "English", "Swahili", "French", "Spanish", "Portuguese", "Arabic", "Chinese", "Japanese"
    )
    val emotions = listOf("Cinematic & Warm", "Excited & Energetic", "Calm Documentary", "Dramatic Trailer", "Inspirational")
    val accents = listOf("East African English", "Neutral Studio", "British RP", "West African", "American")

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "7. AI Voiceover Studio • Multilingual Neural TTS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Gender Selection
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Female", "Male").forEach { gender ->
                    FilterChip(
                        selected = uiState.voiceGender == gender,
                        onClick = { viewModel.updateVoiceoverSettings(gender = gender) },
                        label = { Text("$gender Voice") }
                    )
                }
            }

            // 8 Supported Languages
            Text("Language (8 Languages Supported)", style = MaterialTheme.typography.labelSmall, color = VizoraCyan)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                supportedLanguages.forEach { lang ->
                    FilterChip(
                        selected = uiState.voiceLanguage == lang,
                        onClick = { viewModel.updateVoiceoverSettings(language = lang) },
                        label = { Text(lang) }
                    )
                }
            }

            // Accents & Emotions
            Text("Voice Accent & Emotion", style = MaterialTheme.typography.labelSmall)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                accents.forEach { acc ->
                    FilterChip(
                        selected = uiState.voiceAccent == acc,
                        onClick = { viewModel.updateVoiceoverSettings(accent = acc) },
                        label = { Text(acc) }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                emotions.forEach { emo ->
                    FilterChip(
                        selected = uiState.voiceEmotion == emo,
                        onClick = { viewModel.updateVoiceoverSettings(emotion = emo) },
                        label = { Text(emo) }
                    )
                }
            }

            // Speaking Speed & Voice Pitch
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Speaking Speed: ${"%.1f".format(uiState.voiceSpeed)}x", style = MaterialTheme.typography.labelSmall)
                    Slider(
                        value = uiState.voiceSpeed,
                        onValueChange = { viewModel.updateVoiceoverSettings(speed = it) },
                        valueRange = 0.6f..1.6f
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Voice Pitch: ${"%.1f".format(uiState.voicePitch)}", style = MaterialTheme.typography.labelSmall)
                    Slider(
                        value = uiState.voicePitch,
                        onValueChange = { viewModel.updateVoiceoverSettings(pitch = it) },
                        valueRange = 0.6f..1.5f
                    )
                }
            }

            OutlinedTextField(
                value = uiState.voiceScript,
                onValueChange = { viewModel.updateVoiceoverSettings(script = it) },
                label = { Text("Voiceover Narration Script") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = uiState.voiceStatusMessage,
                style = MaterialTheme.typography.labelSmall,
                color = VizoraEmerald
            )

            Button(
                onClick = { viewModel.generateAndPlayVoiceover() },
                colors = ButtonDefaults.buttonColors(containerColor = VizoraCyan, contentColor = VizoraObsidian),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("generate_voiceover_button")
            ) {
                Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Generate & Speak AI Voiceover")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AiMusicAndSfxPanel(
    uiState: VizoraUiState,
    viewModel: VizoraViewModel
) {
    val allSfx = listOf(
        "Thunder", "Rain", "Explosion", "Crowd", "Ocean", "Birds", "Traffic", "Footsteps", "Door opening"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "8. AI Music Generator & 9. AI Sound Effects",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = uiState.musicPrompt,
                onValueChange = { viewModel.updateMusicSettings(prompt = it) },
                label = { Text("Describe Background Music") },
                placeholder = { Text("Create an emotional Afrobeat instrumental for a romantic video.") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Music Volume: ${(uiState.musicVolume * 100).toInt()}% • Tempo: ${uiState.musicBpm} BPM",
                style = MaterialTheme.typography.labelSmall
            )
            Slider(
                value = uiState.musicVolume,
                onValueChange = { viewModel.updateMusicSettings(volume = it) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Fade In", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = uiState.musicFadeIn,
                        onCheckedChange = { viewModel.updateMusicSettings(fadeIn = it) }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Fade Out", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = uiState.musicFadeOut,
                        onCheckedChange = { viewModel.updateMusicSettings(fadeOut = it) }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Loop", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = uiState.musicLoop,
                        onCheckedChange = { viewModel.updateMusicSettings(loop = it) }
                    )
                }
            }

            Button(
                onClick = { viewModel.generateAndPlayMusic() },
                colors = ButtonDefaults.buttonColors(containerColor = VizoraEmerald, contentColor = VizoraObsidian),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("generate_music_button")
            ) {
                Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Generate & Audition AI Music Track")
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "9. AI Sound Effects (Tap any SFX to synthesize & layer)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allSfx.forEach { sfx ->
                    val selected = uiState.activeSfxList.contains(sfx)
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.toggleAndPlaySfx(sfx) },
                        label = { Text("🔊 $sfx") }
                    )
                }
            }
        }
    }
}

@Composable
private fun AutomaticSubtitlesPanel(
    uiState: VizoraUiState,
    viewModel: VizoraViewModel
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "10. Automatic Speech-to-Text Subtitles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(onClick = { viewModel.regenerateSubtitles() }) {
                    Text("Auto-Generate")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Word-by-Word Highlight", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = uiState.wordByWordHighlight,
                        onCheckedChange = { viewModel.updateCaptionStyle(wordHighlight = it) }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Burn Into Export", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = uiState.burnInCaptions,
                        onCheckedChange = { viewModel.updateCaptionStyle(burnIn = it) }
                    )
                }
            }

            Text("Font Size: ${uiState.captionFontSizeSp}sp", style = MaterialTheme.typography.labelSmall)
            Slider(
                value = uiState.captionFontSizeSp.toFloat(),
                onValueChange = { viewModel.updateCaptionStyle(fontSize = it.toInt()) },
                valueRange = 12f..24f
            )

            uiState.captions.forEach { seg ->
                OutlinedTextField(
                    value = seg.text,
                    onValueChange = { viewModel.updateCaptionSegmentText(seg.id, it) },
                    label = { Text("00:${seg.startSec.toInt().toString().padStart(2, '0')} - 00:${seg.endSec.toInt().toString().padStart(2, '0')}") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun FiltersAndColorGradingPanel(
    uiState: VizoraUiState,
    viewModel: VizoraViewModel
) {
    val filters = listOf("Anamorphic Gold", "Afro Golden", "Cyber Neon", "Kodak 2383", "Noir Contrast")

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Cinema LUT Filters & Color Grading",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filterName ->
                    FilterChip(
                        selected = uiState.activeFilter == filterName,
                        onClick = { viewModel.updateEditorTimeline(filter = filterName) },
                        label = { Text(filterName) }
                    )
                }
            }

            Text("Brightness (${"%.2f".format(uiState.brightnessOffset)})", style = MaterialTheme.typography.labelSmall)
            Slider(
                value = uiState.brightnessOffset,
                onValueChange = { viewModel.updateEditorTimeline(brightness = it) },
                valueRange = -0.3f..0.3f
            )

            Text("Contrast (${"%.2f".format(uiState.contrastScale)})", style = MaterialTheme.typography.labelSmall)
            Slider(
                value = uiState.contrastScale,
                onValueChange = { viewModel.updateEditorTimeline(contrast = it) },
                valueRange = 0.7f..1.5f
            )

            Text("Master Audio Volume (${(uiState.masterVolume * 100).toInt()}%)", style = MaterialTheme.typography.labelSmall)
            Slider(
                value = uiState.masterVolume,
                onValueChange = { viewModel.updateEditorTimeline(volume = it) },
                valueRange = 0f..1f
            )
        }
    }
}
