package ru.guithemes.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.guithemes.ThemeRenderer;

/** Рисуем свой фон поверх ванильного фона любого экрана. */
@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow public int width;
    @Shadow public int height;

    @Inject(method = "renderBackground(Lnet/minecraft/client/gui/DrawContext;)V",
            at = @At("TAIL"), require = 0)
    private void guithemes$renderBackground(DrawContext context, CallbackInfo ci) {
        boolean inWorld = MinecraftClient.getInstance().world != null;
        // на экранах инвентаря/сундуков украшения не рисуем, только оттенок
        boolean decorate = !((Object) this instanceof HandledScreen<?>);
        ThemeRenderer.drawBackground(context, this.width, this.height, inWorld, decorate);
    }
}
