package com.m_w_k.amethystwings.item;

import com.m_w_k.amethystwings.api.util.WingsAction;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

public class WingsCrystalItem extends Item {
    private final EnumSet<WingsAction> supportedActions;
    private final Supplier<Object2DoubleMap<Attribute>> attributeContributions;
    private final byte priority;
    private final Supplier<Byte> mass;

    private final ResourceLocation wingsModelLoc;

    public WingsCrystalItem(Item.Properties properties, byte priority, byte mass, @Nullable Object2DoubleMap<Attribute> attributeContributions, ResourceLocation wingsModelLoc, WingsAction supportedAction, WingsAction... supportedActions) {
        super(properties);
        this.supportedActions = supportedAction.isNone() ? null : EnumSet.of(supportedAction, supportedActions);
        this.priority = priority;
        this.mass = () -> mass;
        this.attributeContributions = attributeContributions == null ? null : () -> attributeContributions;
        this.wingsModelLoc = wingsModelLoc;
    }

    public WingsCrystalItem(Item.Properties properties, byte priority, Supplier<Byte> mass, Supplier<Object2DoubleMap<Attribute>> attributeContributions, ResourceLocation wingsModelLoc, WingsAction supportedAction, WingsAction... supportedActions) {
        super(properties);
        this.supportedActions = supportedAction.isNone() ? null : EnumSet.of(supportedAction, supportedActions);
        this.priority = priority;
        this.mass = mass;
        this.attributeContributions = attributeContributions;
        this.wingsModelLoc = wingsModelLoc;
    }

    public WingsCrystalItem(Item.Properties properties, byte priority, byte mass, Supplier<Object2DoubleMap<Attribute>> attributeContributions, ResourceLocation wingsModelLoc, WingsAction supportedAction, WingsAction... supportedActions) {
        super(properties);
        this.supportedActions = supportedAction.isNone() ? null : EnumSet.of(supportedAction, supportedActions);
        this.priority = priority;
        this.mass = () -> mass;
        this.attributeContributions = attributeContributions;
        this.wingsModelLoc = wingsModelLoc;
    }

    public WingsCrystalItem(Item.Properties properties, byte priority, byte mass, ResourceLocation wingsRenderTexture, WingsAction supportedAction, WingsAction... supportedActions) {
        this(properties, priority, mass, (Object2DoubleMap<Attribute>) null, wingsRenderTexture, supportedAction, supportedActions);
    }

    public WingsCrystalItem(Item.Properties properties, byte priority, Supplier<Byte> mass, ResourceLocation wingsRenderTexture, WingsAction supportedAction, WingsAction... supportedActions) {
        this(properties, priority, mass, null, wingsRenderTexture, supportedAction, supportedActions);
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack p_41456_) {
        return false;
    }


    public ResourceLocation getWingsModelLoc() {
        return this.wingsModelLoc;
    }

    public boolean supportsActions() {
        return supportedActions != null;
    }

    public EnumSet<WingsAction> getSupportedActions() {
        return supportedActions;
    }

    public boolean isActionSupported(WingsAction action) {
        if (supportsActions()) return supportedActions.contains(action);
        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag toolflag) {
        super.appendHoverText(stack, level, components, toolflag);
        if (supportsActions()) getSupportedActions().forEach((action) -> action.appendHoverText(components));
        if (this.attributeContributions != null) {
            Object2DoubleMap<Attribute> contributions = attributeContributions.get();
            if (!contributions.isEmpty()) {
                components.add(CommonComponents.EMPTY);
                components.add(Component.translatable("item.amethystwings.wings_controller.attribute").withStyle(ChatFormatting.GRAY));

                for (var entry : contributions.object2DoubleEntrySet()) {
                    double d0 = entry.getDoubleValue();
                    if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                        d0 *= 10.0D;
                    }

                    if (d0 > 0.0D) {
                        components.add(Component.translatable("attribute.modifier.plus." + AttributeModifier.Operation.ADDITION.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d0), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.BLUE));
                    } else if (d0 < 0.0D) {
                        d0 *= -1.0D;
                        components.add(Component.translatable("attribute.modifier.take." + AttributeModifier.Operation.ADDITION.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d0), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.RED));
                    }
                }
            }
        }
    }

    public byte getPriority() {
        return priority;
    }

    public byte getMass() {
        return mass.get();
    }

    public @NotNull Object2DoubleMap<Attribute> getAttributeContributions() {
        if (attributeContributions == null) return Object2DoubleMaps.emptyMap();
        return attributeContributions.get();
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return stack.isDamaged() ? 1 : 64;
    }
}
