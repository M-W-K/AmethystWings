package com.m_w_k.amethystwings.datagen;

import com.m_w_k.amethystwings.AmethystWingsTags;
import com.m_w_k.amethystwings.registry.AmethystWingsItemsRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AmethystWingsItemTagsProvider extends ItemTagsProvider {

    public AmethystWingsItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagsProvider.TagLookup<Block>> p_275322_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider p_256380_) {
        tag(AmethystWingsTags.RESONANT_CRYSTAL)
                .add(AmethystWingsItemsRegistry.RESONANT_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.STURDY_RESONANT_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.SPATIAL_RESONANT_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.LUCKY_RESONANT_AMETHYST.get());
        tag(AmethystWingsTags.HARDENED_CRYSTAL)
                .add(AmethystWingsItemsRegistry.HARDENED_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.STURDY_HARDENED_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.WARDING_HARDENED_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.REINFORCED_HARDENED_AMETHYST.get());
        tag(AmethystWingsTags.SHAPED_CRYSTAL)
                .add(AmethystWingsItemsRegistry.SHAPED_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.STURDY_SHAPED_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.FLOATY_SHAPED_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.HEAVY_SHAPED_AMETHYST.get());
        tag(AmethystWingsTags.ENERGETIC_CRYSTAL)
                .add(AmethystWingsItemsRegistry.ENERGETIC_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.STURDY_ENERGETIC_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.EMPOWERED_ENERGETIC_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.ENHANCING_ENERGETIC_AMETHYST.get());
        tag(AmethystWingsTags.AURIC_CRYSTAL)
                .add(AmethystWingsItemsRegistry.AURIC_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.BARRIER_AURIC_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.REJUVENATING_AURIC_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.REFOCUSED_AURIC_AMETHYST.get());
        tag(AmethystWingsTags.TOTEMIC_CRYSTAL)
                .add(AmethystWingsItemsRegistry.TOTEMIC_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.VENGEFUL_TOTEMIC_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.MERCIFUL_TOTEMIC_AMETHYST.get())
                .add(AmethystWingsItemsRegistry.NOURISHING_TOTEMIC_AMETHYST.get());

        tag(AmethystWingsTags.FUNCTIONAL_CRYSTAL).addTags(AmethystWingsTags.AURIC_CRYSTAL,
                AmethystWingsTags.ENERGETIC_CRYSTAL, AmethystWingsTags.HARDENED_CRYSTAL,
                AmethystWingsTags.RESONANT_CRYSTAL, AmethystWingsTags.SHAPED_CRYSTAL,
                AmethystWingsTags.TOTEMIC_CRYSTAL);
    }
}
