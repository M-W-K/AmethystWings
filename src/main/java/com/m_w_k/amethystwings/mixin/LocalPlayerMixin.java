package com.m_w_k.amethystwings.mixin;

import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.item.WingsItem;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {

    @Shadow @Final public ClientPacketListener connection;

    private LocalPlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "aiStep", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 25), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void extendedElytraCheck(CallbackInfo ci, boolean flag, boolean flag1, boolean flag2, float f, boolean flag3, boolean flag4, boolean flag5, boolean flag6, boolean flag9, ItemStack itemstack) {
        if (itemstack.canElytraFly(this) || !this.tryToStartFallFlying()) return;
        ItemStack itemStack = this.getItemBySlot(EquipmentSlot.OFFHAND);
        if (itemStack.getItem() instanceof WingsItem item) {
            WingsCapability cap = item.getCapability(itemStack);
            if (cap.canElytra()) this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        }
    }
}
