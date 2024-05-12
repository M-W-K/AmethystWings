package com.m_w_k.amethystwings.datagen;

import com.m_w_k.amethystwings.registry.AmethystWingsItemsRegistry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class AmethystWingsRecipeProvider extends RecipeProvider {
    public AmethystWingsRecipeProvider(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> writer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AmethystWingsItemsRegistry.WINGS.get())
                .pattern("ini").pattern("ttt").pattern("iei")
                .define('i', Items.IRON_INGOT).define('n', Items.NETHER_STAR)
                .define('e', Items.ELYTRA)
                .define('t', AmethystWingsItemsRegistry.TREATED_AMETHYST.get())
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(Items.AMETHYST_SHARD))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.TREATED_AMETHYST.get(), 2)
                .requires(Items.AMETHYST_SHARD).requires(Items.COPPER_INGOT).requires(Items.GLOWSTONE_DUST)
                .requires(Items.REDSTONE).requires(Items.GUNPOWDER)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(Items.AMETHYST_SHARD))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.RESONANT_AMETHYST.get())
                .requires(Items.ENDER_PEARL).requires(AmethystWingsItemsRegistry.TREATED_AMETHYST.get())
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.TREATED_AMETHYST.get()))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.HARDENED_AMETHYST.get())
                .requires(Items.ENDER_PEARL).requires(AmethystWingsItemsRegistry.TREATED_AMETHYST.get())
                .requires(Items.IRON_INGOT)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.TREATED_AMETHYST.get()))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.SHAPED_AMETHYST.get())
                .requires(Items.ENDER_PEARL).requires(AmethystWingsItemsRegistry.TREATED_AMETHYST.get())
                .requires(Items.QUARTZ).requires(Items.PHANTOM_MEMBRANE)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.TREATED_AMETHYST.get()))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.ENERGETIC_AMETHYST.get())
                .requires(Items.ENDER_PEARL).requires(AmethystWingsItemsRegistry.TREATED_AMETHYST.get())
                .requires(Items.GUNPOWDER).requires(Items.REDSTONE)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.TREATED_AMETHYST.get()))
                .save(writer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.AURIC_AMETHYST.get())
                .requires(Items.ENDER_PEARL).requires(AmethystWingsItemsRegistry.TREATED_AMETHYST.get(), 3)
                .requires(Items.NETHERITE_INGOT).requires(Items.MAGMA_CREAM)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.TREATED_AMETHYST.get()))
                .save(writer);
    }
}
