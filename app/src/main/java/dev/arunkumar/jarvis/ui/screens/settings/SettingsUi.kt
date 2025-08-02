package dev.arunkumar.jarvis.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.android.components.ActivityComponent
import dev.arunkumar.jarvis.ui.screens.SettingsScreen
@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(SettingsScreen::class, ActivityComponent::class)
@Composable
fun SettingsUi(state: SettingsScreen.State, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = { state.eventSink(SettingsScreen.Event.OnBackClicked) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            modifier = modifier
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Appearance Section
                SettingsSection(title = "Appearance") {
                    SettingsItem(
                        icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                        title = "Dark Theme",
                        subtitle = "Switch between light and dark theme",
                        trailing = {
                            Switch(
                                checked = state.isDarkTheme,
                                onCheckedChange = { state.eventSink(SettingsScreen.Event.OnThemeToggled) }
                            )
                        }
                    )
                }

                // Notifications Section
                SettingsSection(title = "Notifications") {
                    SettingsItem(
                        icon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
                        title = "Enable Notifications",
                        subtitle = "Receive notifications for important updates",
                        trailing = {
                            Switch(
                                checked = state.isNotificationsEnabled,
                                onCheckedChange = { state.eventSink(SettingsScreen.Event.OnNotificationsToggled) }
                            )
                        }
                    )
                }

                // Language Section
                SettingsSection(title = "Language & Region") {
                    var expanded by remember { mutableStateOf(false) }
                    val languages = listOf("English", "Spanish", "French", "German", "Japanese")

                    SettingsItem(
                        icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                        title = "Language",
                        subtitle = "Select your preferred language",
                        trailing = {
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = state.selectedLanguage,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .width(140.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    languages.forEach { language ->
                                        DropdownMenuItem(
                                            text = { Text(language) },
                                            onClick = {
                                                state.eventSink(SettingsScreen.Event.OnLanguageChanged(language))
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    )
                }

                // About Section
                SettingsSection(title = "About") {
                    Column {
                        Text(
                            text = "Jarvis AI Assistant",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Version 1.0.0",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Built with Circuit navigation and Hilt dependency injection. Demonstrating modern Android development practices with Jetpack Compose.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

@Composable
private fun SettingsSection(
        title: String,
        content: @Composable () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                content()
            }
        }
    }

@Composable
private fun SettingsItem(
        icon: @Composable () -> Unit,
        title: String,
        subtitle: String,
        trailing: @Composable () -> Unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            trailing()
        }
    }