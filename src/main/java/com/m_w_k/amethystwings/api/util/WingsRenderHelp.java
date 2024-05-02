package com.m_w_k.amethystwings.api.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;

import java.util.*;
import java.util.function.Consumer;

import static com.m_w_k.amethystwings.api.util.WingsAction.*;

public class WingsRenderHelp {
    private static double[] CONSTS = new double[] {
            -8/16d, // 0, y offset for shield center
            10/16d, // 1, z offset for shield center
            3.5/16d, // 2, x offset between horizontal shield layers
            5.5/16d, // 3, y offset between vertical shield layers
            0, // 4, z offset between vertical shield layers
            0.25/16d, // 5, y offset as shield crystals run right to left
            0.25/16d, // 6, x offset as shield crystals run up to down

            1/16d, // 7, first idle crystal y offset
            -2/16d, // 8, first idle crystal z offset
    };
    private static Vec3[] VEC_3S = new Vec3[] {
            v(0, 0, 0), // 0, fallback position

            v(0, CONSTS[0], CONSTS[1]), // 1, offset for shield center
            v(CONSTS[2], CONSTS[0] + CONSTS[5], CONSTS[1]), // 2, offset for shield right
            v(-CONSTS[2], CONSTS[0] - CONSTS[5], CONSTS[1]), // 3, offset for shield left
            v(-CONSTS[6], CONSTS[0] - CONSTS[3], CONSTS[1] + CONSTS[4]), // 4, offset for shield down
            v(CONSTS[2] - CONSTS[6], CONSTS[0] - CONSTS[3] + CONSTS[5], CONSTS[1] + CONSTS[4]), // 5, offset for shield down right
            v(-CONSTS[2] - CONSTS[6], CONSTS[0] - CONSTS[3] - CONSTS[5], CONSTS[1] + CONSTS[4]), // 6, offset for shield down left
            v(CONSTS[6], CONSTS[0] + CONSTS[3], CONSTS[1] - CONSTS[4]), // 7, offset for shield up
            v(CONSTS[2] + CONSTS[6], CONSTS[0] + CONSTS[3] + CONSTS[5], CONSTS[1] - CONSTS[4]), // 8, offset for shield up right
            v(-CONSTS[2] + CONSTS[6], CONSTS[0] + CONSTS[3] - CONSTS[5], CONSTS[1] - CONSTS[4]), // 9, offset for shield up left
            v(2*CONSTS[2], CONSTS[0] + 2*CONSTS[5], CONSTS[1]), // 10, offset for shield RIGHT
            v(2*CONSTS[2] - CONSTS[6], CONSTS[0] - CONSTS[3] + 2*CONSTS[5], CONSTS[1] + CONSTS[4]), // 11, offset for shield RIGHT down
            v(2*CONSTS[2] + CONSTS[6], CONSTS[0] + CONSTS[3] + 2*CONSTS[5], CONSTS[1] - CONSTS[4]), // 12, offset for shield RIGHT up
            v(-2*CONSTS[2], CONSTS[0] - 2*CONSTS[5], CONSTS[1]), // 13, offset for shield LEFT
            v(-2*CONSTS[2] - CONSTS[6], CONSTS[0] - CONSTS[3] - 2*CONSTS[5], CONSTS[1] + CONSTS[4]), // 14, offset for shield LEFT down
            v(-2*CONSTS[2] + CONSTS[6], CONSTS[0] + CONSTS[3] - 2*CONSTS[5], CONSTS[1] - CONSTS[4]), // 15, offset for shield LEFT up
            v(-2*CONSTS[6], CONSTS[0] - 2*CONSTS[3], CONSTS[1] + 2*CONSTS[4]), // 16, offset for shield DOWN
            v(CONSTS[2] - 2*CONSTS[6], CONSTS[0] - 2*CONSTS[3] + CONSTS[5], CONSTS[1] + 2*CONSTS[4]), // 17, offset for shield DOWN right
            v(-CONSTS[2] - 2*CONSTS[6], CONSTS[0] - 2*CONSTS[3] - CONSTS[5], CONSTS[1] + 2*CONSTS[4]), // 18, offset for shield DOWN left
            v(2*CONSTS[6], CONSTS[0] + 2*CONSTS[3], CONSTS[1] - 2*CONSTS[4]), // 19, offset for shield UP
            v(2*CONSTS[2] + 2*CONSTS[6], CONSTS[0] + 2*CONSTS[3] + 2*CONSTS[5], CONSTS[1] - 2*CONSTS[4]), // 20, offset for shield UP RIGHT
            v(-2*CONSTS[2] + 2*CONSTS[6], CONSTS[0] + 2*CONSTS[3] - 2*CONSTS[5], CONSTS[1] - 2*CONSTS[4]), // 21, offset for shield UP LEFT

            v(0, CONSTS[7], CONSTS[8]) // 22, offset for first idle
    };
    private static Quaterniondc[] QUATERNIONS = new Quaterniondc[] {
            new Quaterniond(), // 0, fallback rot
            new Quaterniond(1, 0, 1, 0).normalize(), // 1, shield crystal rot
            new Quaterniond(0, 0, 0, 1).normalize(), // 2, idle crystal rot
    };
    public static final EnumMap<WingsAction, CrystalTargetIterable> CRYSTAL_POSITIONS = new EnumMap<>(WingsAction.class) {{
        this.put(NONE, new CrystalTargetIterable(NONE)
                .with(VEC_3S[0], QUATERNIONS[0])
        );
        this.put(SHIELD, new CrystalTargetIterable(SHIELD)
                .with(VEC_3S[1], QUATERNIONS[1])
                .with(VEC_3S[2], QUATERNIONS[1])
                .with(VEC_3S[3], QUATERNIONS[1])
                .with(VEC_3S[4], QUATERNIONS[1])
                .with(VEC_3S[5], QUATERNIONS[1])
                .with(VEC_3S[6], QUATERNIONS[1])
                .with(VEC_3S[8], QUATERNIONS[1])
                .with(VEC_3S[9], QUATERNIONS[1]) // end of min shield
                .with(VEC_3S[7], QUATERNIONS[1])
                .with(VEC_3S[10], QUATERNIONS[1])
                .with(VEC_3S[13], QUATERNIONS[1])
                .with(VEC_3S[16], QUATERNIONS[1])
                .with(VEC_3S[17], QUATERNIONS[1])
                .with(VEC_3S[18], QUATERNIONS[1])
                .with(VEC_3S[11], QUATERNIONS[1])
                .with(VEC_3S[14], QUATERNIONS[1])
                .with(VEC_3S[12], QUATERNIONS[1])
                .with(VEC_3S[15], QUATERNIONS[1])
                .with(VEC_3S[20], QUATERNIONS[1])
                .with(VEC_3S[21], QUATERNIONS[1])
        );
        this.put(IDLE, new CrystalTargetIterable(IDLE)
                .with(VEC_3S[22], QUATERNIONS[2], false)
                .with(VEC_3S[22], QUATERNIONS[2], true, 1)
        );

        CONSTS = null;
        VEC_3S = null;
        QUATERNIONS = null;
    }};

    public static void regenerateIterators() {
        CRYSTAL_POSITIONS.values().stream().filter(Objects::nonNull).forEach(CrystalTargetIterable::regenerateIterator);
    }

    private static Vec3 v(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    public record CrystalTarget(Vec3 targetPosition, Quaterniondc targetRotation, boolean isMirrored, WingsAction group,
                                byte misc) {}

    public static class CrystalTargetIterable extends ObjectArrayList<CrystalTarget> implements Iterator<CrystalTarget> {

        private final WingsAction group;
        private Iterator<CrystalTarget> iterator;
        
        public CrystalTargetIterable(WingsAction group) {
            this.group = group;
        }

        protected void regenerateIterator() {
            this.iterator = this.iterator();
        }

        public CrystalTargetIterable with(Vec3 targetPosition, Quaterniondc targetRotation) {
            return this.with(targetPosition, targetRotation, false, 0);
        }

        public CrystalTargetIterable with(Vec3 targetPosition, Quaterniondc targetRotation, int misc) {
            return this.with(targetPosition, targetRotation, false, misc);
        }

        public CrystalTargetIterable with(Vec3 targetPosition, Quaterniondc targetRotation, boolean isMirrored) {
            return this.with(targetPosition, targetRotation, isMirrored, (byte) 0);
        }

        public CrystalTargetIterable with(Vec3 targetPosition, Quaterniondc targetRotation, boolean isMirrored, int misc) {
            this.add(new CrystalTarget(targetPosition, targetRotation, isMirrored, this.group, (byte) misc));
            return this;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public CrystalTarget next() {
            return this.iterator.next();
        }

        @Override
        public void remove() {
            this.iterator.remove();
        }

        @Override
        public void forEachRemaining(Consumer<? super CrystalTarget> action) {
            this.iterator.forEachRemaining(action);
        }
    }
}
