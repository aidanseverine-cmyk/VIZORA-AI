package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.GenerationMode
import com.example.data.VideoProjectEntity
import com.example.data.VideoTemplateItem
import com.example.ui.theme.VizoraAmber
import com.example.ui.theme.VizoraCyan
import com.example.ui.theme.VizoraEmerald
import com.example.ui.theme.VizoraMagenta
import com.example.ui.theme.VizoraObsidian
import com.example.ui.theme.VizoraViolet
import com.example.viewmodel.VizoraDestination
import com.example.viewmodel.VizoraUiState

@Composable
fun HomeDashboardScreen(
    uiState: VizoraUiState,
    recentProjects: List<VideoProjectEntity>,
    templates: List<VideoTemplateItem>,
    onCreateVideoClick: () -> Unit,
    onExploreTemplatesClick: () -> Unit,
    onOpenProject: (VideoProjectEntity, Boolean) -> Unit,
    onSelectTemplate: (VideoTemplateItem) -> Unit,
    onSelectAiTool: (GenerationMode, VizoraDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_dashboard_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 19. HOME PAGE HERO SECTION
        item {
            HeroBannerCard(
                onCreateVideoClick = onCreateVideoClick,
                onExploreTemplatesClick = onExploreTemplatesClick
            )
        }

        // 20. DASHBOARD STATUS ROW: Credits Remaining + Generation Status + Active Provider
        item {
            DashboardQuickStatsRow(
                uiState = uiState,
                projectCount = recentProjects.size,
                onCreateVideoClick = onCreateVideoClick
            )
        }

        // AI TOOLS QUICK LAUNCHER (Requirement 20)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "AI Creation Suite",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AiToolChipCard(
                        title = "Text → Video",
                        subtitle = "Veo 3.1 Cinema",
                        icon = Icons.Default.MovieCreation,
                        accent = VizoraCyan,
                        onClick = { onSelectAiTool(GenerationMode.TEXT_TO_VIDEO, VizoraDestination.CREATE) }
                    )
                    AiToolChipCard(
                        title = "Story → Video",
                        subtitle = "Multi-Scene AI",
                        icon = Icons.Default.ViewCarousel,
                        accent = VizoraViolet,
                        onClick = { onSelectAiTool(GenerationMode.STORY_TO_VIDEO, VizoraDestination.STORY) }
                    )
                    AiToolChipCard(
                        title = "Image → Video",
                        subtitle = "Motion Control",
                        icon = Icons.Default.Image,
                        accent = VizoraMagenta,
                        onClick = { onSelectAiTool(GenerationMode.IMAGE_TO_VIDEO, VizoraDestination.CREATE) }
                    )
                    AiToolChipCard(
                        title = "AI Voiceover",
                        subtitle = "8 Languages TTS",
                        icon = Icons.Default.RecordVoiceOver,
                        accent = VizoraAmber,
                        onClick = { onSelectAiTool(GenerationMode.TEXT_TO_VIDEO, VizoraDestination.EDITOR) }
                    )
                    AiToolChipCard(
                        title = "AI Music & SFX",
                        subtitle = "Afrobeat & Cinema",
                        icon = Icons.Default.MusicNote,
                        accent = VizoraEmerald,
                        onClick = { onSelectAiTool(GenerationMode.TEXT_TO_VIDEO, VizoraDestination.EDITOR) }
                    )
                    AiToolChipCard(
                        title = "Auto Subtitles",
                        subtitle = "Word Highlight",
                        icon = Icons.Default.Subtitles,
                        accent = VizoraCyan,
                        onClick = { onSelectAiTool(GenerationMode.TEXT_TO_VIDEO, VizoraDestination.EDITOR) }
                    )
                }
            }
        }

        // EXAMPLE GENERATED VIDEOS GALLERY & RECENT PROJECTS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Recent Studio Generations",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap any video to preview, regenerate, or open in AI Timeline Editor",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.clickable { onCreateVideoClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+ Create Video",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        items(recentProjects.take(4), key = { it.id }) { project ->
            RecentProjectGalleryCard(
                project = project,
                onPreviewClick = { onOpenProject(project, false) },
                onEditClick = { onOpenProject(project, true) }
            )
        }

        // FEATURED READY-MADE TEMPLATES CAROUSEL
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ready-Made Cinema Templates",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "View All (${templates.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onExploreTemplatesClick() }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    templates.take(6).forEach { tpl ->
                        TemplateQuickCard(
                            template = tpl,
                            onClick = { onSelectTemplate(tpl) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroBannerCard(
    onCreateVideoClick: () -> Unit,
    onExploreTemplatesClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(VizoraCyan.copy(alpha = 0.65f), VizoraViolet.copy(alpha = 0.65f))),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_hero_banner_1790944365201),
            contentDescription = "VIZORA AI Hero Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            VizoraObsidian.copy(alpha = 0.35f),
                            VizoraObsidian.copy(alpha = 0.85f),
                            VizoraObsidian.copy(alpha = 0.96f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = VizoraCyan.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VizoraCyan.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = VizoraCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "VIZORA AI • Imagine it. Prompt it. Create it.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(id = R.string.hero_headline),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(id = R.string.hero_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onCreateVideoClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VizoraCyan,
                            contentColor = VizoraObsidian
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("hero_create_video_button")
                    ) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(id = R.string.cta_create_video),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    OutlinedButton(
                        onClick = onExploreTemplatesClick,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("hero_explore_templates_button")
                    ) {
                        Text(
                            text = stringResource(id = R.string.cta_explore_templates),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardQuickStatsRow(
    uiState: VizoraUiState,
    projectCount: Int,
    onCreateVideoClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "STUDIO ACCESS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "100% FREE ∞",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Unlimited AI Generations",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .weight(1f)
                .clickable { onCreateVideoClick() }
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "GENERATION STATUS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (uiState.isGenerating) uiState.currentJobStatus.label.uppercase() else "READY • $projectCount VIDEOS",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (uiState.isGenerating) VizoraAmber else VizoraEmerald,
                    fontWeight = FontWeight.Bold
                )
                if (uiState.isGenerating) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { uiState.generationProgressPercent / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "Queue idle • Instant render",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AiToolChipCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
        modifier = Modifier
            .width(152.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = accent, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RecentProjectGalleryCard(
    project: VideoProjectEntity,
    onPreviewClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val thumbRes = when {
        project.style.contains("Cyberpunk", ignoreCase = true) -> R.drawable.img_sample_cyberpunk_1790944386307
        project.generationMode == GenerationMode.STORY_TO_VIDEO.name -> R.drawable.img_sample_pilot_story_1790944396942
        else -> R.drawable.img_sample_afro_cinema_1790944375799
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPreviewClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(165.dp)
            ) {
                Image(
                    painter = painterResource(id = thumbRes),
                    contentDescription = project.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, VizoraObsidian.copy(alpha = 0.88f))
                            )
                        )
                )

                // Top badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = VizoraObsidian.copy(alpha = 0.78f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${project.aspectRatio} • ${project.quality} • ${project.durationSec}s",
                            style = MaterialTheme.typography.labelSmall,
                            color = VizoraCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (project.characterLockName.isNotBlank()) {
                        Surface(
                            color = VizoraObsidian.copy(alpha = 0.78f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = VizoraAmber,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = project.characterLockName.take(15),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VizoraAmber
                                )
                            }
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = "Preview Video",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${project.style} • ${project.cameraMovement} • ${project.lighting}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = project.prompt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Status: ${project.jobStatus} • 100% Free Studio",
                        style = MaterialTheme.typography.labelSmall,
                        color = VizoraEmerald
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onEditClick,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.MovieFilter, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("🎬 Edit", style = MaterialTheme.typography.labelSmall)
                        }
                        Button(
                            onClick = onPreviewClick,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("▶ Preview", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplateQuickCard(
    template: VideoTemplateItem,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .width(220.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = VizoraViolet.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = template.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "${template.aspectRatio} • ${template.durationSec}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = template.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = template.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Use Template",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
