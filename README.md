# 📱 PageBook Android

**Нативная Android-реализация** универсального органайзера с акцентом на плавность работы и Material Design! 🚀

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.09.00-blue.svg)](https://developer.android.com/jetpack/compose)
[![AGP](https://img.shields.io/badge/AGP-9.4.1-green.svg)](https://developer.android.com/build/releases/gradle-plugin)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Version](https://img.shields.io/badge/version-0.2.0--dev-orange.svg)](CHANGELOG.md)

## 🌟 О проекте

PageBook — это нативное Android-приложение для работы со справочными материалами, заметками, задачами и календарём. Часть кроссплатформенного проекта PageBook, разрабатываемого нативно для обеих платформ.

> 📖 **iOS версия**: [PageBook iOS](https://github.com/MaxGog/PageBook-ios)

## 📖 Документация

- **[Архитектура](docs/ARCHITECTURE.md)** — подробное описание слоёв, потока данных и технических решений.
- **[Участие в разработке](docs/CONTRIBUTING.md)** — как настроить окружение, стиль кода и процесс Pull Request.
- **[История изменений](docs/CHANGELOG.md)** — что нового в каждой версии.

## ✨ Основные возможности

### 📝 Умные заметки
- Создание и редактирование заметок с rich-text форматированием
- Адаптивная сетка заметок (StaggeredGrid)
- Создание задач прямо из заметок с автоматическим извлечением дат

### ✅ Менеджер задач
- Задачи с напоминаниями и связью с заметками
- Сортировка: незавершённые сверху, новые первыми
- Автоматическое определение даты из текста заметки (ML Kit)

### 🗓️ Календарь и планирование
- Ежедневное планирование событий с выбором даты и времени
- Реактивное обновление при изменении событий
- Быстрый скролл к выбранной дате

## 🛠 Технологический стек

- **💚 100% Kotlin** — Kotlin 2.2.20
- **🎨 Jetpack Compose** — BOM 2026.09.00, Material 3 Expressive
- **💾 Room Database** — три изолированные БД (notes, tasks, events)
- **🔄 Coroutines & Flow** — реактивное программирование
- **🧩 Hilt** — внедрение зависимостей
- **🧭 Navigation Compose** — type-safe навигация через `@Serializable` destinations
- **🤖 ML Kit Entity Extraction** — извлечение дат из текста заметок
- **📐 Material Design 3** + edge-to-edge
- **🏗 Clean Architecture** — UI / Domain / Data

## 🏗 Архитектура

```mermaid
graph TD
   A[UI Layer<br/>Compose + ViewModel + UiState] --> B[Domain Layer<br/>Use Cases + доменные модели]
   B --> C[Data Layer<br/>Repository Impl + Mappers + Room]
   C --> D[(Local DB - Room<br/>notes, tasks, events)]
   C -.-> E[(Remote - Future)]
```

### Clean Architecture
- **UI Layer**: Jetpack Compose + ViewModel + UiState (sealed interface / data class)
- **Domain Layer**: Use Cases, доменные модели (Note, Task, Event), интерфейсы репозиториев
- **Data Layer**: Room, DAO, мапперы, реализации репозиториев, Hilt-модули

Все три модуля — **Notes**, **Tasks**, **Calendar** — полностью на Clean Architecture.

Подробнее см. в [ARCHITECTURE.md](docs/ARCHITECTURE.md).

## 🚀 Быстрый старт

### Предварительные требования
- Android Studio Otter (2026.1) или новее
- Android SDK 37 (Android 17)
- JDK 21
- Kotlin 2.2.20+

### Установка и запуск

1. **Клонируйте репозиторий**
   ```bash
   git clone https://github.com/MaxGog/PageBook-android.git
   cd PageBook-android
   ```

2. **Откройте в Android Studio**
   - File → Open → Выберите папку проекта
   - Дождитесь завершения сборки зависимостей

3. **Запустите приложение**
   - Подключите Android-устройство или запустите эмулятор
   - Нажмите ▶️ Run или `Ctrl + R`

## 📁 Структура проекта

```
app/src/main/java/ru/maxgog/pagebook/
├── data/
│   ├── dao/            # NoteDao, TaskDao, EventDao
│   ├── models/         # Room-сущности (@Entity)
│   ├── rooms/          # NoteRoomDatabase, TaskRoomDatabase, EventRoomDatabase
│   ├── mapper/         # NoteMapper, TaskMapper, EventMapper
│   ├── repository/     # *RepositoryImpl (реализации интерфейсов)
│   └── di/             # NotesModule, TaskModule, EventModule (Hilt)
├── domain/
│   ├── model/          # Note, Task, Event
│   ├── repository/     # NoteRepository, TaskRepository, EventRepository (интерфейсы)
│   └── usecase/
│       ├── note/       # GetAllNotesUseCase, SaveNoteUseCase, DeleteNoteUseCase
│       ├── task/       # GetAllTasksUseCase, SaveTaskUseCase, DeleteTaskUseCase,
│       │               #   GetTaskByIdUseCase, CreateTaskFromNoteUseCase
│       └── event/      # GetAllEventsUseCase, SaveEventUseCase, DeleteEventUseCase
├── ui/
│   ├── app/            # CombinedApp (корневой Scaffold)
│   ├── navigation/     # Destinations, AppNavHost, BottomNavBar
│   ├── notes/          # NotesScreen + ViewModel + UiState
│   ├── note_edit/      # NoteEditScreen + ViewModel + UiState
│   ├── tasks/          # TaskListScreen, TaskEditScreen, TaskItem + ViewModels
│   ├── calendar/       # CalendarScreen + ViewModel + UiState
│   ├── items/          # NoteItem, EventItem
│   ├── dialogs/        # AddEventDialog, DateTimePickerDialog, AddTodoDialog
│   └── theme/          # Color, Type, Theme
├── PageBookApplication.kt
└── MainActivity.kt
```

## 🧩 Функциональные возможности

| Модуль | Возможности | Технологии |
|--------|-------------|------------|
| **Notes** 📝 | Rich-text заметки, адаптивная сетка, создание задач с извлечением дат | Compose, Room, RichEditor, ML Kit |
| **Tasks** ✅ | Задачи с напоминаниями, связь с заметками (`noteId`), сортировка по статусу | Compose, StateFlow, Room |
| **Calendar** 🗓️ | События по датам, реактивное обновление, скролл к выбранной дате | Compose, kizitonwose Calendar, Room |

## 🛠 Разработка

### Сборка проекта
```bash
./gradlew assembleDebug
```

### Тестирование
```bash
./gradlew test
```

### Анализ кода
```bash
./gradlew ktlintCheck
```

Полный гайд по настройке окружения и процессу PR — в [CONTRIBUTING.md](docs/CONTRIBUTING.md).

## 🤝 Участие в разработке

Мы рады контрибьюторам! Кратко:

- **🐛 Баги** — создайте [Issue](https://github.com/MaxGog/PageBook-android/issues) с шагами воспроизведения.
- **💡 Идеи** — откройте Issue с меткой `enhancement`.
- **🔧 Pull Requests** — следуйте процессу из [CONTRIBUTING.md](docs/CONTRIBUTING.md).
- **🏷️ Good First Issues** — начните с задач с этой меткой.

## 🎯 Планы развития

- [x] **Clean Architecture** для всех модулей (Notes, Tasks, Calendar)
- [x] **Type-safe navigation** через `@Serializable` destinations
- [x] **Hilt** для внедрения зависимостей
- [x] **ML Kit** для извлечения дат из заметок
- [ ] **UI для создания задач из заметок** 🤖
- [ ] **Синхронизация с облаком** ☁️
- [ ] **Ручное переключение тёмной темы** 🌙
- [ ] **Виджеты для главного экрана** 🏠
- [ ] **Резервное копирование** 💾
- [ ] **Голосовые заметки** 🎤
- [ ] **Объединение трёх БД в PageBookDatabase** с ForeignKey
- [ ] **Модуляризация** (feature-модули)
- [ ] **Unit-тесты** для Use Cases и Repository
- [ ] **Kotlin Multiplatform** для общей бизнес-логики

## 👨‍💻 Автор

**MaxGog**
- GitHub: [@MaxGog](https://github.com/MaxGog)

## 💬 Обратная связь

Есть вопросы или предложения? Не стесняйтесь:
- Создать [Issue](https://github.com/MaxGog/PageBook-android/issues)
- Написать в Discussions