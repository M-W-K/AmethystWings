package com.m_w_k.amethystwings.capability;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.m_w_k.amethystwings.api.util.WingsAction;
import com.m_w_k.amethystwings.item.WingsCrystalItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WingsCapability implements IItemHandlerModifiable, ICapabilityProvider {
    public static final Capability<WingsCapability> WINGS_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private static final UUID attributeUUID = UUID.fromString("07822f19-e797-7d6a-d56d-29fcb4271b04");
    private final Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create(1,1);

    public static final WingsCapability EMPTY = new WingsCapability();

    public static final int DURABILITY = 2 * 2 * 3 * 5;
    public static final int DURABILITY_S = 5 * 2 * 3 * 5;

    private final ItemStack stack;
    private final LazyOptional<WingsCapability> holder = LazyOptional.of(() -> this);

    private CompoundTag cachedTag;
    private NonNullList<ItemStack> itemStacksCache;

    private boolean isBlocking;

    private double armorToughnessContribution;
    private static final int MAX_SHIELD_CRYSTALS = 21;
    private final List<Crystal> crystalsShieldSorted = new ObjectArrayList<>();
    private final List<Crystal> crystalsElytraSorted = new ObjectArrayList<>();
    private final List<Crystal> crystalsElytraSortedActive = new ObjectArrayList<>();
    private final List<Crystal> crystalsBoostSorted = new ObjectArrayList<>();
    private final List<Crystal> crystalsBoostSortedActive = new ObjectArrayList<>();
    private final List<Crystal> shatteredCrystals = new ObjectArrayList<>();

    private WingsCapability() {
        this.stack = null;
    }

    public WingsCapability(ItemStack stack) {
        this.stack = stack;
        getItemList();
        onContentsChanged();
    }

    public boolean hasToughness() {
        return this.armorToughnessContribution > 0;
    }

    public Multimap<Attribute, AttributeModifier> getAttributes() {
        return this.attributes;
    }

    public void setBlocking(boolean blocking) {
        if (this.isBlocking != blocking) {
            this.isBlocking = blocking;
            if (blocking) {
                // when shielding, crystals that are in the shield can no longer be used for other things.
                crystalsBoostSortedActive.clear();
                crystalsBoostSortedActive.addAll(crystalsBoostSorted);
                crystalsBoostSortedActive.removeAll(crystalsShieldSorted);
                crystalsElytraSortedActive.clear();
                crystalsElytraSortedActive.addAll(crystalsElytraSorted);
                crystalsElytraSortedActive.removeAll(crystalsShieldSorted);
            } else {
                initActiveLists();
            }
        }
    }

    private void initActiveLists() {
        crystalsBoostSortedActive.clear();
        crystalsBoostSortedActive.addAll(crystalsBoostSorted);
        crystalsElytraSortedActive.clear();
        crystalsElytraSortedActive.addAll(crystalsElytraSorted);
    }

    public boolean canBlock() {
        return crystalsShieldSorted.size() >= 8;
    }

    public boolean canElytra() {
        return crystalsElytraSortedActive.size() >= 24;
    }

    public boolean canBoost() {
        return crystalsBoostSortedActive.size() > 0;
    }

    public void takeBlockDamage(double incomingDamage, boolean shatter, Runnable onExcessDamage) {
        // 1 point of incoming damage = 3 durability damage
        int damage = applyDamageReduction(incomingDamage * 3, true);
        Iterator<Crystal> shieldCrystals = crystalsShieldSorted.iterator();
        int guaranteedShatter = shatter ? 10 : 0;
        while (shieldCrystals.hasNext() && damage > 0) {
            Crystal crystal = shieldCrystals.next();
            if (guaranteedShatter > 0) {
                crystal.shatter();
                guaranteedShatter -= crystal.crystalItem.getMass();
                continue;
            }
            damage = crystal.damage(damage);
        }
        onContentsChanged();
        if (damage > 0)
            onExcessDamage.run();
        for (Crystal crystal : shatteredCrystals) {
            this.setStackInSlot(crystal.slot, ItemStack.EMPTY);
        }
    }

    public boolean elytraFlightTick(LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide) {
            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                if (nextFlightTick % 20 == 0) {
                    int damage = applyDamageReduction(1, false);
                    if (damage == 1) weightedDamageElytraCrystal();
                }
                entity.gameEvent(net.minecraft.world.level.gameevent.GameEvent.ELYTRA_GLIDE);
            }
        }
        return true;
    }

    private void weightedDamageElytraCrystal() {
        double sumDurability = crystalsElytraSortedActive.stream().map(Crystal::getDurabilityRemaining)
                .reduce(Integer::sum).orElse(0);
        if (sumDurability == 0) return;
        double selector = Math.random();
        double discovered = 0;
        for (Crystal crystal : crystalsElytraSortedActive) {
            discovered += crystal.getDurabilityRemaining() / sumDurability;
            if (discovered > selector)
                crystal.damage(1);
        }
    }

    private int applyDamageReduction(double damage, boolean isBlock) {
        double unbreaking = this.stack.getEnchantmentLevel(Enchantments.UNBREAKING);
        if (isBlock) unbreaking /= 2;
        double divisor = 1 + unbreaking;
        damage /= divisor;
        int whole = (int) damage;
        double decimal = damage % 1;
        if (Math.random() < decimal) whole++;
        return whole;
    }

    @Override
    public int getSlots()
    {
        return 54;
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot)
    {
        validateSlotIndex(slot);
        return getItemList().get(slot);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);

        NonNullList<ItemStack> itemStacks = getItemList();

        ItemStack existing = itemStacks.get(slot);

        int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

        if (!existing.isEmpty())
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate)
        {
            if (existing.isEmpty())
            {
                itemStacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else
            {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            setItemList(itemStacks);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        NonNullList<ItemStack> itemStacks = getItemList();
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = itemStacks.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract)
        {
            if (!simulate)
            {
                itemStacks.set(slot, ItemStack.EMPTY);
                setItemList(itemStacks);
                return existing;
            }
            else
            {
                return existing.copy();
            }
        }
        else
        {
            if (!simulate)
            {
                itemStacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                setItemList(itemStacks);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    private void validateSlotIndex(int slot)
    {
        if (slot < 0 || slot >= getSlots())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack)
    {
        return stack.getItem() instanceof WingsCrystalItem || stack.isEmpty();
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack)
    {
        validateSlotIndex(slot);
        if (!isItemValid(slot, stack)) throw new RuntimeException("Invalid stack " + stack + " for slot " + slot + ")");
        NonNullList<ItemStack> itemStacks = getItemList();
        itemStacks.set(slot, stack);
        setItemList(itemStacks);
    }

    public boolean isEmpty() {
        for (ItemStack stack : this.getItemList()) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    public void clear() {
        NonNullList<ItemStack> itemStacks = getItemList();
        itemStacks.clear();
        setItemList(itemStacks);
    }

    private NonNullList<ItemStack> getItemList()
    {
        CompoundTag rootTag = this.stack.getOrCreateTag();
        if (cachedTag == null || !cachedTag.equals(rootTag))
            itemStacksCache = refreshItemList(rootTag);
        return itemStacksCache;
    }

    private NonNullList<ItemStack> refreshItemList(CompoundTag rootTag)
    {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(getSlots(), ItemStack.EMPTY);
        if (rootTag != null && rootTag.contains("Items", CompoundTag.TAG_LIST))
        {
            ContainerHelper.loadAllItems(rootTag, itemStacks);
        }
        cachedTag = rootTag;
        return itemStacks;
    }

    private void setItemList(NonNullList<ItemStack> itemStacks)
    {
        CompoundTag existing = this.stack.getOrCreateTag();
        ContainerHelper.saveAllItems(existing, itemStacks);
        cachedTag = existing;
        onContentsChanged();
    }

    private void onContentsChanged() {
        this.armorToughnessContribution = 0;
        this.crystalsShieldSorted.clear();
        this.crystalsElytraSorted.clear();
        this.crystalsBoostSorted.clear();
        NonNullList<ItemStack> stacks = getItemList();
        for (int i = 0; i < 54; i++) {
            ItemStack stack = stacks.get(i);
            if (stack.getItem() instanceof WingsCrystalItem item) {
                Crystal crystal = new Crystal(stack, i);
                this.armorToughnessContribution += item.getArmorToughnessContribution();
                if (item.supportsActions()) {
                    if (item.isActionSupported(WingsAction.SHIELD)) this.crystalsShieldSorted.add(crystal);
                    if (item.isActionSupported(WingsAction.ELYTRA)) this.crystalsElytraSorted.add(crystal);
                    if (item.isActionSupported(WingsAction.BOOST)) this.crystalsBoostSorted.add(crystal);
                }
            }
        }
        // make sure we sort in descending order, not ascending.
        this.crystalsShieldSorted.sort(Comparator.comparingInt(a -> -a.crystalItem.getPriority() * 1000 + a.crystalStack.getDamageValue()));
        if (this.crystalsShieldSorted.size() > MAX_SHIELD_CRYSTALS) {
            this.crystalsShieldSorted.subList(MAX_SHIELD_CRYSTALS, this.crystalsShieldSorted.size()).clear();
        }
        this.crystalsElytraSorted.sort(Comparator.comparingInt(a -> -a.crystalItem.getPriority() * 1000 + a.crystalStack.getDamageValue()));
        this.crystalsBoostSorted.sort(Comparator.comparingInt(a -> -a.crystalItem.getPriority() * 1000 + a.crystalStack.getDamageValue()));
        if (this.armorToughnessContribution > 0) {
            this.attributes.removeAll(Attributes.ARMOR_TOUGHNESS);
            this.attributes.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(attributeUUID, "Armor toughness",
                    this.armorToughnessContribution, AttributeModifier.Operation.ADDITION));
        }
        initActiveLists();
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        return WINGS_CAPABILITY.orEmpty(cap, this.holder);
    }

    protected class Crystal {
        public final ItemStack crystalStack;
        public final WingsCrystalItem crystalItem;
        public final int slot;

        public boolean isShattered = false;

        public Crystal(ItemStack stack, int slot) {
            this.slot = slot;
            this.crystalStack = stack;
            this.crystalItem = (WingsCrystalItem) stack.getItem();
        }

        public int getDurabilityRemaining() {
            return this.crystalStack.getMaxDamage() - this.crystalStack.getDamageValue();
        }

        public int damage(int damage) {
            damage += this.crystalStack.getDamageValue();
            if (damage >= this.crystalStack.getMaxDamage()) {
                this.shatter();
                return damage - this.crystalStack.getMaxDamage();
            }
            else {
                this.crystalStack.setDamageValue(damage);
                return 0;
            }
        }

        public void shatter() {
            isShattered = true;
            shatteredCrystals.add(this);
        }
    }
}
