package com.m_w_k.amethystwings.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.m_w_k.amethystwings.capability.WingsCapability;
import com.m_w_k.amethystwings.inventory.WingsContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class WingsItem extends Item implements WingsCapability.ChangeListener, Equipable {
    private static final UUID attributeUUID = UUID.fromString("07822f19-e797-7d6a-d56d-29fcb4271b04");
    private final Multimap<Attribute, AttributeModifier> attributes;
    public WingsItem(Properties itemProperties) {
        super(itemProperties);
        attributes = HashMultimap.create(1,1);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack p_41452_) {
        return UseAnim.CUSTOM;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return getCapability(stack).canBlock() ? 72000 : 0;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isCrouching()) {
            if (!level.isClientSide()) {
                var cap = player.getItemInHand(hand).getCapability(ForgeCapabilities.ITEM_HANDLER);
                cap.ifPresent(handler -> {
                    WingsContainer.openGUI((ServerPlayer) player, hand);
                });
            }
            return InteractionResultHolder.success(stack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack p_41456_) {
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.OFFHAND) {
            return this.attributes;
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

    @Override
    public void onContentsChanged(WingsCapability capability) {
        this.attributes.removeAll(Attributes.ARMOR_TOUGHNESS);
        this.attributes.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(attributeUUID, "Armor toughness",
                capability.getSumToughness(), AttributeModifier.Operation.ADDITION));
    }

    private WingsCapability getCapability(ItemStack stack) {
        return stack.getCapability(WingsCapability.WINGS_CAPABILITY).orElse(WingsCapability.EMPTY);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new WingsCapability(stack, this);
    }
}
