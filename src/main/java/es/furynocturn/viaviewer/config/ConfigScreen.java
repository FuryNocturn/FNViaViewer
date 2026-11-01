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
        this.addRenderableWidget(CycleButton.onOffBuilder(config.blocks)
            .withTooltip(value -> Tooltip.create(Component.translatable("fnviaviewer.config.blocks.tooltip")))
            .create(x, y, WIDTH, 20, Component.translatable("fnviaviewer.config.blocks"), (button, value) -> config.blocks = value));
        y += 24;
        this.addRenderableWidget(CycleButton.onOffBuilder(config.items)
            .withTooltip(value -> Tooltip.create(Component.translatable("fnviaviewer.config.items.tooltip")))
            .create(x, y, WIDTH, 20, Component.translatable("fnviaviewer.config.items"), (button, value) -> config.items = value));
        y += 24;
        this.addRenderableWidget(CycleButton.onOffBuilder(config.sounds)
            .withTooltip(value -> Tooltip.create(Component.translatable("fnviaviewer.config.sounds.tooltip")))
            .create(x, y, WIDTH, 20, Component.translatable("fnviaviewer.config.sounds"), (button, value) -> config.sounds = value));
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
