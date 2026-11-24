package ru.guithemes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class GuiThemesClient implements ClientModInitializer {
    private static KeyBinding cycleKey;

    @Override
    public void onInitializeClient() {
        Config.load();

        // Клавиша F6 — переключить тему прямо в игре (когда нет открытых окон)
        cycleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.guithemes.cycle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F6, "category.guithemes"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (cycleKey.wasPressed()) {
                Config.cycle();
                if (client.player != null) {
                    client.player.sendMessage(ThemeRenderer.chipText(), true);
                }
            }
        });

        // Для каждого открывшегося экрана: перерисовываем кнопки и добавляем «плашку» смены темы
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof HandledScreen<?>) return;

            ScreenEvents.afterRender(screen).register((scr, ctx, mouseX, mouseY, tickDelta) ->
                    ThemeRenderer.drawOverlay(scr, ctx, mouseX, mouseY));

            if (ThemeRenderer.hasChip(screen)) {
                ScreenMouseEvents.allowMouseClick(screen).register((scr, mouseX, mouseY, button) -> {
                    if (button == 0 && ThemeRenderer.isOverChip(scr, mouseX, mouseY)) {
                        Config.cycle();
                        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                        return false; // клик обработан нами
                    }
                    return true;
                });
            }
        });
    }
}
