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
- **Domain-модели**: `Tag` (`id`, `name`, `colorHex`) и `TagColor` (enum из 7
  цветов macOS: `RED`, `ORANGE`, `YELLOW`, `GREEN`, `BLUE`, `PURPLE`, `GRAY`).
- **`TagRepository`** (интерфейс) + `TagRepositoryImpl` (реализация) + `TagMapper`.
- **`TagModel`** (`@Entity tableName = "tags"`).
- **Junction-таблицы** для many-to-many:
  `NoteTagCrossRef`, `TaskTagCrossRef`, `EventTagCrossRef`
  (составной PK, `onDelete = CASCADE` на обе стороны).
- **`TagDao`**: CRUD для тегов, `hasTagOn{Note,Task,Event}`, SQL-запросы
  через `INNER JOIN`, `@Transaction`-обёртки `getNoteWithTags`/`getTaskWithTags`/
  `getEventWithTags`.
- **`NoteWithTags`, `TaskWithTags`, `EventWithTags`** — `@Relation` + `@Junction`
  для загрузки сущности вместе с тегами одним запросом (без N+1).
- **Use Cases** в `domain/usecase/tag/`:
  - `GetAllTagsUseCase`, `CreateTagUseCase`, `UpdateTagUseCase`, `DeleteTagUseCase`.
  - `GetTagsForNoteUseCase`, `GetTagsForTaskUseCase`, `GetTagsForEventUseCase`.
  - `ToggleTagOnNoteUseCase`, `ToggleTagOnTaskUseCase`, `ToggleTagOnEventUseCase`
    (возвращают `Boolean` — был ли тег добавлен).
- **UI-компоненты** в `ui/components/`:
  - `TagChip` — `FilterChip` с цветным кружком и опциональной кнопкой ×.
  - `CompactTagChip` — компактная плашка для карточек в списках.
  - `CreateTagDialog` — диалог создания тега с палитрой из 7 цветов.
  - `TagPickerDialog` — выбор тегов для сущности с кнопкой «Создать тег».
- **Интеграция в Notes**: кнопка 🏷️ в `NoteEditScreen`, чипы под заголовком,
  `TagPickerDialog` + `CreateTagDialog`, до 3 `CompactTagChip` на `NoteItem`.
- **Интеграция в Tasks**: аналогично `NoteEditScreen` — кнопка 🏷️, чипы,
  диалоги; до 3 `CompactTagChip` на `TaskItem`.
- **Интеграция в Calendar**: встроенный выбор тегов в `AddEventDialog`,
  `pendingTags` во `CalendarViewModel`, до 3 `CompactTagChip` на `EventItem`.
- **`_pendingTags` / `selectedTags`** — накопление тегов для новой
  (ещё не сохранённой) сущности; привязка после `save*`, когда Room
  сгенерировал `id`.

**Единая база данных**
- **`PageBookDatabase`** — единая `@Database` с 7 сущностями
  (`NoteModel`, `TaskModel`, `EventModel`, `TagModel`, `NoteTagCrossRef`,
  `TaskTagCrossRef`, `EventTagCrossRef`), `version = 2`.
- Заменяет три отдельные БД (`NoteRoomDatabase`, `TaskRoomDatabase`,
  `EventRoomDatabase`) — необходимое условие для `ForeignKey` между
  сущностями и тегами.

**Настройки (DataStore)**
- **`SettingsRepository`** — `Flow<ThemeMode>`, `Flow<LanguageOption>`,
  `setThemeMode()`, `setLanguage()`. Хранит значения в `DataStore<Preferences>`.
- **`DataStoreModule`** (Hilt) — singleton `DataStore<Preferences>`
  (`preferencesDataStore(name = "settings")`).
- **`ThemeViewModel`** — подписка `MainActivity` на `themeMode` для
  реактивного переключения темы без перезапуска.
- **`SettingsViewModel`** — `setLanguage()` вызывает
  `AppCompatDelegate.setApplicationLocales()` (Android 13+ через `LocaleManager`,
  ниже — через AppCompat + `recreate()`).
- **UI**: `SettingsScreen` переработан на `SettingsSection` + `ListItem` +
  `ExposedDropdownMenuBox` (в стиле CharacterBook).
  `ThemePreference`, `LanguagePreference`, `SettingsSection`.

**Навигация и UI**
- **Type-safe navigation**: `Destinations.kt` с `@Serializable` объектами
  и классами данных.
- **`hasRoute<T>()`** вместо сравнения строк в `BottomNavBar` и `CombinedApp`.
- **Debug build type** с `applicationIdSuffix = ".debug"`
  и `versionNameSuffix = "-debug"`.
- **`_isSaved: StateFlow<Boolean>`** во ViewModels редактирования —
  экран закрывается только после завершения сохранения (устранён race condition).

**Зависимости**
- `kotlinx-serialization-json` — для типобезопасной навигации.
- `kizitonwose.calendar:compose` — для экрана календаря.
- **`com.google.mlkit:entity-extraction`** (beta) — извлечение дат из текста.
- `kotlinx-coroutines-play-services` — для `.await()` на ML Kit Task.
- **Room Gradle plugin** — экспорт схемы в `app/schemas/`.
- **`androidx.datastore:datastore-preferences`** — хранение настроек.
- **`androidx.appcompat:appcompat`** — для смены локали на Android < 13.

**Документация**
- Черновик документации: `ARCHITECTURE.md`, `CONTRIBUTING.md`, `CHANGELOG.md`.

### Changed

**Gradle-конфигурация**
- AGP `9.4.1`, Kotlin `2.2.20`, KSP `2.2.20-2.0.4`, Compose BOM `2026.09.00`.
- `compileSdk = 37`, `targetSdk = 36`, `minSdk = 28`, JDK 21.
- Удалён устаревший `buildscript` — версии плагинов резолвятся через `pluginManagement`.
- `kotlinOptions` → `kotlin { compilerOptions { ... } }` (встроено в AGP 9).
- `buildConfig = true` — для `BuildConfig.VERSION_NAME` в настройках.
- `android:localeConfig="@xml/locales_config"` в `AndroidManifest.xml`
  (`ru`, `en`).

**Data Layer**
- **Репозитории** разделены на интерфейс (`domain/repository/`) и реализацию
  (`data/repository/`): `NoteRepositoryImpl`, `TaskRepositoryImpl`,
  `EventRepositoryImpl`, `TagRepositoryImpl`.
- **Hilt**: вместо отдельных `NotesModule`, `TaskModule`, `EventModule`,
  `TagModule` — единый **`RepositoryModule`** с `@Binds` (4 привязки
  `интерфейс → реализация`). `DatabaseModule` предоставляет `PageBookDatabase`
  и 4 DAO.
- **`NoteDao`**: добавлены `@Transaction getAllWithTags()` и `getWithTagsById()`
  для загрузки заметок с тегами. `getAll()` сортирует по `AtCreated DESC`.
- **`TaskDao`**: добавлены `@Transaction getAllWithTags()` и `getWithTagsById()`.
  Сортировка `IsCompleted ASC, AtCreated DESC`, `getTasksByNoteId(noteId)`.
- **`EventDao`**: возвращает `Flow` вместо `List`; добавлены `@Update`,
  `@Transaction getAllWithTags()`, `getWithTagsById()`. `insert()` возвращает `Long`.
- **`Note`, `Task`, `Event`** — добавлено поле `tags: List<Tag> = emptyList()`.
- **Мапперы** `NoteMapper`, `TaskMapper`, `EventMapper` — перегружены:
  `toDomain(tags: List<Tag>)` и `WithTags.toDomain()`.

**UI**
- **Экран заметок** использует `contentPadding` вместо `Modifier.padding` —
  контент корректно прокручивается под системными барами (edge-to-edge).
- **`collectAsStateWithLifecycle()`** вместо `collectAsState()` в подписках.
- **`FormattingToolbar`** получил `navigationBarsPadding()` и фон
  `surfaceContainerLow` — больше не уходит под системную навигацию.
- **`NoteItem`, `TaskItem`, `EventItem`** принимают доменные модели
  (`Note`, `Task`, `Event`) и отображают до 3 `CompactTagChip` + `+N`.
- **`NoteEditUiState`, `TaskEditUiState`** — `NewNote(selectedTags)` /
  `NewTask(selectedTags)` и `Editing(note/task, tags)`.
- **`CalendarViewModel`** — `allTags`, `pendingTags`, `toggleTag`,
  `createAndToggleTag`, привязка тегов в `addEvent`.
- **`LocalConfiguration.current.locales[0]`** вместо устаревшего
  `LocalLocale.current.platformLocale` в `CalendarScreen`.

**Документация**
- `README.md`, `ARCHITECTURE.md`, `CONTRIBUTING.md` приведены в соответствие
  с фактической структурой: Clean Architecture для всех модулей,
  `ui/tasks/`, `ui/calendar/`, `ui/components/`, `usecase/{note,task,event,tag}/`,
  единая `PageBookDatabase`, сквозные теги, DataStore для настроек.

### Removed
- Зависимость `accompanist-systemuicontroller` (deprecated, заменена
  `enableEdgeToEdge()`).
- Плагин `kotlin-android` (встроен в AGP 9).
- Файлы `Screen.kt`, `Routes.kt`, `NotesDestinations.kt` — заменены на
  `Destinations.kt`.
- Старые классы `data/repositories/{NoteRepository,TodoRepository,EventRepository}.kt` —
  заменены интерфейсами в `domain/` + реализациями в `data/repository/`.
- Устаревшие ViewModels `viewmodels/{NoteViewModel,NoteEditViewModel,
  TodoViewModel,CalendarViewModel}.kt` — переехали в `ui/{notes,note_edit,tasks,calendar}/`.
- Все файлы `Todo*` (модель, DAO, база, репозиторий, модуль, экран, item).
- Устаревший `ui/screens/TodoScreen.kt`, `ui/screens/CalendarScreen.kt`.
- **Три отдельные БД**: `NoteRoomDatabase.kt`, `TaskRoomDatabase.kt`,
  `EventRoomDatabase.kt` — заменены единым `PageBookDatabase.kt`.
- **Отдельные Hilt-модули репозиториев**: `NotesModule.kt`, `TaskModule.kt`,
  `EventModule.kt`, `TagModule.kt` — заменены единым `RepositoryModule.kt`
  с `@Binds`.
- **`ui/dialogs/`** (старая папка) — `AddEventDialog`, `DateTimePickerDialog`,
  `AddTodoDialog` переехали в `ui/calendar/`.

### Fixed
- **Нижняя панель навигации** больше не исчезает после перехода на type-safe
  навигацию (`destination.hasRoute<T>()` вместо сравнения строк).
- **Краш `Cannot find startDestination`** — `TaskListDestination` зарегистрирован
  в `AppNavHost` и совпадает с `startDestination`.
- **Краш `Destination TaskEditDestination cannot be found`** — добавлен
  `composable<TaskEditDestination>` в граф.
- **Контент экранов** больше не перекрывается системными барами и нижней
  навигацией (`Modifier.padding(innerPadding)` передаётся в `AppNavHost`).
- **Конфликт версий KSP/Kotlin** — версии синхронизированы.
- **Reserved alias** `androidx-material3-window-size-class` →
  `androidx-material3-window-size`.
- **Ошибка `Unresolved reference 'kizitonwose'`** — зависимость возвращена
  в `libs.versions.toml`.
- **`setEntityTypesFilter` ML Kit** ожидает `Set<Int>`, а не `List<Int>`.
- **`TaskModel` `ForeignKey` на `NoteModel`** не работает между разными БД —
  заменён на `Index("NoteId")` с ручной отвязкой в `DeleteNoteUseCase`.
- **`EventTagCrossRef`, `TaskTagCrossRef`** — исправлены `ForeignKey`
  (правильные entity: `EventModel`/`TaskModel`, поля `eventId`/`taskId`
  вместо `noteId`), `childColumns = ["tagId"]` для FK на `TagModel`.
- **`EventWithTags`, `TaskWithTags`** — `parentColumn = "id"` (а не
  `"EventId"`/`"TaskId"`), `@Embedded val event/task`.
- **`TagDao.getTagsForTask`/`getTagsForEvent`** — `SELECT t.* FROM tags t`
  вместо `FROM tasks t`/`FROM events t`.
- **`DatabaseModule`** — импорты `dagger.Provides` (не
  `com.google.android.datatransport...`), `javax.inject.Singleton`
  (не Jakarta), добавлены `@Module` + `@InstallIn(SingletonComponent::class)`.
- **`[Dagger/DuplicateBindings] TagRepository`** — удалены старые
  `TagModule`/`EventModule`/`NotesModule`/`TaskModule` (дублировали `@Binds`
  из `RepositoryModule`).
- **`SaveNoteUseCase`, `SaveTaskUseCase`, `SaveEventUseCase`** — возвращают
  `Long` (id сохранённой сущности) вместо `Unit`; иначе `savedId.toInt()`
  в ViewModel не компилируется.
- **`NoteEditViewModel.init`** — убран «висячий» `collect`, который никогда
  не завершался (экран навсегда оставался в `Loading`).
- **`NoteEditScreen`, `TaskEditScreen`** — `onBack()` больше не вызывается
  сразу после `viewModel.save()`; переход через `LaunchedEffect(isSaved)`
  (устранён race condition при сохранении тегов).
- **`SettingsRepository`** — импортирует `LanguageOption` из `ui/settings`,
  но фактически используется только в `SettingsViewModel`; нарушение
  Clean Architecture (data → ui) отмечено, будет исправлено переносом
  в `domain/model/`.

### Planned
- UI для `CreateTaskFromNoteUseCase` в `NoteEditScreen` (UseCase готов,
  но не подключён).
- **Фильтрация списков по тегам** в Notes / Tasks / Calendar
  (горизонтальный `LazyRow` с `FilterChip` над списком).
- **Редактирование существующих тегов** (имя/цвет) — `UpdateTagUseCase` готов,
  нужен UI в `CreateTagDialog` или отдельный `EditTagDialog`.
- **Счётчик использования тега** — сколько заметок/задач/событий
  используют (полезно перед удалением).
- Синхронизация с облаком.
- Ручное переключение тёмной темы в настройках (сейчас — через `DataStore`).
- Виджеты для главного экрана.
- Резервное копирование.
- Голосовые заметки.
- Модуляризация (feature-модули).
- Unit-тесты для Use Cases и Repository (включая `ToggleTagOn*UseCase`).
- WorkManager для напоминаний о задачах.
- Настройка `room.schemaLocation` (Room Gradle plugin уже подключён,
  нужен `schemaDirectory("$projectDir/schemas")`).

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