package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CreateVideoStudioScreen
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.ProjectsTemplatesLibraryScreen
import com.example.ui.screens.StoryToVideoScreen
import com.example.ui.screens.StudioAccountAdminScreen
import com.example.ui.screens.VideoEditorStudioScreen
import com.example.ui.theme.VizoraCyan
import com.example.ui.theme.VizoraEmerald
import com.example.ui.theme.VizoraObsidian
import com.example.ui.theme.VizoraTheme
import com.example.ui.theme.VizoraViolet
import com.example.viewmodel.VizoraDestination
import com.example.viewmodel.VizoraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vizoraViewModel: VizoraViewModel = viewModel()
            val uiState by vizoraViewModel.uiState.collectAsStateWithLifecycle()

            VizoraTheme(darkTheme = uiState.isDarkMode) {
                VizoraAppRoot(viewModel = vizoraViewModel)
            }
        }
    }
}

data class NavItemSpec(
    val destination: VizoraDestination,
    val icon: ImageVector,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VizoraAppRoot(
    viewModel: VizoraViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeProjects by viewModel.activeProjects.collectAsStateWithLifecycle()
    val favoriteProjects by viewModel.favoriteProjects.collectAsStateWithLifecycle()
    val trashedProjects by viewModel.trashedProjects.collectAsStateWithLifecycle()
    val savedPrompts by viewModel.savedPrompts.collectAsStateWithLifecycle()
    val characterLocks by viewModel.characterLocks.collectAsStateWithLifecycle()
    val transactions by viewModel.creditTransactions.collectAsStateWithLifecycle()
    val moderationLogs by viewModel.moderationLogs.collectAsStateWithLifecycle()

    // Handle system Back press from secondary screens back to Home
    if (uiState.currentDestination != VizoraDestination.HOME) {
        BackHandler {
            viewModel.navigateTo(VizoraDestination.HOME)
        }
    }

    val navItems = listOf(
        NavItemSpec(VizoraDestination.HOME, Icons.Default.Home, "nav_home"),
        NavItemSpec(VizoraDestination.CREATE, Icons.Default.MovieCreation, "nav_create"),
        NavItemSpec(VizoraDestination.STORY, Icons.Default.ViewCarousel, "nav_story"),
        NavItemSpec(VizoraDestination.EDITOR, Icons.Default.MovieFilter, "nav_editor"),
        NavItemSpec(VizoraDestination.LIBRARY, Icons.Default.VideoLibrary, "nav_library"),
        NavItemSpec(VizoraDestination.STUDIO, Icons.Default.AdminPanelSettings, "nav_studio")
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideTabletOrDesktop = maxWidth >= 700.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(listOf(VizoraCyan, VizoraViolet))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = VizoraObsidian,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "VIZORA AI",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Imagine it. Prompt it. Create it.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        // 100% Free Unlimited Badge
                        Surface(
                            color = VizoraEmerald.copy(alpha = 0.16f),
                            shape = CircleShape,
                            modifier = Modifier
                                .clickable { viewModel.navigateTo(VizoraDestination.STUDIO) }
                                .testTag("free_unlimited_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AllInclusive,
                                    contentDescription = "100% Free Unlimited",
                                    tint = VizoraEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "100% FREE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VizoraEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.toggleDarkMode() },
                            modifier = Modifier.testTag("theme_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (uiState.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Dark/Light Theme"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                if (!isWideTabletOrDesktop) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        navItems.forEach { item ->
                            NavigationBarItem(
                                selected = uiState.currentDestination == item.destination,
                                onClick = { viewModel.navigateTo(item.destination) },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.destination.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.destination.label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                modifier = Modifier.testTag(item.tag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWideTabletOrDesktop) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        navItems.forEach { item ->
                            NavigationRailItem(
                                selected = uiState.currentDestination == item.destination,
                                onClick = { viewModel.navigateTo(item.destination) },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.destination.label
                                    )
                                },
                                label = { Text(item.destination.label) },
                                modifier = Modifier.testTag(item.tag)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Transient Feedback Banner
                    AnimatedVisibility(visible = uiState.statusBannerMessage != null) {
                        uiState.statusBannerMessage?.let { msg ->
                            Surface(
                                color = VizoraCyan.copy(alpha = 0.18f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = msg,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss notification",
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { viewModel.clearStatusBanner() }
                                    )
                                }
                            }
                        }
                    }

                    when (uiState.currentDestination) {
                        VizoraDestination.HOME -> HomeDashboardScreen(
                            uiState = uiState,
                            recentProjects = activeProjects,
                            templates = viewModel.templates,
                            onCreateVideoClick = { viewModel.navigateTo(VizoraDestination.CREATE) },
                            onExploreTemplatesClick = { viewModel.navigateTo(VizoraDestination.LIBRARY) },
                            onOpenProject = { proj, edit -> viewModel.openProjectInStudio(proj, edit) },
                            onSelectTemplate = { tpl -> viewModel.applyTemplate(tpl) },
                            onSelectAiTool = { mode, dest ->
                                viewModel.selectGenerationMode(mode)
                                viewModel.navigateTo(dest)
                            }
                        )

                        VizoraDestination.CREATE -> CreateVideoStudioScreen(
                            uiState = uiState,
                            characterLocks = characterLocks,
                            viewModel = viewModel,
                            onOpenEditor = { viewModel.navigateTo(VizoraDestination.EDITOR) }
                        )

                        VizoraDestination.STORY -> StoryToVideoScreen(
                            uiState = uiState,
                            characterLocks = characterLocks,
                            viewModel = viewModel,
                            onOpenInEditor = { viewModel.navigateTo(VizoraDestination.EDITOR) }
                        )

                        VizoraDestination.EDITOR -> VideoEditorStudioScreen(
                            uiState = uiState,
                            viewModel = viewModel
                        )

                        VizoraDestination.LIBRARY -> ProjectsTemplatesLibraryScreen(
                            activeProjects = activeProjects,
                            favoriteProjects = favoriteProjects,
                            trashedProjects = trashedProjects,
                            savedPrompts = savedPrompts,
                            templates = viewModel.templates,
                            viewModel = viewModel
                        )

                        VizoraDestination.STUDIO -> StudioAccountAdminScreen(
                            uiState = uiState,
                            projects = activeProjects,
                            transactions = transactions,
                            moderationLogs = moderationLogs,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
