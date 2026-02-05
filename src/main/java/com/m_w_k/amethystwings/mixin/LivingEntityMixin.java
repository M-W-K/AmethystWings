package com.m_w_k.amethystwings.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.m_w_k.amethystwings.api.TotemBehavior;
import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.item.WingsItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    private LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot p_21127_);

    @Shadow protected int fallFlyTicks;

    @ModifyVariable(method = "updateFallFlying", at = @At(value = "STORE", ordinal = 1))
    private boolean extendedElytraCheck(boolean flag) {
        if (flag) return true;
        // short-circuiting OR
        return amethystWings$tryExtendedCheck(EquipmentSlot.MAINHAND) || amethystWings$tryExtendedCheck(EquipmentSlot.OFFHAND);
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

    @ModifyReturnValue(method = "checkTotemDeathProtection", at = @At("RETURN"))
    private boolean checkTotemicCrystal(boolean playerWasSaved) {
        if (playerWasSaved) return true;
        // short-circuiting OR
        List<TotemBehavior> shattered = amethystWings$totemicCrystalCheck(EquipmentSlot.MAINHAND);
        if (shattered == null) shattered = amethystWings$totemicCrystalCheck(EquipmentSlot.OFFHAND);
        if (shattered != null) {
            LivingEntity thiss = (LivingEntity) (Object) this;
            thiss.setHealth(1.0F);
            thiss.removeAllEffects();
            thiss.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            thiss.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            thiss.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
            shattered.forEach(b -> b.onSave(thiss));
            this.level().broadcastEntityEvent(this, (byte)35);
            return true;
        }
        return false;
    }

    @Unique
    private @Nullable List<TotemBehavior> amethystWings$totemicCrystalCheck(EquipmentSlot slot) {
        ItemStack itemStack = this.getItemBySlot(slot);
        if (itemStack.getItem() instanceof WingsItem item) {
            WingsCapability cap = item.getCapability(itemStack);
            LivingEntity thiss = (LivingEntity) (Object) this;
            Collection<WingsCapability.Crystal> totemics = cap.getSpecialCrystals(TotemBehavior.IDENTIFIER);
            if (totemics.size() < 4) return null;

            Iterator<WingsCapability.Crystal> iter = totemics.iterator();
            List<TotemBehavior> shattered = new ObjectArrayList<>(4);
            for (int i = 0; i < 4; i++) {
                WingsCapability.Crystal crystal = iter.next();
                crystal.shatter(thiss);
                if (crystal.crystalItem.getSpecial(TotemBehavior.IDENTIFIER) instanceof TotemBehavior behavior) {
                    shattered.add(behavior);
                }
            }
            cap.handleShatteredCrystals(thiss);
            return shattered;
        }
        return null;
    }
}
