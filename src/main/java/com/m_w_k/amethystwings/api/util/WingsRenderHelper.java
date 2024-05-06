package com.m_w_k.amethystwings.api.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;

import java.util.*;
import java.util.function.Consumer;

import static com.m_w_k.amethystwings.api.util.WingsAction.*;

public class WingsRenderHelper {
    private static double[] CONSTS = new double[] {
            8/16d, // 0, y offset for shield center
            -10/16d, // 1, z offset for shield center
            3.5/16d, // 2, x offset between horizontal shield layers
            -5.5/16d, // 3, y offset between vertical shield layers
            0, // 4, z offset between vertical shield layers
            -0.25/16d, // 5, y offset as shield crystals run right to left
            0.25/16d, // 6, x offset as shield crystals run up to down

            4/16d, // 7, elytra crystal z offset
            0, // 8, unused
            4/16d, // 9, first elytra crystal x offset
            1/16d, // 10, first elytra crystal y offset
            1.7/16d, // 11, second elytra crystal x offset
            4.2/16d, // 12, second elytra crystal y offset
            5.5/16d, // 13, third elytra crystal x offset
            6/16d, // 14, third elytra crystal y offset
            4/16d, // 15, fourth elytra crystal x offset
            11.2/16d, // 16, fourth elytra crystal y offset
            0.5/16d, // 17, fifth elytra crystal x offset
            10/16d, // 18, fifth elytra crystal y offset
            0.7/16d, // 19, sixth elytra crystal x offset
            14.5/16d, // 20, sixth elytra crystal y offset
            4.8/16d, // 21, seventh elytra crystal x offset
            17/16d, // 22, seventh elytra crystal y offset
            1/16d, // 23, eighth elytra crystal x offset
            20/16d, // 24, eighth elytra crystal y offset
            -2.5/16d, // 25, ninth elytra crystal x offset
            19/16d, // 26, ninth elytra crystal y offset

            5/16d, // 27, first boost crystal x offset
            18/16d, // 28, first boost crystal y offset
            0, // 29, first boost crystal z offset
            5/16d, // 30, second boost crystal x offset
            16/16d, // 31, second boost crystal y offset
            4/16d, // 32, second boost crystal z offset
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

            v(CONSTS[9], CONSTS[10], CONSTS[7]), // 22, offset for first elytra left
            v(-CONSTS[9], CONSTS[10], CONSTS[7]), // 23, offset for first elytra right
            v(CONSTS[11], CONSTS[12], CONSTS[7]), // 24, offset for second elytra left
            v(-CONSTS[11], CONSTS[12], CONSTS[7]), // 25, offset for second elytra right
            v(CONSTS[13], CONSTS[14], CONSTS[7]), // 26, offset for third elytra left
            v(-CONSTS[13], CONSTS[14], CONSTS[7]), // 27, offset for third elytra right
            v(CONSTS[15], CONSTS[16], CONSTS[7]), // 28, offset for fourth elytra left
            v(-CONSTS[15], CONSTS[16], CONSTS[7]), // 29, offset for fourth elytra right
            v(CONSTS[17], CONSTS[18], CONSTS[7]), // 30, offset for fifth elytra left
            v(-CONSTS[17], CONSTS[18], CONSTS[7]), // 31, offset for fifth elytra right
            v(CONSTS[19], CONSTS[20], CONSTS[7]), // 32, offset for sixth elytra left
            v(-CONSTS[19], CONSTS[20], CONSTS[7]), // 33, offset for sixth elytra right
            v(CONSTS[21], CONSTS[22], CONSTS[7]), // 34, offset for seventh elytra left
            v(-CONSTS[21], CONSTS[22], CONSTS[7]), // 35, offset for seventh elytra right
            v(CONSTS[23], CONSTS[24], CONSTS[7]), // 36, offset for eighth elytra left
            v(-CONSTS[23], CONSTS[24], CONSTS[7]), // 37, offset for eighth elytra right
            v(CONSTS[25], CONSTS[26], CONSTS[7]), // 38, offset for ninth elytra left
            v(-CONSTS[25], CONSTS[26], CONSTS[7]), // 39, offset for ninth elytra right

            v(CONSTS[27], CONSTS[28], CONSTS[29]), // 40, offset for first boost left
            v(-CONSTS[27], CONSTS[28], CONSTS[29]), // 41, offset for first boost left
            v(CONSTS[30], CONSTS[31], CONSTS[32]), // 42, offset for second boost left
            v(-CONSTS[30], CONSTS[31], CONSTS[32]), // 43, offset for second boost left
    };

    private static Quaterniondc[] QUATERNIONS = new Quaterniondc[] {
            new Quaterniond(), // 0, fallback rot

            new Quaterniond(1, 0, 1, 0).normalize(), // 1, shield crystal rot

            new Quaterniond(1, 1, -1, 1).normalize(), // 2, elytra first left crystal rot
            new Quaterniond(1, -1, 1, 1).normalize(), // 3, elytra first right crystal rot
            new Quaterniond(0.2, -1, 0.2, 1).normalize(), // 4, elytra second left crystal rot
            new Quaterniond(0.2, 1, -0.2, 1).normalize(), // 5, elytra second right crystal rot
            new Quaterniond(1, -0.1, 1, 0.1).normalize(), // 6, elytra third left crystal rot
            new Quaterniond(1, 0.1, -1, 0.1).normalize(), // 7, elytra third right crystal rot
            new Quaterniond(-1, -0.2, -1, 0.2).normalize(), // 8, elytra fourth left crystal rot
            new Quaterniond(-1, 0.2, 1, 0.2).normalize(), // 9, elytra fourth right crystal rot
            new Quaterniond(-1, 0.2, 1, 0.2).normalize(), // 10, elytra fifth left crystal rot
            new Quaterniond(-1, -0.2, -1, 0.2).normalize(), // 11, elytra fifth right crystal rot
            new Quaterniond(-0.9, 1, 0.9, 1).normalize(), // 12, elytra sixth left crystal rot
            new Quaterniond(-0.9, -1, -0.9, 1).normalize(), // 13, elytra sixth right crystal rot
            new Quaterniond(0, 1, 0, 1).normalize(), // 14, elytra seventh left crystal rot
            new Quaterniond(0, -1, 0, 1).normalize(), // 15, elytra seventh right crystal rot
            new Quaterniond(0.4, -1, 0.4, 1).normalize(), // 16, elytra eighth left crystal rot
            new Quaterniond(0.4, 1, -0.4, 1).normalize(), // 17, elytra eighth right crystal rot
            new Quaterniond(1, 0.1, -1, 0.1).normalize(), // 18, elytra third left crystal rot
            new Quaterniond(1, -0.1, 1, 0.1).normalize(), // 19, elytra third right crystal rot

            new Quaterniond(0, 0.2, 0, 1).normalize(), // 20, boost first right crystal rot
            new Quaterniond(0, -0.2, 0, 1).normalize(), // 21, boost first left crystal rot
            new Quaterniond(0, 1, 0, 0.2).normalize(), // 22, boost second right crystal rot
            new Quaterniond(0, -1, 0, 0.2).normalize(), // 23, boost second left crystal rot
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
        this.put(SHIELD_IDLE, new CrystalTargetIterable(SHIELD_IDLE));
        this.put(IDLE, new CrystalTargetIterable(IDLE));
        this.put(ELYTRA, new CrystalTargetIterable(ELYTRA)
                .with(VEC_3S[22], QUATERNIONS[2])
                .with(VEC_3S[23], QUATERNIONS[3], 1)
                .with(VEC_3S[24], QUATERNIONS[4])
                .with(VEC_3S[25], QUATERNIONS[5], 1)
                .with(VEC_3S[26], QUATERNIONS[6])
                .with(VEC_3S[27], QUATERNIONS[7], 1)
                .with(VEC_3S[28], QUATERNIONS[8])
                .with(VEC_3S[29], QUATERNIONS[9], 1)
                .with(VEC_3S[30], QUATERNIONS[10])
                .with(VEC_3S[31], QUATERNIONS[11], 1)
                .with(VEC_3S[32], QUATERNIONS[12])
                .with(VEC_3S[33], QUATERNIONS[13], 1)
                .with(VEC_3S[34], QUATERNIONS[14])
                .with(VEC_3S[35], QUATERNIONS[15], 1)
                .with(VEC_3S[36], QUATERNIONS[16])
                .with(VEC_3S[37], QUATERNIONS[17], 1)
                .with(VEC_3S[38], QUATERNIONS[18])
                .with(VEC_3S[39], QUATERNIONS[19], 1)
        );
        this.put(BOOST, new CrystalTargetIterable(BOOST)
                .with(VEC_3S[40], QUATERNIONS[20])
                .with(VEC_3S[41], QUATERNIONS[21])
                .with(VEC_3S[42], QUATERNIONS[22])
                .with(VEC_3S[43], QUATERNIONS[23])
        );
        // free up now-unnecessary cache
        CONSTS = null;
        VEC_3S = null;
        QUATERNIONS = null;
    }

        @Override
        public CrystalTargetIterable get(Object key) {
            var fetch = super.get(key);
            return fetch == null ? null : fetch.regenerateIterator();
        }
    };

    private static Vec3 v(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    public record CrystalTarget(Vec3 targetPosition, Quaterniondc targetRotation, WingsAction group, byte misc) {}

    public static class CrystalTargetIterable extends ObjectArrayList<CrystalTarget> implements Iterator<CrystalTarget> {

        private final WingsAction group;
        private Iterator<CrystalTarget> iterator;
        
        public CrystalTargetIterable(WingsAction group) {
            this.group = group;
        }

        protected CrystalTargetIterable regenerateIterator() {
            this.iterator = this.iterator();
            return this;
        }

        public CrystalTargetIterable with(Vec3 targetPosition, Quaterniondc targetRotation) {
            return this.with(targetPosition, targetRotation, 0);
        }

        public CrystalTargetIterable with(Vec3 targetPosition, Quaterniondc targetRotation, int misc) {
            this.add(new CrystalTarget(targetPosition, targetRotation, this.group, (byte) misc));
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
