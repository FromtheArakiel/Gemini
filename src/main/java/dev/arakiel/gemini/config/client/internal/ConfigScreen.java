package dev.arakiel.gemini.config.client.internal;

import dev.arakiel.gemini.config.ConfigHandle;
import dev.arakiel.gemini.config.ConfigOption;
import dev.arakiel.gemini.config.ConfigValueType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class ConfigScreen extends Screen {
    private static final int ROW_HEIGHT = 44;
    private static final int TOP = 34;
    private static final int FOOTER = 44;

    private final Screen parent;
    private final ConfigHandle<?> handle;
    private final List<OptionRow> rows = new ArrayList<>();
    private int scrollOffset;
    private boolean committed;
    private int contentLeft;
    private int contentWidth;

    public ConfigScreen(Screen parent, ConfigHandle<?> handle) {
        super(Component.translatableWithFallback(
                handle.id().toLanguageKey("config"),
                humanize(handle.id().getPath())
        ));
        this.parent = parent;
        this.handle = handle;
    }

    @Override
    protected void init() {
        rows.clear();
        contentWidth = Math.min(390, width - 32);
        contentLeft = (width - contentWidth) / 2;
        int controlWidth = Math.min(150, contentWidth / 2);
        int controlX = contentLeft + contentWidth - controlWidth;

        for (ConfigOption option : handle.options()) {
            if (option.hidden()) {
                continue;
            }
            AbstractWidget control = createControl(option, controlX, controlWidth);
            rows.add(new OptionRow(option, control));
            addRenderableWidget(control);
        }

        int buttonY = height - 28;
        int buttonWidth = Math.min(100, (contentWidth - 12) / 3);
        int buttonX = width / 2 - (buttonWidth * 3 + 8) / 2;
        addRenderableWidget(Button.builder(Component.translatableWithFallback("gui.done", "Done"), button -> {
            handle.save();
            committed = true;
            onClose();
        }).bounds(buttonX, buttonY, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(Component.translatableWithFallback("gui.cancel", "Cancel"), button -> {
            committed = false;
            onClose();
        }).bounds(buttonX + buttonWidth + 4, buttonY, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Reset"), button -> {
            handle.resetAll();
            rebuildWidgets();
        }).bounds(buttonX + (buttonWidth + 4) * 2, buttonY, buttonWidth, 20).build());

        scrollOffset = 0;
        updatePositions();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        graphics.flush();
        graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFF);
        int top = TOP;
        int bottom = height - FOOTER;
        graphics.fill(contentLeft - 6, top - 4, contentLeft + contentWidth + 6, bottom + 4, 0xC0101018);
        for (OptionRow row : rows) {
            int y = row.control.getY();
            if (y < top - 20 || y > bottom) {
                continue;
            }
            graphics.fill(
                    contentLeft - 4,
                    Math.max(top - 4, y - 3),
                    contentLeft + contentWidth + 4,
                    Math.min(bottom + 4, y + ROW_HEIGHT - 4),
                    0xC0202028
            );
            Component label = Component.translatableWithFallback(
                    row.option.translationKey(),
                    humanize(row.option.path())
            );
            if (row.option.restartRequired()) {
                label = label.copy().append(Component.literal(" *"));
            }
            graphics.drawString(font, label, contentLeft, y + 2, 0xFFFFFF, true);
            if (!row.option.comment().isBlank()) {
                graphics.drawString(font, row.option.comment(), contentLeft, y + 13, 0xFFD8D8D8, true);
            }
        }
        if (handle.needsRestart()) {
            graphics.drawCenteredString(
                    font,
                    Component.translatableWithFallback(
                            "gemini.config.restart_required",
                            "Restart required for starred options"
                    ),
                    width / 2,
                    height - 41,
                    0xFFAA55
            );
        }
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxScroll = Math.max(0, rows.size() * ROW_HEIGHT - (height - TOP - FOOTER));
        if (maxScroll == 0) {
            return false;
        }
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int) (scrollY * ROW_HEIGHT)));
        updatePositions();
        return true;
    }

    @Override
    public void onClose() {
        if (!committed) {
            handle.reload();
        }
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    private void updatePositions() {
        for (int i = 0; i < rows.size(); i++) {
            AbstractWidget control = rows.get(i).control;
            int y = TOP + i * ROW_HEIGHT - scrollOffset;
            control.setY(y);
            control.visible = y >= TOP - 20 && y <= height - FOOTER;
        }
    }

    private AbstractWidget createControl(ConfigOption option, int x, int width) {
        if (option.type() == ConfigValueType.BOOLEAN) {
            return CycleButton.onOffBuilder((Boolean) option.value())
                    .create(x, 0, width, 20, Component.empty(), (button, value) ->
                            handle.stage(option.path(), value));
        }
        if (option.type() == ConfigValueType.ENUM) {
            String current = String.valueOf(option.value());
            return CycleButton.builder(value -> Component.literal(value.toString()))
                    .withValues(option.choices())
                    .withInitialValue(current)
                    .create(x, 0, width, 20, Component.empty(), (button, value) ->
                            handle.stage(option.path(), value));
        }
        EditBox box = new EditBox(font, x, 0, width, 20, Component.empty());
        box.setMaxLength(1024);
        box.setValue(format(option.value()));
        box.setResponder(value -> stageText(option, value));
        return box;
    }

    private void stageText(ConfigOption option, String text) {
        try {
            switch (option.type()) {
                case INTEGER -> handle.stage(option.path(), Integer.parseInt(text.trim()));
                case LONG -> handle.stage(option.path(), Long.parseLong(text.trim()));
                case FLOAT, DOUBLE -> handle.stage(option.path(), Double.parseDouble(text.trim()));
                case STRING -> handle.stage(option.path(), text);
                case BOOLEAN_LIST -> handle.stage(option.path(), parseList(text).stream()
                        .map(Boolean::parseBoolean)
                        .toList());
                case INTEGER_LIST -> handle.stage(option.path(), parseList(text).stream()
                        .map(Integer::parseInt)
                        .toList());
                case LONG_LIST -> handle.stage(option.path(), parseList(text).stream()
                        .map(Long::parseLong)
                        .toList());
                case DOUBLE_LIST -> handle.stage(option.path(), parseList(text).stream()
                        .map(Double::parseDouble)
                        .toList());
                case STRING_LIST -> handle.stage(option.path(), parseList(text));
                default -> {
                }
            }
        } catch (IllegalArgumentException exception) {
        }
    }

    private static String format(Object value) {
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).collect(Collectors.joining(", "));
        }
        return String.valueOf(value);
    }

    private static List<String> parseList(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }

    private static String humanize(String path) {
        String spaced = path.replace('_', ' ').replace('.', ' ');
        if (spaced.isEmpty()) {
            return path;
        }
        String lower = spaced.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private record OptionRow(ConfigOption option, AbstractWidget control) {
    }
}
