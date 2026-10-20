# Changelog

Все значимые изменения проекта документируются в этом файле.

Формат основан на [Keep a Changelog](https://keepachangelog.com/ru/1.1.0/),
проект придерживается [Semantic Versioning](https://semver.org/lang/ru/).

## [Unreleased]

### Added
- Черновик документации: `ARCHITECTURE.md`, `CONTRIBUTING.md`, `CHANGELOG.md`.

### Changed
- README приведён в соответствие с фактической структурой проекта (`ru.maxgog.pagebook`, пакет `rooms/`).

### Planned
- Синхронизация с облаком.
- Тёмная тема.
- Виджеты для главного экрана.
- Резервное копирование.
- Голосовые заметки.
- Выделение Domain Layer (Use Cases).
- Переход на Hilt (при росте числа зависимостей).

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