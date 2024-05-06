package com.m_w_k.amethystwings.registry;

import net.minecraftforge.eventbus.api.IEventBus;

public class AmethystWingsRegistry {
    public static void init(IEventBus modEventBus) {
        AmethystWingsItemsRegistry.ITEMS.register(modEventBus);
        AmethystWingsGUIRegistry.MENU_TYPES.register(modEventBus);
        AmethystWingsCreativeTabsRegistry.CREATIVE_MODE_TABS.register(modEventBus);
        AmethystWingsSoundsRegistry.SOUNDS.register(modEventBus);
    }
}
