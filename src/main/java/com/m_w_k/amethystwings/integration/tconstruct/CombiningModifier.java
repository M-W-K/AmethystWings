package com.m_w_k.amethystwings.integration.tconstruct;

import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierTraitHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CombiningModifier extends Modifier implements ModifierTraitHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MODIFIER_TRAITS);
    }

    @Override
    public void addTraits(@NotNull IToolContext context, @NotNull ModifierEntry modifier, @NotNull TraitBuilder builder, boolean firstEncounter) {
        if (firstEncounter) {
            List<MaterialVariant> materials = context.getMaterials().getList();
            List<MaterialStatsId> stats = ToolMaterialHook.stats(context.getDefinition());
            Set<ModifierId> seenTraits = new HashSet<>();
            for (int i = 0; i < materials.size() && i < stats.size(); i++) {
                List<ModifierEntry> traits = MaterialRegistry.getInstance().getTraits(materials.get(i).getId(), stats.get(i));
                for (ModifierEntry mod : traits) {
                    if (!modifier.matches(mod.getModifier()) && seenTraits.add(mod.getId())) {
                        builder.add(mod.withLevel(modifier.getLevel()));
                    }
                }
            }
        }
    }

    @Override
    public int getPriority() {
        return 210;
    }
}
