package com.m_w_k.amethystwings;

import com.m_w_k.amethystwings.client.Keybindings;
import com.m_w_k.amethystwings.client.renderer.WingsItemStackRenderer;
import com.m_w_k.amethystwings.datagen.AmethystWingsModelProvider;
import com.m_w_k.amethystwings.datagen.AmethystWingsRecipeProvider;
import com.m_w_k.amethystwings.network.PacketHandler;
import com.m_w_k.amethystwings.registry.AmethystWingsRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AmethystWingsMod.MODID)
public class AmethystWingsMod {
    public static final String MODID = "amethystwings";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AmethystWingsMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        PacketHandler.init();

        AmethystWingsRegistry.init(modEventBus);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::onModelRegistration);
        modEventBus.addListener(this::registerBindings);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AmethystWingsConfig.SPEC);
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        gen.addProvider(event.includeServer(), new AmethystWingsRecipeProvider(packOutput));
        gen.addProvider(event.includeServer(), new AmethystWingsModelProvider(packOutput, helper));
    }

    public void onModelRegistration(ModelEvent.RegisterAdditional event) {
        ResourceLocation loc = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> WingsItemStackRenderer::getWingsInventoryModel);
        if (loc != null) event.register(loc);
        event.register(AmethystWingsModelProvider.RESONANT);
        event.register(AmethystWingsModelProvider.HARDENED);
        event.register(AmethystWingsModelProvider.ENERGETIC);
        event.register(AmethystWingsModelProvider.SHAPED);
        event.register(AmethystWingsModelProvider.AURIC);
    }

    public void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(Keybindings.BOOST.get());
    }
}
