package com.m_w_k.amethystwings.capability;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.m_w_k.amethystwings.api.util.WingsAction;
import com.m_w_k.amethystwings.api.util.WingsRenderHelp;
import com.m_w_k.amethystwings.client.model.CrystalModel;
import com.m_w_k.amethystwings.item.WingsCrystalItem;
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
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
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

    private double partialTicks;
    private boolean tickPassed;
    private boolean needsIteratorRegeneration;
    private final static PoseStack ELYTRA_HELPER = new PoseStack();
    private final static ModelPart RIGHT_FAKE_WING = new ModelPart(null, null);
    private final static ModelPart LEFT_FAKE_WING = new ModelPart(null, null);
    private static Matrix4f RIGHT_WING_MATRIX;
    private static Matrix4f LEFT_WING_MATRIX;

    private double armorToughnessContribution;
    private static final int MIN_SHIELD_CRYSTALS = 8;
    private static final int MAX_SHIELD_CRYSTALS = 20;
    private static final int MIN_ELYTRA_CRYSTALS = 24;
    private final List<Crystal> crystalsShieldSorted = new ObjectArrayList<>();
    private final List<Crystal> crystalsElytraSorted = new ObjectArrayList<>();
    private final List<Crystal> crystalsElytraSortedActive = new ObjectArrayList<>();
    private final List<Crystal> crystalsBoostSorted = new ObjectArrayList<>();
    private final List<Crystal> crystalsBoostSortedActive = new ObjectArrayList<>();
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
            this.resetCrystalRenderCache();
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
        return crystalsShieldSorted.size() >= MIN_SHIELD_CRYSTALS;
    }

    public boolean canElytra() {
        return crystalsElytraSortedActive.size() >= MIN_ELYTRA_CRYSTALS;
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
        handleShatteredCrystals();
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
            if (discovered > selector) {
                // no need to handle excess damage, it won't occur.
                crystal.damage(1);
                break;
            }
        }
        handleShatteredCrystals();
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

    public void prepareForRender(double partialTicks, LivingEntity entity) {
        this.tickPassed = this.partialTicks > partialTicks;
        this.partialTicks = partialTicks;
        if (needsIteratorRegeneration) {
            WingsRenderHelp.regenerateIterators();
            needsIteratorRegeneration = false;
        }
        setupAnim(entity);
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
        resetCrystalRenderCache();
    }

    protected void resetCrystalRenderCache() {
        this.crystals.forEach(crystal -> crystal.cachedTarget = null);
        this.needsIteratorRegeneration = true;
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
    private void handleShatteredCrystals() {
        for (Crystal crystal : shatteredCrystals) {
            this.setStackInSlot(crystal.slot, ItemStack.EMPTY);
        }
    }

    public class Crystal {

        private final CrystalModel model;

        private static final double LERP_FACTOR = 0.5;
        private final Quaterniond lastRotation = new Quaterniond();
        private Vec3 lastPosition = new Vec3(0, 0, 0);
        private WingsRenderHelp.CrystalTarget cachedTarget;

        private static final Quaternionf Y180 = new Quaternionf(0, 1, 0, 0);


        public final ItemStack crystalStack;
        public final WingsCrystalItem crystalItem;
        public final int slot;

        public boolean isShattered = false;

        protected Crystal(ItemStack stack, int slot) {
            this.slot = slot;
            this.crystalStack = stack;
            this.crystalItem = (WingsCrystalItem) stack.getItem();
            this.model = new CrystalModel(CrystalModel.createLayer().bakeRoot());
        }

        private double partialTicksC() {
            return partialTicks * LERP_FACTOR;
        }

        private WingsRenderHelp.CrystalTarget resolveTarget() {
            if (this.cachedTarget == null && isBlocking && crystalsShieldSorted.contains(this)) {
                var iter = WingsRenderHelp.CRYSTAL_POSITIONS.get(WingsAction.SHIELD);
                if (iter.hasNext()) this.cachedTarget = iter.next();
            }

            if (this.cachedTarget == null) {
                var iter = WingsRenderHelp.CRYSTAL_POSITIONS.get(WingsAction.IDLE);
                if (iter.hasNext()) this.cachedTarget = iter.next();
            }

            // fallback
            if (this.cachedTarget == null)
                this.cachedTarget = WingsRenderHelp.CRYSTAL_POSITIONS.get(WingsAction.NONE).get(0);
            return this.cachedTarget;
        }

        public Vec3 calculateOffset(Vec3 entityPosition, double entityRot) {
            // code from Vec3, but collated to reduce instantiation count.
            double f = Math.cos(-entityRot);
            double f1 = Math.sin(-entityRot);
            Vec3 target = resolveTarget().targetPosition();
            if (resolveTarget().group().isElytraAttached())
                target = transformOffset(target);

            double x = target.x() * f + target.z() * f1;
            double y = target.y();
            double z = target.z() * f - target.x() * f1;

            return this.lerpPosition(new Vec3(x + entityPosition.x(), y + entityPosition.y(), z + entityPosition.z()), entityPosition);
        }

        private Vec3 transformOffset(Vec3 offset) {
            Vector3d vec = new Vector3d(offset.x(), offset.y(), offset.z());
            vec.mulDirection(resolveTarget().misc() == 0 ? LEFT_WING_MATRIX : RIGHT_WING_MATRIX);
            return new Vec3(vec.x(), vec.y(), vec.z());
        }

        public Quaterniond calculateRotation(double entityRot) {
            Quaterniondc target = resolveTarget().targetRotation();
            Quaterniond rot = new Quaterniond(new AxisAngle4d(entityRot, 0, 1, 0));
            return this.lerpRotation(rot.mul(target));
        }

        private Vec3 lerpPosition(Vec3 target, Vec3 entityPosition) {
            verifyPositionCache(target);
            if (tickPassed) {
                this.lastPosition = new Vec3(
                        Mth.lerp(LERP_FACTOR, this.lastPosition.x(), target.x()),
                        Mth.lerp(LERP_FACTOR, this.lastPosition.y(), target.y()),
                        Mth.lerp(LERP_FACTOR, this.lastPosition.z(), target.z())
                );
            }
            return new Vec3(
                    Mth.lerp(partialTicksC(), this.lastPosition.x(), target.x()) - entityPosition.x(),
                    Mth.lerp(partialTicksC(), this.lastPosition.y(), target.y()) - entityPosition.y(),
                    Mth.lerp(partialTicksC(), this.lastPosition.z(), target.z()) - entityPosition.z()
            );

        }

        private void verifyPositionCache(Vec3 target) {
            if (this.lastPosition.vectorTo(target).lengthSqr() > 2) {
                this.lastPosition = target;
            }
        }

        private Quaterniond lerpRotation(Quaterniond target) {
            target.normalize();
            if (tickPassed) this.lastRotation.nlerp(target, LERP_FACTOR);
            return this.lastRotation.nlerp(target, partialTicksC(), new Quaterniond());
        }

        public void render(PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            if (this.resolveTarget().isMirrored()) {
                poseStack.mulPose(Y180);
            }
            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            BakedModel model = renderer.getItemModelShaper().getModelManager().getModel(crystalItem.getWingsModelLoc());
            renderer.render(stack, ItemDisplayContext.NONE, false, poseStack, buffer, combinedLightIn, combinedOverlayIn, model);
            poseStack.popPose();
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
