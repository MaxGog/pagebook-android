package ru.maxgog.pagebook.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.LocalImageLoader
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.domain.model.Attachment
import ru.maxgog.pagebook.domain.model.Tag
import ru.maxgog.pagebook.ui.components.AttachmentImageLoader
import ru.maxgog.pagebook.ui.components.AttachmentPreviewList
import ru.maxgog.pagebook.ui.components.CreateTagDialog
import ru.maxgog.pagebook.ui.components.FullScreenAttachmentViewer
import ru.maxgog.pagebook.ui.components.TagChip
import ru.maxgog.pagebook.ui.components.TagPickerDialog
import ru.maxgog.pagebook.ui.components.rememberCameraPicker
import ru.maxgog.pagebook.ui.components.rememberFilePicker
import ru.maxgog.pagebook.ui.components.rememberPhotoPicker

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalRichTextApi::class)
@Composable
fun NoteEditScreen(
    onBack: () -> Unit,
    viewModel: NoteEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allTags by viewModel.allTags.collectAsStateWithLifecycle()
    val taskCreating by viewModel.taskCreating.collectAsStateWithLifecycle()
    val richTextState = rememberRichTextState()

    val pickImage = rememberPhotoPicker { uri -> viewModel.attachUri(uri) }
    val pickFile = rememberFilePicker { uri -> viewModel.attachUri(uri) }
    val pickCamera = rememberCameraPicker(
        onPicked = { uri -> viewModel.attachUri(uri) }
    )

    var showTagPicker by remember { mutableStateOf(false) }
    var showCreateTag by remember { mutableStateOf(false) }
    var editingTag by remember { mutableStateOf<Tag?>(null) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var viewingAttachment by remember { mutableStateOf<Attachment?>(null) }

    val currentNote = (uiState as? NoteEditUiState.Editing)?.note
    val currentTags = when (val s = uiState) {
        is NoteEditUiState.Editing -> s.tags
        is NoteEditUiState.NewNote -> s.selectedTags
        else -> emptyList()
    }
    val currentAttachments = when (val s = uiState) {
        is NoteEditUiState.Editing -> s.attachments
        is NoteEditUiState.NewNote -> s.pendingAttachments
        else -> emptyList()
    }
    val isLoading = uiState is NoteEditUiState.Loading
    val isNewNote = uiState is NoteEditUiState.NewNote
    val canCreateTask = currentNote != null && !taskCreating

    var titleState by remember(currentNote?.id) { mutableStateOf(currentNote?.title ?: "") }

    LaunchedEffect(currentNote?.id) {
        currentNote?.let { richTextState.setHtml(it.content) }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                NoteEditEvent.Saved, NoteEditEvent.Deleted -> onBack()
            }
        }
    }

    val saveEnabled = titleState.isNotBlank() &&
            richTextState.annotatedString.isNotEmpty() &&
            !isLoading

    CompositionLocalProvider(LocalImageLoader provides AttachmentImageLoader) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isNewNote) stringResource(R.string.new_note)
                            else stringResource(R.string.edit_note),
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
                        IconButton(
                            onClick = { viewModel.save(titleState, richTextState.toHtml()) },
                            enabled = saveEnabled
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.save),
                                tint = if (saveEnabled) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
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
                                onDismissRequest = { showMoreMenu = false }
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
                                if (currentNote != null) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.create_task)) },
                                        onClick = {
                                            showMoreMenu = false
                                            viewModel.createTaskFromCurrentNote()
                                        },
                                        enabled = canCreateTask,
                                        leadingIcon = {
                                            if (taskCreating) {
                                                CircularProgressIndicator(
                                                    Modifier.size(20.dp),
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Icon(Icons.Default.AddTask, contentDescription = null)
                                            }
                                        }
                                    )
                                    HorizontalDivider()
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
            },
            bottomBar = { FormattingToolbar(state = richTextState) },
            contentWindowInsets = WindowInsets(0)
        ) { padding ->
            when {
                isLoading -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    Alignment.Center
                ) { CircularProgressIndicator() }

                uiState is NoteEditUiState.Error -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    Alignment.Center
                ) {
                    Text(
                        text = (uiState as NoteEditUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                ) {
                    BasicTextField(
                        value = titleState,
                        onValueChange = { titleState = it },
                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 8.dp),
                        decorationBox = { inner ->
                            Box {
                                if (titleState.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.title),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                                inner()
                            }
                        }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
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

                    if (currentAttachments.isNotEmpty()) {
                        AttachmentPreviewList(
                            attachments = currentAttachments,
                            onRemove = viewModel::removeAttachment,
                            onDownload = viewModel::downloadAttachment,
                            modifier = Modifier.padding(bottom = 8.dp),
                            onImageClick = { viewingAttachment = it }
                        )
                    }

                    RichTextEditor(
                        state = richTextState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.content),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    )
                }
            }
        }
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
            onDismiss = { showTagPicker = false },
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

@Composable
fun FormattingToolbar(
    state: RichTextState,
    modifier: Modifier = Modifier
) {
    val currentStyle = state.currentSpanStyle
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 3.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.navigationBars.union(WindowInsets.ime)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToolbarToggleButton(
                icon = Icons.Default.FormatBold,
                checked = currentStyle.fontWeight == FontWeight.Bold,
                onClick = {
                    state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
                }
            )
            ToolbarToggleButton(
                icon = Icons.Default.FormatItalic,
                checked = currentStyle.fontStyle == FontStyle.Italic,
                onClick = {
                    state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
                }
            )
            ToolbarToggleButton(
                icon = Icons.Default.FormatUnderlined,
                checked = currentStyle.textDecoration == TextDecoration.Underline,
                onClick = {
                    state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                }
            )
            ToolbarToggleButton(
                icon = Icons.Default.FormatStrikethrough,
                checked = currentStyle.textDecoration == TextDecoration.LineThrough,
                onClick = {
                    state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                }
            )
            ToolbarToggleButton(
                icon = Icons.Default.FormatSize,
                checked = currentStyle.fontSize == 24.sp,
                onClick = {
                    state.toggleSpanStyle(SpanStyle(fontSize = 24.sp))
                }
            )
        }
    }
}

@Composable
private fun ToolbarToggleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onClick: () -> Unit
) {
    IconToggleButton(
        checked = checked,
        onCheckedChange = { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (checked) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}