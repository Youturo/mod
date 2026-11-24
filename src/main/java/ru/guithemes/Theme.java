package ru.guithemes;

/** Набор цветов для одной темы. Цвета в формате ARGB. */
public enum Theme {
    // Тёмная: ночное небо со звёздами, фиолетовый акцент
    DARK("dark",
            0xFF07070C, 0xFF17172B,   // фон (верх, низ)
            0xC0070710, 0xD0141428,   // затемнение поверх мира (верх, низ)
            0x40000010,               // лёгкий оттенок на главном экране
            0xFF1B1B26, 0xFF2A2A3C, 0xFF12121A,   // кнопка: обычная, под курсором, выключенная
            0xFF3A3A55, 0xFF8B6CFF, 0xFF24243A,   // рамка: обычная, под курсором, выключенная
            0xFFE8E8F5, 0xFFFFFFFF, 0xFF6A6A7A,   // текст: обычный, под курсором, выключенный
            true),

    // Кошачья: тёплая сливовая комната, персиковые кнопки с ушками, лапки на фоне
    CAT("cat",
            0xFF2B2136, 0xFF40294A,
            0xB0302040, 0xC0442A50,
            0x30502060,
            0xFFFFC98B, 0xFFFFE0B8, 0xFF8A7A78,
            0xFFB5651D, 0xFFFF7FA8, 0xFF5A4A48,
            0xFF4A2C2A, 0xFF2A1210, 0xFF5A4A48,
            false),

    // Неон: тёмный фон, бегущая сетка, светящиеся рамки
    NEON("neon",
            0xFF05030F, 0xFF140826,
            0xB005030F, 0xC0200A30,
            0x40100030,
            0xFF0A0A1E, 0xFF14143A, 0xFF08081A,
            0xFF00E5FF, 0xFFFF2BD6, 0xFF2A4A55,
            0xFFBFFBFF, 0xFFFFFFFF, 0xFF4A6A75,
            true),

    // Выключено: ванильный вид
    OFF("off",
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            false);

    public final String key;
    public final int bgTop, bgBottom;
    public final int worldTop, worldBottom;
    public final int titleTint;
    public final int btn, btnHover, btnOff;
    public final int border, borderHover, borderOff;
    public final int text, textHover, textOff;
    public final boolean shadow;

    Theme(String key,
          int bgTop, int bgBottom,
          int worldTop, int worldBottom,
          int titleTint,
          int btn, int btnHover, int btnOff,
          int border, int borderHover, int borderOff,
          int text, int textHover, int textOff,
          boolean shadow) {
        this.key = key;
        this.bgTop = bgTop;
        this.bgBottom = bgBottom;
        this.worldTop = worldTop;
        this.worldBottom = worldBottom;
        this.titleTint = titleTint;
        this.btn = btn;
        this.btnHover = btnHover;
        this.btnOff = btnOff;
        this.border = border;
        this.borderHover = borderHover;
        this.borderOff = borderOff;
        this.text = text;
        this.textHover = textHover;
        this.textOff = textOff;
        this.shadow = shadow;
    }

    public String langKey() {
        return "guithemes.theme." + key;
    }

    public Theme next() {
        Theme[] all = values();
        return all[(ordinal() + 1) % all.length];
    }

    public static Theme byKey(String key) {
        for (Theme t : values()) {
            if (t.key.equals(key)) return t;
        }
        return DARK;
    }
}
