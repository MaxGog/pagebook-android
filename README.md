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
- Создание и редактирование заметок
- Быстрый поиск и фильтрация

### ✅ Менеджер задач
- To-do листы с приоритетами
- Напоминания и дедлайны
- Прогресс выполнения

### 🗓️ Календарь и планирование
- Ежедневное и недельное планирование
- Интеграция задач и событий
- Гибкие настройки отображения

## 🛠 Технологический стек

- **💚 100% Kotlin** — Kotlin 2.2.20
- **🎨 Jetpack Compose** — BOM 2026.09.00, Material 3 Expressive
- **💾 Room Database** — надёжное локальное хранение
- **🔄 Coroutines & Flow** — реактивное программирование
- **🧩 Hilt** — внедрение зависимостей
- **🧭 Navigation Compose** — type-safe навигация
- **📐 Material Design 3** + edge-to-edge
- **🏗 Clean Architecture** — UI / Domain / Data

## 🏗 Архитектура

```mermaid
graph TD
   A[UI Layer<br/>Compose + ViewModel] --> B[Domain Layer<br/>Use Cases + интерфейсы]
   B --> C[Data Layer<br/>Repository Impl + Room]
   C --> D[(Local DB - Room)]
   C -.-> E[(Remote - Future)]
```

### MVVM + Repository
- **UI Layer**: Jetpack Compose + ViewModel
- **Data Layer**: Repository pattern + Room
- **Domain Layer**: _Планируется_ (Use Cases пока отсутствуют)

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
│   ├── dao/            # NoteDao, TodoDao, EventDao
│   ├── models/         # Room-сущности
│   ├── rooms/          # RoomDatabase
│   ├── mapper/         # Note ↔ NoteModel
│   ├── repository/     # NoteRepositoryImpl
│   └── di/             # Hilt-модули
├── domain/
│   ├── model/          # Note
│   ├── repository/     # NoteRepository (интерфейс)
│   └── usecase/        # GetAllNotesUseCase, SaveNoteUseCase, ...
├── ui/
│   ├── app/            # CombinedApp
│   ├── navigation/     # Destinations, AppNavHost, BottomNavBar
│   ├── notes/          # NotesScreen + ViewModel + UiState
│   ├── note_edit/      # NoteEditScreen + ViewModel + UiState
│   ├── screens/        # Todo, Calendar, Settings
│   ├── items/          # NoteItem, TodoItem, EventItem
│   └── theme/          # Material 3
├── PageBookApplication.kt
└── MainActivity.kt
```

## 🧩 Функциональные возможности

| Возможность | Описание                  | Технологии               |
|-------------|---------------------------|--------------------------|
| **Notes**   | Управление заметками 📝   | Compose, Room, ViewModel |
| **Todos**   | To-do листы и задачи ✅   | Compose, StateFlow, Room |
| **Events**  | Планирование и события 🗓️ | Compose, Date Picker     |

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

- [ ] **Синхронизация с облаком** ☁️
- [ ] **Ручное переключение тёмной темы** 🌙
- [ ] **Виджеты для главного экрана** 🏠
- [ ] **Резервное копирование** 💾
- [ ] **Голосовые заметки** 🎤
- [ ] **Перенос Todos и Events на Clean Architecture**
- [ ] **Kotlin Multiplatform** для общей бизнес-логики

## 👨‍💻 Автор

**MaxGog**
- GitHub: [@MaxGog](https://github.com/MaxGog)

## 💬 Обратная связь

Есть вопросы или предложения? Не стесняйтесь:
- Создать [Issue](https://github.com/MaxGog/PageBook-android/issues)
- Написать в Discussions