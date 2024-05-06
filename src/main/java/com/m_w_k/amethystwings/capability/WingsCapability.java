package com.m_w_k.amethystwings.capability;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.m_w_k.amethystwings.api.util.WingsAction;
import com.m_w_k.amethystwings.api.util.WingsRenderHelper;
import com.m_w_k.amethystwings.item.WingsCrystalItem;
import com.m_w_k.amethystwings.network.WingsBoostPacket;
import com.m_w_k.amethystwings.registry.AmethystWingsSoundsRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;
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
    private long lastBoostTick = 0;

    private boolean crystalDamaged;

    private float partialTicks;
    private boolean tickPassed;
    private Vec3 lastEntityLocation = new Vec3(0, 0, 0);
    private final Vector3d drift = new Vector3d();
    private boolean crouching;
    private boolean crystalRenderCacheInvalid = true;
    private final static PoseStack ELYTRA_HELPER = new PoseStack();
    private final static ModelPart RIGHT_FAKE_WING = new ModelPart(null, null);
    private final static ModelPart LEFT_FAKE_WING = new ModelPart(null, null);
    private static Matrix4f RIGHT_WING_MATRIX;
    private static Matrix4f LEFT_WING_MATRIX;

    private double armorToughnessContribution;
    private static final int MIN_SHIELD_CRYSTALS = 8;
    private static final int MAX_SHIELD_CRYSTALS = 20;
    private static final int MIN_ELYTRA_CRYSTALS = 18;
    private static final int MAX_ELYTRA_CRYSTALS = 18;
    private static final int MIN_BOOST_CRYSTALS = 1;
    private static final int MAX_BOOST_CRYSTALS = 4;
    private final SortedCrystalList crystalsShieldSorted = new SortedCrystalList();
    private final SortedCrystalList crystalsElytraSorted = new SortedCrystalList();
    private final SortedCrystalList crystalsElytraSortedActive = new SortedCrystalList();
    private final SortedCrystalList crystalsBoostSorted = new SortedCrystalList();
    private final SortedCrystalList crystalsBoostSortedActive = new SortedCrystalList();
    private final List<Crystal> crystals = new ObjectArrayList<>();
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
            this.invalidateCrystalRenderCache();
            this.isBlocking = blocking;
            initActiveLists();
        }
    }

    private void initActiveLists() {
        invalidateCrystalRenderCache();
        crystalsBoostSortedActive.clear();
        crystalsBoostSortedActive.addAll(crystalsBoostSorted.manyAction);
        crystalsElytraSortedActive.clear();
        crystalsElytraSortedActive.addAll(crystalsElytraSorted.manyAction);
        if (this.isBlocking) {
            // when blocking, crystals that are in the shield cannot be used.
            crystalsBoostSortedActive.removeAll(crystalsShieldSorted.manyAction);
            crystalsElytraSortedActive.removeAll(crystalsShieldSorted.manyAction);
        }

        if (crystalsElytraSortedActive.size() + crystalsElytraSorted.singleAction.size() >= MIN_ELYTRA_CRYSTALS) {
            // we can form a full elytra, prioritize
            crystalsElytraSortedActive.addAll(crystalsElytraSorted.singleAction, false);
            crystalsElytraSortedActive.truncate(MAX_ELYTRA_CRYSTALS);
            crystalsBoostSortedActive.removeAll(crystalsElytraSortedActive.manyAction);
            crystalsBoostSortedActive.truncate(MAX_BOOST_CRYSTALS);
        } else {
            // otherwise, prioritize boost
            crystalsBoostSortedActive.addAll(crystalsBoostSorted.singleAction, false);
            crystalsBoostSortedActive.truncate(MAX_BOOST_CRYSTALS);
            crystalsElytraSortedActive.removeAll(crystalsBoostSortedActive.manyAction);
            crystalsElytraSortedActive.truncate(MAX_ELYTRA_CRYSTALS);
        }
    }

    private static void truncateList(List<?> list, int maxSize) {
        if (list.size() > maxSize) {
            list.subList(maxSize, list.size()).clear();
        }
    }

    public boolean canBlock() {
        return crystalsShieldSorted.fullList.size() >= MIN_SHIELD_CRYSTALS;
    }

    public boolean canElytra() {
        return crystalsElytraSortedActive.size() >= MIN_ELYTRA_CRYSTALS;
    }

    public boolean canBoost() {
        return crystalsBoostSortedActive.size() >= MIN_BOOST_CRYSTALS;
    }

    public void doBoost(LivingEntity entity, boolean elytraBoost, boolean mainHand) {
        long tick = entity.level().getGameTime();
        if (tick < this.lastBoostTick + 20) return;

        if (entity.level().isClientSide()) WingsBoostPacket.send(mainHand);

        this.lastBoostTick = tick;
        Vec3 deltaMovement = entity.getDeltaMovement();
        if (elytraBoost) {
            Vec3 lookAngle = entity.getLookAngle();
            entity.setDeltaMovement(deltaMovement.add(lookAngle.scale(2)));
        } else {
            entity.setDeltaMovement(deltaMovement.x(), 1, deltaMovement.z());
        }
        takeBoostDamage(entity);
    }

    public void takeBoostDamage(@Nullable LivingEntity owningEntity) {
        Iterator<Crystal> boostCrystals = crystalsBoostSortedActive.fullList.iterator();
        if (!boostCrystals.hasNext()) return;
        boostCrystals.next().damage(applyDamageReduction(60, false));
        handleShatteredCrystals(owningEntity);
    }

    public void takeBlockDamage(@Nullable LivingEntity owningEntity, double incomingDamage, boolean shatter, Runnable onExcessDamage) {
        // 1 point of incoming damage = 3 durability damage
        int damage = applyDamageReduction(incomingDamage * 3, true);
        Iterator<Crystal> shieldCrystals = crystalsShieldSorted.fullList.iterator();
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
        if (damage > 0)
            onExcessDamage.run();
        handleShatteredCrystals(owningEntity);
    }

    public boolean elytraFlightTick(LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide) {
            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                if (nextFlightTick % 20 == 0) {
                    weightedDamage(entity, crystalsElytraSortedActive.fullList, applyDamageReduction(1, false), false);
                }
                entity.gameEvent(net.minecraft.world.level.gameevent.GameEvent.ELYTRA_GLIDE);
            }
        }
        return true;
    }

    public void weightedDamage(@Nullable LivingEntity owningEntity, List<Crystal> targets, int amount, boolean spread) {
        if (amount == 0) return;
        int sumDurability = targets.stream().map(Crystal::getDurabilityRemaining)
                .reduce(Integer::sum).orElse(0);
        if (sumDurability == 0) return;
        amount = Math.min(amount, sumDurability);
        double selector;
        double discovered;

        while (amount > 0) {
            selector = Math.random();
            discovered = 0;
            for (Crystal crystal : targets) {
                discovered += (double) crystal.getDurabilityRemaining() / sumDurability;
                if (discovered > selector) {
                    if (spread) amount += crystal.damage(1) - 1;
                    else amount = crystal.damage(amount);
                    break;
                }
            }
            if (amount == 0) break;
            sumDurability = targets.stream().map(Crystal::getDurabilityRemaining)
                    .reduce(Integer::sum).orElse(0);
            if (sumDurability == 0) return;
        }
        handleShatteredCrystals(owningEntity);
    }

    public int weightedRepair(List<Crystal> targets, int amount, boolean spread) {
        int sumDamage;
        double selector;
        double discovered;

        while (amount > 0) {
            sumDamage = targets.stream().map(Crystal::getDamage).reduce(Integer::sum).orElse(0);
            if (sumDamage == 0) break;
            selector = Math.random();
            discovered = 0;
            for (Crystal crystal : targets) {
                discovered += (double) crystal.getDamage() / sumDamage;
                if (discovered > selector) {
                    if (spread) amount += crystal.repair(1) - 1;
                    else amount = crystal.repair(amount);
                    break;
                }
            }
        }
        onContentsChanged();
        return amount;
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

    public void prepareForRender(float partialTicks, LivingEntity entity) {
        this.tickPassed = this.partialTicks > partialTicks;
        double delta = partialTicks - this.partialTicks + (this.tickPassed ? 1 : 0);
        this.partialTicks = partialTicks;
        this.crouching = entity.hasPose(Pose.CROUCHING);
        Vec3 entityLocation = entity.getPosition(partialTicks);
        this.drift.set(
                -log((entityLocation.x() - lastEntityLocation.x()) / delta) / 8,
                -log((entityLocation.y() - lastEntityLocation.y()) / delta) / 8,
                -log((entityLocation.z() - lastEntityLocation.z()) / delta) / 8
        );
        this.lastEntityLocation = entityLocation;
        if (this.crystalRenderCacheInvalid) rebuildCrystalRenderCache();
        setupAnim(entity);
    }

    private static double log(double num) {
        return Math.log1p(Math.abs(num)) * Math.signum(num);
    }

    private void rebuildCrystalRenderCache() {
        Iterator<WingsRenderHelper.CrystalTarget> iter;
        List<Crystal> assignedCrystals = new ObjectArrayList<>();
        if (isBlocking) {
            iter = WingsRenderHelper.CRYSTAL_POSITIONS.get(WingsAction.SHIELD);
            for (Crystal crystal : crystalsShieldSorted.fullList) {
                if (!iter.hasNext()) break;
                crystal.cachedTarget = iter.next();
                assignedCrystals.add(crystal);
            }
        } else {
            iter = WingsRenderHelper.CRYSTAL_POSITIONS.get(WingsAction.SHIELD_IDLE);
            for (Crystal crystal : crystalsShieldSorted.singleAction) {
                if (!iter.hasNext()) break;
                crystal.cachedTarget = iter.next();
                assignedCrystals.add(crystal);
            }
        }

        iter = WingsRenderHelper.CRYSTAL_POSITIONS.get(WingsAction.ELYTRA);
        for (Crystal crystal : crystalsElytraSortedActive.fullList) {
            if (!iter.hasNext()) break;
            crystal.cachedTarget = iter.next();
            assignedCrystals.add(crystal);
        }

        iter = WingsRenderHelper.CRYSTAL_POSITIONS.get(WingsAction.BOOST);
        for (Crystal crystal : crystalsBoostSortedActive.fullList) {
            if (!iter.hasNext()) break;
            crystal.cachedTarget = iter.next();
            assignedCrystals.add(crystal);
        }

        iter = WingsRenderHelper.CRYSTAL_POSITIONS.get(WingsAction.IDLE);
        for (Crystal crystal : crystals) {
            if (assignedCrystals.contains(crystal)) continue;
            if (!iter.hasNext()) break;
            crystal.cachedTarget = iter.next();
        }

        crystalRenderCacheInvalid = false;
    }

    /**
     * Ripped directly from {@link net.minecraft.client.model.ElytraModel}
     */
    private void setupAnim(LivingEntity entity) {
        float f = 0.2617994F;
        float f1 = -0.2617994F;
        float f2 = 0.0F;
        float f3 = 0.0F;
        if (entity.isFallFlying()) {
            float f4 = 1.0F;
            Vec3 vec3 = entity.getDeltaMovement();
            if (vec3.y < 0.0D) {
                Vec3 vec31 = vec3.normalize();
                f4 = 1.0F - (float)Math.pow(-vec31.y, 1.5D);
            }

            f = f4 * 0.34906584F + (1.0F - f4) * f;
            f1 = f4 * (-(float)Math.PI / 2F) + (1.0F - f4) * f1;
        } else if (entity.isCrouching()) {
            f = 0.6981317F;
            f1 = (-(float)Math.PI / 4F);
            f2 = 3.0F;
            f3 = 0.08726646F;
        }

        LEFT_FAKE_WING.y = f2;
        if (entity instanceof AbstractClientPlayer abstractclientplayer) {
            abstractclientplayer.elytraRotX += (f - abstractclientplayer.elytraRotX) * 0.1F;
            abstractclientplayer.elytraRotY += (f3 - abstractclientplayer.elytraRotY) * 0.1F;
            abstractclientplayer.elytraRotZ += (f1 - abstractclientplayer.elytraRotZ) * 0.1F;
            LEFT_FAKE_WING.xRot = abstractclientplayer.elytraRotX;
            LEFT_FAKE_WING.yRot = abstractclientplayer.elytraRotY;
            LEFT_FAKE_WING.zRot = abstractclientplayer.elytraRotZ;
        } else {
            LEFT_FAKE_WING.xRot = f;
            LEFT_FAKE_WING.zRot = f1;
            LEFT_FAKE_WING.yRot = f3;
        }

        RIGHT_FAKE_WING.yRot = -LEFT_FAKE_WING.yRot;
        RIGHT_FAKE_WING.y = LEFT_FAKE_WING.y;
        RIGHT_FAKE_WING.xRot = LEFT_FAKE_WING.xRot;
        RIGHT_FAKE_WING.zRot = -LEFT_FAKE_WING.zRot;

        ELYTRA_HELPER.pushPose();
        RIGHT_FAKE_WING.translateAndRotate(ELYTRA_HELPER);
        RIGHT_WING_MATRIX = ELYTRA_HELPER.last().pose();
        ELYTRA_HELPER.popPose();
        ELYTRA_HELPER.pushPose();
        LEFT_FAKE_WING.translateAndRotate(ELYTRA_HELPER);
        LEFT_WING_MATRIX = ELYTRA_HELPER.last().pose();
        ELYTRA_HELPER.popPose();
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
        this.crystals.clear();
        this.crystalsShieldSorted.clear();
        this.crystalsElytraSorted.clear();
        this.crystalsBoostSorted.clear();
        NonNullList<ItemStack> stacks = getItemList();
        for (int i = 0; i < 54; i++) {
            ItemStack stack = stacks.get(i);
            if (stack.getItem() instanceof WingsCrystalItem item) {
                Crystal crystal = new Crystal(stack, i);
                this.armorToughnessContribution += item.getArmorToughnessContribution();
                this.crystals.add(crystal);
                if (item.supportsActions()) {
                    if (item.isActionSupported(WingsAction.SHIELD)) this.crystalsShieldSorted.add(crystal);
                    if (item.isActionSupported(WingsAction.ELYTRA)) this.crystalsElytraSorted.add(crystal);
                    if (item.isActionSupported(WingsAction.BOOST)) this.crystalsBoostSorted.add(crystal);
                }
            }
        }
        this.crystalsShieldSorted.truncate(MAX_SHIELD_CRYSTALS);
        this.crystalsElytraSorted.sort();
        this.crystalsBoostSorted.sort();
        if (this.armorToughnessContribution > 0) {
            this.attributes.removeAll(Attributes.ARMOR_TOUGHNESS);
            this.attributes.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(attributeUUID, "Armor toughness",
                    this.armorToughnessContribution, AttributeModifier.Operation.ADDITION));
        }
        initActiveLists();
    }

    /**
     * Call this whenever the positions of crystals should change.
     */
    protected void invalidateCrystalRenderCache() {
        if (this.crystalRenderCacheInvalid) return;
        this.crystals.forEach(crystal -> crystal.cachedTarget = null);
        this.crystalRenderCacheInvalid = true;
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        return WINGS_CAPABILITY.orEmpty(cap, this.holder);
    }

    public List<Crystal> getCrystals() {
        return this.crystals;
    }

    /**
     * Should always be called after crystals are damaged for any reason.
     */
    private void handleShatteredCrystals(@Nullable LivingEntity owningEntity) {
        boolean playSounds = owningEntity != null && !owningEntity.level().isClientSide();
        boolean crystalDamaged = this.crystalDamaged;
        this.crystalDamaged = false;

        if (shatteredCrystals.size() == 0) {
            if (crystalDamaged && playSounds) {
                Vec3 pos = owningEntity.position();
                // TODO localize sounds to crystal locations rather than player location?
                owningEntity.level().playSound(null, pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5,
                        AmethystWingsSoundsRegistry.CRYSTAL_DAMAGE.get(), SoundSource.BLOCKS, 0.2f, 2f);
            }
            onContentsChanged();
            return;
        }
        NonNullList<ItemStack> itemStacks = getItemList();
        for (Crystal crystal : shatteredCrystals) {
            itemStacks.set(crystal.slot, ItemStack.EMPTY);
        }
        shatteredCrystals.clear();
        setItemList(itemStacks);

        if (playSounds) {
            Vec3 pos = owningEntity.position();
            owningEntity.level().playSound(null, pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5,
                    AmethystWingsSoundsRegistry.CRYSTAL_SHATTER.get(), SoundSource.BLOCKS, 1f, 1f);
        }
    }

    public class Crystal {

        private final Quaterniond lastRotation = new Quaterniond();
        private final Vector3f lastPosition = new Vector3f();
        private final Vector3f targetPosition = new Vector3f();
        private WingsRenderHelper.CrystalTarget cachedTarget;

        private static final Matrix4f HELPER = new Matrix4f();
        private static final Matrix4f HELPER2 = new Matrix4f();


        public final ItemStack crystalStack;
        public final WingsCrystalItem crystalItem;
        public final int slot;

        public boolean isShattered = false;

        protected Crystal(@NotNull ItemStack stack, int slot) {
            this.slot = slot;
            this.crystalStack = stack;
            this.crystalItem = (WingsCrystalItem) stack.getItem();
        }

        public boolean singleAction() {
            return this.crystalItem.supportsActions() && this.crystalItem.getSupportedActions().size() == 1;
        }

        public WingsRenderHelper.CrystalTarget getTarget() {
            // fallback
            if (this.cachedTarget == null)
                this.cachedTarget = WingsRenderHelper.CRYSTAL_POSITIONS.get(WingsAction.NONE).get(0);
            return this.cachedTarget;
        }

        public Vec3 calculateOffset(Matrix4f rot) {
            Vector3f target = getTarget().targetPosition().toVector3f();
            boolean leftWing = getTarget().misc() == 0;
            if (getTarget().group().isElytraAttached()) {
                // correction that probably belongs at a different step but IDK where
                if (crouching) target.add(leftWing ? -3/16f : 3/16f, 4/16f, -4/16f);
                target.mulDirection(leftWing ? LEFT_WING_MATRIX : RIGHT_WING_MATRIX);
            }
            rot.transformDirection(target);
            return this.lerpPosition(new Vec3(target.x(), target.y(), target.z()));
        }

        public Quaterniond calculateRotation(Matrix4f rot) {
            Quaterniondc target = getTarget().targetRotation();
            // why can I not transform quaternions by a matrix directly smh
            Matrix4f targetAbsolute = target.get(HELPER).mul(rot.invert(HELPER2));
            if (this.getTarget().group().isElytraAttached()) {
                Matrix4f matrix = (this.getTarget().misc() == 0 ? LEFT_WING_MATRIX : RIGHT_WING_MATRIX);
                matrix.mul(targetAbsolute, targetAbsolute);
            }
            Quaterniond out = this.lerpRotation(targetAbsolute.getNormalizedRotation(new Quaterniond()));
            return out.get(HELPER).mul(rot).getNormalizedRotation(out);
        }

        @Contract("_ -> new")
        private @NotNull Vec3 lerpPosition(Vec3 target) {
            if (tickPassed) {
                this.lastPosition.set(targetPosition);
                this.targetPosition.set(target.x(), target.y(), target.z());
            }
            return new Vec3(
                    Mth.lerp(partialTicks, this.lastPosition.x(), targetPosition.x()) + drift.x(),
                    Mth.lerp(partialTicks, this.lastPosition.y(), targetPosition.y()) + drift.y(),
                    Mth.lerp(partialTicks, this.lastPosition.z(), targetPosition.z()) + drift.z()
            );

        }

        private Quaterniond lerpRotation(@NotNull Quaterniond target) {
            if (tickPassed) this.lastRotation.set(target);
            return this.lastRotation.nlerp(target, partialTicks, target);
        }

        public void render(@NotNull PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            BakedModel model = renderer.getItemModelShaper().getModelManager().getModel(crystalItem.getWingsModelLoc());
            renderer.render(stack, ItemDisplayContext.NONE, false, poseStack, buffer, combinedLightIn, combinedOverlayIn, model);
            poseStack.popPose();
        }

        public int getDamage() {
            return this.crystalStack.getDamageValue();
        }

        public int getDurabilityRemaining() {
            return this.crystalStack.getMaxDamage() - this.crystalStack.getDamageValue();
        }

        public int repair(int amount) {
            int damage = this.crystalStack.getDamageValue() - amount;
            if (damage < 0) {
                this.crystalStack.setDamageValue(0);
                return -damage;
            } else {
                this.crystalStack.setDamageValue(damage);
                return 0;
            }
        }

        public int damage(int damage) {
            if (damage != 0) crystalDamaged = true;
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

    protected class SortedCrystalList {
        public List<Crystal> fullList = new ObjectArrayList<>();
        public List<Crystal> singleAction = new ObjectArrayList<>();
        public List<Crystal> manyAction = new ObjectArrayList<>();

        public SortedCrystalList() {}

        /**
         * Sort should be called manually after all crystals are added
         */
        public void add(Crystal crystal) {
            fullList.add(crystal);
        }

        /**
         * Sorts automatically
         */
        public void addAll(Collection<? extends Crystal> crystals) {
            addAll(crystals, true);
        }

        public void addAll(Collection<? extends Crystal> crystals, boolean sort) {
            fullList.addAll(crystals);
            if (sort) sort();
        }

        /**
         * Sorts automatically
         */
        public void removeAll(Collection<? extends Crystal> crystals) {
            removeAll(crystals, true);
        }

        public void removeAll(Collection<? extends Crystal> crystals, boolean sort) {
            fullList.removeAll(crystals);
            if (sort) sort();
        }

        /**
         * Sorts automatically
         */
        public void truncate(int maxSize) {
            sortAlt();
            truncateList(fullList, maxSize);
            sort();
        }

        public int size() {
            return fullList.size();
        }

        public void clear() {
            fullList.clear();
            singleAction.clear();
            manyAction.clear();
        }

        /**
         * Sort in descending order, prioritizing single action crystals.
         */
        public void sortAlt() {
            this.singleAction.clear();
            this.manyAction.clear();
            this.fullList.sort(Comparator.comparingInt(a -> -(a.singleAction() ? 1000000 : 0) -
                    a.crystalItem.getPriority() * 1000 + a.crystalStack.getDamageValue()));
            for (Crystal crystal : fullList) {
                if (crystal.singleAction()) this.singleAction.add(crystal);
                else this.manyAction.add(crystal);
            }

        }

        /**
         * Sort in descending order
         */
        public void sort() {
            this.singleAction.clear();
            this.manyAction.clear();
            this.fullList.sort(Comparator.comparingInt(a -> -a.crystalItem.getPriority() * 1000 + a.crystalStack.getDamageValue()));
            for (Crystal crystal : fullList) {
                if (crystal.singleAction()) this.singleAction.add(crystal);
                else this.manyAction.add(crystal);
            }

        }
    }
}
