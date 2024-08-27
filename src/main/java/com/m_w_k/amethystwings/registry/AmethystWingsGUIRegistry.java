package com.m_w_k.amethystwings.registry;

import com.m_w_k.amethystwings.AmethystWingsMod;
import com.m_w_k.amethystwings.gui.menu.WingsMenu;
import com.m_w_k.amethystwings.gui.screen.WingsScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AmethystWingsGUIRegistry {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AmethystWingsMod.MODID);

    public static final RegistryObject<MenuType<WingsMenu>> WINGS_MENU;

    static {
        MenuType<WingsMenu> type = IForgeMenuType.create(WingsMenu::fromNetwork);
        MenuScreens.register(type, WingsScreen::new);
        WINGS_MENU = MENU_TYPES.register("amethyst_wings_menu", () -> type);
    }
}
