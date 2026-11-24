package ru.guithemes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Вся отрисовка тем: фон, кнопки, украшения и плашка смены темы. */
public final class ThemeRenderer {
    private ThemeRenderer() {}

    // Пиксельный кот: # — шерсть, p — розовое, e — глаз, n — нос, . — пусто
    private static final String[] CAT = {
            "##..........##",
            "#p##......##p#",
            "##############",
            "##############",
            "###ee####ee###",
            "###ee####ee###",
            "##############",
            "######nn######",
            "##############",
            ".############.",
            "..##########.."
    };

    // ------------------------------------------------------------------ фон

    public static void drawBackground(DrawContext ctx, int w, int h, boolean inWorld, boolean decorate) {
        Theme t = Config.theme();
        if (t == Theme.OFF || w <= 0 || h <= 0) return;

        if (inWorld) {
            ctx.fillGradient(0, 0, w, h, t.worldTop, t.worldBottom);
        } else {
            ctx.fillGradient(0, 0, w, h, t.bgTop, t.bgBottom);
        }
        if (!decorate) return;

        long time = System.currentTimeMillis();
        switch (t) {
            case DARK -> {
                if (!inWorld) drawStars(ctx, w, h, time);
            }
            case CAT -> {
                if (!inWorld) drawPaws(ctx, w, h, time);
                drawCat(ctx, h, time);
            }
            case NEON -> {
                if (!inWorld) drawGrid(ctx, w, h, time);
            }
            default -> { }
        }
    }

    private static void drawStars(DrawContext ctx, int w, int h, long time) {
        for (int i = 0; i < 90; i++) {
            int x = Math.floorMod(i * 73856093 ^ 0x5bd1e995, w);
            int y = Math.floorMod(i * 19349663 ^ 0x27d4eb2f, h);
            boolean bright = ((time / 500) + i) % 7 == 0;
            int c = bright ? 0xFFFFFFFF : (i % 3 == 0 ? 0x90B8B8FF : 0x50B8B8FF);
            int s = (i % 11 == 0) ? 2 : 1;
            ctx.fill(x, y, x + s, y + s, c);
        }
    }

    private static void drawPaws(DrawContext ctx, int w, int h, long time) {
        int step = 64;
        int drift = (int) ((time / 120) % step); // медленно плывём вправо
        for (int row = -1; row * step < h + step; row++) {
            for (int col = -1; col * step < w + step; col++) {
                int x = col * step + ((row & 1) == 0 ? 0 : step / 2) + drift;
                int y = row * step + 8;
                drawPaw(ctx, x, y, 2, 0x28FFB3D1);
            }
        }
    }

    /** Лапка из прямоугольников без наложений (чтобы прозрачность не удваивалась). */
    private static void drawPaw(DrawContext ctx, int x, int y, int s, int c) {
        ctx.fill(x, y + 4 * s, x + 2 * s, y + 7 * s, c);            // внешний палец слева
        ctx.fill(x + 3 * s, y + s, x + 5 * s, y + 4 * s, c);        // внутренний слева
        ctx.fill(x + 7 * s, y + s, x + 9 * s, y + 4 * s, c);        // внутренний справа
        ctx.fill(x + 10 * s, y + 4 * s, x + 12 * s, y + 7 * s, c);  // внешний справа
        ctx.fill(x + 3 * s, y + 6 * s, x + 9 * s, y + 7 * s, c);    // подушечка, верх
        ctx.fill(x + 2 * s, y + 7 * s, x + 10 * s, y + 10 * s, c);  // подушечка, середина
        ctx.fill(x + 3 * s, y + 10 * s, x + 9 * s, y + 11 * s, c);  // подушечка, низ
    }

    /** Кот в левом нижнем углу: моргает раз в 4 секунды, усы по бокам. */
    private static void drawCat(DrawContext ctx, int h, long time) {
        int s = 3;
        int cols = CAT[0].length();
        int x0 = 10;
        int y0 = h - CAT.length * s - 10;
        boolean blink = (time % 4000) < 150;

        for (int r = 0; r < CAT.length; r++) {
            String row = CAT[r];
            for (int c = 0; c < cols; c++) {
                char ch = row.charAt(c);
                if (ch == '.') continue;
                int color;
                switch (ch) {
                    case 'p' -> color = 0xFFFFA3C4;
                    case 'n' -> color = 0xFFFF7FA8;
                    case 'e' -> color = (blink && r == 4) ? 0xFFE8A04C : 0xFF2A1A20;
                    default -> color = 0xFFE8A04C;
                }
                ctx.fill(x0 + c * s, y0 + r * s, x0 + (c + 1) * s, y0 + (r + 1) * s, color);
            }
        }
        // усы
        int wy = y0 + 7 * s;
        int right = x0 + cols * s;
        ctx.fill(x0 - 12, wy, x0, wy + 1, 0xB0FFFFFF);
        ctx.fill(x0 - 10, wy + 4, x0, wy + 5, 0xB0FFFFFF);
        ctx.fill(right, wy, right + 12, wy + 1, 0xB0FFFFFF);
        ctx.fill(right, wy + 4, right + 10, wy + 5, 0xB0FFFFFF);
    }

    private static void drawGrid(DrawContext ctx, int w, int h, long time) {
        int step = 32;
        int off = (int) ((time / 50) % step); // сетка медленно едет вниз
        int line = 0x2800E5FF;
        for (int x = 0; x < w; x += step) {
            ctx.fill(x, 0, x + 1, h, line);
        }
        for (int y = off - step; y < h; y += step) {
            ctx.fill(0, y, w, y + 1, line);
        }
        ctx.fillGradient(0, h * 2 / 3, w, h, 0x00FF2BD6, 0x50FF2BD6);
        ctx.fill(0, 0, w, 1, 0xFF00E5FF);
    }

    // -------------------------------------------------- кнопки и плашка

    /** Рисуется после экрана: перекрашивает кнопки и рисует плашку смены темы. */
    public static void drawOverlay(Screen screen, DrawContext ctx, int mouseX, int mouseY) {
        Theme t = Config.theme();
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        if (t != Theme.OFF) {
            if (screen instanceof TitleScreen) {
                ctx.fill(0, 0, screen.width, screen.height, t.titleTint);
            }
            for (Element e : screen.children()) {
                if (e instanceof ButtonWidget b) {
                    drawButton(ctx, tr, t, b);
                }
            }
        }
        if (hasChip(screen)) {
            drawChip(ctx, tr, t, screen.width, mouseX, mouseY);
        }
    }

    private static void drawButton(DrawContext ctx, TextRenderer tr, Theme t, ButtonWidget b) {
        // маленькие кнопки-иконки и кнопки без подписи оставляем ванильными
        if (!b.visible || b.getWidth() < 40) return;
        Text msg = b.getMessage();
        if (msg.getString().isEmpty()) return;

        int x = b.getX();
        int y = b.getY();
        int w = b.getWidth();
        int h = b.getHeight();
        boolean on = b.active;
        boolean hover = on && b.isHovered();

        int fill = !on ? t.btnOff : hover ? t.btnHover : t.btn;
        int border = !on ? t.borderOff : hover ? t.borderHover : t.border;
        int text = !on ? t.textOff : hover ? t.textHover : t.text;

        if (t == Theme.NEON) {
            frame(ctx, x - 1, y - 1, w + 2, h + 2, (border & 0x00FFFFFF) | 0x55000000); // свечение
        }
        if (t == Theme.CAT) {
            drawEars(ctx, x, y, w, border);
        }
        ctx.fill(x, y, x + w, y + h, fill);
        frame(ctx, x, y, w, h, border);
        if (b.isFocused()) {
            frame(ctx, x - 1, y - 1, w + 2, h + 2, 0xFFFFFFFF);
        }
        int tx = x + (w - tr.getWidth(msg)) / 2;
        int ty = y + (h - 8) / 2;
        ctx.drawText(tr, msg, tx, ty, text, t.shadow);
    }

    /** Кошачьи ушки над кнопкой. */
    private static void drawEars(DrawContext ctx, int x, int y, int w, int c) {
        ctx.fill(x + 5, y - 3, x + 7, y - 2, c);
        ctx.fill(x + 4, y - 2, x + 8, y - 1, c);
        ctx.fill(x + 3, y - 1, x + 9, y, c);
        int r = x + w;
        ctx.fill(r - 7, y - 3, r - 5, y - 2, c);
        ctx.fill(r - 8, y - 2, r - 4, y - 1, c);
        ctx.fill(r - 9, y - 1, r - 3, y, c);
    }

    private static void frame(DrawContext ctx, int x, int y, int w, int h, int c) {
        ctx.fill(x, y, x + w, y + 1, c);
        ctx.fill(x, y + h - 1, x + w, y + h, c);
        ctx.fill(x, y + 1, x + 1, y + h - 1, c);
        ctx.fill(x + w - 1, y + 1, x + w, y + h - 1, c);
    }

    // ------------------------------------------------------------ плашка

    public static boolean hasChip(Screen s) {
        return s instanceof TitleScreen || s instanceof GameMenuScreen || s instanceof OptionsScreen;
    }

    public static Text chipText() {
        return Text.translatable("guithemes.chip", Text.translatable(Config.theme().langKey()));
    }

    private static int[] chipBounds(int screenWidth, TextRenderer tr) {
        int w = tr.getWidth(chipText()) + 12;
        int h = 14;
        return new int[] { screenWidth - w - 4, 4, w, h };
    }

    public static boolean isOverChip(Screen screen, double mx, double my) {
        int[] r = chipBounds(screen.width, MinecraftClient.getInstance().textRenderer);
        return mx >= r[0] && mx < r[0] + r[2] && my >= r[1] && my < r[1] + r[3];
    }

    private static void drawChip(DrawContext ctx, TextRenderer tr, Theme t, int screenWidth, int mx, int my) {
        int[] r = chipBounds(screenWidth, tr);
        boolean hover = mx >= r[0] && mx < r[0] + r[2] && my >= r[1] && my < r[1] + r[3];
        boolean off = t == Theme.OFF;

        int fill = off ? 0xCC000000 : (hover ? t.btnHover : t.btn);
        int border = off ? (hover ? 0xFFFFFFFF : 0xFFA0A0A0) : (hover ? t.borderHover : t.border);
        int text = off ? 0xFFFFFFFF : t.text;

        ctx.fill(r[0], r[1], r[0] + r[2], r[1] + r[3], fill);
        frame(ctx, r[0], r[1], r[2], r[3], border);
        ctx.drawText(tr, chipText(), r[0] + 6, r[1] + 3, text, off || t.shadow);
    }
}
