package com.m_w_k.amethystwings.registry;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.m_w_k.amethystwings.AmethystWingsMod.MODID;

public class AmethystWingsAttributeRegistry {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MODID);

    public static final RegistryObject<Attribute> WARDING = ATTRIBUTES.register("amethystwings.warding",
            () -> new RangedAttribute("attribute.name.amethystwings.warding", 0, 0, 64));
    public static final RegistryObject<Attribute> BARRIER = ATTRIBUTES.register("amethystwings.barrier",
            () -> new RangedAttribute("attribute.name.amethystwings.barrier", 0, 0, 1));
}
