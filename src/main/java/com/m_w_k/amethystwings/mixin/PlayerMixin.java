package com.m_w_k.amethystwings.mixin;

import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.item.WingsItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    private PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Shadow public abstract @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot p_36257_);

    @Shadow public abstract void startFallFlying();

    @Inject(method = "tryToStartFallFlying", at = @At(value = "JUMP", opcode = Opcodes.IFEQ), cancellable = true)
    private void extendedElytraCheck(CallbackInfoReturnable<Boolean> cir) {
        if (amethystWings$tryFlyWithStack(EquipmentSlot.MAINHAND) ||
                amethystWings$tryFlyWithStack(EquipmentSlot.OFFHAND)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean amethystWings$tryFlyWithStack(EquipmentSlot slot) {
        ItemStack itemStack = this.getItemBySlot(slot);
        if (itemStack.getItem() instanceof WingsItem item) {
            WingsCapability cap = item.getCapability(itemStack);
            if (cap.canElytra()) {
                this.startFallFlying();
                return true;
            }
        }
        return false;
    }
}
