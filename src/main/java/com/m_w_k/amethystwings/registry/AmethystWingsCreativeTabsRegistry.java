package com.m_w_k.amethystwings.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.m_w_k.amethystwings.AmethystWingsMod.MODID;

public class AmethystWingsCreativeTabsRegistry {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> WINGS_TAB = CREATIVE_MODE_TABS.register("amethystwings_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> AmethystWingsItemsRegistry.WINGS.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(AmethystWingsItemsRegistry.WINGS.get());
            }).build());
}
