package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.CreditTransactionEntity
import com.example.data.ModerationLogEntity
import com.example.data.VideoProjectEntity
import com.example.ui.theme.VizoraAmber
import com.example.ui.theme.VizoraCyan
import com.example.ui.theme.VizoraEmerald
import com.example.ui.theme.VizoraViolet
import com.example.viewmodel.VizoraUiState
import com.example.viewmodel.VizoraViewModel

@Composable
fun StudioAccountAdminScreen(
    uiState: VizoraUiState,
    projects: List<VideoProjectEntity>,
    transactions: List<CreditTransactionEntity>,
    moderationLogs: List<ModerationLogEntity>,
    viewModel: VizoraViewModel,
    modifier: Modifier = Modifier
) {
    var editName by remember(uiState.userName) { mutableStateOf(uiState.userName) }
    var editEmail by remember(uiState.userEmail) { mutableStateOf(uiState.userEmail) }
    var editHandle by remember(uiState.userHandle) { mutableStateOf(uiState.userHandle) }
    var announcementDraft by remember(uiState.systemAnnouncement) { mutableStateOf(uiState.systemAnnouncement) }

    val providers = listOf(
        "Google Veo 3.1 (veo-3.1-fast-generate-preview)",
        "Google Veo 3.1 Pro (veo-3.1-generate-preview)",
        "Runway Gen-3 Alpha Turbo Adapter",
        "Luma Dream Machine Adapter"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("studio_account_admin_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 100% FREE UNLIMITED CREATOR STUDIO BANNER
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, VizoraEmerald.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(VizoraEmerald.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AllInclusive,
                                contentDescription = null,
                                tint = VizoraEmerald,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "100% Free Unlimited Studio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = VizoraEmerald
                            )
                            Text(
                                text = "All Text-to-Video, Story-to-Video, 4K Render, AI Voiceover & Music tools are unlocked 100% free with zero paywalls.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 14. USER ACCOUNT SYSTEM (Profile, Google Sign-in Status, Account Settings)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
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
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = VizoraCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Creator Profile & Account",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            color = VizoraCyan.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = VizoraCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Google Connected", style = MaterialTheme.typography.labelSmall, color = VizoraCyan)
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Creator Name") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editHandle,
                            onValueChange = { editHandle = it },
                            label = { Text("Studio Handle") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Account Email") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Dark Cinema Theme", style = MaterialTheme.typography.labelLarge)
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = uiState.isDarkMode,
                                onCheckedChange = { viewModel.toggleDarkMode() }
                            )
                        }
                        Button(onClick = { viewModel.updateAccountProfile(editName, editEmail, editHandle) }) {
                            Text("Save Profile")
                        }
                    }
                }
            }
        }

        // 22 & 23. AI PROVIDER ABSTRACTION LAYER & API ARCHITECTURE CONFIGURATION
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, VizoraViolet.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = VizoraViolet)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Provider Abstraction Layer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Active VideoProvider: ${uiState.configuredProviderName}. API keys are managed securely via BuildConfig (.env / Secrets panel).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        providers.forEach { p ->
                            FilterChip(
                                selected = uiState.configuredProviderName == p,
                                onClick = { viewModel.adminSwitchProvider(p) },
                                label = { Text(p) }
                            )
                        }
                    }
                }
            }
        }

        // 25 & 26. ADMIN DASHBOARD & AI SAFETY MODERATION CONTROLS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(20.dp),
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
                            Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = VizoraAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Admin Panel & System Telemetry",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Switch(
                            checked = uiState.isAdminMode,
                            onCheckedChange = { viewModel.toggleAdminMode() }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminMetricBox(label = "Projects", value = "${projects.size}", modifier = Modifier.weight(1f))
                        AdminMetricBox(label = "Safety Logs", value = "${moderationLogs.size}", modifier = Modifier.weight(1f))
                        AdminMetricBox(label = "System Status", value = "99.9% Up", modifier = Modifier.weight(1f))
                    }

                    OutlinedTextField(
                        value = announcementDraft,
                        onValueChange = { announcementDraft = it },
                        label = { Text("Global Studio Announcement") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedButton(
                        onClick = { viewModel.adminUpdateAnnouncement(announcementDraft) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Publish Announcement")
                    }
                }
            }
        }

        // 26. AI SAFETY & GENERATION LOGS
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = VizoraEmerald)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Generation & AI Safety Audit Logs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items(transactions.take(6), key = { it.id }) { tx ->
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(imageVector = Icons.Default.History, contentDescription = null, tint = VizoraCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = tx.description, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Text(text = tx.planTier, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Text(
                        text = "FREE",
                        style = MaterialTheme.typography.labelLarge,
                        color = VizoraEmerald
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminMetricBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = VizoraCyan)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
