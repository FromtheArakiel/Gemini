package dev.xkmc.l2tabs.tabs.contents;

import net.minecraft.world.entity.ai.attributes.Attribute;

public record AttributeEntry(Attribute attr, boolean usePercent, int order, double intrinsic) {
}
