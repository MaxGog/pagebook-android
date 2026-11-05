# Changelog

Все значимые изменения проекта документируются в этом файле.

Формат основан на [Keep a Changelog](https://keepachangelog.com/ru/1.1.0/),
проект придерживается [Semantic Versioning](https://semver.org/lang/ru/).

## [Unreleased]

### Added

**Notes — Domain Layer**
- **Domain Layer для заметок**: `Note`, `NoteRepository` (интерфейс), Use Cases
  (`GetAllNotesUseCase`, `GetNoteByIdUseCase`, `SaveNoteUseCase`, `DeleteNoteUseCase`).
- **Mapper** `Note ↔ NoteModel` (`data/mapper/NoteMapper.kt`).
- **UI State** для экранов заметок: `NotesUiState`, `NoteEditUiState`
  (`Loading`/`NewNote`/`Editing`/`Error`).
- **Адаптивная сетка заметок**: `StaggeredGridCells.Adaptive(minSize = 160.dp)`.

**Tasks — Clean Architecture и переименование Todo → Task**
- **Переименование модуля**: `TodoModel` → `TaskModel`, `TodoDao` → `TaskDao`,
  `TodoRepository` → `TaskRepository`, `TodoModule` → `TaskModule`,
  `TodoScreen` → `TaskListScreen`, `TodoItem` → `TaskItem`,
  `TodoViewModel` → `TaskListViewModel`, `TodoDestination` → `TaskListDestination`.
- **Domain Layer**: `Task`, `TaskRepository` (интерфейс), Use Cases
  (`GetAllTasksUseCase`, `GetTaskByIdUseCase`, `SaveTaskUseCase`,
  `DeleteTaskUseCase`, `CreateTaskFromNoteUseCase`).
- **Mapper** `Task ↔ TaskModel` (`data/mapper/TaskMapper.kt`).
- **UI State**: `TaskListUiState`, `TaskEditUiState`
  (`Loading`/`NewTask`/`Editing`/`Error`).
- **Экран редактирования** `TaskEditScreen` + `TaskEditViewModel`
  с навигацией через `TaskEditDestination(taskId: Int?)`.
- **Связь Task ↔ Note** через колонку `NoteId` с `Index("NoteId")`
  в `TaskModel`.
- **Авто-извлечение дат** из текста заметки через
  **ML Kit Entity Extraction** (`TYPE_DATE_TIME`) — `CreateTaskFromNoteUseCase`.
- **Сортировка задач**: незавершённые сверху (`IsCompleted ASC`),
  внутри группы — новые первыми (`AtCreated DESC`).

**Calendar — Clean Architecture**
- **Domain Layer для событий**: `Event`, `EventRepository` (интерфейс),
  Use Cases (`GetAllEventsUseCase`, `SaveEventUseCase`, `DeleteEventUseCase`).
- **Mapper** `Event ↔ EventModel` (`data/mapper/EventMapper.kt`).
- **UI State**: `CalendarUiState` (data class) — собирается через `combine`
  из списка событий, выбранной даты и флага диалога.
- **`CalendarScreen`** переехал в `ui/calendar/` и полностью переписан:
  больше нет закомментированного кода и устаревшего `SetTransparentSystemBars`.
- **Реактивность**: `EventDao.getAllEvents()` теперь возвращает
  `Flow<List<EventModel>>` вместо `suspend List`.
- **`@Update`** в `EventDao` — редактирование событий.
- **Автоскролл** к месяцу выбранной даты через
  `LaunchedEffect(uiState.selectedDate)`.

**Tags — сквозная система цветных тегов**
- **Domain-модели**: `Tag` (`id`, `name`, `colorHex`, `emoji`) и `TagColor` (enum из 7
  цветов macOS: `RED`, `ORANGE`, `YELLOW`, `GREEN`, `BLUE`, `PURPLE`, `GRAY`).
- **`TagRepository`** (интерфейс) + `TagRepositoryImpl` (реализация) + `TagMapper`.
- **`TagModel`** (`@Entity tableName = "tags"`, колонка `Emoji`).
- **Junction-таблицы** для many-to-many:
  `NoteTagCrossRef`, `TaskTagCrossRef`, `EventTagCrossRef`
  (составной PK, `onDelete = CASCADE` на обе стороны).
- **`TagDao`**: CRUD для тегов, `hasTagOn{Note,Task,Event}`, SQL-запросы
  через `INNER JOIN`, `@Transaction`-обёртки `getNoteWithTags`/`getTaskWithTags`/
  `getEventWithTags`.
- **`NoteWithTags`, `TaskWithTags`, `EventWithTags`** — `@Relation` + `@Junction`
  для загрузки сущности вместе с тегами одним запросом (без N+1).
- **Use Cases** в `domain/usecase/tag/`:
  - `GetAllTagsUseCase`, `CreateTagUseCase` (`name, color, emoji`),
    `UpdateTagUseCase` (`name, color, emoji`), `DeleteTagUseCase`.
  - `GetTagsForNoteUseCase`, `GetTagsForTaskUseCase`, `GetTagsForEventUseCase`.
  - `ToggleTagOnNoteUseCase`, `ToggleTagOnTaskUseCase`, `ToggleTagOnEventUseCase`
    (возвращают `Boolean` — был ли тег добавлен).
- **UI-компоненты** в `ui/components/`:
  - `TagChip` — `FilterChip` с эмодзи (или цветным кружком) и опциональной кнопкой ×.
  - `CompactTagChip` — компактная плашка `"🎯 Цели"`.
  - `CreateTagDialog` — диалог с превью, полем имени, кнопкой «Добавить эмодзи», палитрой 7 цветов.
  - `TagPickerDialog` — выбор тегов для сущности с кнопкой «Создать тег».
  - **`EmojiCatalog`** — 200+ эмодзи в 10 категориях (`@StringRes` для названий).
  - **`EmojiPickerDialog`** — `FilterChip`-категории + `LazyVerticalGrid` эмодзи.
- **Интеграция в Notes/Tasks/Calendar**: кнопка 🏷️, чипы под заголовком/описанием,
  `TagPickerDialog` + `CreateTagDialog`, до 3 `CompactTagChip` на карточках.
- **`_pendingTags` / `selectedTags`** — накопление тегов для новой
  (ещё не сохранённой) сущности; привязка после `save*`, когда Room
  сгенерировал `id`.

**Tags — фильтрация списков по тегам (Notes / Tasks / Calendar)**
- **Новый компонент `TagFilterRow`** (`ui/components/`) — горизонтальный
  `LazyRow` из `FilterChip` (переиспользует `TagChip` с `selected = true`).
  В конце ряда — `AssistChip` **«Сбросить»** (`clear_filters`), появляется
  только при активном фильтре. Не рендерится, если `allTags` пуст.
- **`_selectedTagIds: MutableStateFlow<Set<Int>>`** в `NotesViewModel`,
  `TaskListViewModel`, `CalendarViewModel` + `allTags: StateFlow<List<Tag>>`
  из `GetAllTagsUseCase`. Публичные методы `toggleTagFilter(tagId)` и
  `clearTagFilter()`.
- **OR-логика фильтрации** — сущность проходит, если у неё есть **хотя бы
  один** из выбранных тегов; пустой `Set` = без фильтра. Реализовано через
  `combine(getAll*(), _selectedTagIds)` — **без новых DAO-запросов**,
  фильтрация в памяти по уже загруженным `@Relation tags`.
- **`CalendarUiState.datesWithEvents: Set<LocalDate>`** — вычисляется
  из **отфильтрованного** списка событий. Точки-индикаторы на днях
  календаря автоматически сужаются под фильтр. **Заодно исправлен баг**:
  раньше точка рисовалась только на выбранном дне, а не на всех днях
  с событиями.
- **EmptyState при активном фильтре** — новая строка
  `R.string.no_matching_items` («Ничего не найдено» / «Nothing found»)
  вместо «Нет заметок / задач / событий», чтобы пользователь понимал,
  что дело в фильтре, а не в отсутствии данных.
- **Новые строки** (`strings.xml` ru/en): `no_matching_items`,
  `clear_filters`.
- **Затронутые экраны**: `NotesScreen`, `TaskListScreen`, `CalendarScreen`
  — над списком добавлен `Column` с `TagFilterRow`.

**Tags — persist-фильтр, OR/AND, badge, редактирование и счётчик использования**
- **`TagFilterState`** (`domain/model/`):
  `data class TagFilterState(tagIds: Set<Int>, mode: FilterMode)` с
  `isEmpty`/`count`. **`FilterMode`** — enum `OR`/`AND`.
  **`TagFilterModule`** — enum `NOTES`/`TASKS`/`CALENDAR`.
- **`TagFilterRepository`** (интерфейс) + **`TagFilterRepositoryImpl`**
  (`@Singleton`, `DataStore<Preferences>`):
  - `getFilter(module): Flow<TagFilterState>`;
  - `setFilter(module, state)`;
  - `clearFilter(module)`.
  - Ключи `tag_filter_<module>_ids` и `tag_filter_<module>_mode`.
  - **Persist-фильтр переживает перезапуск приложения**, отдельно для
    каждого модуля.
- **`applyTagFilter`** — общая утилита в **`domain/util/TagFilterExt.kt`**:
  ```kotlin
  fun <T> List<T>.applyTagFilter(
      filter: TagFilterState,
      tagsOf: (T) -> List<Tag>
  ): List<T>
  ```
  Устраняет дублирование OR/AND между тремя ViewModel, тестируется один раз.
- **`FilterMode.OR` / `FilterMode.AND`** в фильтре:
  - **OR** (по умолчанию) — сущность проходит, если есть **хотя бы один**
    из выбранных тегов;
  - **AND** — должны присутствовать **все** выбранные теги;
  - переключение: **тап на чип `OR/AND`** в начале `TagFilterRow` **или
    long-press на любом теге** (`TagChip.onLongClick` → `toggleFilterMode`).
- **`FilterBadgeIcon`** (`ui/components/`) — `IconButton` с `BadgedBox`
  в `TopAppBar` всех трёх главных экранов:
  - показывает `filterState.count` (число выбранных тегов, `99+` при > 99);
  - тап скрывает/показывает `TagFilterRow` (экономит вертикальное
    место, когда фильтр не активен).
- **Редактирование тегов**:
  - **`CreateTagDialog(initialTag: Tag? = null)`** — двухрежимный диалог:
    создание / редактирование. В режиме редактирования заголовок
    `edit_tag`, кнопка `save`, строка `tag_usage_format`.
  - **`TagEditViewModel`** (`@HiltViewModel`, `ui/components/`) —
    подгружает `TagUsage` через `GetTagUsageUseCase`, отдаёт в диалог
    через `usage: StateFlow<TagUsage?>`.
  - **`TagChip.onEdit`** — иконка карандаша в `trailingIcon`, показывается
    только если передан колбэк.
  - **`TagPickerDialog.onEditTag`** — опциональный колбэк, открывает
    `CreateTagDialog(initialTag = tag, …)` поверх пикера.
- **Счётчик использования тега**:
  - **`TagUsage`** (`domain/model/`): `notes` / `tasks` / `events` + `total`.
  - **`TagDao.getTagUsage(tagId): TagUsageRow`** — один SQL-запрос
    с 3 `COUNT(*)` по junction-таблицам `note_tag_cross_ref` /
    `task_tag_cross_ref` / `event_tag_cross_ref`.
  - **`GetTagUsageUseCase`** (`domain/usecase/tag/`).
  - Отображается в `CreateTagDialog` как
    `Заметок: N · Задач: N · Событий: N`.

**Emoji picker — поиск по эмодзи**
- **`EmojiCatalog.search(query, categoryNames)`** (`ui/components/EmojiCatalog.kt`) —
  чистая функция без Compose/Context, тестируется без Android-зависимостей.
- **4 канала матчинга** одновременно:
  1. **Сам символ** — пользователь может вставить `🎯` из буфера обмена.
  2. **Ключевые слова** `ru/en` из `Map<String, List<String>> keywords` —
     `"серд"` → все сердца, `"работа"` → 💼, `"cat"` → 🐱 (если появится).
  3. **Unicode-имя** через `Character.getName(codePoint)` — fallback,
     покрывает даже эмодзи без явных ключевых слов (`"grinning"`, `"face"`).
  4. **Название категории** — `categoryNames` передаются с UI
     (`stringResource(cat.nameRes)`), поэтому запрос `"работа"` тянет
     всю категорию Work.
- **Поле поиска** (`OutlinedTextField` с `Icons.Default.Search` и кнопкой
  `Clear`) вверху `EmojiPickerDialog`. Пока поле не пусто —
  категории-чипы и «Недавние» **скрываются**, показывается только сетка
  результатов.
- **Пустой результат** → новая строка `R.string.no_emojis_found`
  («Эмодзи не найдены» / «No emojis found»).

**Emoji picker — недавние эмодзи**
- **`RecentEmojiRepository`** (Domain, interface) — `getRecentEmojis(): Flow<List<String>>`
  + `addRecentEmoji(emoji)`.
- **`RecentEmojiRepositoryImpl`** (Data, `@Singleton`, Hilt) на `DataStore<Preferences>`:
  - ключ `recent_emojis`, сериализация через `joinToString("|")`
    (разделитель безопасен — эмодзи не содержат `|`);
  - **до 20** записей, **дедупликация** (выбранный эмодзи перемещается
    в начало), свежие — первыми.
- **`EmojiPickerViewModel`** (`ui/components/`, `@HiltViewModel`):
  ```kotlin
  val recentEmojis: StateFlow<List<String>> = recentEmojiRepository
      .getRecentEmojis()
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  fun onEmojiSelected(emoji: String) {
      viewModelScope.launch { recentEmojiRepository.addRecentEmoji(emoji) }
  }
  ```
- **Секция «Недавние»** над категориями в `EmojiPickerDialog` (заголовок
  `R.string.recent_emojis` + `LazyRow`). Секция **не рендерится**,
  если список пуст — не засоряет UI у нового пользователя.
- При выборе любого эмодзи (из «Недавних», категории или поиска) —
  `viewModel.onEmojiSelected(emoji)` **перед** `onEmojiSelected(emoji)`,
  поэтому список обновится к следующему открытию пикера.
- Новые строки (`strings.xml` ru/en): `search_emoji`, `recent_emojis`,
  `no_emojis_found`.

**Attachments — вложения для Notes и Tasks**
- **Domain-модели**: `Attachment` (`id`, `ownerType`, `ownerId`, `type`,
  `localPath`, `mimeType`, `displayName`, `sizeBytes`, `createdAt`),
  `AttachmentType` (`IMAGE`/`VIDEO`/`AUDIO`/`FILE`),
  `OwnerType` (`NOTE`/`TASK`).
- **`AttachmentRepository`** (интерфейс) + `AttachmentRepositoryImpl` + `AttachmentMapper`.
- **`AttachmentEntity`** — полиморфная связь через `ownerType` + `ownerId`,
  `Index("ownerType", "ownerId")`.
- **`AttachmentDao`** — `getForOwner`, `getAll`, `insert`, **`update`**, `delete`.
- **Use Cases** в `domain/usecase/attachment/`:
  - `SaveAttachmentUseCase` — сохраняет файл в app-private storage
    (`AttachmentFileStore`), возвращает `Attachment` с `id`.
  - `GetAttachmentsForNoteUseCase`, `GetAttachmentsForTaskUseCase`
    (возвращают `Flow<List<Attachment>>`).
  - `DeleteAttachmentUseCase` — удаляет и запись, и файл с диска.
  - `DownloadAttachmentUseCase` — экспорт в `Pictures`/`Downloads` через
    `MediaStoreDownloader` (`ContentResolver.insert`).
- **Data-слой**:
  - **`AttachmentFileStore`** — app-private storage в `filesDir/attachments/`,
    сохраняет через UUID + расширение, **возвращает относительный путь**.
  - **`MediaStoreDownloader`** — копирует файл в `MediaStore.Images/Video/Audio/Downloads`
    (`@RequiresApi(Q)`), `RELATIVE_PATH = DIRECTORY_DOWNLOADS` для `FILE`.
- **Hilt-модуль** `AttachmentsModule` (`@Binds` + `@Singleton`).
- **UI-компоненты**:
  - **`AttachmentPreviewList`** — `FlowRow` с превью изображений и
    `AssistChip` для файлов, кнопки «Удалить» и «Скачать».
  - **`AttachmentImageLoader`** — реализует `LocalImageLoader` для
    RichEditor, чтобы `<img src="...">` показывали изображения из
    app-private storage.
  - **`rememberPhotoPicker`** — обёртка над `PickVisualMedia` (Photo Picker).
  - **`rememberFilePicker`** — обёртка над `OpenDocument` (`*/*`).
  - **`ResizeImageDialog`** — Slider ширины с сохранением пропорций.
- **Интеграция в Notes/Tasks**: кнопки «Фото» и «Файл» в `TopAppBar`
  (активны и для сохранённых, и для новых сущностей); превью под
  заголовком/тегами; pending-вложения для новых сущностей привязываются
  к `ownerId` после `save*` через **`updateAttachment`**.

**Attachments — камера и fullscreen-просмотр**
- **`rememberCameraPicker`** (`ui/components/CameraPicker.kt`) — обёртка над
  `ActivityResultContracts.TakePicture()`.
  - Временный снимок пишется в `cacheDir/camera_<timestamp>.jpg`.
  - Отдаётся системной камере через **`FileProvider`**
    (`authority = "${packageName}.fileprovider"`), объявленный в
    `AndroidManifest.xml` + `res/xml/file_paths.xml`
    (`<cache-path name="camera_cache" path="." />`).
  - При `success = true` → `onPicked(contentUri)` → `attachUri()` →
    `SaveAttachmentUseCase` копирует содержимое в `filesDir/attachments/`.
    Кэш-файл вычищается системой.
  - При `success = false` пустой файл удаляется, `pendingFile` сбрасывается.
  - **`CAMERA` permission не нужен** — `ACTION_IMAGE_CAPTURE` выполняет
    отдельное приложение камеры.
- **`FullScreenAttachmentViewer`** (`ui/components/FullScreenAttachmentViewer.kt`) —
  полноэкранный просмотр изображения-вложения:
  - `Dialog(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)`.
  - **pinch-to-zoom** (1×..5×) через `rememberTransformableState`; панорамирование
    двумя пальцами только при `scale > 1`, при 1× `offset` сбрасывается в
    `Offset.Zero`.
  - **double-tap** через `detectTapGestures(onDoubleTap = …)` — 1× ↔ 2.5×.
  - Верхняя панель (`Surface` + `windowInsetsPadding(WindowInsets.statusBars)`):
    закрыть / имя файла / скачать (`downloadAttachment` из ViewModel).
  - Bitmap кэшируется через `remember(attachment.localPath)`.
  - Путь разрешается относительно `filesDir/attachments` (см. правило
    про относительный `localPath`).
- **`AttachmentPreviewList`** — новый параметр
  `onImageClick: ((Attachment) -> Unit)? = null`; тап по превью (не по
  крестику/скачиванию) открывает fullscreen.
- **`NoteEditScreen` / `TaskEditScreen`** — пункт **«Сделать фото»**
  (`Icons.Default.PhotoCamera`) в `DropdownMenu` `showMoreMenu`; вызов
  `pickCamera()`. `var viewingAttachment by remember { mutableStateOf<Attachment?>(null) }`
  + `FullScreenAttachmentViewer` в конце композиции.
- **`AndroidManifest.xml`** — `<provider>` для
  `androidx.core.content.FileProvider`
  (`authorities = "${applicationId}.fileprovider"`, `exported = false`,
  `grantUriPermissions = true`).
- **`res/xml/file_paths.xml`** — новый файл, `<cache-path name="camera_cache" path="." />`.
- **Строки** (`strings.xml` ru/en): `take_photo`, `close`, `download`,
  `attachment_load_error`.

**Единая база данных**
- **`PageBookDatabase`** — единая `@Database` с **8 сущностями**
  (`NoteModel`, `TaskModel`, `EventModel`, `TagModel`, `NoteTagCrossRef`,
  `TaskTagCrossRef`, `EventTagCrossRef`, `AttachmentEntity`), **`version = 4`**.
- Заменяет три отдельные БД — необходимое условие для `ForeignKey` между
  сущностями и тегами.
- **`MIGRATION_2_3`** — `ALTER TABLE tags ADD COLUMN Emoji TEXT`.
- **`MIGRATION_3_4`** — создание таблицы `attachments` с индексами
  (в текущей сборке используется `fallbackToDestructiveMigration()`,
  миграция — в roadmap).

**Настройки (DataStore + AppCompatDelegate)**
- **`SettingsRepository`** — `Flow<ThemeMode>`, `setThemeMode()`. Хранит тему в `DataStore<Preferences>`.
- **`DataStoreModule`** (Hilt) — singleton `DataStore<Preferences>`.
- **`ThemeViewModel`** — подписка `MainActivity` на `themeMode` для реактивного переключения темы.
- **`AppLanguage`** (enum с `tag: String?` и `@StringRes labelRes`):
  `current()` читает из `AppCompatDelegate`, `apply()` устанавливает локаль.
- **`SettingsViewModel.setLanguage()`** — вызывает только `AppLanguage.apply()`.
- **Material 3 Expressive UI**:
  - Группы настроек в `Card` с `RoundedCornerShape(28.dp)`.
  - `ListItem` с `Color.Transparent` внутри групп.
  - `supportingContent` с пояснениями (`theme_system_desc`, `language_ru_desc` и т.д.).
  - Заголовки разделов через `SettingsSection` (`titleSmall`, `primary`).
- **`OverflowMenu`** (три точки) в `TopAppBar` всех главных экранов (Tasks, Notes, Calendar) с пунктом «Настройки».

**Создание задач из заметок (UI)**
- **Кнопка `AddTask` в `TopAppBar` `NoteEditScreen`** — только для сохранённой заметки.
- **`NoteEditViewModel.createTaskFromCurrentNote()`** — вызывает `CreateTaskFromNoteUseCase`, привязывает `noteId`, сохраняет через `SaveTaskUseCase`.
- **`taskCreating: StateFlow<Boolean>`** — `CircularProgressIndicator` в кнопке + disabled.
- **`snackbarMessageRes: StateFlow<Int?>`** — `@StringRes` для локализации Snackbar
  (`task_created`, `task_created_with_reminder`, `task_creation_error`).
- **`stripHtml` в `CreateTaskFromNoteUseCase`** — HTML очищается перед ML Kit.
- **`EntityExtractor` кеширован** (`by lazy` + `@Singleton`).

**Навигация и адаптивный UI**
- **Type-safe navigation** через `@Serializable` destinations.
- **`hasRoute<T>()`** вместо сравнения строк.
- **`NavigationSuiteScaffold`** (`material3-adaptive-navigation-suite`) —
  автоматически переключает `NavigationBar` ↔ `NavigationRail` по `WindowSizeClass`.
  Управление видимостью через `scaffoldState.hide()/show()`.
- **`ListDetailPaneScaffold`** для `TaskListDestination` при `WindowWidthSizeClass.Expanded`:
  `TaskListDetailPane` показывает список и редактор рядом (планшеты/Chromebook).
- **`calculateWindowSizeClass(this)`** в `MainActivity` → `CombinedApp`.
- **One-Shot Events** через `Channel<T>(Channel.BUFFERED)` + `receiveAsFlow()`
  для `TaskEditEvent`, `NoteEditEvent` (`Saved`, `Deleted`). Заменяет
  `StateFlow<Boolean> isSaved` — событие доставляется **ровно один раз**.
- **`TaskListDetailPane`**: `rememberListDetailPaneScaffoldNavigator<TaskEditDestination>()`,
  `navigator.navigateTo(Detail, TaskEditDestination(id))` (в `scope.launch`).
- **`TaskEditViewModel.setTaskId(id)`** — для detail-pane, когда `SavedStateHandle` пуст.

**Google Keep-style Note Editor**
- **`BasicTextField` без рамки** для заголовка (`headlineSmall`, placeholder через `decorationBox`).
- **Теги в `FlowRow`** — переносятся на новую строку, без `Card`.
- **`RichTextEditor` на `weight(1f)`** — занимает всё оставшееся пространство.
- **`FormattingToolbar`** использует `windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))` — тулбар **всегда над клавиатурой** (и над навбаром, когда клавиатура закрыта).

**Зависимости**
- `kotlinx-serialization-json`, `kizitonwose.calendar:compose`,
  `com.google.mlkit:entity-extraction` (beta), `kotlinx-coroutines-play-services`,
  Room Gradle plugin, `androidx.datastore:datastore-preferences`,
  `androidx.appcompat:appcompat`, **`material3-adaptive-navigation-suite`**,
  **`material3-adaptive` / `adaptive-layout` / `adaptive-navigation`**,
  **`material3-window-size-class`**, `com.mohamedrejeb.richeditor:richeditor-compose`.

**Документация**
- `ARCHITECTURE.md`, `CONTRIBUTING.md`, `CHANGELOG.md`, `README.md` (актуализированы).
- **`ARCHITECTURE.md`**: подразделы **«🔍 Фильтрация списков по тегам»**
  (TagFilterRow, OR/AND, persist, badge), **«✏️ Редактирование тегов
  и счётчик использования»** (CreateTagDialog в режиме edit, TagEditViewModel,
  TagUsage) и **«🔎 Emoji picker: поиск и недавние»** (4 канала матчинга,
  `RecentEmojiRepository` на DataStore, UI-поток); **новые подразделы
  «📷 Камера как источник изображения»** (TakePicture + FileProvider)
  **и «🖼 Fullscreen-просмотр вложений»** (pinch-to-zoom, double-tap);
  новые mermaid-диаграммы «Снимок с камеры → вложение»,
  «Fullscreen-просмотр вложения», «Фильтрация списка по тегу (persist + OR/AND)»,
  «Редактирование тега с usage» и «Выбор эмодзи с поиском и недавними»;
  обновлены «Ключевые архитектурные решения» (новые строки про FileProvider,
  TakePicture, fullscreen viewer, rememberTransformableState) и roadmap.
- **`README.md`**: раздел «🏷️ Сквозные теги: полный цикл» (плюс блок
  «Выбор эмодзи» про поиск и недавних), новый раздел «✏️ Редактирование
  тегов и счётчик использования», **новый раздел «🔎 Emoji picker:
  поиск и недавние»**, **раздел «📷 Камера и 🖼 fullscreen-просмотр»**,
  обновлённый стек (Camera / Fullscreen viewer), `CameraPicker` /
  `FullScreenAttachmentViewer` в структуре проекта, новые пункты в roadmap.
- **`CHANGELOG.md`**: добавлены блоки про persist / OR-AND / badge /
  edit / usage / emoji search / recent emojis / **camera / fullscreen viewer**,
  соответствующие пункты перенесены из `Planned` в `Added`.

### Changed

**Gradle-конфигурация**
- AGP `9.4.1`, Kotlin `2.2.20`, KSP `2.2.20-2.0.4`, Compose BOM `2026.09.00`.
- `compileSdk = 37`, `targetSdk = 36`, `minSdk = 28`, JDK 21.
- Удалён устаревший `buildscript`.
- `kotlinOptions` → `kotlin { compilerOptions { ... } }`.
- `buildConfig = true` — для `BuildConfig.VERSION_NAME`.
- `android:localeConfig="@xml/locales_config"` в `AndroidManifest.xml` (`ru`, `en`).

**Data Layer**
- **Репозитории** разделены на интерфейс (`domain/repository/`) и реализацию (`data/repository/`).
- **Hilt**: единый **`RepositoryModule`** с `@Binds` + отдельный **`AttachmentsModule`**
  (`@Binds AttachmentRepository`). Добавлены `@Binds TagFilterRepository`
  и **`@Binds RecentEmojiRepository`**.
- **`NoteDao` / `TaskDao` / `EventDao`**: добавлены `@Transaction getAllWithTags()`,
  `getWithTagsById()`; `insert()` возвращает `Long`; сортировки (`AtCreated DESC`,
  `IsCompleted ASC, AtCreated DESC`, `date ASC, time ASC`).
- **`AttachmentDao`**: добавлен `@Update suspend fun update(entity)` — для
  перепривязки pending-вложений (`ownerId = 0 → savedId`) без повторного `insert`.
- **`TagDao`**: добавлен `getTagUsage(tagId): TagUsageRow` — 3 `COUNT(*)`
  по junction-таблицам одним запросом.
- **`PageBookDatabase`** — `version = 4`, `entities` включает `AttachmentEntity`.
- **Доменные модели** `Note`, `Task`, `Event` получили `tags: List<Tag> = emptyList()`.
- **Мапперы** перегружены: `toDomain(tags: List<Tag>)`, `WithTags.toDomain()`;
  добавлен `AttachmentMapper` (`Entity ↔ Domain`).
- **`SettingsRepository`**: убран `LANGUAGE_KEY`, `language: Flow<...>`,
  `setLanguage()` — язык теперь хранится **только** в `AppCompatDelegate`.
- **`DataStore<Preferences>`** теперь хранит **и тему, и фильтры тегов**
  (`tag_filter_<module>_ids` / `tag_filter_<module>_mode`),
  **и недавние эмодзи** (`recent_emojis`).
- **`EmojiCatalog`** (`ui/components/`) — добавлены `Map<String, List<String>> keywords`
  (покрытие всех эмодзи каталога на `ru`/`en`), `fun search(query, categoryNames): List<String>`
  и приватный `unicodeName(emoji): String?` (через `Character.getName`).
  `object` остаётся свободным от Compose/Android-зависимостей.

**UI**
- **Экраны** используют `contentPadding` вместо `Modifier.padding` (edge-to-edge).
- **`collectAsStateWithLifecycle()`** вместо `collectAsState()`.
- **`NoteItem` / `TaskItem` / `EventItem`**: `shapes.large`, `FlowRow` для тегов (все, а не `take(3)`).
- **`NoteItem`**: `Text(AnnotatedString.fromHtml(note.content))` — HTML теги
  (`<p>`, `<br>`, `&nbsp;`) больше не видны в карточке.
- **`TaskEditScreen` / `NoteEditScreen`**: `ContentDescription` через `stringResource`.
- **`CalendarScreen`**: добавлен `CenterAlignedTopAppBar` с `OverflowMenu`.
- **`NotesScreen`**: `IconButton(Settings)` → `OverflowMenu`.
- **`TaskListScreen`**: + `onSettingsClick`, `OverflowMenu` в `TopAppBar`.
- **`SettingsScreen`**: `SettingsGroup` (Card 28dp) + `ListItem` с прозрачным фоном;
  секции с `SettingsSection`; `AboutSettings` в `Card`.
- **`AppLanguage`**: `SYSTEM(null)`, `RUSSIAN("ru")`, `ENGLISH("en")`, `@StringRes` названия.
- **`NoteEditScreen` / `TaskEditScreen`**: добавлены кнопки `AddPhotoAlternate`,
  `AttachFile` и **`PhotoCamera`** в `DropdownMenu`; `AttachmentPreviewList`
  под заголовком; **`FullScreenAttachmentViewer` при тапе по превью**.
- **`FormattingToolbar`**: `windowInsetsPadding(navigationBars ∪ ime)` — не уходит под клавиатуру.
- **`TagChip`**: новые параметры `onEdit` и `onLongClick` (иконка карандаша
  в `trailingIcon`, long-press для переключения OR↔AND).
- **`CreateTagDialog`**: двухрежимный — создание / редактирование (`initialTag`),
  строка `tag_usage_format` в режиме редактирования.
- **`TagPickerDialog`**: новый параметр `onEditTag: ((Tag) -> Unit)? = null`.
- **`TagFilterRow`**: новые параметры `mode: FilterMode` и `onToggleMode: () -> Unit`,
  `AssistChip` режима в начале ряда.
- **`NotesScreen` / `TaskListScreen` / `CalendarScreen`**: `FilterBadgeIcon`
  в `actions` `TopAppBar`; `TagFilterRow` скрыт по умолчанию
  (`rememberSaveable`), раскрывается тапом по бейджу или автоматически
  при активном фильтре.
- **ViewModels** (`NotesViewModel`, `TaskListViewModel`, `CalendarViewModel`):
  `filterState: StateFlow<TagFilterState>` (persist через `TagFilterRepository`)
  вместо локального `_selectedTagIds`; методы `toggleTagFilter`, `toggleFilterMode`,
  `clearTagFilter`. Явный `combine<List<T>, TagFilterState, UiState>` для
  корректной работы `.catch { emit(Error(...)) }`.
- **`CalendarViewModel`**: `filterState` добавлен 4-м источником в `combine`;
  `datesWithEvents` вычисляется из отфильтрованного списка.
- **`EmojiPickerDialog`** (`ui/components/`) — переработан:
  - добавлен `EmojiPickerViewModel` (`hiltViewModel()`) для недавних;
  - `OutlinedTextField` поиска + `Icons.Default.Search` / `Clear`;
  - `if (isSearching)` — сетка `searchResults`, иначе — секция «Недавние»
    (если не пусто) + категории + сетка категории;
  - приватный `EmojiCell` (`Box` 44dp + `Text` 26sp) — переиспользуется
    и в недавних, и в поиске, и в категориях;
  - сигнатура Composable не изменилась (`onDismiss`, `onEmojiSelected`),
    `CreateTagDialog` менять не нужно — `hiltViewModel()` подхватит VM из графа.

**Attachments / Camera / Fullscreen**
- **`AttachmentPreviewList`**: новый параметр `onImageClick: ((Attachment) -> Unit)? = null`.
  `ImageThumb` получил `onClick: (() -> Unit)? = null`; тап по картинке
  открывает fullscreen, внутренние `IconButton` (крестик/скачивание)
  перехватывают свои клики (не «протекают» в родителя).
- **`rememberCameraPicker`** — `pendingFile: File?` в state (не `Uri`),
  потому что `TakePicture` возвращает только `Boolean`; `File` нужен
  и для повторного `getUriForFile`, и для `delete()` при отмене.
- **`NoteEditScreen` / `TaskEditScreen`** — `pickCamera` + `viewingAttachment`
  + финальный `viewingAttachment?.let { FullScreenAttachmentViewer(...) }`.
- **`AndroidManifest.xml`** — `<provider>` для `FileProvider`.
- **`res/xml/file_paths.xml`** — новый файл, `<cache-path>` для камеры.
- **`strings.xml`** (ru/en) — новые строки `take_photo`, `close`, `download`,
  `attachment_load_error`.

**Notifications**
- Единая система внутренних уведомлений `AppNotificationManager` (Hilt `@Singleton`)
  на `Channel` + `SharedFlow`-подобный API (`success/error/info/easterEgg`).
- Кастомный `FursonaSnackbar` с аватаром-фурсоной и цветовыми типами
  (SUCCESS/ERROR/INFO/EASTER_EGG) — Material 3 Expressive (`shapes.extraLarge`,
  `tertiaryContainer` для пасхалки).
- `AppNotificationHost` — единственная точка подписки в `MainActivity`,
  гарантирует однократную доставку и прерывает текущий snackbar новым.

**Settings**
- Пасхалка: 7 тапов по номеру версии открывают карточку с фурсоной
  + специальное EASTER_EGG-уведомление.
- Аватар разработчика (`leadingContent`) в `AboutSettings`.

**Локализация (`res/values/strings.xml`, `res/values-ru/strings.xml`)**
- Добавлены для фильтрации: `no_matching_items`, `clear_filters`,
  `filter_tags`, `filter_mode_or_desc`, `filter_mode_and_desc`.
- Добавлены для редактирования тегов: `edit_tag`, `tag_usage_format`.
- **Добавлены для emoji picker**: `search_emoji` («Поиск эмодзи» / «Search emoji»),
  `recent_emojis` («Недавние» / «Recent»), `no_emojis_found`
  («Эмодзи не найдены» / «No emojis found»).
- **Добавлены для камеры и fullscreen**: `take_photo` («Сделать фото» / «Take photo»),
  `close` («Закрыть» / «Close»), `download` («Скачать» / «Download»),
  `attachment_load_error` («Не удалось загрузить изображение» / «Failed to load image»).

**Документация**
- `ARCHITECTURE.md`: добавлены разделы «Адаптивный UI», «One-Shot Events»,
  «Настройки (DataStore + AppCompatDelegate)», **«Подсистема вложений (Attachments)»**
  (включая **«📷 Камера как источник изображения»** и **«🖼 Fullscreen-просмотр вложений»**),
  **«Фильтрация списков по тегам»** (persist, OR/AND, badge),
  **«Редактирование тегов и счётчик использования»**,
  **«🔎 Emoji picker: поиск и недавние»**; обновлены диаграммы (sequence для
  attach, camera, fullscreen viewer, фильтрации, редактирования тега,
  **выбора эмодзи**), БД (`version = 4`, 8 сущностей), roadmap.
- `README.md`: обновлены стек (Adaptive, M3 Expressive, Attachments,
  Tag filtering / Tag usage / Tag editing, **Emoji search / Recent emojis**,
  **Camera / Fullscreen viewer**), новые разделы «🏷️ Фильтрация по тегам»,
  «✏️ Редактирование тегов и счётчик использования», **«🔎 Emoji picker:
  поиск и недавние»**, **«📷 Камера и 🖼 fullscreen-просмотр»**, roadmap.
- `CHANGELOG.md`: актуализирован, добавлены блоки про камеру и fullscreen viewer.
- `CONTRIBUTING.md`: добавлен подраздел про «ловушку» с `localPath`.

### Removed
- `accompanist-systemuicontroller` (deprecated → `enableEdgeToEdge()`).
- Плагин `kotlin-android` (встроен в AGP 9).
- `Screen.kt`, `Routes.kt`, `NotesDestinations.kt` → `Destinations.kt`.
- Старые `data/repositories/{NoteRepository,TodoRepository,EventRepository}.kt`.
- Устаревшие `viewmodels/{Note,NoteEdit,Todo,Calendar}ViewModel.kt`.
- Все файлы `Todo*`.
- `ui/screens/{TodoScreen,CalendarScreen}.kt`.
- Три отдельные БД (`NoteRoomDatabase`, `TaskRoomDatabase`, `EventRoomDatabase`).
- Отдельные Hilt-модули репозиториев (`NotesModule`, `TaskModule`, `EventModule`, `TagModule`).
- `ui/dialogs/` — переехали в `ui/calendar/`.
- **`SettingsRepository.setLanguage()`** и `LANGUAGE_KEY` — язык хранится только в `AppCompatDelegate`.
- **`segmentedListItemColors()`** и `itemShape` — заменены на `Card(28.dp)` + `ListItem(Color.Transparent)`.
- **`_selectedTagIds: MutableStateFlow<Set<Int>>`** во ViewModel — заменён на
  `filterState: StateFlow<TagFilterState>` (persist через `TagFilterRepository`).

### Fixed
- **Нижняя панель навигации** больше не исчезает (`destination.hasRoute<T>()`).
- **Краш `Cannot find startDestination`** — `composable<TaskListDestination>` зарегистрирован.
- **Краш `Destination TaskEditDestination cannot be found`** — добавлен в граф.
- **Краш `Room cannot verify the data integrity`** — версия БД увеличена, `MIGRATION_2_3` / `MIGRATION_3_4`.
- **Краш `[Dagger/DuplicateBindings] TagRepository`** — удалены старые модули.
- **Краш `[Dagger/MissingBinding] EventRepository`** — `RepositoryModule` с `@Binds`.
- **Контент экранов** больше не перекрывается системными барами (`Modifier.padding(innerPadding)`).
- **Конфликт KSP/Kotlin** — версии синхронизированы.
- **Reserved alias** `androidx-material3-window-size-class` → `androidx-material3-window-size`.
- **`Unresolved reference 'kizitonwose'`** — зависимость восстановлена.
- **`setEntityTypesFilter` ML Kit** ожидает `Set<Int>`, а не `List<Int>`.
- **`TaskModel.ForeignKey(NoteModel)`** между разными БД → `Index("NoteId")` + ручная отвязка в `DeleteNoteUseCase`.
- **`EventTagCrossRef` / `TaskTagCrossRef`**: правильные entity (`EventModel`/`TaskModel`), `childColumns = ["tagId"]`.
- **`EventWithTags` / `TaskWithTags`**: `parentColumn = "id"`, `@Embedded val event/task`.
- **`TagDao.getTagsForTask/getTagsForEvent`**: `SELECT t.* FROM tags t` (а не `FROM tasks`/`events`).
- **`DatabaseModule`**: правильные импорты (`dagger.Provides`, `javax.inject.Singleton`), `@Module` + `@InstallIn`.
- **`Save*UseCase`** возвращают `Long` (id сохранённой сущности).
- **`NoteEditViewModel.init`** — убран «висячий» `collect`, экран не застревал в `Loading`.
- **`NoteEditScreen` / `TaskEditScreen`** — `onBack()` через `LaunchedEffect(Unit) { events.collect { ... } }` (нет race condition).
- **`CreateTaskFromNoteUseCase`**: `Entity.TYPE_DATE_TIME` (не `EntityExtraction.EntityType`), `asDateTimeEntity()?.timestampMillis` (только у `DateTimeEntity`).
- **Выбор ближайшей будущей даты** — `mapNotNull { asDateTimeEntity()?.timestampMillis }.filter { > now }.minOrNull()`.
- **`try/catch` вокруг `annotate()`** — при отсутствии модели/сети задача создаётся без напоминания, без краша.
- **`AddEventDialog`: `Unresolved reference 'viewModel'`** — заменено на `onCreateTag(...)` (диалог — чистый Composable без VM).
- **`SettingsViewModel.setLanguage` — Unresolved reference** — метод восстановлен, вызывает только `AppLanguage.apply()`.
- **Дизайн настроек сломался** (`SegmentedListItem` нет в стабильной `material3`) — заменён на `Card(28.dp)` + `ListItem(Color.Transparent)`.
- **`SettingsGroup` без заголовков** — добавлены `SettingsSection` + `supportingContent` с описаниями.
- **`HTML` в карточках заметок** — `Text(note.content)` → `Text(AnnotatedString.fromHtml(note.content))`.
- **`FormattingToolbar` уходил под клавиатуру** — `windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))`.
- **`BuildConfig` не компилировался** — `buildConfig = true` в `build.gradle.kts`.
- **`SegmentListItem` не компилировался** — заменён на стабильные `ListItem` + `Card`.

**Смена языка на Android < 13 — ✅ исправлено**
- `MainActivity` наследуется от **`AppCompatActivity`**.
- Родительская тема **`Theme.PageBook`** (в `themes.xml` и `values-night/themes.xml`)
  мигрирована на **`Theme.AppCompat.DayNight.NoActionBar`** (обязательное условие
  для работы `AppCompatDelegate.setApplicationLocales()` на API < 33).
- В `AndroidManifest.xml` внутри `<application>` добавлен сервис
  **`androidx.appcompat.app.AppLocalesMetadataHolderService`** с
  `<meta-data android:name="autoStoreLocales" android:value="true"/>` —
  автоматическое сохранение/восстановление локали на Android 12 и ниже.
- Проверено: смена языка через `AppLanguage.apply()` работает на API 28–36
  без перезапуска приложения.

**Attachments**
- **Изображения не отображались** (`AttachmentPreviewList`, `AttachmentImageLoader`) —
  `File(attachment.localPath)` искал файл в корне рабочей директории, а
  `AttachmentFileStore` возвращает **относительный** путь. Теперь путь
  разрешается относительно `File(context.filesDir, "attachments")`.
- **Изображения из rich-text не грузились** (`<img src="...">`) — та же причина
  + неверный каст `ImageBitmap as Painter`. Заменено на
    `BitmapPainter(bitmap.asImageBitmap())`.
- **Заметка/задача «не сохранялась» с прикреплённым фото** — в
  `NoteEditViewModel.save()` / `TaskEditViewModel.save()` для pending-вложений
  вызывался повторный `insertAttachment` с уже существующим `id`, что
  приводило к `SQLiteConstraintException`. Заменено на `updateAttachment`
  (новый метод в `AttachmentRepository` + `@Update` в `AttachmentDao`).
- **Ошибки сохранения маскировались** — `catch (_: Exception) { }` в
  `NoteEditViewModel.save` / `TaskEditViewModel.save` заменён на
  `catch (e: Exception) { Log.e(...); _snackbarMessageRes.value = R.string.unknown_error }`.
  Пользователь теперь видит snackbar вместо «зависшего» экрана.
- **«Осиротевшие» pending-вложения** при уходе без сохранения — добавлена
  очистка в `onCleared()` (файл + запись в БД удаляются через `GlobalScope`,
  т.к. `viewModelScope` уже отменён).

**Tags / фильтрация**
- **Точки-индикаторы в календаре** раньше рисовались только на выбранном дне,
  хотя события были и на других. Теперь `datesWithEvents` вычисляется из
  полного (или отфильтрованного) списка событий — точки видны на **всех**
  днях с событиями.
- **Пустой результат фильтра** больше не маскируется под «нет данных» —
  показывается отдельное сообщение «Ничего не найдено».
- **`Type inference failed` в `.catch`** (`NotesViewModel`, `TaskListViewModel`) —
  явные type-аргументы `combine<List<T>, TagFilterState, UiState>` гарантируют,
  что `R = UiState`, а не `UiState.Success`, — `.catch { emit(Error(...)) }`
  компилируется.
- **Подсветка выбранного дня в календаре** — `day.date` из `kizitonwose` имеет
  тип `java.time.LocalDate`, а `uiState.selectedDate`/`datesWithEvents` —
  `kotlinx.datetime.LocalDate`. Сравнение и `in` возвращали `false` / падали
  с `Type inference failed`. Исправлено однократной конвертацией
  `day.date.toKotlinxLocalDate()` в `dayContent`.

**Emoji picker**
- **Поиск чувствителен к локали** — `EmojiCatalog.search()` приводит
  и запрос, и ключевые слова к `lowercase()` (без `Locale.getDefault()`,
  чтобы не зависеть от системной локали — кириллица корректно работает
  в `ru`/`en`).
- **`Character.getName` падает на не-эмодзи** — обёрнут в `try/catch`
  (`IllegalArgumentException`), возвращает `null`, поиск по Unicode-имени
  просто не срабатывает для этого символа.
- **Разделитель в DataStore** — выбран `|`, а не `,`/`;`, т.к. эмодзи
  гарантированно не содержат `|`. Это исключает «разваливание» списка
  на неверные токены и появление «фантомных» эмодзи в секции «Недавние».

**Camera / Fullscreen**
- **`FileProvider` «не видит» файл** — файл в `cacheDir` должен быть создан
  **до** `getUriForFile`, иначе `FileProvider` кинет `IllegalArgumentException`.
  В `rememberCameraPicker` файл создаётся через `createNewFile()` перед
  вызовом `FileProvider`.
- **`TakePicture` не возвращает URI** — храним `pendingFile: File?` в state,
  а не `Uri`; на успехе достаём URI повторно из `File`, на отмене —
  `file.delete()`.
- **Ошибка «Cannot resolve authority»** — `android:authorities` в манифесте
  должен точно совпадать с `"${context.packageName}.fileprovider"`,
  иначе `FileProvider.getUriForFile` кинет `IllegalArgumentException`.
  `authority` лучше писать через `${applicationId}` — совпадёт автоматически.
- **Fullscreen viewer не открывается по тапу** — `Modifier.clickable`
  навешан на сам `Box` превью, а не на `Image`; иначе `Image` без
  `clickable` не получает события. Также `IconButton` крестика/скачивания
  перехватывают свои клики — это желаемое поведение.

### Planned
- **Удаление из недавних эмодзи** — long-press на эмодзи в секции «Недавние»
  (`removeRecentEmoji(emoji)` в репозитории).
- **Кнопка «Очистить недавние»** рядом с секцией (аналог `clear_filters`).
- **`ListDetailPaneScaffold` для Notes и Calendar** (аналогично Tasks).
- **Мультиверсионные заметки для соцсетей** — несколько версий одной заметки
  под разные платформы (`NoteVariant`).
- **`MIGRATION_3_4`** для `attachments` (сейчас `fallbackToDestructiveMigration`).
- Синхронизация с облаком.
- Виджеты для главного экрана.
- Резервное копирование.
- Голосовые заметки.
- Модуляризация (feature-модули).
- Unit-тесты для Use Cases и Repository (в т.ч. `AttachmentRepository`,
  `applyTagFilter`, `TagFilterRepositoryImpl`, **`EmojiCatalog.search`**,
  **`RecentEmojiRepositoryImpl`**).
- WorkManager для напоминаний о задачах.
- Настройка `room.schemaLocation` (Room Gradle plugin уже подключён).

## [0.1.0] — 04-02-2025

Первая публичная версия нативного Android-приложения PageBook.

### Added
- **Заметки (Notes):**
  - Создание, редактирование и удаление заметок.
  - Список заметок с реактивным обновлением через `Flow`.
- **Задачи (Todos):**
  - To-do листы с отметкой выполнения.
  - Поля: заголовок, описание, напоминание, привязка к календарю.
- **События (Events):**
  - Создание и удаление событий с датой и временем.
  - Сортировка событий по дате и времени.
- **Архитектура:**
  - MVVM + Repository.
  - Три независимые базы данных Room: `notes_db`, `todo_db`, `event_database`.
  - Ручное внедрение зависимостей через `PageBookApplication`.
- **UI:**
  - Jetpack Compose + Material Design 3.
  - Тема оформления в `ui/theme`.
  - Корневой Composable `CombinedApp` с навигацией.
- **Инфраструктура:**
  - Настроен `ktlint` для проверки стиля кода.
  - Настроены задачи `assembleDebug` и `test`.

### Known Issues
- Отсутствует Domain Layer (Use Cases) — ViewModel напрямую работает с репозиториями.
- В `NoteRoomDatabase` используется `fallbackToDestructiveMigration()` — данные будут удалены при изменении схемы.
- Нет unit-тестов для репозиториев и ViewModel.

[Unreleased]: https://github.com/MaxGog/PageBook-android/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/MaxGog/PageBook-android/releases/tag/v0.1.0