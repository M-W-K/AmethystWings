package com.m_w_k.amethystwings;

import com.m_w_k.amethystwings.api.util.WingsAction;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class CrystalStats {
    private final @NotNull ResourceLocation wingsModelLoc;
    private EnumSet<WingsAction> supportedActions;
    private Supplier<Object2DoubleMap<Attribute>> attributeContributions;
    private int priority;
    private IntSupplier mass;
    private DoubleSupplier shatterMult;
    private DoubleSupplier boostBonus;

    public CrystalStats(@NotNull ResourceLocation wingsModelLoc) {
        this.wingsModelLoc = wingsModelLoc;
    }

    public CrystalStats priority(int prio) {
        this.priority = prio;
        return this;
    }

    public CrystalStats attributeContributions(Supplier<Object2DoubleMap<Attribute>> attributeContributions) {
        this.attributeContributions = attributeContributions;
        return this;
    }

    public CrystalStats mass(int mass) {
        this.mass = () -> mass;
        return this;
    }

    public CrystalStats mass(IntSupplier mass) {
        this.mass = mass;
        return this;
    }

    public CrystalStats shatterMult(double shatterMult) {
        this.shatterMult = () -> shatterMult;
        return this;
    }

    public CrystalStats shatterMult(DoubleSupplier shatterMult) {
        this.shatterMult = shatterMult;
        return this;
    }

    public CrystalStats boostBonus(double boostBonus) {
        this.boostBonus = () -> boostBonus;
        return this;
    }

    public CrystalStats boostBonus(DoubleSupplier boostBonus) {
        this.boostBonus = boostBonus;
        return this;
    }

    public CrystalStats action(WingsAction action) {
        if (action.isNone()) supportedActions = null;
        else if (supportedActions == null) supportedActions = EnumSet.of(action);
        else supportedActions.add(action);
        return this;
    }

    public CrystalStats action(WingsAction action, WingsAction... actions) {
        if (action.isNone()) supportedActions = null;
        else if (supportedActions == null) supportedActions = EnumSet.of(action, actions);
        else {
            supportedActions.add(action);
            supportedActions.addAll(Arrays.asList(actions));
        }
        return this;
    }

    public @NotNull ResourceLocation getWingsModelLoc() {
        return wingsModelLoc;
    }

    public EnumSet<WingsAction> getSupportedActions() {
        return supportedActions;
    }

    public Supplier<Object2DoubleMap<Attribute>> getAttributeContributions() {
        return attributeContributions;
    }

    public int getPriority() {
        return priority;
    }

    public IntSupplier getMass() {
        return mass;
    }

    public DoubleSupplier getShatterMult() {
        return shatterMult;
    }

    public DoubleSupplier getBoostBonus() {
        return boostBonus;
    }
}
