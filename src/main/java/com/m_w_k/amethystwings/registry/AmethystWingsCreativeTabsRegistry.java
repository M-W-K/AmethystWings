package com.m_w_k.amethystwings.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.m_w_k.amethystwings.AmethystWingsMod.MODID;

public class AmethystWingsCreativeTabsRegistry {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> WINGS_TAB = CREATIVE_MODE_TABS.register("amethystwings_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.amethystwings"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> AmethystWingsItemsRegistry.TREATED_AMETHYST.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                AmethystWingsItemsRegistry.ITEMS.getEntries().forEach(o -> output.accept(o.get()));
            }).build());
}
