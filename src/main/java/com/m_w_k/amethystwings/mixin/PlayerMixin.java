package com.m_w_k.amethystwings.mixin;

import com.m_w_k.amethystwings.api.TotemBehavior;
import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.item.WingsItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    private PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Shadow public abstract @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot p_36257_);

    @Shadow public abstract void startFallFlying();

    @Shadow
    public abstract FoodData getFoodData();

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

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;tick(Lnet/minecraft/world/entity/player/Player;)V", shift = At.Shift.AFTER))
    private void checkNourishingCrystalTrigger(CallbackInfo ci) {
        // triggers on unable to sprint or unable to regen at under half health
        if (this.getFoodData().getFoodLevel() <= 6.0 ||
                (2 * this.getHealth() < this.getMaxHealth() && this.getFoodData().getFoodLevel() < 18.0
                        && this.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION))) {
            if (!amethystWings$tryShatterNourishingCrystal(EquipmentSlot.MAINHAND))
                amethystWings$tryShatterNourishingCrystal(EquipmentSlot.OFFHAND);
        }
    }

    private boolean amethystWings$tryShatterNourishingCrystal(EquipmentSlot slot) {
        ItemStack itemStack = this.getItemBySlot(slot);
        if (itemStack.getItem() instanceof WingsItem item) {
            WingsCapability cap = item.getCapability(itemStack);
            Collection<WingsCapability.Crystal> nourishing = cap.getSpecialCrystals(TotemBehavior.NOURISHING);
            if (nourishing.isEmpty()) return false;
            WingsCapability.Crystal crystal = nourishing.iterator().next();
            crystal.shatter(this);
            cap.handleShatteredCrystals(this);
            getFoodData().setExhaustion(0);
            getFoodData().setFoodLevel(20);
            getFoodData().setSaturation(20);
            return true;
        }
        return false;
    }
}
