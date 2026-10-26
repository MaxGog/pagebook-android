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
  (`Loading`/`Success`/`Error`).
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
  в `TaskModel` (без `ForeignKey` — БД изолированы).
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

**Навигация и UI**
- **Type-safe navigation**: `Destinations.kt` с `@Serializable` объектами
  и классами данных.
- **`hasRoute<T>()`** вместо сравнения строк в `BottomNavBar` и `CombinedApp`.
- **Debug build type** с `applicationIdSuffix = ".debug"`
  и `versionNameSuffix = "-debug"` — установка рядом с release.

**Зависимости**
- `kotlinx-serialization-json` — для типобезопасной навигации.
- `kizitonwose.calendar:compose` — для экрана календаря.
- **`com.google.mlkit:entity-extraction`** (beta) — извлечение дат из текста.
- `kotlinx-coroutines-play-services` — для `.await()` на ML Kit Task.
- **Room Gradle plugin** — экспорт схемы в `app/schemas/`.

**Документация**
- Черновик документации: `ARCHITECTURE.md`, `CONTRIBUTING.md`, `CHANGELOG.md`.

### Changed

**Gradle-конфигурация**
- AGP `9.4.1`, Kotlin `2.2.20`, KSP `2.2.20-2.0.4`, Compose BOM `2026.09.00`.
- `compileSdk = 37`, `targetSdk = 36`, `minSdk = 28`, JDK 21.
- Удалён устаревший `buildscript` — версии плагинов резолвятся через `pluginManagement`.
- `kotlinOptions` → `kotlin { compilerOptions { ... } }` (встроено в AGP 9).

**Data Layer**
- **Репозитории** разделены на интерфейс (`domain/repository/`) и реализацию
  (`data/repository/`): `NoteRepositoryImpl`, `TaskRepositoryImpl`,
  `EventRepositoryImpl`.
- **`NoteDao`**: `getAll()` сортирует по `AtCreated DESC`, `insert()` возвращает
  `Long`, `getById()` стал `suspend`.
- **`TaskDao`**: `getAllTasks()` сортирует по `IsCompleted ASC, AtCreated DESC`;
  добавлен `getTasksByNoteId(noteId)` и `suspend getById(id)`.
- **`EventDao`**: возвращает `Flow` вместо `List`; добавлен `@Update`.
- **Hilt-модули** (`NotesModule`, `TaskModule`, `EventModule`) привязывают
  интерфейс к реализации.

**UI**
- **Экран заметок** использует `contentPadding` вместо `Modifier.padding` —
  контент корректно прокручивается под системными барами (edge-to-edge).
- **`collectAsStateWithLifecycle()`** вместо `collectAsState()` в подписках.
- **`FormattingToolbar`** получил `navigationBarsPadding()` и фон
  `surfaceContainerLow` — больше не уходит под системную навигацию.
- **`NoteItem`, `TaskItem`, `EventItem`** принимают доменные модели
  (`Note`, `Task`, `Event`), а не Entity.
- **`LocalConfiguration.current.locales[0]`** вместо устаревшего
  `LocalLocale.current.platformLocale` в `CalendarScreen`.

**Документация**
- `README.md`, `ARCHITECTURE.md`, `CONTRIBUTING.md` приведены в соответствие
  с фактической структурой (`ru.maxgog.pagebook`, пакет `rooms/`,
  Clean Architecture для всех модулей, `ui/tasks/`, `ui/calendar/`,
  `usecase/{note,task,event}/`).

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

### Planned
- UI для `CreateTaskFromNoteUseCase` в `NoteEditScreen` (сейчас UseCase готов,
  но не подключён к экрану).
- Синхронизация с облаком.
- Ручное переключение тёмной темы в настройках (сейчас — по системной теме).
- Виджеты для главного экрана.
- Резервное копирование.
- Голосовые заметки.
- Объединение трёх БД в `PageBookDatabase` с `ForeignKey(Note)`.
- Модуляризация (feature-модули).
- Unit-тесты для Use Cases и Repository.
- WorkManager для напоминаний о задачах.

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