package com.m_w_k.amethystwings.item;

import com.m_w_k.amethystwings.CrystalStats;
import com.m_w_k.amethystwings.api.AttributeBehaviors;
import com.m_w_k.amethystwings.api.util.WingsAction;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class WingsCrystalItem extends Item {
    private final @NotNull ResourceLocation wingsModelLoc;
    private final @Nullable EnumSet<WingsAction> supportedActions;
    private final @Nullable Supplier<Object2DoubleMap<Attribute>> attributeContributions;
    private final int priority;
    private final @Nullable IntSupplier mass;
    private final @Nullable DoubleSupplier shatterMult;
    private final @Nullable DoubleSupplier boostBonus;
    private final @Nullable Object2ObjectOpenHashMap<String, Object> special;

    public WingsCrystalItem(Item.Properties properties, CrystalStats stats) {
        super(properties);
        this.wingsModelLoc = stats.getWingsModelLoc();
        this.supportedActions = stats.getSupportedActions();
        this.attributeContributions = stats.getAttributeContributions();
        this.priority = stats.getPriority();
        this.mass = stats.getMass();
        this.shatterMult = stats.getShatterMult();
        this.boostBonus = stats.getBoostBonus();
        this.special = stats.getSpecial();
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack p_41456_) {
        return false;
    }


    public @NotNull ResourceLocation getWingsModelLoc() {
        return this.wingsModelLoc;
    }

    public final boolean supportsActions() {
        return supportedActions != null;
    }

    @UnknownNullability
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
        components.add(Component.translatableWithFallback(getDescriptionId(stack) + ".tooltip", "").withStyle(ChatFormatting.GRAY));
        if (supportsActions()) getSupportedActions().forEach((action) -> action.appendHoverText(components));
        if (this.attributeContributions != null) {
            Object2DoubleMap<Attribute> contributions = attributeContributions.get();
            if (!contributions.isEmpty()) {
                components.add(CommonComponents.EMPTY);
                components.add(Component.translatable("item.amethystwings.wings_controller.attribute").withStyle(ChatFormatting.GRAY));

                for (var entry : contributions.object2DoubleEntrySet()) {
                    AttributeModifier.Operation op = AttributeBehaviors.getType(entry.getKey());
                    double d0 = entry.getDoubleValue();
                    if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                        d0 *= 10.0D;
                    } else if (op != AttributeModifier.Operation.ADDITION) {
                        d0 *= 100;
                    }

                    if (d0 > 0.0D) {
                        components.add(Component.translatable("attribute.modifier.plus." + op.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d0), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.BLUE));
                    } else if (d0 < 0.0D) {
                        components.add(Component.translatable("attribute.modifier.take." + op.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(-d0), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.RED));
                    }
                }
            }
        }
    }

    public int getPriority() {
        return priority;
    }

    public int getMass() {
        if (mass == null) return 5;
        return mass.getAsInt();
    }

    public double getShatterMult() {
        if (shatterMult == null) return 1;
        return shatterMult.getAsDouble();
    }

    public double getBoostBonus() {
        if (boostBonus == null) return 0;
        return boostBonus.getAsDouble();
    }

    public @NotNull Object2DoubleMap<Attribute> getAttributeContributions() {
        if (attributeContributions == null) return Object2DoubleMaps.emptyMap();
        return attributeContributions.get();
    }

    public @NotNull Set<String> getSpecials() {
        if (special == null) return Collections.emptySet();
        return special.keySet();
    }

    public @Nullable Object getSpecial(@NotNull String identifier) {
        if (special == null) return null;
        return special.get(identifier);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return stack.isDamaged() ? 1 : 64;
    }
}
