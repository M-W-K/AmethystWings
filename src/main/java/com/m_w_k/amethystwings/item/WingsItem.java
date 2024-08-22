package com.m_w_k.amethystwings.item;

import com.google.common.collect.Multimap;
import com.m_w_k.amethystwings.capability.WingsCapDataCache;
import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.client.renderer.WingsItemStackRenderer;
import com.m_w_k.amethystwings.inventory.WingsContainer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.NonNullLazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class WingsItem extends Item implements Equipable {
    public WingsItem(Properties itemProperties) {
        super(itemProperties);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public @Nullable CompoundTag getShareTag(ItemStack stack) {
        CompoundTag tag = super.getShareTag(stack);
        if (tag != null) tag.putInt("ID", getCapability(stack).getDataID());
        return tag;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        super.readShareTag(stack, nbt);
        if (nbt != null) getCapability(stack).setDataID(nbt.getInt("ID"));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack p_41452_) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return getCapability(stack).canBlock() ? 72000 : 0;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                WingsContainer.openGUI((ServerPlayer) player, hand);
            }
            return InteractionResultHolder.success(stack);
        } else if (getUseDuration(stack) != 0) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        } else {
            return InteractionResultHolder.pass(stack);
        }
    }

    @Override
    public void onUseTick(@NotNull Level p_41428_, @NotNull LivingEntity entity, @NotNull ItemStack stack, int p_41431_) {
        super.onUseTick(p_41428_, entity, stack, p_41431_);
        WingsCapability cap = getCapability(stack);
        cap.setBlocking(true);
        if (!cap.canBlock()) entity.stopUsingItem();
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        super.onStopUsing(stack, entity, count);
        getCapability(stack).setBlocking(false);
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack p_41456_) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.category == EnchantmentCategory.BREAKABLE;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 1;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        WingsCapability cap = getCapability(stack);
        if (slot == EquipmentSlot.OFFHAND && cap.hasToughness()) {
            return cap.getAttributes();
        }
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return net.minecraftforge.common.ToolActions.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.OFFHAND;
    }

    public WingsCapability getCapability(ItemStack stack) {
        stack.reviveCaps();
        return stack.getCapability(WingsCapability.WINGS_CAPABILITY).orElse(WingsCapability.EMPTY);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        Integer dataID = nbt == null ? null : nbt.contains("Parent") ? nbt.getInt("Parent") : null;
        return WingsCapDataCache.getCap(stack, dataID);
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final NonNullLazy<WingsItemStackRenderer> renderer = NonNullLazy.of(WingsItemStackRenderer::new);

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer.get();
            }
        });
    }
}
