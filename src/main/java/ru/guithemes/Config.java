package ru.guithemes;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Хранит выбранную тему в config/guithemes.properties. */
public final class Config {
    private static Theme theme = Theme.DARK;

    private Config() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("guithemes.properties");
    }

    public static Theme theme() {
        return theme;
    }

    public static void cycle() {
        theme = theme.next();
        save();
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        try (Reader r = Files.newBufferedReader(f)) {
            Properties p = new Properties();
            p.load(r);
            theme = Theme.byKey(p.getProperty("theme"));
        } catch (IOException | RuntimeException e) {
            System.out.println("[GUI Themes] не удалось прочитать конфиг: " + e);
        }
    }

    private static void save() {
        try (Writer w = Files.newBufferedWriter(file())) {
            Properties p = new Properties();
            p.setProperty("theme", theme.key);
            p.store(w, "GUI Themes");
        } catch (IOException | RuntimeException e) {
            System.out.println("[GUI Themes] не удалось сохранить конфиг: " + e);
        }
    }
}
