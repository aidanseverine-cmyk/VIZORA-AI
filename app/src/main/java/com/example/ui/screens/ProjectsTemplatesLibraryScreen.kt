package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.SavedPromptEntity
import com.example.data.VideoProjectEntity
import com.example.data.VideoTemplateItem
import com.example.ui.theme.VizoraAmber
import com.example.ui.theme.VizoraCrimson
import com.example.ui.theme.VizoraCyan
import com.example.ui.theme.VizoraEmerald
import com.example.ui.theme.VizoraViolet
import com.example.viewmodel.VizoraDestination
import com.example.viewmodel.VizoraViewModel

@Composable
fun ProjectsTemplatesLibraryScreen(
    activeProjects: List<VideoProjectEntity>,
    favoriteProjects: List<VideoProjectEntity>,
    trashedProjects: List<VideoProjectEntity>,
    savedPrompts: List<SavedPromptEntity>,
    templates: List<VideoTemplateItem>,
    viewModel: VizoraViewModel,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableIntStateOf(0) } // 0=Projects, 1=Templates (14), 2=Prompt Library, 3=Favorites, 4=Trash
    var searchQuery by remember { mutableStateOf("") }
    var newPromptTitle by remember { mutableStateOf("") }
    var newPromptBody by remember { mutableStateOf("") }
    var newPromptCategory by remember { mutableStateOf("African Cinema") }

    val sections = listOf(
        "Projects (${activeProjects.size})",
        "Templates (${templates.size})",
        "Prompt Library (${savedPrompts.size})",
        "Favorites (${favoriteProjects.size})",
        "Trash (${trashedProjects.size})"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("library_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedSection,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clip(RoundedCornerShape(14.dp))
            ) {
                sections.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedSection == idx,
                        onClick = { selectedSection = idx },
                        text = { Text(title, style = MaterialTheme.typography.labelLarge) }
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                placeholder = { Text("Search projects, 14 templates, or saved prompts...") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("library_search_input")
            )
        }

        when (selectedSection) {
            // 0: Active Projects (Videos, Images, Audio, Timeline saved)
            0 -> {
                val filtered = activeProjects.filter {
                    searchQuery.isBlank() ||
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.prompt.contains(searchQuery, ignoreCase = true) ||
                        it.style.contains(searchQuery, ignoreCase = true)
                }
                items(filtered, key = { it.id }) { proj ->
                    ProjectWorkspaceCard(
                        project = proj,
                        isTrashed = false,
                        onOpen = { viewModel.openProjectInStudio(proj, false) },
                        onEdit = { viewModel.openProjectInStudio(proj, true) },
                        onToggleFavorite = { viewModel.toggleProjectFavorite(proj) },
                        onTrashOrRestore = { viewModel.moveProjectToTrash(proj) },
                        onDeleteForever = {}
                    )
                }
            }

            // 1: 14 Ready-Made Templates (Requirement 17)
            1 -> {
                val filteredTemplates = templates.filter {
                    searchQuery.isBlank() ||
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
                }
                items(filteredTemplates, key = { it.id }) { tpl ->
                    TemplateLibraryDetailCard(
                        template = tpl,
                        onUseTemplate = { viewModel.applyTemplate(tpl) }
                    )
                }
            }

            // 2: Prompt Library (Save, Favorite, Duplicate, Edit, Delete, Search, Categorize - Requirement 18)
            2 -> {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, VizoraCyan.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Save New Prompt to Library",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newPromptTitle,
                                    onValueChange = { newPromptTitle = it },
                                    label = { Text("Prompt Title") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = newPromptCategory,
                                    onValueChange = { newPromptCategory = it },
                                    label = { Text("Category") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            OutlinedTextField(
                                value = newPromptBody,
                                onValueChange = { newPromptBody = it },
                                label = { Text("Detailed Video Prompt") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    viewModel.savePromptToLibrary(newPromptTitle, newPromptBody, newPromptCategory)
                                    newPromptTitle = ""
                                    newPromptBody = ""
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Prompt")
                            }
                        }
                    }
                }

                val filteredPrompts = savedPrompts.filter {
                    searchQuery.isBlank() ||
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.promptText.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
                }
                items(filteredPrompts, key = { it.id }) { saved ->
                    SavedPromptCard(
                        savedPrompt = saved,
                        onUsePrompt = {
                            viewModel.updatePromptText(saved.promptText)
                            viewModel.navigateTo(VizoraDestination.CREATE)
                        },
                        onToggleFavorite = { viewModel.toggleSavedPromptFavorite(saved) },
                        onDuplicate = { viewModel.duplicateSavedPrompt(saved) },
                        onDelete = { viewModel.deleteSavedPrompt(saved) }
                    )
                }
            }

            // 3: Favorites
            3 -> {
                items(favoriteProjects, key = { it.id }) { proj ->
                    ProjectWorkspaceCard(
                        project = proj,
                        isTrashed = false,
                        onOpen = { viewModel.openProjectInStudio(proj, false) },
                        onEdit = { viewModel.openProjectInStudio(proj, true) },
                        onToggleFavorite = { viewModel.toggleProjectFavorite(proj) },
                        onTrashOrRestore = { viewModel.moveProjectToTrash(proj) },
                        onDeleteForever = {}
                    )
                }
            }

            // 4: Trash
            4 -> {
                items(trashedProjects, key = { it.id }) { proj ->
                    ProjectWorkspaceCard(
                        project = proj,
                        isTrashed = true,
                        onOpen = { viewModel.openProjectInStudio(proj, false) },
                        onEdit = { viewModel.openProjectInStudio(proj, true) },
                        onToggleFavorite = { viewModel.toggleProjectFavorite(proj) },
                        onTrashOrRestore = { viewModel.restoreProjectFromTrash(proj) },
                        onDeleteForever = { viewModel.deleteProjectForever(proj) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectWorkspaceCard(
    project: VideoProjectEntity,
    isTrashed: Boolean,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onToggleFavorite: () -> Unit,
    onTrashOrRestore: () -> Unit,
    onDeleteForever: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${project.aspectRatio} • ${project.quality} • ${project.durationSec}s • ${project.style}",
                        style = MaterialTheme.typography.labelSmall,
                        color = VizoraCyan
                    )
                }
                Row {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (project.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (project.isFavorite) VizoraAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onTrashOrRestore) {
                        Icon(
                            imageVector = if (isTrashed) Icons.Default.RestoreFromTrash else Icons.Default.Delete,
                            contentDescription = if (isTrashed) "Restore" else "Trash",
                            tint = if (isTrashed) VizoraEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isTrashed) {
                        IconButton(onClick = onDeleteForever) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = "Delete Permanently",
                                tint = VizoraCrimson
                            )
                        }
                    }
                }
            }

            Text(
                text = project.prompt,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved: Video • Audio • Timeline • Captions",
                    style = MaterialTheme.typography.labelSmall,
                    color = VizoraEmerald
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onEdit) {
                        Icon(imageVector = Icons.Default.MovieFilter, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit")
                    }
                    Button(onClick = onOpen) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Open")
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplateLibraryDetailCard(
    template: VideoTemplateItem,
    onUseTemplate: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = template.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${template.category} • ${template.aspectRatio} • ${template.durationSec}s • ${template.style}",
                        style = MaterialTheme.typography.labelSmall,
                        color = VizoraCyan
                    )
                }
                Button(onClick = onUseTemplate) {
                    Text("Use Template")
                }
            }
            Text(
                text = template.description,
                style = MaterialTheme.typography.bodySmall
            )
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Prompt: \"${template.defaultPrompt}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun SavedPromptCard(
    savedPrompt: SavedPromptEntity,
    onUsePrompt: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = savedPrompt.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${savedPrompt.category} • ${savedPrompt.style} • ${savedPrompt.aspectRatio}",
                        style = MaterialTheme.typography.labelSmall,
                        color = VizoraViolet
                    )
                }
                Row {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (savedPrompt.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite Prompt",
                            tint = if (savedPrompt.isFavorite) VizoraAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDuplicate) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Duplicate Prompt"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Prompt",
                            tint = VizoraCrimson
                        )
                    }
                }
            }
            Text(
                text = savedPrompt.promptText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onUsePrompt,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Load Into Studio")
            }
        }
    }
}
