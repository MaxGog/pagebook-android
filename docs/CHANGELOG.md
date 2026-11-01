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
  (`@Binds AttachmentRepository`).
- **`NoteDao` / `TaskDao` / `EventDao`**: добавлены `@Transaction getAllWithTags()`,
  `getWithTagsById()`; `insert()` возвращает `Long`; сортировки (`AtCreated DESC`,
  `IsCompleted ASC, AtCreated DESC`, `date ASC, time ASC`).
- **`AttachmentDao`**: добавлен `@Update suspend fun update(entity)` — для
  перепривязки pending-вложений (`ownerId = 0 → savedId`) без повторного `insert`.
- **`PageBookDatabase`** — `version = 4`, `entities` включает `AttachmentEntity`.
- **Доменные модели** `Note`, `Task`, `Event` получили `tags: List<Tag> = emptyList()`.
- **Мапперы** перегружены: `toDomain(tags: List<Tag>)`, `WithTags.toDomain()`;
  добавлен `AttachmentMapper` (`Entity ↔ Domain`).
- **`SettingsRepository`**: убран `LANGUAGE_KEY`, `language: Flow<...>`,
  `setLanguage()` — язык теперь хранится **только** в `AppCompatDelegate`.

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
- **`NoteEditScreen` / `TaskEditScreen`**: добавлены кнопки `AddPhotoAlternate` и
  `AttachFile` в `TopAppBar`, `AttachmentPreviewList` под заголовком.
- **`FormattingToolbar`**: `windowInsetsPadding(navigationBars ∪ ime)` — не уходит под клавиатуру.

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

**Документация**
- `ARCHITECTURE.md`: добавлены разделы «Адаптивный UI», «One-Shot Events»,
  «Настройки (DataStore + AppCompatDelegate)», **«Подсистема вложений (Attachments)»**,
  обновлены диаграммы (sequence для attach), БД (`version = 4`, 8 сущностей), roadmap.
- `README.md`: обновлены стек (Adaptive, M3 Expressive, Attachments), roadmap.
- `CHANGELOG.md`: актуализирован.
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
- **Смена языка не работала** — требуется `MainActivity : AppCompatActivity` + `Theme.AppCompat.*` (в процессе).
- **`BuildConfig` не компилировался** — `buildConfig = true` в `build.gradle.kts`.
- **`SegmentListItem` не компилировался** — заменён на стабильные `ListItem` + `Card`.

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

### Planned
- **Смена языка на Android < 13** — миграция `MainActivity` на `AppCompatActivity` и темы на `Theme.AppCompat.*`.
- **Фильтрация списков по тегам** (Notes / Tasks / Calendar).
- **Редактирование существующих тегов** (имя/цвет/эмодзи).
- **Счётчик использования тега**.
- **Поиск по эмодзи** в `EmojiPickerDialog`.
- **Недавние эмодзи** (последние 20 в `DataStore`).
- **`ListDetailPaneScaffold` для Notes и Calendar** (аналогично Tasks).
- **Fullscreen-просмотр вложений** по тапу на превью.
- **Камера как источник изображения** (сейчас только галерея + документы).
- **`MIGRATION_3_4`** для `attachments` (сейчас `fallbackToDestructiveMigration`).
- Синхронизация с облаком.
- Виджеты для главного экрана.
- Резервное копирование.
- Голосовые заметки.
- Модуляризация (feature-модули).
- Unit-тесты для Use Cases и Repository (в т.ч. `AttachmentRepository`).
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