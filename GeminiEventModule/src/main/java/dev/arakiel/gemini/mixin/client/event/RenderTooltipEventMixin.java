package dev.arakiel.gemini.mixin.client.event;

import com.mojang.datafixers.util.Either;
import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.client.RenderTooltipEvent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(GuiGraphics.class)
public abstract class RenderTooltipEventMixin {
    @Unique
    private static final int GEMINI$BACKGROUND_COLOR = -267386864;
    @Unique
    private static final int GEMINI$BORDER_COLOR_START = 1347420415;
    @Unique
    private static final int GEMINI$BORDER_COLOR_END = 1344798847;
    @Unique
    private static final ThreadLocal<ItemStack> GEMINI$TOOLTIP_STACK = new ThreadLocal<>();

    @Shadow
    private void renderTooltipInternal(
            Font font,
            List<ClientTooltipComponent> components,
            int x,
            int y,
            ClientTooltipPositioner positioner
    ) {
        throw new AssertionError();
    }

    @Inject(
            method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("HEAD")
    )
    private void gemini$captureTooltipStack(
            Font font,
            ItemStack stack,
            int x,
            int y,
            CallbackInfo callback
    ) {
        GEMINI$TOOLTIP_STACK.set(stack);
    }

    @Inject(
            method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("RETURN")
    )
    private void gemini$clearTooltipStack(
            Font font,
            ItemStack stack,
            int x,
            int y,
            CallbackInfo callback
    ) {
        GEMINI$TOOLTIP_STACK.remove();
    }

    @Inject(
            method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gemini$gatherTooltipComponents(
            Font font,
            List<Component> lines,
            Optional<TooltipComponent> image,
            int x,
            int y,
            CallbackInfo callback
    ) {
        List<Either<FormattedText, TooltipComponent>> elements = new ArrayList<>(lines.size() + 1);
        for (Component line : lines) {
            elements.add(Either.left(line));
        }
        image.ifPresent(component -> elements.add(
                elements.isEmpty() ? 0 : 1,
                Either.right(component)
        ));

        RenderTooltipEvent.GatherComponents event = new RenderTooltipEvent.GatherComponents(
                gemini$tooltipStack(),
                ((GuiGraphics) (Object) this).guiWidth(),
                ((GuiGraphics) (Object) this).guiHeight(),
                elements,
                0
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
            return;
        }

        List<ClientTooltipComponent> components = event.getTooltipElements().stream()
                .map(element -> element.map(
                        RenderTooltipEventMixin::gemini$createTextComponent,
                        ClientTooltipComponent::create
                ))
                .toList();
        this.renderTooltipInternal(font, components, x, y, DefaultTooltipPositioner.INSTANCE);
        callback.cancel();
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "renderTooltipInternal", at = @At("HEAD"), cancellable = true)
    private void gemini$renderTooltip(
            Font font,
            List<ClientTooltipComponent> components,
            int x,
            int y,
            ClientTooltipPositioner positioner,
            CallbackInfo callback
    ) {
        if (components.isEmpty()) {
            return;
        }

        RenderTooltipEvent.Pre pre = new RenderTooltipEvent.Pre(
                gemini$tooltipStack(),
                (GuiGraphics) (Object) this,
                x,
                y,
                ((GuiGraphics) (Object) this).guiWidth(),
                ((GuiGraphics) (Object) this).guiHeight(),
                font,
                components,
                positioner
        );
        GeminiEvents.post(pre);
        if (pre.isCanceled()) {
            callback.cancel();
            return;
        }

        int width = 0;
        int height = components.size() == 1 ? -2 : 0;
        for (ClientTooltipComponent component : components) {
            width = Math.max(width, component.getWidth(pre.getFont()));
            height += component.getHeight();
        }
        int tooltipWidth = width;
        int tooltipHeight = height;

        Vector2ic positioned = pre.getTooltipPositioner().positionTooltip(
                pre.getScreenWidth(),
                pre.getScreenHeight(),
                pre.getX(),
                pre.getY(),
                tooltipWidth,
                tooltipHeight
        );
        int tooltipX = positioned.x();
        int tooltipY = positioned.y();

        GuiGraphics graphics = (GuiGraphics) (Object) this;
        graphics.pose().pushPose();
        RenderTooltipEvent.Color color = new RenderTooltipEvent.Color(
                pre.getItemStack(),
                graphics,
                tooltipX,
                tooltipY,
                pre.getFont(),
                GEMINI$BACKGROUND_COLOR,
                GEMINI$BORDER_COLOR_START,
                GEMINI$BORDER_COLOR_END,
                components
        );
        GeminiEvents.post(color);
        graphics.drawManaged(() -> gemini$renderTooltipBackground(
                graphics,
                tooltipX,
                tooltipY,
                tooltipWidth,
                tooltipHeight,
                400,
                color
        ));
        graphics.pose().translate(0.0F, 0.0F, 400.0F);

        int textY = tooltipY;
        for (int index = 0; index < components.size(); index++) {
            ClientTooltipComponent component = components.get(index);
            component.renderText(
                    pre.getFont(),
                    tooltipX,
                    textY,
                    graphics.pose().last().pose(),
                    graphics.bufferSource()
            );
            textY += component.getHeight() + (index == 0 ? 2 : 0);
        }

        int imageY = tooltipY;
        for (int index = 0; index < components.size(); index++) {
            ClientTooltipComponent component = components.get(index);
            component.renderImage(pre.getFont(), tooltipX, imageY, graphics);
            imageY += component.getHeight() + (index == 0 ? 2 : 0);
        }
        graphics.pose().popPose();
        callback.cancel();
    }

    @Unique
    private static ItemStack gemini$tooltipStack() {
        ItemStack stack = GEMINI$TOOLTIP_STACK.get();
        return stack == null ? ItemStack.EMPTY : stack;
    }

    @Unique
    private static ClientTooltipComponent gemini$createTextComponent(FormattedText text) {
        if (text instanceof Component component) {
            return ClientTooltipComponent.create(component.getVisualOrderText());
        }
        return ClientTooltipComponent.create(
                FormattedCharSequence.forward(text.getString(), Style.EMPTY)
        );
    }

    @Unique
    private static void gemini$renderTooltipBackground(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            int z,
            RenderTooltipEvent.Color color
    ) {
        int left = x - 3;
        int top = y - 3;
        int outerWidth = width + 6;
        int outerHeight = height + 6;

        graphics.fill(left, top - 1, left + outerWidth, top, z, color.getBackgroundStart());
        graphics.fill(left, top + outerHeight, left + outerWidth, top + outerHeight + 1, z, color.getBackgroundEnd());
        graphics.fill(left, top, left + outerWidth, top + outerHeight, z, color.getBackgroundStart());
        graphics.fill(left - 1, top, left, top + outerHeight, z, color.getBackgroundStart());
        graphics.fill(left + outerWidth, top, left + outerWidth + 1, top + outerHeight, z, color.getBackgroundEnd());

        int frameHeight = outerHeight - 2;
        graphics.fillGradient(left, top + 1, left + 1, top + 1 + frameHeight, z, color.getBorderStart(), color.getBorderEnd());
        graphics.fillGradient(
                left + outerWidth - 1,
                top + 1,
                left + outerWidth,
                top + 1 + frameHeight,
                z,
                color.getBorderStart(),
                color.getBorderEnd()
        );
        graphics.fill(left, top, left + outerWidth, top + 1, z, color.getBorderStart());
        graphics.fill(
                left,
                top + outerHeight - 1,
                left + outerWidth,
                top + outerHeight,
                z,
                color.getBorderEnd()
        );
    }
}
