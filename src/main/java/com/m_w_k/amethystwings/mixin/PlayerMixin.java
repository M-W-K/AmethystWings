package com.m_w_k.amethystwings.mixin;

import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.item.WingsItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot p_36257_);

    @Shadow public abstract void startFallFlying();

    @Inject(method = "tryToStartFallFlying", at = @At(value = "JUMP", opcode = Opcodes.IFEQ), cancellable = true)
    private void extendedElytraCheck(CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = this.getItemBySlot(EquipmentSlot.OFFHAND);
        if (itemStack.getItem() instanceof WingsItem item) {
            WingsCapability cap = item.getCapability(itemStack);
            if (cap.canElytra()) {
                this.startFallFlying();
                cir.setReturnValue(true);
            }
        }
    }
}
