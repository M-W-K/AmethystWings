package com.m_w_k.amethystwings.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static com.m_w_k.amethystwings.AmethystWingsMod.MODID;

public class AmethystWingsModelProvider extends ItemModelProvider {

    protected static final ResourceLocation BASE_CRYSTAL_MODEL = rl("entity/crystal");
    public static final ResourceLocation RESONANT = rl("entity/resonant_crystal");
    public static final ResourceLocation HARDENED = rl("entity/hardened_crystal");
    public static final ResourceLocation ENERGETIC = rl("entity/energetic_crystal");
    public static final ResourceLocation SHAPED = rl("entity/shaped_crystal");
    public static final ResourceLocation AURIC = rl("entity/auric_crystal");

    public AmethystWingsModelProvider(PackOutput p_252226_, ExistingFileHelper helper) {
        super(p_252226_, MODID, helper);
    }

    protected static ResourceLocation rl(String loc) {
        return new ResourceLocation(MODID, loc);
    }

    @Override
    protected void registerModels() {
        crystalModel(RESONANT, rl("block/resonant_crystal"));
        crystalModel(HARDENED, rl("block/hardened_crystal"));
        crystalModel(ENERGETIC, rl("block/energetic_crystal"));
        crystalModel(SHAPED, rl("block/shaped_crystal"));
        crystalModel(AURIC, rl("block/auric_crystal"));
    }

    protected void crystalModel(ResourceLocation destLoc, ResourceLocation texLoc) {
        this.withExistingParent(destLoc.getPath(), BASE_CRYSTAL_MODEL)
                .texture("layer0", texLoc);
    }
}
