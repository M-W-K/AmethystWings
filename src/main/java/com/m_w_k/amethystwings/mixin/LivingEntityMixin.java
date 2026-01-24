package com.m_w_k.amethystwings.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.item.WingsItem;
import com.m_w_k.amethystwings.registry.AmethystWingsAttributeRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    private LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot p_21127_);

    @Shadow protected int fallFlyTicks;

    @Shadow public abstract boolean hasEffect(MobEffect p_21024_);

    @ModifyVariable(method = "updateFallFlying", at = @At(value = "JUMP", opcode = Opcodes.IFNE, ordinal = 3))
    private boolean extendedElytraCheck(boolean flag) {
        if (flag) return true;
        boolean canFly = !this.onGround() && !this.isPassenger() && !this.hasEffect(MobEffects.LEVITATION);
        if (this.getSharedFlag(7) && canFly) {
            flag = amethystWings$tryExtendedCheck(EquipmentSlot.MAINHAND);
            if (!flag) flag = amethystWings$tryExtendedCheck(EquipmentSlot.OFFHAND);
        }
        // failsafe -- when player is fallflying with both elytra and wings, it doesn't stop when it should.
        if (!canFly && ((Object) this) instanceof Player player) player.stopFallFlying();
        return flag;
    }

    @Unique
    private boolean amethystWings$tryExtendedCheck(EquipmentSlot slot) {
        ItemStack itemStack = this.getItemBySlot(slot);
        if (itemStack.getItem() instanceof WingsItem item) {
            WingsCapability cap = item.getCapability(itemStack);
            LivingEntity thiss = (LivingEntity) (Object) this;
            return cap.canElytra() && cap.elytraFlightTick(thiss, this.fallFlyTicks);
        }
        return false;
    }

    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder attachWarding(AttributeSupplier.Builder builder) {
        return builder.add(AmethystWingsAttributeRegistry.WARDING.get())
                .add(AmethystWingsAttributeRegistry.BARRIER.get());
    }
}
