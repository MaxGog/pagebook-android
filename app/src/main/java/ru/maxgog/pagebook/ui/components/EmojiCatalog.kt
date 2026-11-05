package ru.maxgog.pagebook.ui.components

import androidx.annotation.StringRes
import ru.maxgog.pagebook.R

object EmojiCatalog {
    data class Category(
        @StringRes val nameRes: Int,
        val emojis: List<String>
    )

    val categories: List<Category> = listOf(
        Category(R.string.emoji_cat_smiles, listOf(
            "😀","😃","😄","😁","😊","😎","🤔","😅","😂","🙂",
            "😉","🥰","😍","🤩","😇","🥳","😴","🤯","🥺","😢",
            "😭","😤","😡","🤬","🤗","🤭","🤫","😶","🙄","😬",
            "😷","🤒","🤕","🤢","🤮","🤧","😵","😱","😨","😰"
        )),
        Category(R.string.emoji_cat_work, listOf(
            "💼","🖥️","📊","📈","📉","📋","📝","✏️","📌","📎",
            "🖇️","📁","📂","🗂️","🗃️","🗄️","📅","📆","🗓️","⏰",
            "⌛","⏳","📞","📧","📨","📩","📤","📥","💾","💿"
        )),
        Category(R.string.emoji_cat_ideas, listOf(
            "💡","🎯","🔥","⭐","✨","🌟","💫","🚀","🎨","🎭",
            "🧠","🔮","🎁","🏆","🥇","🥈","🥉","🎉","🎊","💎",
            "🔑","🗝️","🔒","🔓","🔔","🔕","📢","📣","💬","💭"
        )),
        Category(R.string.emoji_cat_study, listOf(
            "📚","📖","📕","📗","📘","📙","📓","📔","📒","🔖",
            "🎓","✏️","🖊️","🖋️","🖌️","🖍️","📐","📏","🔬","🔭",
            "🧪","🧬","🧮","🗺️","🌍","🌎","🌏","🧭","🔍","🔎"
        )),
        Category(R.string.emoji_cat_home, listOf(
            "🏠","🏡","🏘️","🏢","🏬","🏭","🛋️","🛏️","🚪","🪟",
            "🪑","🛁","🚿","🚽","🧴","🧻","🧹","🧺","🧼","🧽",
            "🪣","🔧","🔨","🪛","🪚","⚙️","🔩","🪤","🧯","🔌"
        )),
        Category(R.string.emoji_cat_food, listOf(
            "☕","🍵","🥤","🧃","🥛","🍕","🍔","🍟","🌮","🌯",
            "🍣","🍱","🍜","🍝","🍲","🥗","🍎","🍌","🍇","🍓",
            "🍒","🍰","🎂","🧁","🍪","🍩","🍫","🍬","🍭","🍯"
        )),
        Category(R.string.emoji_cat_nature, listOf(
            "🌿","🌸","🌺","🌻","🌹","🌷","🌼","🍀","🌱","🌳",
            "🌲","🌴","🌵","🌾","🌊","🏔️","⛰️","🌋","🏝️","🏖️",
            "☀️","🌙","⭐","☁️","🌧️","⛈️","❄️","☃️","🌈","🔥"
        )),
        Category(R.string.emoji_cat_sport, listOf(
            "⚽","🏀","🏈","⚾","🎾","🏐","🏉","🎱","🏓","🏸",
            "🥊","🥋","🎽","🛹","🛼","🛷","⛸️","🏂","🏄","🏊",
            "🚴","🚵","🏋️","🤸","⛹️","🤺","🏇","🤼","🤽","🏌️"
        )),
        Category(R.string.emoji_cat_travel, listOf(
            "✈️","🚀","🚁","🚂","🚆","🚇","🚌","🚎","🚐","🚑",
            "🚒","🚓","🚕","🚗","🚙","🚚","🚛","🛴","🚲","🛵",
            "🏍️","🛺","🚢","⛵","🛥️","🚤","⛴️","🛳️","🗽","🗼"
        )),
        Category(R.string.emoji_cat_symbols, listOf(
            "❤️","🧡","💛","💚","💙","💜","🖤","🤍","🤎","💔",
            "❣️","💕","💞","💓","💗","💖","💘","💝","✅","❌",
            "⚠️","🚫","♻️","⚡","💤","💢","💥","💦","💨","🕳️"
        ))
    )

    /**
     * Ключевые слова для поиска (ru + en).
     * Покрывают все эмодзи каталога; для отсутствующих сработает
     * fallback на Unicode-имя (англ.) через Character.getName().
     */
    private val keywords: Map<String, List<String>> = mapOf(
        // 😀 Smiles
        "😀" to listOf("улыбка", "смайл", "grin"),
        "😃" to listOf("улыбка", "радость", "smile"),
        "😄" to listOf("смех", "радость", "smile"),
        "😁" to listOf("ухмылка", "радость", "beam"),
        "😊" to listOf("мило", "улыбка", "blush"),
        "😎" to listOf("крутой", "очки", "cool"),
        "🤔" to listOf("раздумье", "мысль", "think"),
        "😅" to listOf("неловко", "смущение", "sweat"),
        "😂" to listOf("смех", "слёзы", "laugh", "tears"),
        "🙂" to listOf("улыбка", "smile"),
        "😉" to listOf("подмигивание", "wink"),
        "🥰" to listOf("любовь", "влюблён", "hearts"),
        "😍" to listOf("влюблён", "любовь", "heart eyes"),
        "🤩" to listOf("восторг", "звёзды", "star"),
        "😇" to listOf("ангел", "невинность", "halo"),
        "🥳" to listOf("праздник", "вечеринка", "party"),
        "😴" to listOf("сон", "устал", "sleep"),
        "🤯" to listOf("взрыв", "шок", "mind blown"),
        "🥺" to listOf("мольба", "умоляю", "pleading"),
        "😢" to listOf("слёзы", "грусть", "cry"),
        "😭" to listOf("рыдания", "плач", "sob"),
        "😤" to listOf("пар", "злость", "triumph"),
        "😡" to listOf("злость", "гнев", "angry"),
        "🤬" to listOf("мат", "ругань", "swear"),
        "🤗" to listOf("объятия", "обнимаю", "hug"),
        "🤭" to listOf("хитрая", "смешок", "giggle"),
        "🤫" to listOf("тишина", "шшш", "shush"),
        "😶" to listOf("безмолвие", "silent"),
        "🙄" to listOf("закатывает глаза", "eye roll"),
        "😬" to listOf("неловко", "кривая", "grimace"),
        "😷" to listOf("маска", "болезнь", "mask"),
        "🤒" to listOf("болею", "температура", "sick"),
        "🤕" to listOf("травма", "бинт", "bandage"),
        "🤢" to listOf("тошнит", "nausea"),
        "🤮" to listOf("рвота", "vomit"),
        "🤧" to listOf("чихание", "простуда", "sneeze"),
        "😵" to listOf("головокружение", "dizzy"),
        "😱" to listOf("крик", "ужас", "scream"),
        "😨" to listOf("страх", "fear"),
        "😰" to listOf("тревога", "anxious"),

        // 💼 Work
        "💼" to listOf("работа", "портфель", "briefcase", "work"),
        "🖥️" to listOf("компьютер", "desktop"),
        "📊" to listOf("диаграмма", "chart", "bar"),
        "📈" to listOf("рост", "график", "up"),
        "📉" to listOf("спад", "down"),
        "📋" to listOf("список", "задачи", "clipboard"),
        "📝" to listOf("заметка", "запись", "memo"),
        "✏️" to listOf("карандаш", "pencil"),
        "📌" to listOf("булавка", "pin"),
        "📎" to listOf("скрепка", "paperclip"),
        "🖇️" to listOf("скрепки", "paperclips"),
        "📁" to listOf("папка", "folder"),
        "📂" to listOf("папка", "open folder"),
        "🗂️" to listOf("органайзер", "divider"),
        "🗃️" to listOf("архив", "file box"),
        "🗄️" to listOf("шкаф", "cabinet"),
        "📅" to listOf("календарь", "calendar"),
        "📆" to listOf("календарь", "calendar"),
        "🗓️" to listOf("календарь", "calendar"),
        "⏰" to listOf("будильник", "alarm"),
        "⌛" to listOf("время", "hourglass"),
        "⏳" to listOf("время", "hourglass"),
        "📞" to listOf("телефон", "phone"),
        "📧" to listOf("почта", "email"),
        "📨" to listOf("письмо", "envelope"),
        "📩" to listOf("письмо", "envelope"),
        "📤" to listOf("отправить", "outbox"),
        "📥" to listOf("получить", "inbox"),
        "💾" to listOf("дискета", "сохранить", "save", "floppy"),
        "💿" to listOf("диск", "cd"),

        // 💡 Ideas
        "💡" to listOf("идея", "лампочка", "idea", "bulb"),
        "🎯" to listOf("цель", "target"),
        "🔥" to listOf("огонь", "fire"),
        "⭐" to listOf("звезда", "star"),
        "✨" to listOf("искры", "волшебство", "sparkles"),
        "🌟" to listOf("звезда", "star"),
        "💫" to listOf("звёзды", "dizzy"),
        "🚀" to listOf("ракета", "rocket"),
        "🎨" to listOf("искусство", "палитра", "art"),
        "🎭" to listOf("театр", "theater"),
        "🧠" to listOf("мозг", "ум", "brain"),
        "🔮" to listOf("магия", "crystal"),
        "🎁" to listOf("подарок", "gift"),
        "🏆" to listOf("победа", "трофей", "trophy"),
        "🥇" to listOf("первое место", "gold"),
        "🥈" to listOf("второе место", "silver"),
        "🥉" to listOf("третье место", "bronze"),
        "🎉" to listOf("праздник", "party"),
        "🎊" to listOf("конфетти", "confetti"),
        "💎" to listOf("бриллиант", "gem"),
        "🔑" to listOf("ключ", "key"),
        "🗝️" to listOf("ключ", "old key"),
        "🔒" to listOf("замок", "lock"),
        "🔓" to listOf("открыто", "unlock"),
        "🔔" to listOf("звонок", "bell"),
        "🔕" to listOf("без звука", "mute"),
        "📢" to listOf("громкоговоритель", "loudspeaker"),
        "📣" to listOf("рупор", "megaphone"),
        "💬" to listOf("сообщение", "чат", "message"),
        "💭" to listOf("мысль", "thought"),

        // 📚 Study
        "📚" to listOf("книги", "books"),
        "📖" to listOf("книга", "book"),
        "📕" to listOf("книга", "book"),
        "📗" to listOf("книга", "book"),
        "📘" to listOf("книга", "book"),
        "📙" to listOf("книга", "book"),
        "📓" to listOf("блокнот", "notebook"),
        "📔" to listOf("блокнот", "notebook"),
        "📒" to listOf("журнал", "ledger"),
        "🔖" to listOf("закладка", "bookmark"),
        "🎓" to listOf("выпускник", "учёба", "graduation"),
        "🖊️" to listOf("ручка", "pen"),
        "🖋️" to listOf("ручка", "fountain"),
        "🖌️" to listOf("кисть", "brush"),
        "🖍️" to listOf("мелок", "crayon"),
        "📐" to listOf("линейка", "треугольник", "ruler"),
        "📏" to listOf("линейка", "ruler"),
        "🔬" to listOf("микроскоп", "microscope"),
        "🔭" to listOf("телескоп", "telescope"),
        "🧪" to listOf("химия", "test tube"),
        "🧬" to listOf("днк", "dna"),
        "🧮" to listOf("счёты", "abacus"),
        "🗺️" to listOf("карта", "map"),
        "🌍" to listOf("земля", "earth"),
        "🌎" to listOf("земля", "earth"),
        "🌏" to listOf("земля", "earth"),
        "🧭" to listOf("компас", "compass"),
        "🔍" to listOf("поиск", "лупа", "search"),
        "🔎" to listOf("поиск", "лупа", "search"),

        // 🏠 Home
        "🏠" to listOf("дом", "house"),
        "🏡" to listOf("дом", "house"),
        "🏘️" to listOf("дома", "houses"),
        "🏢" to listOf("офис", "office"),
        "🏬" to listOf("магазин", "store"),
        "🏭" to listOf("завод", "factory"),
        "🛋️" to listOf("диван", "couch"),
        "🛏️" to listOf("кровать", "bed"),
        "🚪" to listOf("дверь", "door"),
        "🪟" to listOf("окно", "window"),
        "🪑" to listOf("стул", "chair"),
        "🛁" to listOf("ванна", "bath"),
        "🚿" to listOf("душ", "shower"),
        "🚽" to listOf("туалет", "toilet"),
        "🧴" to listOf("лосьон", "lotion"),
        "🧻" to listOf("бумага", "paper"),
        "🧹" to listOf("метла", "broom"),
        "🧺" to listOf("корзина", "basket"),
        "🧼" to listOf("мыло", "soap"),
        "🧽" to listOf("губка", "sponge"),
        "🪣" to listOf("ведро", "bucket"),
        "🔧" to listOf("гаечный ключ", "wrench"),
        "🔨" to listOf("молоток", "hammer"),
        "🪛" to listOf("отвёртка", "screwdriver"),
        "🪚" to listOf("пила", "saw"),
        "⚙️" to listOf("шестерёнка", "gear"),
        "🔩" to listOf("болт", "bolt"),
        "🪤" to listOf("мышеловка", "trap"),
        "🧯" to listOf("огнетушитель", "extinguisher"),
        "🔌" to listOf("вилка", "plug"),

        // ☕ Food
        "☕" to listOf("кофе", "coffee"),
        "🍵" to listOf("чай", "tea"),
        "🥤" to listOf("напиток", "drink"),
        "🧃" to listOf("сок", "juice"),
        "🥛" to listOf("молоко", "milk"),
        "🍕" to listOf("пицца", "pizza"),
        "🍔" to listOf("бургер", "burger"),
        "🍟" to listOf("картошка фри", "fries"),
        "🌮" to listOf("тако", "taco"),
        "🌯" to listOf("буррито", "burrito"),
        "🍣" to listOf("суши", "sushi"),
        "🍱" to listOf("бенто", "bento"),
        "🍜" to listOf("рамен", "лапша", "ramen"),
        "🍝" to listOf("паста", "spaghetti"),
        "🍲" to listOf("суп", "soup"),
        "🥗" to listOf("салат", "salad"),
        "🍎" to listOf("яблоко", "apple"),
        "🍌" to listOf("банан", "banana"),
        "🍇" to listOf("виноград", "grapes"),
        "🍓" to listOf("клубника", "strawberry"),
        "🍒" to listOf("вишня", "cherry"),
        "🍰" to listOf("торт", "cake"),
        "🎂" to listOf("торт", "день рождения", "birthday"),
        "🧁" to listOf("капкейк", "cupcake"),
        "🍪" to listOf("печенье", "cookie"),
        "🍩" to listOf("пончик", "donut"),
        "🍫" to listOf("шоколад", "chocolate"),
        "🍬" to listOf("конфета", "candy"),
        "🍭" to listOf("леденец", "lollipop"),
        "🍯" to listOf("мёд", "honey"),

        // 🌿 Nature
        "🌿" to listOf("трава", "herb"),
        "🌸" to listOf("цветок", "сакура", "blossom"),
        "🌺" to listOf("цветок", "hibiscus"),
        "🌻" to listOf("подсолнух", "sunflower"),
        "🌹" to listOf("роза", "rose"),
        "🌷" to listOf("тюльпан", "tulip"),
        "🌼" to listOf("цветок", "blossom"),
        "🍀" to listOf("клевер", "удача", "clover"),
        "🌱" to listOf("росток", "seedling"),
        "🌳" to listOf("дерево", "tree"),
        "🌲" to listOf("ёлка", "evergreen"),
        "🌴" to listOf("пальма", "palm"),
        "🌵" to listOf("кактус", "cactus"),
        "🌾" to listOf("пшеница", "grain"),
        "🌊" to listOf("волна", "wave"),
        "🏔️" to listOf("гора", "mountain"),
        "⛰️" to listOf("гора", "mountain"),
        "🌋" to listOf("вулкан", "volcano"),
        "🏝️" to listOf("остров", "island"),
        "🏖️" to listOf("пляж", "beach"),
        "☀️" to listOf("солнце", "sun"),
        "🌙" to listOf("луна", "moon"),
        "☁️" to listOf("облако", "cloud"),
        "🌧️" to listOf("дождь", "rain"),
        "⛈️" to listOf("гроза", "thunder"),
        "❄️" to listOf("снег", "snow"),
        "☃️" to listOf("снеговик", "snowman"),
        "🌈" to listOf("радуга", "rainbow"),

        // ⚽ Sport
        "⚽" to listOf("футбол", "soccer"),
        "🏀" to listOf("баскетбол", "basketball"),
        "🏈" to listOf("американский футбол", "football"),
        "⚾" to listOf("бейсбол", "baseball"),
        "🎾" to listOf("теннис", "tennis"),
        "🏐" to listOf("волейбол", "volleyball"),
        "🏉" to listOf("регби", "rugby"),
        "🎱" to listOf("бильярд", "billiards"),
        "🏓" to listOf("пинг-понг", "ping pong"),
        "🏸" to listOf("бадминтон", "badminton"),
        "🥊" to listOf("бокс", "boxing"),
        "🥋" to listOf("карате", "martial arts"),
        "🎽" to listOf("бег", "running"),
        "🛹" to listOf("скейт", "skate"),
        "🛼" to listOf("ролики", "roller"),
        "🛷" to listOf("санки", "sled"),
        "⛸️" to listOf("коньки", "ice skate"),
        "🏂" to listOf("сноуборд", "snowboard"),
        "🏄" to listOf("сёрфинг", "surf"),
        "🏊" to listOf("плавание", "swim"),
        "🚴" to listOf("велосипед", "cycle"),
        "🚵" to listOf("велосипед", "mountain bike"),
        "🏋️" to listOf("штанга", "weight lifting"),
        "🤸" to listOf("гимнастика", "gymnastics"),
        "⛹️" to listOf("баскетбол", "basketball"),
        "🤺" to listOf("фехтование", "fencing"),
        "🏇" to listOf("скачки", "horse racing"),
        "🤼" to listOf("борьба", "wrestling"),
        "🤽" to listOf("водное поло", "water polo"),
        "🏌️" to listOf("гольф", "golf"),

        // ✈️ Travel
        "✈️" to listOf("самолёт", "airplane"),
        "🚁" to listOf("вертолёт", "helicopter"),
        "🚂" to listOf("поезд", "train"),
        "🚆" to listOf("поезд", "train"),
        "🚇" to listOf("метро", "metro"),
        "🚌" to listOf("автобус", "bus"),
        "🚎" to listOf("троллейбус", "trolley"),
        "🚐" to listOf("микроавтобус", "minibus"),
        "🚑" to listOf("скорая", "ambulance"),
        "🚒" to listOf("пожарная", "fire"),
        "🚓" to listOf("полиция", "police"),
        "🚕" to listOf("такси", "taxi"),
        "🚗" to listOf("машина", "car"),
        "🚙" to listOf("машина", "suv"),
        "🚚" to listOf("грузовик", "truck"),
        "🚛" to listOf("фура", "lorry"),
        "🛴" to listOf("самокат", "scooter"),
        "🚲" to listOf("велосипед", "bicycle"),
        "🛵" to listOf("скутер", "motor scooter"),
        "🏍️" to listOf("мотоцикл", "motorcycle"),
        "🛺" to listOf("тук-тук", "rickshaw"),
        "🚢" to listOf("корабль", "ship"),
        "⛵" to listOf("парусник", "sailboat"),
        "🛥️" to listOf("катер", "motor boat"),
        "🚤" to listOf("катер", "speedboat"),
        "⛴️" to listOf("паром", "ferry"),
        "🛳️" to listOf("лайнер", "passenger ship"),
        "🗽" to listOf("статуя свободы", "statue"),
        "🗼" to listOf("башня", "tower"),

        // ❤️ Symbols
        "❤️" to listOf("сердце", "любовь", "heart"),
        "🧡" to listOf("сердце", "heart"),
        "💛" to listOf("сердце", "heart"),
        "💚" to listOf("сердце", "heart"),
        "💙" to listOf("сердце", "heart"),
        "💜" to listOf("сердце", "heart"),
        "🖤" to listOf("сердце", "heart"),
        "🤍" to listOf("сердце", "heart"),
        "🤎" to listOf("сердце", "heart"),
        "💔" to listOf("разбитое сердце", "broken heart"),
        "❣️" to listOf("сердце", "heart"),
        "💕" to listOf("сердца", "hearts"),
        "💞" to listOf("сердца", "hearts"),
        "💓" to listOf("сердце", "heartbeat"),
        "💗" to listOf("сердце", "heart"),
        "💖" to listOf("сердце", "heart"),
        "💘" to listOf("сердце", "heart"),
        "💝" to listOf("сердце", "heart"),
        "✅" to listOf("галочка", "да", "check"),
        "❌" to listOf("крест", "нет", "cross"),
        "⚠️" to listOf("внимание", "warning"),
        "🚫" to listOf("запрет", "prohibited"),
        "♻️" to listOf("переработка", "recycling"),
        "⚡" to listOf("молния", "lightning"),
        "💤" to listOf("сон", "zzz"),
        "💢" to listOf("злость", "anger"),
        "💥" to listOf("взрыв", "collision"),
        "💦" to listOf("вода", "sweat"),
        "💨" to listOf("ветер", "dash"),
        "🕳️" to listOf("дыра", "hole")
    )

    fun search(query: String, categoryNames: List<String>): List<String> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        val raw = query.trim()

        return categories.flatMapIndexed { idx, cat ->
            val catName = categoryNames.getOrNull(idx)?.lowercase().orEmpty()
            val byCategory = catName.isNotEmpty() && catName.contains(q)
            cat.emojis.filter { emoji ->
                byCategory ||
                        emoji == raw ||
                        keywords[emoji]?.any { it.lowercase().contains(q) } == true ||
                        unicodeName(emoji)?.lowercase()?.contains(q) == true
            }
        }.distinct()
    }

    private fun unicodeName(emoji: String): String? = try {
        Character.getName(emoji.codePointAt(0))
    } catch (_: IllegalArgumentException) {
        null
    }
}