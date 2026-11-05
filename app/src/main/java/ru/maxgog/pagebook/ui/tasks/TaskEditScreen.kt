package ru.maxgog.pagebook.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.domain.model.Attachment
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.ui.components.AttachmentPreviewList
import ru.maxgog.pagebook.ui.components.CreateTagDialog
import ru.maxgog.pagebook.ui.components.DateTimePickerDialog
import ru.maxgog.pagebook.ui.components.FullScreenAttachmentViewer
import ru.maxgog.pagebook.ui.components.TagChip
import ru.maxgog.pagebook.ui.components.TagPickerDialog
import ru.maxgog.pagebook.ui.components.rememberCameraPicker
import ru.maxgog.pagebook.ui.components.rememberFilePicker
import ru.maxgog.pagebook.ui.components.rememberPhotoPicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskEditScreen(
    onBack: () -> Unit,
    taskId: Int? = null,
    viewModel: TaskEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allTags by viewModel.allTags.collectAsStateWithLifecycle()

    var showTagPicker by remember { mutableStateOf(false) }
    var showCreateTag by remember { mutableStateOf(false) }
    var editingTag by remember { mutableStateOf<Tag?>(null) }
    var showDateTimePicker by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    val currentTask = (uiState as? TaskEditUiState.Editing)?.task
    val currentTags = when (val s = uiState) {
        is TaskEditUiState.Editing -> s.tags
        is TaskEditUiState.NewTask -> s.selectedTags
        else -> emptyList()
    }
    val isLoading = uiState is TaskEditUiState.Loading
    val isNewTask = uiState is TaskEditUiState.NewTask

    var title by remember(currentTask?.id) { mutableStateOf(currentTask?.title ?: "") }
    var description by remember(currentTask?.id) { mutableStateOf(currentTask?.description ?: "") }
    var reminderTime by remember(currentTask?.id) { mutableStateOf(currentTask?.reminderTime) }

    val pickImage = rememberPhotoPicker { uri -> viewModel.attachUri(uri) }
    val pickFile = rememberFilePicker { uri -> viewModel.attachUri(uri) }
    val pickCamera = rememberCameraPicker(
        onPicked = { uri -> viewModel.attachUri(uri) }
    )
    var viewingAttachment by remember { mutableStateOf<Attachment?>(null) }

    val currentAttachments = when (val s = uiState) {
        is TaskEditUiState.Editing -> s.attachments
        is TaskEditUiState.NewTask -> s.pendingAttachments
        else -> emptyList()
    }

    LaunchedEffect(taskId) {
        if (taskId != null) viewModel.setTaskId(taskId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                TaskEditEvent.Saved, TaskEditEvent.Deleted -> onBack()
            }
        }
    }

    val saveEnabled = title.isNotBlank() && !isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(if (isNewTask) R.string.new_task else R.string.edit_task),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    FilledTonalIconButton(
                        onClick = { viewModel.save(title, description, reminderTime) },
                        enabled = saveEnabled
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = stringResource(R.string.save)
                        )
                    }

                    IconButton(onClick = { showTagPicker = true }) {
                        BadgedBox(
                            badge = {
                                if (currentTags.isNotEmpty()) {
                                    Badge { Text(currentTags.size.toString()) }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Label,
                                contentDescription = stringResource(R.string.tags)
                            )
                        }
                    }

                    Box {
                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.more_options)
                            )
                        }
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false },
                            shape = MaterialTheme.shapes.large
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.add_photo)) },
                                onClick = { showMoreMenu = false; pickImage() },
                                leadingIcon = {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.attach_file)) },
                                onClick = { showMoreMenu = false; pickFile() },
                                leadingIcon = {
                                    Icon(Icons.Default.AttachFile, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.take_photo)) },
                                onClick = { showMoreMenu = false; pickCamera() },
                                leadingIcon = {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null)
                                }
                            )
                            if (currentTask != null) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.delete),
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    onClick = { showMoreMenu = false; viewModel.delete() },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        when {
            isLoading -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            uiState is TaskEditUiState.Error -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (uiState as TaskEditUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.task_title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.task_description)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    maxLines = 6,
                    shape = MaterialTheme.shapes.large
                )

                if (currentAttachments.isNotEmpty()) {
                    AttachmentPreviewList(
                        attachments = currentAttachments,
                        onRemove = viewModel::removeAttachment,
                        onDownload = viewModel::downloadAttachment,
                        onImageClick = { viewingAttachment = it },
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    currentTags.forEach { tag ->
                        TagChip(
                            tag = tag,
                            selected = true,
                            onDelete = { viewModel.toggleTag(tag) }
                        )
                    }
                    AssistChip(
                        onClick = { showTagPicker = true },
                        label = { Text(stringResource(R.string.tags)) },
                        leadingIcon = {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.reminder),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = reminderTime?.let {
                                    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                                        .format(Date(it))
                                } ?: stringResource(R.string.reminder_not_set),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (reminderTime != null) {
                            TextButton(onClick = { reminderTime = null }) {
                                Text(stringResource(R.string.clear))
                            }
                        }
                    }
                }

                FilledTonalButton(
                    onClick = { showDateTimePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(
                            if (reminderTime == null) R.string.set_reminder else R.string.change_reminder
                        )
                    )
                }

                if (currentTask?.noteId != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddTask,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = stringResource(R.string.linked_to_note, currentTask.noteId!!),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDateTimePicker) {
        DateTimePickerDialog(
            initialMillis = reminderTime,
            onDismiss = { showDateTimePicker = false },
            onConfirm = { millis ->
                reminderTime = millis
                showDateTimePicker = false
            }
        )
    }

    if (showTagPicker) {
        TagPickerDialog(
            allTags = allTags,
            selectedTags = currentTags,
            onToggle = viewModel::toggleTag,
            onCreateTag = {
                showTagPicker = false
                editingTag = null
                showCreateTag = true
            },
            onEditTag = { tag ->
                showTagPicker = false
                editingTag = tag
                showCreateTag = true
            },
            onDismiss = { showTagPicker = false }
        )
    }

    if (showCreateTag) {
        val tagBeingEdited = editingTag
        CreateTagDialog(
            initialTag = tagBeingEdited,
            onDismiss = {
                showCreateTag = false
                editingTag = null
            },
            onConfirm = { name, color, emoji ->
                if (tagBeingEdited != null) {
                    viewModel.updateTag(
                        tagBeingEdited.copy(
                            name = name.trim(),
                            colorHex = color.hex,
                            emoji = emoji
                        )
                    )
                } else {
                    viewModel.createAndToggleTag(name, color, emoji)
                }
                showCreateTag = false
                editingTag = null
            }
        )
    }

    viewingAttachment?.let { attachment ->
        FullScreenAttachmentViewer(
            attachment = attachment,
            onDismiss = { viewingAttachment = null },
            onDownload = { viewModel.downloadAttachment(attachment) }
        )
    }
}