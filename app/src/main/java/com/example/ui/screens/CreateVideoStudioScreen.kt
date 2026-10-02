package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AspectRatioOption
import com.example.data.CameraMovement
import com.example.data.CharacterLockEntity
import com.example.data.EnhancedPromptBreakdown
import com.example.data.GenerationMode
import com.example.data.LightingMode
import com.example.data.SocialPreset
import com.example.data.VideoQuality
import com.example.data.VideoStyle
import com.example.ui.components.VizoraCinemaPlayer
import com.example.ui.theme.VizoraAmber
import com.example.ui.theme.VizoraCrimson
import com.example.ui.theme.VizoraCyan
import com.example.ui.theme.VizoraEmerald
import com.example.ui.theme.VizoraMagenta
import com.example.ui.theme.VizoraObsidian
import com.example.ui.theme.VizoraViolet
import com.example.viewmodel.VizoraUiState
import com.example.viewmodel.VizoraViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateVideoStudioScreen(
    uiState: VizoraUiState,
    characterLocks: List<CharacterLockEntity>,
    viewModel: VizoraViewModel,
    onOpenEditor: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAdvancedBottomSheet by remember { mutableStateOf(false) }
    var showCharacterLockSheet by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateImageToVideoSettings(
                uri = uri.toString(),
                label = "Custom Reference Frame (${uri.lastPathSegment ?: "Image"})"
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("create_video_studio_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. GENERATION MODE SELECTOR (Text-to-Video, Image-to-Video, Video-to-Video, Story-to-Video)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GenerationMode.entries.forEach { mode ->
                    val selected = uiState.generationMode == mode
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.selectGenerationMode(mode) },
                        label = {
                            Text(
                                text = mode.label,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VizoraCyan.copy(alpha = 0.22f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = if (selected) VizoraCyan else MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }

        // 28. MOBILE-FIRST PROMPT BOX + [Enhance Prompt] + [Generate Video]
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, VizoraCyan.copy(alpha = 0.4f)),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = VizoraCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Describe your video...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = VizoraEmerald.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "100% Free Unlimited",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VizoraEmerald,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    viewModel.savePromptToLibrary(
                                        title = uiState.promptText.take(26),
                                        promptText = uiState.promptText,
                                        category = uiState.selectedStyle.label
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkAdd,
                                    contentDescription = "Save Prompt to Library",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.promptText,
                        onValueChange = { viewModel.updatePromptText(it) },
                        placeholder = {
                            Text("Create a cinematic 10-second video of a young African man walking through Dar es Salaam at sunset...")
                        },
                        minLines = 3,
                        maxLines = 6,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("main_prompt_input")
                    )

                    // Optional Image-to-Video / Reference Image Controls (Requirement 5)
                    AnimatedVisibility(visible = uiState.generationMode == GenerationMode.IMAGE_TO_VIDEO || uiState.generationMode == GenerationMode.VIDEO_TO_VIDEO) {
                        ImageToVideoControlCard(
                            uiState = uiState,
                            onPickImage = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            onMotionChange = { viewModel.updateImageToVideoSettings(motion = it) },
                            onImgPreservationChange = { viewModel.updateImageToVideoSettings(imgPreservation = it) },
                            onCharPreservationChange = { viewModel.updateImageToVideoSettings(charPreservation = it) }
                        )
                    }

                    // Action Buttons Row: [Enhance Prompt] & [Generate Video]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.enhanceCurrentPrompt() },
                            enabled = !uiState.isEnhancingPrompt && !uiState.isGenerating,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, VizoraViolet),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("enhance_prompt_button")
                        ) {
                            if (uiState.isEnhancingPrompt) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = VizoraViolet,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = R.string.btn_enhance_prompt),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        Button(
                            onClick = { viewModel.generateVideo() },
                            enabled = !uiState.isGenerating,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VizoraCyan,
                                contentColor = VizoraObsidian
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1.15f)
                                .height(50.dp)
                                .testTag("generate_video_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = R.string.btn_generate_video),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    // Mobile-First Quick Pill Bar: Duration | Ratio | Style + Advanced Bottom Sheet trigger
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        QuickPillItem(
                            icon = Icons.Default.Timer,
                            label = "Duration: ${uiState.selectedDurationSec}s",
                            onClick = { showAdvancedBottomSheet = true }
                        )
                        QuickPillItem(
                            icon = Icons.Default.AspectRatio,
                            label = "Ratio: ${uiState.selectedAspectRatio.ratio}",
                            onClick = { showAdvancedBottomSheet = true }
                        )
                        QuickPillItem(
                            icon = Icons.Default.Palette,
                            label = "Style: ${uiState.selectedStyle.label}",
                            onClick = { showAdvancedBottomSheet = true }
                        )
                        QuickPillItem(
                            icon = Icons.Default.Lock,
                            label = characterLocks.find { it.id == uiState.activeCharacterLockId }?.name?.take(14)
                                ?: "Character Lock",
                            onClick = { showCharacterLockSheet = true }
                        )
                        Surface(
                            color = VizoraViolet.copy(alpha = 0.22f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, VizoraViolet),
                            modifier = Modifier
                                .clickable { showAdvancedBottomSheet = true }
                                .testTag("open_advanced_settings_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Advanced Settings",
                                    tint = VizoraViolet,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Advanced Settings",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. AI PROMPT ENHANCER BREAKDOWN PREVIEW CARD
        if (uiState.showEnhancedPreviewCard && uiState.enhancedPromptBreakdown != null) {
            item {
                EnhancedPromptInspectionCard(
                    breakdown = uiState.enhancedPromptBreakdown,
                    onDismiss = { viewModel.dismissEnhancedPreviewCard() }
                )
            }
        }

        // 21. GENERATION EXPERIENCE PROGRESS CARD (Non-blocking asynchronous job progress)
        if (uiState.isGenerating) {
            item {
                GenerationProgressCard(uiState = uiState)
            }
        }

        // 29. ERROR HANDLING CARD (Retry | Edit Prompt | Report Problem)
        if (uiState.generationErrorMessage != null) {
            item {
                GenerationErrorCard(
                    errorMessage = uiState.generationErrorMessage,
                    onRetry = { viewModel.generateVideo() },
                    onEditPrompt = { viewModel.updatePromptText(uiState.promptText) },
                    onReportProblem = { viewModel.reportGenerationProblem(uiState.generationErrorMessage) }
                )
            }
        }

        // 1 & 28. GENERATED VIDEO PREVIEW + ACTION BAR (▶ Preview, ⬇ Download, ↗ Share, ✨ Regenerate, 🎬 Edit + Extend/Variations/Continue)
        item {
            val activeProj = uiState.activeProject
            val activeChar = characterLocks.find { it.id == uiState.activeCharacterLockId }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
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
                        Column {
                            Text(
                                text = activeProj?.title ?: "Live Studio Cinema Preview",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = activeProj?.providerUsed ?: uiState.configuredProviderName,
                                style = MaterialTheme.typography.labelSmall,
                                color = VizoraCyan
                            )
                        }

                        Surface(
                            color = VizoraEmerald.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = VizoraEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = uiState.currentJobStatus.label.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VizoraEmerald
                                )
                            }
                        }
                    }

                    VizoraCinemaPlayer(
                        prompt = uiState.promptText,
                        style = uiState.selectedStyle.label,
                        cameraMovement = uiState.selectedCamera.label,
                        lighting = uiState.selectedLighting.label,
                        aspectRatioStr = uiState.selectedAspectRatio.ratio,
                        quality = uiState.selectedQuality.label,
                        durationSec = uiState.selectedDurationSec,
                        captions = uiState.captions,
                        wordByWordHighlight = uiState.wordByWordHighlight,
                        captionFontSizeSp = uiState.captionFontSizeSp,
                        textOverlay = uiState.textOverlay,
                        activeSticker = uiState.activeSticker,
                        activeFilter = uiState.activeFilter,
                        rotationDegrees = uiState.rotationDegrees,
                        playbackSpeed = uiState.playbackSpeed,
                        characterLockName = activeChar?.name.orEmpty(),
                        previewSeed = activeProj?.previewSeed ?: 101
                    )

                    // Requirement 28: Generated video actions: ▶ Preview | ⬇ Download | ↗ Share | ✨ Regenerate | 🎬 Edit
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ActionChipButton(
                            label = "▶ Preview",
                            icon = Icons.Default.PlayArrow,
                            accent = VizoraCyan,
                            tag = "action_preview_video",
                            onClick = { /* Interactive player toggles playback on tap */ }
                        )
                        ActionChipButton(
                            label = "⬇ Download",
                            icon = Icons.Default.Download,
                            accent = VizoraEmerald,
                            tag = "action_download_video",
                            onClick = { viewModel.exportAndDownloadVideo() }
                        )
                        ActionChipButton(
                            label = "↗ Share",
                            icon = Icons.Default.Share,
                            accent = VizoraViolet,
                            tag = "action_share_video",
                            onClick = { viewModel.shareActiveVideo() }
                        )
                        ActionChipButton(
                            label = "✨ Regenerate",
                            icon = Icons.Default.Refresh,
                            accent = VizoraAmber,
                            tag = "action_regenerate_video",
                            onClick = { viewModel.generateVideo(actionVariant = "Regenerate") }
                        )
                        ActionChipButton(
                            label = "🎬 Edit",
                            icon = Icons.Default.MovieFilter,
                            accent = VizoraMagenta,
                            tag = "action_edit_video",
                            onClick = onOpenEditor
                        )
                    }

                    // Requirement 1: Extend video | Create variations | Continue from previous scene
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.extendActiveVideo() },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Extend Video (+5s)", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = { viewModel.createVideoVariation() },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CopyAll, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create Variations", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = { viewModel.continueFromPreviousScene() },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Continue Scene", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // 11. ONE-CLICK SOCIAL MEDIA VIDEO PRESETS
        item {
            SocialMediaPresetsCard(
                selectedPreset = uiState.selectedSocialPreset,
                onSelectPreset = { viewModel.applySocialPreset(it) }
            )
        }
    }

    // 2 & 28. ADVANCED VIDEO SETTINGS BOTTOM SHEET
    if (showAdvancedBottomSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showAdvancedBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            AdvancedVideoSettingsSheetContent(
                uiState = uiState,
                viewModel = viewModel,
                onDone = { showAdvancedBottomSheet = false }
            )
        }
    }

    // 12. CHARACTER LOCK BOTTOM SHEET
    if (showCharacterLockSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showCharacterLockSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            CharacterLockSheetContent(
                characterLocks = characterLocks,
                activeCharacterId = uiState.activeCharacterLockId,
                onSelectCharacter = { viewModel.selectCharacterLock(it) },
                onCreateCharacter = { name, face, hair, clothing, body, age, skin ->
                    viewModel.createCharacterLock(name, face, hair, clothing, body, age, skin)
                },
                onClose = { showCharacterLockSheet = false }
            )
        }
    }
}

@Composable
private fun QuickPillItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(50),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VizoraCyan,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ActionChipButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    tag: String,
    onClick: () -> Unit
) {
    Surface(
        color = accent.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f)),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = accent, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ImageToVideoControlCard(
    uiState: VizoraUiState,
    onPickImage: () -> Unit,
    onMotionChange: (Float) -> Unit,
    onImgPreservationChange: (Float) -> Unit,
    onCharPreservationChange: (Float) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI Image-to-Video Reference",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.uploadedImageLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(onClick = onPickImage) {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload Image", style = MaterialTheme.typography.labelSmall)
                }
            }

            Text(
                text = "Motion Intensity: ${(uiState.motionIntensity * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall
            )
            Slider(value = uiState.motionIntensity, onValueChange = onMotionChange)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Image Preservation: ${(uiState.imagePreservation * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Slider(value = uiState.imagePreservation, onValueChange = onImgPreservationChange)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Character Preservation: ${(uiState.characterPreservation * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Slider(value = uiState.characterPreservation, onValueChange = onCharPreservationChange)
                }
            }
        }
    }
}

@Composable
private fun EnhancedPromptInspectionCard(
    breakdown: EnhancedPromptBreakdown,
    onDismiss: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, VizoraViolet),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✨ AI Prompt Enhancer • 12-Point Cinema Breakdown",
                    style = MaterialTheme.typography.titleSmall,
                    color = VizoraCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Hide",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onDismiss() }
                )
            }
            val specs = listOf(
                "Subject" to breakdown.subject,
                "Environment" to breakdown.environment,
                "Action" to breakdown.action,
                "Camera Movement" to breakdown.cameraMovement,
                "Lens" to breakdown.lens,
                "Lighting" to breakdown.lighting,
                "Atmosphere" to breakdown.atmosphere,
                "Composition" to breakdown.composition,
                "Visual Style" to breakdown.visualStyle,
                "Motion" to breakdown.motion,
                "Duration" to breakdown.duration,
                "Aspect Ratio" to breakdown.aspectRatio
            )
            specs.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pair.forEach { (k, v) ->
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = k.uppercase(), style = MaterialTheme.typography.labelSmall, color = VizoraCyan)
                                Text(text = v, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GenerationProgressCard(uiState: VizoraUiState) {
    val allSteps = listOf(
        "Preparing your prompt...",
        "Generating scenes...",
        "Creating motion...",
        "Rendering video...",
        "Adding audio...",
        "Finalizing your video..."
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, VizoraCyan),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("generation_progress_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = VizoraCyan,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = uiState.generationStepText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${uiState.generationProgressPercent}% • ${uiState.currentJobStatus.label}",
                    style = MaterialTheme.typography.labelMedium,
                    color = VizoraCyan
                )
            }

            LinearProgressIndicator(
                progress = { uiState.generationProgressPercent / 100f },
                color = VizoraCyan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allSteps.forEach { step ->
                    val isCurrent = uiState.generationStepText == step
                    Surface(
                        color = if (isCurrent) VizoraCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = step,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isCurrent) VizoraCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GenerationErrorCard(
    errorMessage: String,
    onRetry: () -> Unit,
    onEditPrompt: () -> Unit,
    onReportProblem: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = VizoraCrimson.copy(alpha = 0.14f)),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, VizoraCrimson),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("generation_error_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = VizoraCrimson
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.error_generation_failed),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = VizoraCrimson)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(id = R.string.btn_retry))
                }
                OutlinedButton(onClick = onEditPrompt) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(id = R.string.btn_edit_prompt))
                }
                OutlinedButton(onClick = onReportProblem) {
                    Icon(imageVector = Icons.Default.ReportProblem, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(id = R.string.btn_report_problem))
                }
            }
        }
    }
}

@Composable
private fun SocialMediaPresetsCard(
    selectedPreset: SocialPreset?,
    onSelectPreset: (SocialPreset) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "One-Click Social Media Presets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Auto-optimizes aspect ratio, resolution, duration & safe text zones",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SocialPreset.entries.forEach { preset ->
                    val isSelected = selectedPreset == preset
                    Surface(
                        color = if (isSelected) VizoraCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) VizoraCyan else Color.Transparent
                        ),
                        modifier = Modifier.clickable { onSelectPreset(preset) }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                text = preset.title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${preset.ratio} • ${preset.resolution} • ${preset.durationSec}s",
                                style = MaterialTheme.typography.labelSmall,
                                color = VizoraCyan
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdvancedVideoSettingsSheetContent(
    uiState: VizoraUiState,
    viewModel: VizoraViewModel,
    onDone: () -> Unit
) {
    val durations = listOf(5, 10, 15, 30, 60)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Advanced Video Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = onDone) {
                Text("Done")
            }
        }

        // Video Duration (5s, 10s, 15s, 30s, 60s)
        Text("Video Duration", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            durations.forEach { sec ->
                FilterChip(
                    selected = uiState.selectedDurationSec == sec,
                    onClick = { viewModel.updateDuration(sec) },
                    label = { Text("${sec}s") }
                )
            }
        }

        // Aspect Ratio (16:9, 9:16, 1:1, 4:5, 21:9)
        Text("Aspect Ratio", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AspectRatioOption.entries.forEach { opt ->
                FilterChip(
                    selected = uiState.selectedAspectRatio == opt,
                    onClick = { viewModel.updateAspectRatio(opt) },
                    label = { Text("${opt.ratio} (${opt.platform})") }
                )
            }
        }

        // Quality (Standard, HD, Full HD, 4K)
        Text("Render Quality", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VideoQuality.entries.forEach { q ->
                FilterChip(
                    selected = uiState.selectedQuality == q,
                    onClick = { viewModel.updateQuality(q) },
                    label = { Text(q.label) }
                )
            }
        }

        // Style (16 styles)
        Text("Visual Style", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VideoStyle.entries.forEach { st ->
                FilterChip(
                    selected = uiState.selectedStyle == st,
                    onClick = { viewModel.updateStyle(st) },
                    label = { Text(st.label) }
                )
            }
        }

        // Camera Movement (13 movements)
        Text("Camera Movement", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CameraMovement.entries.forEach { cam ->
                FilterChip(
                    selected = uiState.selectedCamera == cam,
                    onClick = { viewModel.updateCameraMovement(cam) },
                    label = { Text(cam.label) }
                )
            }
        }

        // Lighting (9 lighting modes)
        Text("Lighting Atmosphere", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LightingMode.entries.forEach { light ->
                FilterChip(
                    selected = uiState.selectedLighting == light,
                    onClick = { viewModel.updateLighting(light) },
                    label = { Text(light.label) }
                )
            }
        }

        // Multiple Video Generations Batch Size
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Batch Variations:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                (1..4).forEach { count ->
                    FilterChip(
                        selected = uiState.numberOfGenerations == count,
                        onClick = { viewModel.updateNumberOfGenerations(count) },
                        label = { Text("${count}x") }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CharacterLockSheetContent(
    characterLocks: List<CharacterLockEntity>,
    activeCharacterId: Int?,
    onSelectCharacter: (Int?) -> Unit,
    onCreateCharacter: (String, String, String, String, String, String, String) -> Unit,
    onClose: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var face by remember { mutableStateOf("") }
    var hair by remember { mutableStateOf("") }
    var clothing by remember { mutableStateOf("") }
    var skin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔒 Character Lock (Identity Preservation)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = onClose) { Text("Done") }
        }
        Text(
            text = "Preserve Face, Hair, Clothing, Body, Age & Skin appearance across every scene.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        characterLocks.forEach { charLock ->
            val selected = charLock.id == activeCharacterId
            Surface(
                color = if (selected) VizoraAmber.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (selected) VizoraAmber else Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectCharacter(if (selected) null else charLock.id) }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = charLock.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(text = charLock.identityToken, style = MaterialTheme.typography.labelSmall, color = VizoraAmber)
                    }
                    Text(
                        text = "Face: ${charLock.faceDescription} • Attire: ${charLock.clothingDescription}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Text("Create New Character Lock", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Character Name") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = face,
                onValueChange = { face = it },
                label = { Text("Face & Age") },
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = hair,
                onValueChange = { hair = it },
                label = { Text("Hair & Skin") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = clothing,
                onValueChange = { clothing = it },
                label = { Text("Clothing & Body") },
                modifier = Modifier.weight(1f)
            )
        }
        Button(
            onClick = {
                onCreateCharacter(name, face, hair, clothing, "Consistent build", "Adult", skin.ifBlank { hair })
                name = ""
                face = ""
                hair = ""
                clothing = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save & Lock Character Identity")
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}
