package com.m_w_k.amethystwings.mixin;

import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.item.WingsItem;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
    @ModifyVariable(method = "repairPlayerItems", at = @At(value = "HEAD"), argsOnly = true)
    private int repairWingsItemInjection(int quant, Player p_147093_) {
        if (quant == 0) return 0;
        ItemStack stack = p_147093_.getMainHandItem();
        quant = amethystWings$repairWingsItem(p_147093_, stack, quant);
        if (quant == 0) return 0;
        stack = p_147093_.getOffhandItem();
        quant = amethystWings$repairWingsItem(p_147093_, stack, quant);
        return quant;
    }

    @Unique
    private int amethystWings$repairWingsItem(Player player, ItemStack wingsStack, int amount) {
        if (wingsStack.getItem() instanceof WingsItem item
                && wingsStack.getEnchantmentLevel(Enchantments.MENDING) > 0) {
            WingsCapability cap = item.getCapability(wingsStack);
            amount = cap.weightedRepair(player, cap.getCrystals(), amount, false);
        }
        return amount;
    }
}
