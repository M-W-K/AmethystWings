package com.m_w_k.amethystwings;

import com.m_w_k.amethystwings.datagen.AmethystWingsRecipeProvider;
import com.m_w_k.amethystwings.registry.AmethystWingsRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AmethystWingsMod.MODID)
public class AmethystWingsMod {
    public static final String MODID = "amethystwings";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AmethystWingsMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AmethystWingsRegistry.init(modEventBus);
        modEventBus.addListener(this::gatherData);

//        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        gen.addProvider(event.includeServer(), new AmethystWingsRecipeProvider(packOutput));
    }
}
