# Changelog

Все значимые изменения проекта документируются в этом файле.

Формат основан на [Keep a Changelog](https://keepachangelog.com/ru/1.1.0/),
проект придерживается [Semantic Versioning](https://semver.org/lang/ru/).

## [Unreleased]

### Added
- Черновик документации: `ARCHITECTURE.md`, `CONTRIBUTING.md`, `CHANGELOG.md`.
- **Domain Layer для заметок**: `Note`, `NoteRepository` (интерфейс), Use Cases
  (`GetAllNotesUseCase`, `GetNoteByIdUseCase`, `SaveNoteUseCase`, `DeleteNoteUseCase`).
- **Mapper** `Note ↔ NoteModel` (`data/mapper/NoteMapper.kt`).
- **UI State** для экранов заметок: `NotesUiState`, `NoteEditUiState` (`Loading`/`Success`/`Error`).
- **Type-safe navigation**: `Destinations.kt` с `@Serializable` объектами и классами данных.
- **Debug build type** с `applicationIdSuffix = ".debug"` и `versionNameSuffix = "-debug"`.
- **Адаптивная сетка заметок**: `StaggeredGridCells.Adaptive(minSize = 160.dp)`.
- Зависимость `kotlinx-serialization-json` для типобезопасной навигации.
- Зависимость `kizitonwose.calendar:compose` для экрана календаря.

### Changed
- README приведён в соответствие с фактической структурой проекта (`ru.maxgog.pagebook`, пакет `rooms/`).
- **Gradle-конфигурация** приведена к стандартам 2026:
- AGP `9.4.1`, Kotlin `2.2.20`, KSP `2.2.20-2.0.4`, Compose BOM `2026.09.00`.
- `compileSdk = 37`, `targetSdk = 36`, `minSdk = 28`, JDK 21.
- Удалён устаревший `buildscript` — версии плагинов резолвятся через `pluginManagement`.
- **Репозиторий заметок** разделён на интерфейс (`domain/repository/NoteRepository.kt`)
  и реализацию (`data/repository/NoteRepositoryImpl.kt`).
- **`NoteDao`**: `getAll()` теперь сортирует по `AtCreated DESC`, `insert()` возвращает `Long`,
  `getById()` стал `suspend`.
- **Экран заметок** использует `contentPadding` вместо `Modifier.padding` — контент
  корректно прокручивается под системными барами (edge-to-edge).
- **`collectAsStateWithLifecycle()`** вместо `collectAsState()` в ViewModel-подписках.
- **`FormattingToolbar`** получил `navigationBarsPadding()` и фон `surfaceContainerLow` —
  больше не уходит под системную навигацию.
- Обновлены разделы документации `README.md`, `ARCHITECTURE.md`, `CONTRIBUTING.md`.

### Removed
- Зависимость `accompanist-systemuicontroller` (deprecated, заменена `enableEdgeToEdge()`).
- Плагин `kotlin-android` (встроен в AGP 9).
- Файлы `Screen.kt`, `Routes.kt`, `NotesDestinations.kt` — заменены на `Destinations.kt`.
- Старый класс `data/repositories/NoteRepository.kt` — заменён на интерфейс + реализацию.
- Устаревшие ViewModels `viewmodels/NoteViewModel.kt`, `viewmodels/NoteEditViewModel.kt` —
  переехали в `ui/notes/` и `ui/note_edit/`.

### Fixed
- **Нижняя панель навигации** больше не исчезает после перехода на type-safe навигацию
  (используется `destination.hasRoute<T>()` вместо сравнения строк).
- **Контент экранов** больше не перекрывается системными барами и нижней навигацией
  (`Modifier.padding(innerPadding)` передаётся в `AppNavHost`).
- **Конфликт версий KSP/Kotlin** — версии синхронизированы.
- **Reserved alias** `androidx-material3-window-size-class` → `androidx-material3-window-size`.

### Planned
- Синхронизация с облаком.
- Ручное переключение тёмной темы в настройках (сейчас — по системной теме).
- Виджеты для главного экрана.
- Резервное копирование.
- Голосовые заметки.
- Перенос Todos и Events на Clean Architecture (по аналогии с Notes).
- Модуляризация (feature-модули).

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