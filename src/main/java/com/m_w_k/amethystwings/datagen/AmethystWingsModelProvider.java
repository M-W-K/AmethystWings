package com.m_w_k.amethystwings.datagen;

import com.m_w_k.amethystwings.registry.AmethystWingsItemsRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import static com.m_w_k.amethystwings.AmethystWingsMod.MODID;

public class AmethystWingsModelProvider extends ItemModelProvider {

    protected static final ResourceLocation BASE_CRYSTAL_MODEL = rl("entity/crystal");
    public static final ResourceLocation RESONANT = rl("entity/resonant_crystal");
    public static final ResourceLocation HARDENED = rl("entity/hardened_crystal");
    public static final ResourceLocation ENERGETIC = rl("entity/energetic_crystal");
    public static final ResourceLocation SHAPED = rl("entity/shaped_crystal");
    public static final ResourceLocation AURIC = rl("entity/auric_crystal");
    public static final ResourceLocation TOTEMIC = rl("entity/totemic_crystal");

    public AmethystWingsModelProvider(PackOutput p_252226_, ExistingFileHelper helper) {
        super(p_252226_, MODID, helper);
    }

    protected static ResourceLocation rl(String loc) {
        return new ResourceLocation(MODID, loc);
    }

    @Override
    protected void registerModels() {
        Item crystal = AmethystWingsItemsRegistry.RESONANT_AMETHYST.get();
        basicItem(crystal);
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.STURDY_RESONANT_AMETHYST.get(), "sturdy");
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.SPATIAL_RESONANT_AMETHYST.get(), "spatial");
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.LUCKY_RESONANT_AMETHYST.get(), "lucky");

        crystal = AmethystWingsItemsRegistry.HARDENED_AMETHYST.get();
        basicItem(crystal);
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.STURDY_HARDENED_AMETHYST.get(), "sturdy");
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.WARDING_HARDENED_AMETHYST.get(), "warding");
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.REINFORCED_HARDENED_AMETHYST.get(), "reinforced");

        crystal = AmethystWingsItemsRegistry.SHAPED_AMETHYST.get();
        basicItem(crystal);
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.STURDY_SHAPED_AMETHYST.get(), "sturdy");
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.FLOATY_SHAPED_AMETHYST.get(), "floaty");
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.HEAVY_SHAPED_AMETHYST.get(), "heavy");

        crystal = AmethystWingsItemsRegistry.ENERGETIC_AMETHYST.get();
        basicItem(crystal);
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.STURDY_ENERGETIC_AMETHYST.get(), "sturdy");
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.EMPOWERED_ENERGETIC_AMETHYST.get(), "empowered");
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.ENHANCING_ENERGETIC_AMETHYST.get(), "enhancing");

        crystal = AmethystWingsItemsRegistry.AURIC_AMETHYST.get();
        basicItem(crystal);
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.BARRIER_AURIC_AMETHYST.get(), "barrier");
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.REJUVENATING_AURIC_AMETHYST.get(), "rejuvenating");
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.REFOCUSED_AURIC_AMETHYST.get(), "refocused");

        crystal = AmethystWingsItemsRegistry.TOTEMIC_AMETHYST.get();
        basicItem(crystal);
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.VENGEFUL_TOTEMIC_AMETHYST.get(), "vengeful");
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.MERCIFUL_TOTEMIC_AMETHYST.get(), "merciful");
        modifiedCrystal(crystal, AmethystWingsItemsRegistry.NOURISHING_TOTEMIC_AMETHYST.get(), "nourishing");

        basicItem(AmethystWingsItemsRegistry.TREATED_AMETHYST.get());

        crystalModel(RESONANT, rl("block/resonant_crystal"));
        crystalModel(HARDENED, rl("block/hardened_crystal"));
        crystalModel(ENERGETIC, rl("block/energetic_crystal"));
        crystalModel(SHAPED, rl("block/shaped_crystal"));
        crystalModel(AURIC, rl("block/auric_crystal"));
        crystalModel(TOTEMIC, rl("block/totemic_crystal"));
    }

    private void modifiedCrystal(Item base, Item mod, String modName) {
        getBuilder(mod.toString())
                .parent(new ModelFile.UncheckedModelFile(new ResourceLocation(MODID, "item/" + ForgeRegistries.ITEMS.getKey(base).getPath())))
                .texture("layer1", new ResourceLocation(MODID, "item/modifier/" + modName));
    }


    protected void crystalModel(ResourceLocation destLoc, ResourceLocation texLoc) {
        this.withExistingParent(destLoc.getPath(), BASE_CRYSTAL_MODEL)
                .texture("layer0", texLoc);
    }
}
