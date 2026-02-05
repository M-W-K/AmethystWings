package com.m_w_k.amethystwings;

import com.m_w_k.amethystwings.client.Keybindings;
import com.m_w_k.amethystwings.client.renderer.WingsItemStackRenderer;
import com.m_w_k.amethystwings.datagen.AmethystWingsBlockTagsProvider;
import com.m_w_k.amethystwings.datagen.AmethystWingsItemTagsProvider;
import com.m_w_k.amethystwings.datagen.AmethystWingsModelProvider;
import com.m_w_k.amethystwings.datagen.AmethystWingsRecipeProvider;
import com.m_w_k.amethystwings.network.PacketHandler;
import com.m_w_k.amethystwings.registry.AmethystWingsRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;

import static com.m_w_k.amethystwings.registry.AmethystWingsAttributeRegistry.BARRIER;
import static com.m_w_k.amethystwings.registry.AmethystWingsAttributeRegistry.WARDING;

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
        modEventBus.addListener(this::attachAttributes);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(Keybindings::registerBindings));

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AmethystWingsConfig.SPEC);
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        gen.addProvider(event.includeServer(), new AmethystWingsRecipeProvider(packOutput));
        gen.addProvider(event.includeClient(), new AmethystWingsModelProvider(packOutput, helper));
        AmethystWingsBlockTagsProvider blocks = new AmethystWingsBlockTagsProvider(packOutput, event.getLookupProvider(), MODID, helper);
        gen.addProvider(event.includeServer(), blocks);
        gen.addProvider(event.includeServer(), new AmethystWingsItemTagsProvider(packOutput, event.getLookupProvider(), blocks.contentsGetter(), MODID, helper));
    }

    public void onModelRegistration(ModelEvent.RegisterAdditional event) {
        List<ResourceLocation> locs = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> WingsItemStackRenderer::getWingsInventoryModels);
        if (locs != null) locs.forEach(event::register);
        event.register(AmethystWingsModelProvider.RESONANT);
        event.register(AmethystWingsModelProvider.HARDENED);
        event.register(AmethystWingsModelProvider.ENERGETIC);
        event.register(AmethystWingsModelProvider.SHAPED);
        event.register(AmethystWingsModelProvider.AURIC);
        event.register(AmethystWingsModelProvider.TOTEMIC);
    }

    public void attachAttributes(@NotNull EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> t : event.getTypes()) {
            if (!event.has(t, WARDING.get())) {
                event.add(t, WARDING.get());
            }
            if (!event.has(t, BARRIER.get())) {
                event.add(t, BARRIER.get());
            }
        }
    }
}
