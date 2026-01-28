package com.m_w_k.amethystwings.datagen;

import com.m_w_k.amethystwings.AmethystWingsTags;
import com.m_w_k.amethystwings.registry.AmethystWingsItemsRegistry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
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
        resonant(writer);
        hardened(writer);
        shaped(writer);
        energetic(writer);
        auric(writer);
        totemic(writer);
    }

    private void resonant(@NotNull Consumer<FinishedRecipe> writer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.RESONANT_AMETHYST.get())
                .requires(Items.ENDER_PEARL).requires(AmethystWingsItemsRegistry.TREATED_AMETHYST.get())
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.TREATED_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.STURDY_RESONANT_AMETHYST.get())
                .requires(AmethystWingsTags.RESONANT_CRYSTAL).requires(Tags.Items.INGOTS_COPPER)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.RESONANT_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.SPATIAL_RESONANT_AMETHYST.get())
                .requires(AmethystWingsTags.RESONANT_CRYSTAL).requires(Items.ENDER_EYE)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.RESONANT_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.LUCKY_RESONANT_AMETHYST.get())
                .requires(AmethystWingsTags.RESONANT_CRYSTAL).requires(Items.RABBIT_FOOT)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.RESONANT_AMETHYST.get()))
                .save(writer);
    }

    private void hardened(@NotNull Consumer<FinishedRecipe> writer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.HARDENED_AMETHYST.get())
                .requires(Items.ENDER_PEARL).requires(AmethystWingsItemsRegistry.TREATED_AMETHYST.get())
                .requires(Items.IRON_INGOT).requires(Items.GOLD_INGOT)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.TREATED_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.STURDY_HARDENED_AMETHYST.get())
                .requires(AmethystWingsItemsRegistry.HARDENED_AMETHYST.get()).requires(Tags.Items.INGOTS_COPPER)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.HARDENED_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.WARDING_HARDENED_AMETHYST.get())
                .requires(AmethystWingsItemsRegistry.HARDENED_AMETHYST.get()).requires(Items.DIAMOND)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.HARDENED_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.REINFORCED_HARDENED_AMETHYST.get())
                .requires(AmethystWingsItemsRegistry.HARDENED_AMETHYST.get()).requires(Items.CHAIN)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.HARDENED_AMETHYST.get()))
                .save(writer);
    }

    private void shaped(@NotNull Consumer<FinishedRecipe> writer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.SHAPED_AMETHYST.get())
                .requires(Items.ENDER_PEARL).requires(AmethystWingsItemsRegistry.TREATED_AMETHYST.get())
                .requires(Items.QUARTZ).requires(Items.PHANTOM_MEMBRANE)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.TREATED_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.STURDY_SHAPED_AMETHYST.get())
                .requires(AmethystWingsItemsRegistry.SHAPED_AMETHYST.get()).requires(Tags.Items.INGOTS_COPPER)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.SHAPED_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.FLOATY_SHAPED_AMETHYST.get())
                .requires(AmethystWingsItemsRegistry.SHAPED_AMETHYST.get()).requires(Tags.Items.FEATHERS)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.SHAPED_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.HEAVY_SHAPED_AMETHYST.get())
                .requires(AmethystWingsItemsRegistry.SHAPED_AMETHYST.get()).requires(Tags.Items.STONE)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.SHAPED_AMETHYST.get()))
                .save(writer);
    }

    private void energetic(@NotNull Consumer<FinishedRecipe> writer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.ENERGETIC_AMETHYST.get())
                .requires(Items.ENDER_PEARL).requires(AmethystWingsItemsRegistry.TREATED_AMETHYST.get())
                .requires(Items.GUNPOWDER).requires(Items.REDSTONE)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.TREATED_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.STURDY_ENERGETIC_AMETHYST.get())
                .requires(AmethystWingsItemsRegistry.ENERGETIC_AMETHYST.get()).requires(Tags.Items.INGOTS_COPPER)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.ENERGETIC_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.EMPOWERED_ENERGETIC_AMETHYST.get())
                .requires(AmethystWingsItemsRegistry.ENERGETIC_AMETHYST.get()).requires(Items.BLAZE_POWDER)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.ENERGETIC_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.ENHANCING_ENERGETIC_AMETHYST.get())
                .requires(AmethystWingsItemsRegistry.ENERGETIC_AMETHYST.get()).requires(Items.SUGAR)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.ENERGETIC_AMETHYST.get()))
                .save(writer);
    }

    private void auric(@NotNull Consumer<FinishedRecipe> writer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.AURIC_AMETHYST.get())
                .requires(Items.ENDER_PEARL).requires(AmethystWingsItemsRegistry.TREATED_AMETHYST.get(), 3)
                .requires(Items.NETHERITE_INGOT).requires(Items.MAGMA_CREAM)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.TREATED_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.BARRIER_AURIC_AMETHYST.get())
                .requires(AmethystWingsItemsRegistry.AURIC_AMETHYST.get()).requires(Items.END_CRYSTAL)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.AURIC_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.REJUVENATING_AURIC_AMETHYST.get())
                .requires(AmethystWingsItemsRegistry.AURIC_AMETHYST.get()).requires(Items.GOLDEN_APPLE)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.AURIC_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.REFOCUSED_AURIC_AMETHYST.get())
                .requires(AmethystWingsItemsRegistry.AURIC_AMETHYST.get()).requires(Items.DRAGON_BREATH)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.AURIC_AMETHYST.get()))
                .save(writer);
    }

    private void totemic(@NotNull Consumer<FinishedRecipe> writer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.TOTEMIC_AMETHYST.get(), 5)
                .requires(Items.ENDER_PEARL, 4).requires(AmethystWingsItemsRegistry.TREATED_AMETHYST.get(), 4)
                .requires(Items.TOTEM_OF_UNDYING)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.TREATED_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.VENGEFUL_TOTEMIC_AMETHYST.get())
                .requires(AmethystWingsItemsRegistry.TOTEMIC_AMETHYST.get()).requires(Items.WITHER_ROSE)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.TOTEMIC_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.MERCIFUL_TOTEMIC_AMETHYST.get())
                .requires(AmethystWingsItemsRegistry.TOTEMIC_AMETHYST.get()).requires(Items.DIAMOND)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.TOTEMIC_AMETHYST.get()))
                .save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AmethystWingsItemsRegistry.NOURISHING_TOTEMIC_AMETHYST.get())
                .requires(AmethystWingsItemsRegistry.TOTEMIC_AMETHYST.get()).requires(Items.GOLDEN_CARROT)
                .unlockedBy("criteria", InventoryChangeTrigger.TriggerInstance.hasItems(AmethystWingsItemsRegistry.TOTEMIC_AMETHYST.get()))
                .save(writer);
    }
}
