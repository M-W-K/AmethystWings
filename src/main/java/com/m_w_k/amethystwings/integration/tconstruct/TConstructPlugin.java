package com.m_w_k.amethystwings.integration.tconstruct;

import com.m_w_k.amethystwings.AmethystWingsMod;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

public class TConstructPlugin {

    private static final ModifierId COMBINING = new ModifierId(AmethystWingsMod.MODID, "combining");

    public static void registerModifiers(ModifierManager.ModifierRegistrationEvent event) {
        event.registerStatic(COMBINING, new CombiningModifier());
    }
}
