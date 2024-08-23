package com.m_w_k.amethystwings.api.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;

import java.util.EnumMap;
import java.util.Iterator;
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

            1/16d, // 33, first idle chain origin x offset
            -3/16d, // 34, first idle chain origin y offset
            6/16d, // 35, first idle chain origin z offset
            0/16d, // 36, x offset between first idle chain crystals
            -2.5/16d, // 37, y offset between first idle chain crystals
            4/16d, // 38, z offset between first idle chain crystals
            3/16d, // 39, second idle chain origin x offset
            2/16d, // 40, second idle chain origin y offset
            7/16d, // 41, second idle chain origin z offset
            1/16d, // 42, x offset between second idle chain crystals
            -1.5/16d, // 43, y offset between second idle chain crystals
            5/16d, // 44, z offset between second idle chain crystals
            4/16d, // 45, third idle chain origin x offset
            -2/16d, // 46, third idle chain origin y offset
            10/16d, // 47, third idle chain origin z offset
            1/16d, // 48, x offset between third idle chain crystals
            -1.8/16d, // 49, y offset between third idle chain crystals
            3.5/16d, // 50, z offset between third idle chain crystals
            4/16d, // 51, fourth idle chain origin x offset
            8/16d, // 52, fourth idle chain origin y offset
            7.5/16d, // 53, fourth idle chain origin z offset
            3/16d, // 54, x offset between fourth idle chain crystals
            -0.1/16d, // 55, y offset between fourth idle chain crystals
            3/16d, // 56, z offset between fourth idle chain crystals
            0.8/16d, // 57, fifth idle chain origin x offset
            7.3/16d, // 58, fifth idle chain origin y offset
            8.5/16d, // 59, fifth idle chain origin z offset
            3/16d, // 60, x offset between fifth idle chain crystals
            0.1/16d, // 61, y offset between fifth idle chain crystals
            3/16d, // 62, z offset between fifth idle chain crystals
            -1.5/16d, // 63, sixth idle chain origin x offset
            14/16d, // 64, sixth idle chain origin y offset
            6/16d, // 65, sixth idle chain origin z offset
            2.5/16d, // 66, x offset between sixth idle chain crystals
            1.3/16d, // 67, y offset between sixth idle chain crystals
            3/16d, // 68, z offset between sixth idle chain crystals
            -1.5/16d, // 69, seventh idle chain origin x offset
            11/16d, // 70, seventh idle chain origin y offset
            8/16d, // 71, seventh idle chain origin z offset
            3/16d, // 72, x offset between seventh idle chain crystals
            1/16d, // 73, y offset between seventh idle chain crystals
            5/16d, // 74, z offset between seventh idle chain crystals

            9.5/16d, // 75, x offset for first shield idle
            -3/16d, // 76, y offset for first shield idle
            0/16d, // 77, z offset for first shield idle
            11.5/16d, // 78, first shield idle chain origin x offset
            -0.3/16d, // 79, first shield idle chain origin y offset
            -1.3/16d, // 80, first shield idle chain origin z offset
            0.3/16d, // 81, x offset between first shield idle chain crystals
            5.3/16d, // 82, y offset between first shield idle chain crystals
            -0.3/16d, // 83, z offset between first shield idle chain crystals
            11/16d, // 84, second shield idle chain origin x offset
            -0.5/16d, // 85, second shield idle chain origin y offset
            2.5/16d, // 86, second shield idle chain origin z offset
            0.3/16d, // 87, x offset between second shield idle chain crystals
            5.3/16d, // 88, y offset between second shield idle chain crystals
            -0.4/16d, // 89, z offset between second shield idle chain crystals
            10.5/16d, // 90, third shield idle chain origin x offset
            -0.1/16d, // 91, third shield idle chain origin y offset
            -5/16d, // 92, third shield idle chain origin z offset
            0.3/16d, // 93, x offset between third shield idle chain crystals
            5.3/16d, // 94, y offset between third shield idle chain crystals
            -0.2/16d, // 95, z offset between third shield idle chain crystals
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
            v(-CONSTS[27], CONSTS[28], CONSTS[29]), // 41, offset for first boost right
            v(CONSTS[30], CONSTS[31], CONSTS[32]), // 42, offset for second boost left
            v(-CONSTS[30], CONSTS[31], CONSTS[32]), // 43, offset for second boost right

            v(CONSTS[33], CONSTS[34], CONSTS[35]), // 44, offset for first idle chain left crystal a
            v(-CONSTS[33], CONSTS[34], CONSTS[35]), // 45, offset for first idle chain right crystal a
            v(CONSTS[33] + CONSTS[36], CONSTS[34] + CONSTS[37], CONSTS[35] + CONSTS[38]), // 46, offset for first idle chain left crystal b
            v(-CONSTS[33] - CONSTS[36], CONSTS[34] + CONSTS[37], CONSTS[35] + CONSTS[38]), // 47, offset for first idle chain right crystal b
            v(CONSTS[33] + 2*CONSTS[36], CONSTS[34] + 2*CONSTS[37], CONSTS[35] + 2*CONSTS[38]), // 48, offset for first idle chain left crystal c
            v(-CONSTS[33] - 2*CONSTS[36], CONSTS[34] + 2*CONSTS[37], CONSTS[35] + 2*CONSTS[38]), // 49, offset for first idle chain right crystal c
            v(CONSTS[33] + 3*CONSTS[36], CONSTS[34] + 3*CONSTS[37], CONSTS[35] + 3*CONSTS[38]), // 50, offset for first idle chain left crystal d
            v(-CONSTS[33] - 3*CONSTS[36], CONSTS[34] + 3*CONSTS[37], CONSTS[35] + 3*CONSTS[38]), // 51, offset for first idle chain right crystal d
            v(CONSTS[39], CONSTS[40], CONSTS[41]), // 52, offset for second idle chain left crystal a
            v(-CONSTS[39], CONSTS[40], CONSTS[41]), // 53, offset for second idle chain right crystal a
            v(CONSTS[39] + CONSTS[42], CONSTS[40] + CONSTS[43], CONSTS[41] + CONSTS[44]), // 54, offset for second idle chain left crystal b
            v(-CONSTS[39] - CONSTS[42], CONSTS[40] + CONSTS[43], CONSTS[41] + CONSTS[44]), // 55, offset for second idle chain right crystal b
            v(CONSTS[39] + 2*CONSTS[42], CONSTS[40] + 2*CONSTS[43], CONSTS[41] + 2*CONSTS[44]), // 56, offset for second idle chain left crystal c
            v(-CONSTS[39] - 2*CONSTS[42], CONSTS[40] + 2*CONSTS[43], CONSTS[41] + 2*CONSTS[44]), // 57, offset for second idle chain right crystal c
            v(CONSTS[39] + 3*CONSTS[42], CONSTS[40] + 3*CONSTS[43], CONSTS[41] + 3*CONSTS[44]), // 58, offset for second idle chain left crystal d
            v(-CONSTS[39] - 3*CONSTS[42], CONSTS[40] + 3*CONSTS[43], CONSTS[41] + 3*CONSTS[44]), // 59, offset for second idle chain right crystal d
            v(CONSTS[45], CONSTS[46], CONSTS[47]), // 60, offset for third idle chain left crystal a
            v(-CONSTS[45], CONSTS[46], CONSTS[47]), // 61, offset for third idle chain right crystal a
            v(CONSTS[45] + CONSTS[48], CONSTS[46] + CONSTS[49], CONSTS[47] + CONSTS[50]), // 62, offset for third idle chain left crystal b
            v(-CONSTS[45] - CONSTS[48], CONSTS[46] + CONSTS[49], CONSTS[47] + CONSTS[50]), // 63, offset for third idle chain right crystal b
            v(CONSTS[45] + 2*CONSTS[48], CONSTS[46] + 2*CONSTS[49], CONSTS[47] + 2*CONSTS[50]), // 64, offset for third idle chain left crystal c
            v(-CONSTS[45] - 2*CONSTS[48], CONSTS[46] + 2*CONSTS[49], CONSTS[47] + 2*CONSTS[50]), // 65, offset for third idle chain right crystal c
            v(CONSTS[45] + 3*CONSTS[48], CONSTS[46] + 3*CONSTS[49], CONSTS[47] + 3*CONSTS[50]), // 66, offset for third idle chain left crystal d
            v(-CONSTS[45] - 3*CONSTS[48], CONSTS[46] + 3*CONSTS[49], CONSTS[47] + 3*CONSTS[50]), // 67, offset for third idle chain right crystal d
            v(CONSTS[51], CONSTS[52], CONSTS[53]), // 68, offset for fourth idle chain left crystal a
            v(-CONSTS[51], CONSTS[52], CONSTS[53]), // 69, offset for fourth idle chain right crystal a
            v(CONSTS[51] + CONSTS[54], CONSTS[52] + CONSTS[55], CONSTS[53] + CONSTS[56]), // 70, offset for fourth idle chain left crystal b
            v(-CONSTS[51] - CONSTS[54], CONSTS[52] + CONSTS[55], CONSTS[53] + CONSTS[56]), // 71, offset for fourth idle chain right crystal b
            v(CONSTS[51] + 2*CONSTS[54], CONSTS[52] + 2*CONSTS[55], CONSTS[53] + 2*CONSTS[56]), // 72, offset for fourth idle chain left crystal c
            v(-CONSTS[51] - 2*CONSTS[54], CONSTS[52] + 2*CONSTS[55], CONSTS[53] + 2*CONSTS[56]), // 73, offset for fourth idle chain right crystal c
            v(CONSTS[51] + 3*CONSTS[54], CONSTS[52] + 3*CONSTS[55], CONSTS[53] + 3*CONSTS[56]), // 74, offset for fourth idle chain left crystal d
            v(-CONSTS[51] - 3*CONSTS[54], CONSTS[52] + 3*CONSTS[55], CONSTS[53] + 3*CONSTS[56]), // 75, offset for fourth idle chain right crystal d
            v(CONSTS[57], CONSTS[58], CONSTS[59]), // 76, offset for fifth idle chain left crystal a
            v(-CONSTS[57], CONSTS[58], CONSTS[59]), // 77, offset for fifth idle chain right crystal a
            v(CONSTS[57] + CONSTS[60], CONSTS[58] + CONSTS[61], CONSTS[59] + CONSTS[62]), // 78, offset for fifth idle chain left crystal b
            v(-CONSTS[57] - CONSTS[60], CONSTS[58] + CONSTS[61], CONSTS[59] + CONSTS[62]), // 79, offset for fifth idle chain right crystal b
            v(CONSTS[57] + 2*CONSTS[60], CONSTS[58] + 2*CONSTS[61], CONSTS[59] + 2*CONSTS[62]), // 80, offset for fifth idle chain left crystal c
            v(-CONSTS[57] - 2*CONSTS[60], CONSTS[58] + 2*CONSTS[61], CONSTS[59] + 2*CONSTS[62]), // 81, offset for fifth idle chain right crystal c
            v(CONSTS[57] + 3*CONSTS[60], CONSTS[58] + 3*CONSTS[61], CONSTS[59] + 3*CONSTS[62]), // 82, offset for fifth idle chain left crystal d
            v(-CONSTS[57] - 3*CONSTS[60], CONSTS[58] + 3*CONSTS[61], CONSTS[59] + 3*CONSTS[62]), // 83, offset for fifth idle chain right crystal d
            v(CONSTS[63], CONSTS[64], CONSTS[65]), // 84, offset for sixth idle chain left crystal a
            v(-CONSTS[63], CONSTS[64], CONSTS[65]), // 85, offset for sixth idle chain right crystal a
            v(CONSTS[63] + CONSTS[66], CONSTS[64] + CONSTS[67], CONSTS[65] + CONSTS[68]), // 86, offset for sixth idle chain left crystal b
            v(-CONSTS[63] - CONSTS[66], CONSTS[64] + CONSTS[67], CONSTS[65] + CONSTS[68]), // 87, offset for sixth idle chain right crystal b
            v(CONSTS[63] + 2*CONSTS[66], CONSTS[64] + 2*CONSTS[67], CONSTS[65] + 2*CONSTS[68]), // 88, offset for sixth idle chain left crystal c
            v(-CONSTS[63] - 2*CONSTS[66], CONSTS[64] + 2*CONSTS[67], CONSTS[65] + 2*CONSTS[68]), // 89, offset for sixth idle chain right crystal c
            v(CONSTS[63] + 3*CONSTS[66], CONSTS[64] + 3*CONSTS[67], CONSTS[65] + 3*CONSTS[68]), // 90, offset for sixth idle chain left crystal d
            v(-CONSTS[63] - 3*CONSTS[66], CONSTS[64] + 3*CONSTS[67], CONSTS[65] + 3*CONSTS[68]), // 91, offset for sixth idle chain right crystal d
            v(CONSTS[69], CONSTS[70], CONSTS[71]), // 92, offset for seventh idle chain left crystal a
            v(-CONSTS[69], CONSTS[70], CONSTS[71]), // 93, offset for seventh idle chain right crystal a
            v(CONSTS[69] + CONSTS[72], CONSTS[70] + CONSTS[73], CONSTS[71] + CONSTS[74]), // 94, offset for seventh idle chain left crystal b
            v(-CONSTS[69] - CONSTS[72], CONSTS[70] + CONSTS[73], CONSTS[71] + CONSTS[74]), // 95, offset for seventh idle chain right crystal b
            v(CONSTS[69] + 2*CONSTS[72], CONSTS[70] + 2*CONSTS[73], CONSTS[71] + 2*CONSTS[74]), // 96, offset for seventh idle chain left crystal c
            v(-CONSTS[69] - 2*CONSTS[72], CONSTS[70] + 2*CONSTS[73], CONSTS[71] + 2*CONSTS[74]), // 97, offset for seventh idle chain right crystal c

            v(CONSTS[75], CONSTS[76], CONSTS[77]), // 98, first shield idle crystal left
            v(-CONSTS[75], CONSTS[76], CONSTS[77]), // 99, first shield idle crystal right
            v(CONSTS[78], CONSTS[79], CONSTS[80]), // 100, first shield idle chain left a
            v(-CONSTS[78], CONSTS[79], CONSTS[80]), // 101, first shield idle chain right a
            v(CONSTS[78] + CONSTS[81], CONSTS[79] + CONSTS[82], CONSTS[80] + CONSTS[83]), // 102, first shield idle chain left b
            v(-CONSTS[78] - CONSTS[81], CONSTS[79] + CONSTS[82], CONSTS[80] + CONSTS[83]), // 103, first shield idle chain right b
            v(CONSTS[78] + 2*CONSTS[81], CONSTS[79] + 2*CONSTS[82], CONSTS[80] + 2*CONSTS[83]), // 104, first shield idle chain left b
            v(-CONSTS[78] - 2*CONSTS[81], CONSTS[79] + 2*CONSTS[82], CONSTS[80] + 2*CONSTS[83]), // 105, first shield idle chain right b
            v(CONSTS[84], CONSTS[85], CONSTS[86]), // 106, second shield idle chain left a
            v(-CONSTS[84], CONSTS[85], CONSTS[86]), // 107, second shield idle chain right a
            v(CONSTS[84] + CONSTS[87], CONSTS[85] + CONSTS[88], CONSTS[86] + CONSTS[89]), // 108, second shield idle chain left b
            v(-CONSTS[84] - CONSTS[87], CONSTS[85] + CONSTS[88], CONSTS[86] + CONSTS[89]), // 109, second shield idle chain right b
            v(CONSTS[84] + 2*CONSTS[87], CONSTS[85] + 2*CONSTS[88], CONSTS[86] + 2*CONSTS[89]), // 110, second shield idle chain left b
            v(-CONSTS[84] - 2*CONSTS[87], CONSTS[85] + 2*CONSTS[88], CONSTS[86] + 2*CONSTS[89]), // 111, second shield idle chain right b
            v(CONSTS[90], CONSTS[91], CONSTS[92]), // 112, third shield idle chain left a
            v(-CONSTS[90], CONSTS[91], CONSTS[92]), // 113, third shield idle chain right a
            v(CONSTS[90] + CONSTS[93], CONSTS[91] + CONSTS[94], CONSTS[92] + CONSTS[95]), // 114, third shield idle chain left b
            v(-CONSTS[90] - CONSTS[93], CONSTS[91] + CONSTS[94], CONSTS[92] + CONSTS[95]), // 115, third shield idle chain right b
            v(CONSTS[90] + 2*CONSTS[93], CONSTS[91] + 2*CONSTS[94], CONSTS[92] + 2*CONSTS[95]), // 116, third shield idle chain left b
            v(-CONSTS[90] - 2*CONSTS[93], CONSTS[91] + 2*CONSTS[94], CONSTS[92] + 2*CONSTS[95]), // 117, third shield idle chain right b
    };

    private static Quaterniondc[] QUATERNIONS = new Quaterniondc[] {
            q(0, 0, 0, 1), // 0, fallback rot

            q(1, 0, 1, 0), // 1, shield crystal rot

            q(1, 1, -1, 1), // 2, elytra first left crystal rot
            q(1, -1, 1, 1), // 3, elytra first right crystal rot
            q(0.2, -1, 0.2, 1), // 4, elytra second left crystal rot
            q(0.2, 1, -0.2, 1), // 5, elytra second right crystal rot
            q(1, -0.1, 1, 0.1), // 6, elytra third left crystal rot
            q(1, 0.1, -1, 0.1), // 7, elytra third right crystal rot
            q(-1, -0.2, -1, 0.2), // 8, elytra fourth left crystal rot
            q(-1, 0.2, 1, 0.2), // 9, elytra fourth right crystal rot
            q(-1, 0.2, 1, 0.2), // 10, elytra fifth left crystal rot
            q(-1, -0.2, -1, 0.2), // 11, elytra fifth right crystal rot
            q(-0.9, 1, 0.9, 1), // 12, elytra sixth left crystal rot
            q(-0.9, -1, -0.9, 1), // 13, elytra sixth right crystal rot
            q(0, 1, 0, 1), // 14, elytra seventh left crystal rot
            q(0, -1, 0, 1), // 15, elytra seventh right crystal rot
            q(0.4, -1, 0.4, 1), // 16, elytra eighth left crystal rot
            q(0.4, 1, -0.4, 1), // 17, elytra eighth right crystal rot
            q(1, 0.1, -1, 0.1), // 18, elytra third left crystal rot
            q(1, -0.1, 1, 0.1), // 19, elytra third right crystal rot

            q(0, 0.2, 0, 1), // 20, boost first left crystal rot
            q(0, -0.2, 0, 1), // 21, boost first right crystal rot
            q(0, 1, 0, 0.2), // 22, boost second left crystal rot
            q(0, -1, 0, 0.2), // 23, boost second right crystal rot

            q(-0.5, 1.2, -0.3, 0.9), // 24, first idle chain left rot
            q(-0.5, -1.2, 0.3, 0.9), // 25, first idle chain right rot
            q(-0.7, 1, -0.5, 1.1), // 26, second idle chain left rot
            q(-0.7, -1, 0.5, 1.1), // 27, second idle chain right rot
            q(0.5, 0.3, -0.2, 1.1), // 28, third idle chain left rot
            q(0.5, 0.1, 0.3, 1.2), // 29, third idle chain right rot
            q(-0.7, -0.9, -1, -0.5), // 30, fourth idle chain left rot
            q(-0.7, 0.9, 1, -0.5), // 31, fourth idle chain right rot
            q(-0.7, -0.9, -1, -0.5), // 32, fifth idle chain left rot
            q(-0.7, 0.9, 1, -0.5), // 33, fifth idle chain right rot
            q(-0.8, -0.7, -0.4, -1), // 34, sixth idle chain left rot
            q(-0.8, 0.7, 0.4, -1), // 35, sixth idle chain right rot
            q(0.05, 0.15, 0.15, 1), // 36, seventh idle chain left rot
            q(0.05, -0.15, -0.15, 1), // 37, seventh idle chain right rot

            q(-1, -1, 1, 1.1), // 38, first shield idle left rot
            q(1, -1, 1, -1.1), // 39, first shield idle right rot
            q(0, 2, 0, -0.1), // 40, first shield idle chain left rot
            q(0, 2, 0, 0.1), // 41, first shield idle chain right rot
            q(0, 1, 0, 0.2), // 42, second shield idle chain left rot
            q(0, 1, 0, -0.2), // 43, second shield idle chain right rot
            q(0, 1, 0, -0.3), // 44, third shield idle chain left rot
            q(0, 1, 0, 0.3), // 45, third shield idle chain right rot
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
        this.put(SHIELD_IDLE, new CrystalTargetIterable(SHIELD_IDLE)
                .with(VEC_3S[98], QUATERNIONS[38])
                .with(VEC_3S[99], QUATERNIONS[39])
                .with(VEC_3S[100], QUATERNIONS[40])
                .with(VEC_3S[101], QUATERNIONS[41])
                .with(VEC_3S[106], QUATERNIONS[42])
                .with(VEC_3S[107], QUATERNIONS[43])
                .with(VEC_3S[112], QUATERNIONS[44])
                .with(VEC_3S[113], QUATERNIONS[45])
                .with(VEC_3S[102], QUATERNIONS[40])
                .with(VEC_3S[103], QUATERNIONS[41])
                .with(VEC_3S[108], QUATERNIONS[42])
                .with(VEC_3S[109], QUATERNIONS[43])
                .with(VEC_3S[114], QUATERNIONS[44])
                .with(VEC_3S[115], QUATERNIONS[45])
                .with(VEC_3S[104], QUATERNIONS[40])
                .with(VEC_3S[105], QUATERNIONS[41])
                .with(VEC_3S[110], QUATERNIONS[42])
                .with(VEC_3S[111], QUATERNIONS[43])
                .with(VEC_3S[116], QUATERNIONS[44])
                .with(VEC_3S[117], QUATERNIONS[45])
        );
        this.put(IDLE, new CrystalTargetIterable(IDLE).defaultElytraAttached(true)
                .with(VEC_3S[44], QUATERNIONS[24], 1)
                .with(VEC_3S[45], QUATERNIONS[25])
                .with(VEC_3S[52], QUATERNIONS[26], 1)
                .with(VEC_3S[53], QUATERNIONS[27])
                .with(VEC_3S[60], QUATERNIONS[28], 1)
                .with(VEC_3S[61], QUATERNIONS[29])
                .with(VEC_3S[68], QUATERNIONS[30], false)
                .with(VEC_3S[69], QUATERNIONS[31], false)
                .with(VEC_3S[76], QUATERNIONS[32])
                .with(VEC_3S[77], QUATERNIONS[33], 1)
                .with(VEC_3S[84], QUATERNIONS[34])
                .with(VEC_3S[85], QUATERNIONS[35], 1)
                .with(VEC_3S[46], QUATERNIONS[24], 1)
                .with(VEC_3S[47], QUATERNIONS[25])
                .with(VEC_3S[54], QUATERNIONS[26], 1)
                .with(VEC_3S[55], QUATERNIONS[27])
                .with(VEC_3S[62], QUATERNIONS[28], 1)
                .with(VEC_3S[63], QUATERNIONS[29])
                .with(VEC_3S[70], QUATERNIONS[30], false)
                .with(VEC_3S[71], QUATERNIONS[31], false)
                .with(VEC_3S[78], QUATERNIONS[32])
                .with(VEC_3S[79], QUATERNIONS[33], 1)
                .with(VEC_3S[86], QUATERNIONS[34])
                .with(VEC_3S[87], QUATERNIONS[35], 1)
                .with(VEC_3S[92], QUATERNIONS[36])
                .with(VEC_3S[93], QUATERNIONS[37], 1)
                .with(VEC_3S[48], QUATERNIONS[24], 1)
                .with(VEC_3S[49], QUATERNIONS[25])
                .with(VEC_3S[56], QUATERNIONS[26], 1)
                .with(VEC_3S[57], QUATERNIONS[27])
                .with(VEC_3S[64], QUATERNIONS[28], 1)
                .with(VEC_3S[65], QUATERNIONS[29])
                .with(VEC_3S[72], QUATERNIONS[30], false)
                .with(VEC_3S[73], QUATERNIONS[31], false)
                .with(VEC_3S[80], QUATERNIONS[32])
                .with(VEC_3S[81], QUATERNIONS[33], 1)
                .with(VEC_3S[88], QUATERNIONS[34])
                .with(VEC_3S[89], QUATERNIONS[35], 1)
                .with(VEC_3S[94], QUATERNIONS[36])
                .with(VEC_3S[95], QUATERNIONS[37], 1)
                .with(VEC_3S[50], QUATERNIONS[24], 1)
                .with(VEC_3S[51], QUATERNIONS[25])
                .with(VEC_3S[58], QUATERNIONS[26], 1)
                .with(VEC_3S[59], QUATERNIONS[27])
                .with(VEC_3S[66], QUATERNIONS[28], 1)
                .with(VEC_3S[67], QUATERNIONS[29])
                .with(VEC_3S[74], QUATERNIONS[30], false)
                .with(VEC_3S[75], QUATERNIONS[31], false)
                .with(VEC_3S[82], QUATERNIONS[32])
                .with(VEC_3S[83], QUATERNIONS[33], 1)
                .with(VEC_3S[90], QUATERNIONS[34])
                .with(VEC_3S[91], QUATERNIONS[35], 1)
                .with(VEC_3S[96], QUATERNIONS[36])
                .with(VEC_3S[97], QUATERNIONS[37], 1)
        );
        this.put(ELYTRA, new CrystalTargetIterable(ELYTRA).defaultElytraAttached(true)
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
    
    private static Quaterniondc q(double x, double y, double z, double w) {
        return new Quaterniond(x, y, z, w).normalize();
    }

    public record CrystalTarget(Vec3 targetPosition, Quaterniondc targetRotation, WingsAction group, byte misc, boolean elytraAttached) {}

    public static class CrystalTargetIterable extends ObjectArrayList<CrystalTarget> implements Iterator<CrystalTarget> {

        private final WingsAction group;
        private boolean defaultElytraAttached = false;
        private Iterator<CrystalTarget> iterator;
        
        public CrystalTargetIterable(WingsAction group) {
            this.group = group;
        }

        protected CrystalTargetIterable regenerateIterator() {
            this.iterator = this.iterator();
            return this;
        }

        public CrystalTargetIterable defaultElytraAttached(boolean elytraAttached) {
            this.defaultElytraAttached = elytraAttached;
            return this;
        }

        public CrystalTargetIterable with(Vec3 targetPosition, Quaterniondc targetRotation) {
            return this.with(targetPosition, targetRotation, 0);
        }

        public CrystalTargetIterable with(Vec3 targetPosition, Quaterniondc targetRotation, int misc) {
            return this.with(targetPosition, targetRotation, misc, defaultElytraAttached);
        }

        public CrystalTargetIterable with(Vec3 targetPosition, Quaterniondc targetRotation, boolean elytraAttached) {
            return this.with(targetPosition, targetRotation, 0, elytraAttached);
        }

        public CrystalTargetIterable with(Vec3 targetPosition, Quaterniondc targetRotation, int misc, boolean elytraAttached) {
            this.add(new CrystalTarget(targetPosition, targetRotation, this.group, (byte) misc, elytraAttached));
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
