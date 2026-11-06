package es.furynocturn.viaviewer.config;

import es.furynocturn.viaviewer.FNViaViewer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Pantalla propia (sin Cloth/YACL): interruptor general, uno por tipo y volver a bajar los assets. */
public final class ConfigScreen extends Screen {

    private static final int WIDTH = 220;
    private static final int COLUMN = 150;
    private final Screen parent;

    public ConfigScreen(final Screen parent) {
        super(Component.translatable("fnviaviewer.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        final ViewerConfig config = ViewerConfig.get();
        final int x = (this.width - WIDTH) / 2;
        int y = this.height / 6 + 12;

        this.addRenderableWidget(CycleButton.onOffBuilder(config.enabled)
            .withTooltip(value -> Tooltip.create(Component.translatable("fnviaviewer.config.enabled.tooltip")))
            .create(x, y, WIDTH, 20, Component.translatable("fnviaviewer.config.enabled"), (button, value) -> config.enabled = value));
        y += 32;
        // Dos columnas: con seis botones seguidos no caben en pantallas pequenas
        final int left = this.width / 2 - COLUMN - 4;
        final int right = this.width / 2 + 4;
        this.toggle(left, y, "blocks", config.blocks, value -> config.blocks = value);
        this.toggle(right, y, "items", config.items, value -> config.items = value);
        y += 24;
        this.toggle(left, y, "sounds", config.sounds, value -> config.sounds = value);
        this.toggle(right, y, "particles", config.particles, value -> config.particles = value);
        y += 24;
        this.toggle(left, y, "entities", config.entities, value -> config.entities = value);
        this.toggle(right, y, "babies", config.babies, value -> config.babies = value);
        y += 32;
        this.addRenderableWidget(Button.builder(Component.translatable("fnviaviewer.config.redownload"), button -> {
                button.active = false;
                FNViaViewer.redownloadAssets();
            })
            .tooltip(Tooltip.create(Component.translatable("fnviaviewer.config.redownload.tooltip")))
            .bounds(x, y, WIDTH, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> this.onClose())
            .bounds(x, this.height - 28, WIDTH, 20).build());
    }

    private void toggle(final int x, final int y, final String key, final boolean value, final java.util.function.Consumer<Boolean> setter) {
        this.addRenderableWidget(CycleButton.onOffBuilder(value)
            .withTooltip(v -> Tooltip.create(Component.translatable("fnviaviewer.config." + key + ".tooltip")))
            .create(x, y, COLUMN, 20, Component.translatable("fnviaviewer.config." + key), (button, v) -> setter.accept(v)));
    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFFFF);
        graphics.drawCenteredString(this.font, Component.translatable("fnviaviewer.config.rejoin"), this.width / 2,
            this.height - 44, 0xFFA0A0A0);
    }

    @Override
    public void onClose() {
        ViewerConfig.get().save();
        this.minecraft.setScreen(this.parent);
    }
}
