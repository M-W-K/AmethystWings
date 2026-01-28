package com.m_w_k.amethystwings;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class AmethystWingsTags {

    public static final TagKey<Item> FUNCTIONAL_CRYSTAL = bind("crystal");
    public static final TagKey<Item> RESONANT_CRYSTAL = bind("resonant_crystal");
    public static final TagKey<Item> HARDENED_CRYSTAL = bind("hardened_crystal");
    public static final TagKey<Item> ENERGETIC_CRYSTAL = bind("energetic_crystal");
    public static final TagKey<Item> SHAPED_CRYSTAL = bind("shaped_crystal");
    public static final TagKey<Item> AURIC_CRYSTAL = bind("auric_crystal");
    public static final TagKey<Item> TOTEMIC_CRYSTAL = bind("totemic_crystal");


    private static TagKey<Item> bind(String name) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(AmethystWingsMod.MODID, name));
    }
}
