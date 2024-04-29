package com.m_w_k.amethystwings.item;

import com.m_w_k.amethystwings.api.util.WingsAction;
import net.minecraft.world.item.Item;

import java.util.Arrays;
import java.util.EnumSet;

public class WingsCrystalItem extends Item {
    private final EnumSet<WingsAction> supportedActions;
    private final double armorToughnessContribution;
    private final byte priority;
    private final byte mass;

    public WingsCrystalItem(Item.Properties properties, byte priority, byte mass, double armorToughnessContribution, WingsAction supportedAction, WingsAction... supportedActions) {
        super(properties);
        this.supportedActions = supportedAction.isNone() ? null : EnumSet.of(supportedAction, supportedActions);
        this.priority = priority;
        this.mass = mass;
        this.armorToughnessContribution = armorToughnessContribution;
    }

    public WingsCrystalItem(Item.Properties properties, byte priority, byte mass, WingsAction supportedAction, WingsAction... supportedActions) {
        this(properties, priority, (byte) 0, mass, supportedAction, supportedActions);
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

    public byte getPriority() {
        return priority;
    }

    public byte getMass() {
        return mass;
    }

    public double getArmorToughnessContribution() {
        return armorToughnessContribution;
    }
}
