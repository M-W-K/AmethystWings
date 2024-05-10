package com.m_w_k.amethystwings.capability;

import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.WeakHashMap;
import java.util.function.Consumer;

public final class WingsCapDataCache {
    private static final WeakHashMap<Integer, WingsCapData> CACHE = new WeakHashMap<>();
    private static int id;

    private static WingsCapability generateNewCap(ItemStack stack) {
        Integer key = id++;
        WingsCapData data = new WingsCapData();
        CACHE.put(key, data);
        stack.getOrCreateTag().putInt("CapID", id);
        return new WingsCapability(stack, key, data);
    }

    public static WingsCapability getCap(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains("CapID")) {
            Integer nbtkey = stack.getTag().getInt("CapID");
            WingsCapData data = CACHE.get(nbtkey);
            if (data != null) {
                return new WingsCapability(stack, nbtkey, data);
            } else {
                data = new WingsCapData();
                CACHE.put(nbtkey, data);
                id = Math.max(id, nbtkey); // make sure the pointer is always higher
                return new WingsCapability(stack, nbtkey, data);
            }
        }
        return generateNewCap(stack);
    }

    @Nullable
    public static WingsCapData accessData(int dataID) {
        return CACHE.get(dataID);
    }

    /**
     * Data storage primarily so that client data is not lost on cap reinit on server sync packet.
     */
    public static final class WingsCapData {
        private static final NonNullList<CrystalData> crystals = NonNullList.withSize(54, CrystalData.EMPTY);

        public long lastBoostTick;

        private WingsCapData() {}

        public void removeData(int slot) {
            crystals.set(slot, CrystalData.EMPTY);
        }

        public CrystalData getData(int slot) {
            CrystalData data = crystals.get(slot);
            if (data == CrystalData.EMPTY) {
                data = new CrystalData();
                crystals.set(slot, data);
            }
            return data;
        }

        public void forNonEmpty(Consumer<CrystalData> action) {
            for (int i = 0; i < 54; i++) {
                CrystalData data = crystals.get(i);
                if (data != CrystalData.EMPTY) action.accept(data);
            }
        }

        public static final class CrystalData {
            private static final CrystalData EMPTY = new CrystalData();

            final Quaterniond lastRotation = new Quaterniond();
            final Vector3f lastPosition = new Vector3f();
            final Vector3f targetPosition = new Vector3f();

            ItemStack particlesStack;

            public int particlesToRender;

            void handleParticles(@NotNull LivingEntity entity, double partialTicks, Vector3d drift) {
                if (!entity.level().isClientSide() || particlesToRender == 0) return;
                ItemParticleOption option = new ItemParticleOption(ParticleTypes.ITEM, particlesStack);

                Vector3f pos = entity.position().toVector3f().add(lerpPosition(partialTicks, drift));
                Vec3 vel = entity.getDeltaMovement();
                RandomSource randomSource = entity.getRandom();
                for (int i = 0; i < particlesToRender; i++) {
                    double xv = randomSource.nextGaussian() * 0.15 + vel.x() / 2;
                    double yv = randomSource.nextGaussian() * 0.15 + vel.y() / 2;
                    double zv = randomSource.nextGaussian() * 0.15 + vel.z() / 2;
                    entity.level().addParticle(option, false, pos.x() + xv, pos.y() + yv, pos.z() + zv, xv, yv, zv);
                }
                particlesToRender = 0;
            }

            @Contract("_, _ -> new")
            @NotNull Vector3f lerpPosition(double partialTicks, @NotNull Vector3d drift) {
                return new Vector3f(
                        (float) (Mth.lerp(partialTicks, lastPosition.x(), targetPosition.x()) + drift.x()),
                        (float) (Mth.lerp(partialTicks, lastPosition.y(), targetPosition.y()) + drift.y()),
                        (float) (Mth.lerp(partialTicks, lastPosition.z(), targetPosition.z()) + drift.z())
                );
            }
        }
    }
}
