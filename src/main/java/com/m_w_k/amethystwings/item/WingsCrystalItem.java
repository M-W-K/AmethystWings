package com.m_w_k.amethystwings.item;

import com.m_w_k.amethystwings.api.util.WingsAction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
    private final Supplier<Double> armorToughnessContribution;
    private final byte priority;
    private final Supplier<Byte> mass;

    private final ResourceLocation wingsModelLoc;

    public WingsCrystalItem(Item.Properties properties, byte priority, byte mass, double armorToughnessContribution, ResourceLocation wingsModelLoc, WingsAction supportedAction, WingsAction... supportedActions) {
        super(properties);
        this.supportedActions = supportedAction.isNone() ? null : EnumSet.of(supportedAction, supportedActions);
        this.priority = priority;
        this.mass = () -> mass;
        this.armorToughnessContribution = () -> armorToughnessContribution;
        this.wingsModelLoc = wingsModelLoc;
    }

    public WingsCrystalItem(Item.Properties properties, byte priority, Supplier<Byte> mass, Supplier<Double> armorToughnessContribution, ResourceLocation wingsModelLoc, WingsAction supportedAction, WingsAction... supportedActions) {
        super(properties);
        this.supportedActions = supportedAction.isNone() ? null : EnumSet.of(supportedAction, supportedActions);
        this.priority = priority;
        this.mass = mass;
        this.armorToughnessContribution = armorToughnessContribution;
        this.wingsModelLoc = wingsModelLoc;
    }

    public WingsCrystalItem(Item.Properties properties, byte priority, byte mass, Supplier<Double> armorToughnessContribution, ResourceLocation wingsModelLoc, WingsAction supportedAction, WingsAction... supportedActions) {
        super(properties);
        this.supportedActions = supportedAction.isNone() ? null : EnumSet.of(supportedAction, supportedActions);
        this.priority = priority;
        this.mass = () -> mass;
        this.armorToughnessContribution = armorToughnessContribution;
        this.wingsModelLoc = wingsModelLoc;
    }

    public WingsCrystalItem(Item.Properties properties, byte priority, byte mass, ResourceLocation wingsRenderTexture, WingsAction supportedAction, WingsAction... supportedActions) {
        this(properties, priority, mass, 0, wingsRenderTexture, supportedAction, supportedActions);
    }

    public WingsCrystalItem(Item.Properties properties, byte priority, Supplier<Byte> mass, ResourceLocation wingsRenderTexture, WingsAction supportedAction, WingsAction... supportedActions) {
        this(properties, priority, mass, () -> 0d, wingsRenderTexture, supportedAction, supportedActions);
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
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        if (supportsActions()) getSupportedActions().forEach((action) -> action.appendHoverText(components));
        if (this.armorToughnessContribution.get() > 0)
            components.add(Component.translatable("item.amethystwings.wings_controller.attribute", this.armorToughnessContribution.get()).withStyle(ChatFormatting.DARK_GRAY));
    }

    public byte getPriority() {
        return priority;
    }

    public byte getMass() {
        return mass.get();
    }

    public double getArmorToughnessContribution() {
        return armorToughnessContribution.get();
    }
}
