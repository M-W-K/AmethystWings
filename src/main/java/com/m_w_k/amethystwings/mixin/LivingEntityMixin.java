package com.m_w_k.amethystwings.mixin;

import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.item.WingsItem;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot p_21127_);

    @Shadow protected int fallFlyTicks;

    @Shadow public abstract boolean hasEffect(MobEffect p_21024_);

    @ModifyVariable(method = "updateFallFlying", at = @At(value = "JUMP", opcode = Opcodes.IFNE, ordinal = 3))
    private boolean extendedElytraCheck(boolean flag) {
        if (flag) return true;
        if (this.getSharedFlag(7) && !this.onGround() && !this.isPassenger() && !this.hasEffect(MobEffects.LEVITATION)) {
            ItemStack itemStack = this.getItemBySlot(EquipmentSlot.OFFHAND);
            if (itemStack.getItem() instanceof WingsItem item) {
                WingsCapability cap = item.getCapability(itemStack);
                flag = cap.canElytra() && cap.elytraFlightTick(((LivingEntity) (Object) this), this.fallFlyTicks);
            }
        }
        return flag;
    }
}
