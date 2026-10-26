# 🏗 Архитектура PageBook Android

## 🎯 Обзор

PageBook Android следует официальным рекомендациям Google по архитектуре
приложений и использует **Clean Architecture** с тремя слоями:

- **UI Layer** — Compose + ViewModel + UI State.
- **Domain Layer** — Use Cases, доменные модели, интерфейсы репозиториев.
- **Data Layer** — Room, DAO, реализации репозиториев, мапперы.

Внедрение зависимостей — **Hilt** (`@HiltAndroidApp`, `@HiltViewModel`, модули в `data/di/`).

Навигация — **type-safe** на основе `@Serializable` destinations (Navigation Compose 2.8+).

> **Статус миграции:** Clean Architecture полностью внедрена для всех трёх
> модулей — **Notes**, **Tasks** и **Events (Calendar)**. Todos переименованы
> в Tasks и получили связь с заметками через `noteId`.

## 🛠 Технологический стек

| Компонент           | Технология                               | Версия         |
|:--------------------|:-----------------------------------------|:---------------|
| Язык                | Kotlin                                   | 2.2.20         |
| UI                  | Jetpack Compose + Material 3             | BOM 2026.09.00 |
| Архитектура         | Clean Architecture (UI/Domain/Data)      | —              |
| Локальное хранилище | Room                                     | 2.7.2          |
| Асинхронность       | Coroutines & Flow                        | 1.10.2         |
| DI                  | **Hilt**                                 | 2.56.2         |
| Навигация           | Navigation Compose (type-safe)           | 2.9.0          |
| ML                  | **ML Kit Entity Extraction** (beta)      | 16.0.0-beta6   |
| Сборка              | AGP                                      | 9.4.1          |
| SDK                 | compileSdk 37 / targetSdk 36 / minSdk 28 | —              |
| JDK                 | Java 21                                  | —              |

## 📂 Структура пакетов

```
ru.maxgog.pagebook
├── PageBookApplication.kt        # @HiltAndroidApp
├── MainActivity.kt               # enableEdgeToEdge() + setContent { CombinedAppTheme { ... } }
│
├── data/
│   ├── dao/                      # NoteDao, TaskDao, EventDao
│   ├── models/                   # NoteModel, TaskModel, EventModel (@Entity)
│   ├── rooms/                    # NoteRoomDatabase, TaskRoomDatabase, EventRoomDatabase
│   ├── mapper/                   # NoteMapper, TaskMapper, EventMapper
│   ├── repository/               # NoteRepositoryImpl, TaskRepositoryImpl, EventRepositoryImpl
│   └── di/                       # NotesModule, TaskModule, EventModule
│
├── domain/
│   ├── model/                    # Note, Task, Event
│   ├── repository/               # NoteRepository, TaskRepository, EventRepository (интерфейсы)
│   └── usecase/
│       ├── note/                 # GetAllNotesUseCase, SaveNoteUseCase, DeleteNoteUseCase
│       ├── task/                 # GetAllTasksUseCase, SaveTaskUseCase,
│       │                         #   DeleteTaskUseCase, GetTaskByIdUseCase,
│       │                         #   CreateTaskFromNoteUseCase
│       └── event/                # GetAllEventsUseCase, SaveEventUseCase, DeleteEventUseCase
│
└── ui/
├── app/                      # CombinedApp — корневой Composable со Scaffold
├── navigation/               # Destinations.kt, AppNavHost.kt, BottomNavBar.kt
├── notes/                    # NotesScreen, NotesViewModel, NotesUiState
├── note_edit/                # NoteEditScreen, NoteEditViewModel, NoteEditUiState
├── tasks/                    # TaskListScreen, TaskListViewModel, TaskListUiState
│                             #   TaskEditScreen, TaskEditViewModel, TaskEditUiState
│                             #   TaskItem
├── calendar/                 # CalendarScreen, CalendarViewModel, CalendarUiState
├── items/                    # NoteItem, EventItem, (TaskItem — в ui/tasks)
├── dialogs/                  # AddEventDialog, DateTimePickerDialog, AddTodoDialog
└── theme/                    # Color, Type, Theme
```

## 💾 Три независимые базы данных

Проект использует **три отдельные БД** — по одной на модуль:

| БД          | Таблица  | Модуль   | Особенности                                      |
|:------------|:---------|:---------|:-------------------------------------------------|
| `notes_db`  | `notes`  | Notes    | Базовая сущность, к которой привязываются задачи |
| `tasks_db`  | `tasks`  | Tasks    | Содержит колонку `NoteId` + `Index("NoteId")`    |
| `events_db` | `events` | Calendar | `date` (LocalDate) + `time` (String)             |

### ⚠️ Связь Task ↔ Note без ForeignKey

`TaskModel.noteId` — обычная колонка с индексом, **без** `ForeignKey`. Room не
поддерживает внешние ключи между разными БД. Целостность поддерживается на
уровне Use Case: при удалении заметки `DeleteNoteUseCase` обнуляет `noteId`
у связанных задач. Это осознанный компромисс: каскадные операции выполняются
вручную, но модули остаются изолированными.

## 🔄 Поток данных

### Пример 1: список заметок (Notes)

```mermaid
sequenceDiagram
    participant UI as NotesScreen
    participant VM as NotesViewModel
    participant UC as GetAllNotesUseCase
    participant Repo as NoteRepository (interface)
    participant Impl as NoteRepositoryImpl
    participant DAO as NoteDao
    participant DB as Room

    UI->>VM: collectAsStateWithLifecycle(uiState)
    VM->>UC: invoke()
    UC->>Repo: getAllNotes()
    Repo-->>Impl: getAllNotes()
    Impl->>DAO: getAll()
    DAO->>DB: SELECT * ORDER BY AtCreated DESC
    DB-->>DAO: Flow<List<NoteModel>>
    DAO-->>Impl: Flow<List<NoteModel>>
    Impl-->>Repo: Flow<List<Note>> (через NoteMapper)
    Repo-->>UC: Flow<List<Note>>
    UC-->>VM: Flow<List<Note>>
    VM-->>UI: StateFlow<NotesUiState.Success>
```

### Пример 2: список задач (Tasks)

```mermaid
sequenceDiagram
    participant UI as TaskListScreen
    participant VM as TaskListViewModel
    participant UC as GetAllTasksUseCase
    participant Impl as TaskRepositoryImpl
    participant DAO as TaskDao
    participant DB as Room

    UI->>VM: collectAsStateWithLifecycle(uiState)
    VM->>UC: invoke()
    UC->>Impl: getAllTasks()
    Impl->>DAO: getAllTasks()
    DAO->>DB: SELECT * ORDER BY IsCompleted ASC, AtCreated DESC
    DB-->>DAO: Flow<List<TaskModel>>
    DAO-->>Impl: Flow<List<TaskModel>>
    Impl-->>UC: Flow<List<Task>> (через TaskMapper)
    UC-->>VM: Flow<List<Task>>
    VM-->>UI: StateFlow<TaskListUiState.Success>
```

**Особенность:** сортировка незавершённых сверху (`IsCompleted ASC`), внутри
группы — новые первыми (`AtCreated DESC`).

### Пример 3: календарь (Events)

```mermaid
sequenceDiagram
    participant UI as CalendarScreen
    participant VM as CalendarViewModel
    participant UC as GetAllEventsUseCase
    participant Impl as EventRepositoryImpl
    participant DAO as EventDao
    participant DB as Room

    UI->>VM: collectAsStateWithLifecycle(uiState)
    VM->>UC: invoke()
    UC->>Impl: getAllEvents()
    Impl->>DAO: getAllEvents()
    DAO->>DB: SELECT * ORDER BY date ASC, time ASC
    DB-->>DAO: Flow<List<EventModel>>
    DAO-->>Impl: Flow<List<EventModel>>
    Impl-->>UC: Flow<List<Event>> (через EventMapper)
    UC-->>VM: Flow<List<Event>>
    Note over VM: combine(events, selectedDate, showDialog)
    VM-->>UI: StateFlow<CalendarUiState>
```

**Особенность:** UI-состояние собирается через `combine` трёх источников —
список событий из Flow, выбранная дата и флаг диалога. Фильтрация
«события выбранного дня» выполняется во ViewModel, а не в Composable.

### Пример 4: создание задачи из заметки

```mermaid
sequenceDiagram
    participant UI as NoteEditScreen
    participant VM as NoteEditViewModel
    participant UC as CreateTaskFromNoteUseCase
    participant ML as ML Kit Entity Extraction
    participant Save as SaveTaskUseCase

    UI->>VM: createTaskFromCurrentNote()
    VM->>UC: invoke(noteContent, noteTitle)
    UC->>ML: annotate(text, filter=TYPE_DATE_TIME)
    ML-->>UC: List<EntityAnnotation>
    UC-->>VM: Task (с готовым reminderTime)
    VM->>Save: invoke(task)
    Save-->>VM: (insert в tasks_db)
```

**Особенность:** ML Kit извлекает даты из текста заметки и автоматически
заполняет `reminderTime` — пользователю остаётся только подтвердить.

## 🧭 Ключевые архитектурные решения

| Решение                                      | Обоснование                                                                                  |
|:---------------------------------------------|:---------------------------------------------------------------------------------------------|
| **Clean Architecture**                       | UI не зависит от схемы БД; Use Cases инкапсулируют бизнес-логику; легко тестировать.         |
| **Repository как интерфейс**                 | Позволяет подменить источник данных (например, добавить сеть) без изменения Domain/UI.       |
| **UI State (sealed interface / data class)** | Явные состояния `Loading`/`Success`/`Error` — не «дёргающийся» UI и предсказуемое поведение. |
| **Hilt**                                     | Масштабируемый DI, генерируемый на этапе компиляции, интеграция с `@HiltViewModel`.          |
| **Type-safe navigation**                     | Компилятор проверяет маршруты и аргументы; безопасный рефакторинг.                           |
| **`collectAsStateWithLifecycle()`**          | Подписка живёт только пока экран видим — экономия ресурсов.                                  |
| **`contentPadding` в LazyColumn/Grid**       | Правильный edge-to-edge: контент прокручивается под барами.                                  |
| **Три независимые БД**                       | Изоляция модулей: проще миграции, возможность развивать фичи независимо.                     |
| **`noteId` без ForeignKey**                  | Room не поддерживает FK между БД; связь поддерживается через Use Case.                       |
| **`combine` во ViewModel**                   | Множественные источники (список + выбранная дата + флаг) — один `StateFlow`.                 |
| **ML Kit для извлечения дат**                | Не требует ручного ввода; `TYPE_DATE_TIME` покрывает большинство форматов.                   |

## 🧩 Особенности отдельных модулей

### Tasks

- **Связь с заметками** — через `noteId` (nullable). Позволяет создавать
  задачи из заметок и отображать связанные задачи.
- **Сортировка** — незавершённые сверху, новые внутри группы.
- **`TaskEditDestination(taskId: Int?)`** — `null` = новая задача, число =
  редактирование существующей. `taskId` извлекается в ViewModel через
  `SavedStateHandle.get<Int>("taskId")`.

### Calendar

- **Реактивность** — `EventDao.getAllEvents()` возвращает `Flow`, поэтому
  изменения в БД сразу отражаются в UI.
- **Скролл к выбранной дате** — `LaunchedEffect(uiState.selectedDate)`
  автоматически скроллит календарь к месяцу выбранного дня.
- **Фильтрация событий** — во ViewModel через `combine`, UI получает уже
  готовый список `eventsForSelectedDate`.

### Notes

- **Rich Text** — HTML-контент через `richeditor-compose`.
- **Адаптивная сетка** — `StaggeredGridCells.Adaptive(minSize = 160.dp)`.
- **Удаление** — `DeleteNoteUseCase` обнуляет `noteId` у связанных задач.

## 🚀 Планы по развитию

- [x] Внедрить **Hilt** для DI.
- [x] Выделить **Domain Layer** (Use Cases) для Notes.
- [x] Перенести **Tasks** на Clean Architecture + переименовать Todo → Task.
- [x] Перенести **Events (Calendar)** на Clean Architecture.
- [x] Добавить **отдельные Domain-модели** для всех трёх модулей.
- [x] Подключить **ML Kit** для извлечения дат из заметок.
- [ ] Реализовать UI для `CreateTaskFromNoteUseCase` в `NoteEditScreen`.
- [ ] Объединить три БД в одну `PageBookDatabase` с `ForeignKey(Note)`.
- [ ] Настроить **модуляризацию** (feature-модули) для ускорения сборки.
- [ ] Unit-тесты для Use Cases и Repository.
- [ ] Добавить **WorkManager** для напоминаний о задачах.
- [ ] Синхронизация с облаком.

## 📚 Полезные ссылки

- [Guide to App Architecture (Google)](https://developer.android.com/topic/architecture)
- [Now in Android (официальный референс)](https://github.com/android/nowinandroid)
- [Navigation — type safety](https://developer.android.com/guide/navigation/design/type-safety)
- [ML Kit Entity Extraction](https://developers.google.com/ml-kit/language/entity-extraction)
- [Room — Foreign Keys](https://developer.android.com/training/data-storage/room/relationships)