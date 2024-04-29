package com.m_w_k.amethystwings.registry;

import com.m_w_k.amethystwings.AmethystWingsMod;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AmethystWingsGUIRegistry {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AmethystWingsMod.MODID);
}
