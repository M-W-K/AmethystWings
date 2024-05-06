package com.m_w_k.amethystwings.registry;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.m_w_k.amethystwings.AmethystWingsMod.MODID;

public class AmethystWingsSoundsRegistry {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public static final RegistryObject<SoundEvent> CRYSTAL_SHATTER = SOUNDS.register("crystal_shatter",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "crystal_shatter")));
    public static final RegistryObject<SoundEvent> CRYSTAL_DAMAGE = SOUNDS.register("crystal_damage",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "crystal_damage")));
}
