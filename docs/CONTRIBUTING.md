# 🤝 Участие в разработке PageBook Android

Спасибо за интерес к проекту! Этот документ описывает, как настроить окружение, придерживаться стиля кода и правильно отправлять Pull Request.

## 📜 Кодекс поведения

Участвуя в проекте, вы соглашаетесь соблюдать [Contributor Covenant](https://www.contributor-covenant.org/version/2/1/code_of_conduct/). Будьте уважительны и конструктивны.

## 🐛 Как сообщить о баге

Перед созданием Issue проверьте, не описана ли проблема уже в существующих [Issues](https://github.com/MaxGog/PageBook-android/issues).

Хороший баг-репорт содержит:
- **Краткое и понятное название** проблемы.
- **Шаги для воспроизведения**: что вы сделали, что ожидали увидеть, что произошло на самом деле.
- **Версию приложения** (см. [CHANGELOG.md](CHANGELOG.md)).
- **Версию Android** и модель устройства.
- **Логи** (Logcat), если приложение падает.
- **Скриншоты или видео** — приветствуются.

Шаблон:

```markdown
### Описание
Кратко о проблеме.

### Шаги для воспроизведения
1. Открыть экран ...
2. Нажать ...
3. ...

### Ожидаемое поведение
...

### Фактическое поведение
...

### Окружение
- Устройство: Pixel 7
- Android: 16
- Версия PageBook: 0.2.0-dev (build 6)
```

## 💡 Как предложить улучшение

1. Откройте Issue с меткой `enhancement`.
2. Опишите **проблему**, а не только решение: «Я хочу X, потому что Y».
3. Обсудите с мейнтейнером, прежде чем писать код — это сэкономит время обеим сторонам.

## 🛠 Настройка окружения

### Требования
- **Android Studio** Otter (2026.1) или новее
- **JDK 21**
- **Android SDK 37** (через SDK Manager)
- **Kotlin 2.2.20+**
- **Git**

### Шаги
```bash
# 1. Форкните репозиторий через UI GitHub

# 2. Клонируйте свой форк
git clone https://github.com/<ваш-username>/PageBook-android.git
cd PageBook-android

# 3. Добавьте upstream
git remote add upstream https://github.com/MaxGog/PageBook-android.git

# 4. Откройте в Android Studio и дождитесь Gradle Sync
```

## 🌿 Работа с ветками

Мы используем **feature branches** от `main`:

| Тип ветки   | Пример                        | Назначение                          |
|-------------|-------------------------------|-------------------------------------|
| `feature/`  | `feature/note-search`         | Новая функциональность              |
| `fix/`      | `fix/crash-on-empty-tasks`    | Исправление бага                    |
| `docs/`     | `docs/architecture-update`    | Только документация                 |
| `refactor/` | `refactor/di-cleanup`         | Рефакторинг без изменений поведения |

```bash
git checkout -b feature/my-awesome-feature
```

## 🏗 Куда добавлять код

Проект следует **Clean Architecture**. Каждая фича проходит через три слоя — не «перепрыгивайте» их.

### Правило потока

```
UI (Compose + ViewModel) → Domain (UseCase) → Repository (interface) → Data (Impl + DAO + Room)
```

- **UI** не знает про Room и Entity.
- **Domain** не знает про Android SDK и Hilt-аннотации (кроме `@Inject` в UseCase).
- **Data** — единственный слой, который работает с `@Entity`, `@Dao` и `RoomDatabase`.

### Куда что класть

| Что добавляете | Куда | Пример |
| :--- | :--- | :--- |
| Новую бизнес-операцию | `domain/usecase/<module>/` | `ArchiveTaskUseCase.kt` |
| Новую доменную модель | `domain/model/` | `Tag.kt` |
| Новый источник данных | `data/repository/` + `domain/repository/` | `TagRepository` + `TagRepositoryImpl` |
| Новую таблицу Room | `data/models/`, `data/dao/`, `data/rooms/` | `TagModel`, `TagDao`, `TagRoomDatabase` |
| Маппер Entity ↔ Domain | `data/mapper/` | `TagMapper.kt` |
| Новый экран | `ui/<module>/` | `ui/tags/TagsScreen.kt` + `TagsViewModel.kt` + `TagsUiState.kt` |

### Три независимые базы данных

Сейчас в проекте **три БД**: `notes_db`, `tasks_db`, `events_db`. Это осознанное решение — модули изолированы. **Не создавайте новую БД** без обсуждения: скорее всего, сущность логичнее добавить в одну из существующих.

Связь `Task → Note` реализована через `noteId` (без `ForeignKey`, так как FK между разными БД невозможен в Room). При удалении заметки `DeleteNoteUseCase` обнуляет `noteId` у связанных задач. Если добавляете новую связь между модулями — придерживайтесь того же паттерна.

## ✍️ Стиль кода

### Kotlin
- **Официальный [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)**.
- Отступы — **4 пробела**, длина строки — **до 120 символов**.
- Пакеты в нижнем регистре: `ru.maxgog.pagebook.ui.app`.
- Классы — `PascalCase`, функции и переменные — `camelCase`, константы — `UPPER_SNAKE_CASE`.
- **KDoc** для всех публичных классов и функций доменного слоя:
  ```kotlin
  /**
   * Возвращает все заметки, отсортированные по дате создания (новые первыми).
   *
   * @return [Flow] с доменными моделями [Note].
   */
  fun getAllNotes(): Flow<List<Note>>
  ```

### Compose
- Каждый Composable — отдельная функция в `PascalCase`.
- Имя файла совпадает с именем основного Composable.
- Превью-функции — с суффиксом `Preview` и аннотацией `@Preview`.
- Подписки на `StateFlow` — только через `collectAsStateWithLifecycle()`.
- Отступы edge-to-edge — через `contentPadding` в `LazyColumn`/`LazyGrid`, а не `Modifier.padding` на всём списке.

### Clean Architecture
- **Domain Layer** не должен зависеть от Android SDK, Room и Hilt-компонентов.
- Domain-модели (`domain/model/`) — чистый Kotlin, без `@Entity` и `@ColumnInfo`.
- ViewModel работает только с Use Cases, а не с репозиториями напрямую.
- Каждый экран оформляется как `UiState` (sealed interface или data class) + ViewModel + Screen.
- Используйте `suspend` и `Flow` — не блокируйте главный поток.

### Проверка стиля
```bash
./gradlew ktlintCheck
```

Перед PR обязательно убедитесь, что `ktlintCheck` проходит без ошибок.

## ✅ Тестирование

```bash
# Unit-тесты
./gradlew test

# Инструментальные тесты (нужен эмулятор/устройство)
./gradlew connectedAndroidTest
```

### Что тестировать в первую очередь

| Слой | Что тестируем | Как |
| :--- | :--- | :--- |
| **Domain (UseCase)** | Бизнес-логика | Фейковые `FakeNoteRepository`, `FakeTaskRepository` (in-memory `MutableStateFlow`) |
| **Data (Repository Impl)** | Маппинг + запросы | In-memory Room (`Room.inMemoryDatabaseBuilder`) |
| **UI (ViewModel)** | `UiState` переходы | `kotlinx-coroutines-test` + `Turbine` |

Пример unit-теста UseCase:

```kotlin
class SaveTaskUseCaseTest {
    private val repository = FakeTaskRepository()
    private val useCase = SaveTaskUseCase(repository)

    @Test
    fun `insert called when id is zero`() = runTest {
        useCase(Task(title = "New"))
        assertEquals(1, repository.inserted.size)
        assertEquals(0, repository.updated.size)
    }

    @Test
    fun `update called when id is not zero`() = runTest {
        useCase(Task(id = 42, title = "Existing"))
        assertEquals(0, repository.inserted.size)
        assertEquals(1, repository.updated.size)
    }
}
```

**Требования к PR:**
- Новая функциональность → добавлены unit-тесты для UseCase или ViewModel.
- Исправление бага → по возможности добавлен регрессионный тест.
- Все существующие тесты проходят.

## 📝 Формат коммитов

Мы следуем [Conventional Commits](https://www.conventionalcommits.org/ru/v1.0.0/):

```
<тип>(<область>): <краткое описание>

[необязательное тело]

[необязательные футеры]
```

**Типы:**
- `feat` — новая функциональность
- `fix` — исправление бага
- `docs` — только документация
- `style` — форматирование, пробелы (не меняет логику)
- `refactor` — рефакторинг
- `test` — добавление/изменение тестов
- `chore` — обновление зависимостей, конфигурации
- `perf` — оптимизация производительности

**Области (scope):** `notes`, `note_edit`, `tasks`, `task_edit`, `calendar`, `navigation`, `di`, `build`, `docs`.

**Примеры:**
```
feat(notes): добавить поиск по содержимому
feat(tasks): создавать задачу из выделенного текста заметки
fix(tasks): исправить падение при пустом списке
fix(calendar): скорректировать скролл к выбранной дате
refactor(tasks): переименовать Todo → Task
docs(readme): обновить структуру проекта
chore(deps): обновить Compose BOM до 2026.09.00
```

## 🔧 Процесс Pull Request

1. **Синхронизируйтесь с upstream:**
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Убедитесь, что всё собирается и проходит проверки:**
   ```bash
   ./gradlew assembleDebug ktlintCheck test
   ```

3. **Запушьте ветку в свой форк:**
   ```bash
   git push origin feature/my-awesome-feature
   ```

4. **Откройте Pull Request** в `MaxGog/PageBook-android:main`.

5. **Заполните шаблон PR:**
   - Что сделано и зачем.
   - Ссылка на Issue (`Closes #123`).
   - Скриншоты/видео для UI-изменений.
   - Чек-лист: тесты, ktlint, документация обновлена.

6. **Дождитесь ревью.** Адресуйте комментарии новыми коммитами (не force-push без необходимости).

### Что НЕ приветствуется
- Смешивание рефакторинга и функциональных изменений в одном PR.
- Изменение форматирования во всём файле «заодно».
- PR без описания или без связи с Issue.
- Прямая работа ViewModel с `*Model` (Entity) — используйте доменные модели.
- Создание новой БД без обсуждения.
- Игнорирование замечаний ревьюера.

## 🏷️ Good First Issues

Ищете, с чего начать? Смотрите задачи с метками:
- `good first issue` — для новичков.
- `help wanted` — нужна помощь мейнтейнера.

## 📬 Вопросы

- **GitHub Discussions** — для общих вопросов и идей.
- **GitHub Issues** — для багов и конкретных предложений.