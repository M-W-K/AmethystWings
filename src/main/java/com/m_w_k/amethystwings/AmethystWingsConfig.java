package com.m_w_k.amethystwings;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = AmethystWingsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AmethystWingsConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue RESONANT_TOUGHNESS = BUILDER
            .comment("Toughness gained per slotted resonant crystal")
            .defineInRange("resonantToughness", 0.5d, 0d, 10d);

    private static final ForgeConfigSpec.DoubleValue AURIC_TOUGHNESS = BUILDER
            .comment("Toughness gained per slotted auric crystal")
            .defineInRange("auricToughness", 1d, 0d, 10d);

    private static final ForgeConfigSpec.BooleanValue SHIELD_BREAK_COOLDOWN = BUILDER
            .comment("Whether shield-breaking hits should force wings into cooldown")
            .define("shieldBreakCooldown", false);

    private static final ForgeConfigSpec.IntValue SHIELD_BREAK_MASS_DAMAGE = BUILDER
            .comment("Mass damage dealt by shield-breaking hits. Mass damage to crystals occurs before normal damage, and cannot be reduced.")
            .defineInRange("shieldBreakMassDamage", 10, 0, 100);

    private static final ForgeConfigSpec.IntValue RESONANT_MASS = BUILDER
            .comment("Mass of resonant crystals")
            .defineInRange("resonantMass", 5, 1, 100);

    private static final ForgeConfigSpec.IntValue HARDENED_MASS = BUILDER
            .comment("Mass of hardened crystals")
            .defineInRange("hardenedMass", 10, 1, 100);

    private static final ForgeConfigSpec.IntValue BOOST_DAMAGE = BUILDER
            .comment("Damage dealt to boost crystals on boost")
            .defineInRange("boostDamage", 60, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.BooleanValue ALT_BOOST_DAMAGE = BUILDER
            .comment("If true, boost damage will be inflicted to a more random crystal instead of the highest durability crystal.")
            .define("altBoostDamage", false);

    private static final ForgeConfigSpec.DoubleValue BOOST_STRENGTH = BUILDER
            .comment("The strength factor for non-elytra boosts.")
            .defineInRange("boostStrength", 1, 0.1, 10);

    private static final ForgeConfigSpec.DoubleValue ELYTRA_BOOST_STRENGTH = BUILDER
            .comment("The strength factor for elytra boosts.")
            .defineInRange("elytraBoostStrength", 1, 0.1, 10);

    private static final ForgeConfigSpec.DoubleValue ATTRIBUTE_SOFT_CAP_FACTOR = BUILDER
            .comment("The strength of the soft cap for attribute bonuses.")
            .defineInRange("attributeSoftCapFactor", 2d, 1, 10);

    private static final ForgeConfigSpec.Builder MODIFIERS = BUILDER.push("Modifiers");

    private static final ForgeConfigSpec.DoubleValue RESONANT_MOD_SPATIAL = BUILDER
            .comment("The reach distance granted by the Spatial mod.")
            .defineInRange("modSpatialEffect", 0.7, 0.1, 5);
    private static final ForgeConfigSpec.DoubleValue RESONANT_MOD_LUCKY = BUILDER
            .comment("The luck granted by the Lucky mod.")
            .defineInRange("modLuckyEffect", 0.2, 0.01, 2);
    private static final ForgeConfigSpec.DoubleValue HARDENED_MOD_WARDING = BUILDER
            .comment("The flat damage reduction granted by the Warding mod.",
                    "Damage reduction applies to anything blocked by armor or classified as magic.",
                    "Cannot reduce incoming damage below 1, and applies before other sources of damage reduction.")
            .defineInRange("modWardingEffect", 0.5, 0.1, 5);
    private static final ForgeConfigSpec.DoubleValue HARDENED_MOD_REINFORCED = BUILDER
            .comment("The shatter multiplier granted by the Reinforced mod.",
                    "Crystal durability will be reduced by its max durability times this value when hit by an axe.")
            .defineInRange("modReinforcedEffect", 0.5, 0.1, 1);
    private static final ForgeConfigSpec.DoubleValue ENERGETIC_MOD_EMPOWERED = BUILDER
            .comment("The boost power increase granted by the Empowered mod.")
            .defineInRange("modEmpoweredEffect", 0.05, 0.01, 0.5);
    private static final ForgeConfigSpec.DoubleValue ENERGETIC_MOD_ENHANCING = BUILDER
            .comment("The speed and swim boost granted by the Enhancing mod.")
            .defineInRange("modEnhancingEffect", 0.1, 0.01, 0.5);
    private static final ForgeConfigSpec.DoubleValue SHAPED_MOD_FLOATY = BUILDER
            .comment("The gravity decrease granted by the Floaty mod.")
            .defineInRange("modFloatyEffect", 0.1, 0.01, 1);
    private static final ForgeConfigSpec.DoubleValue SHAPED_MOD_HEAVY = BUILDER
            .comment("The gravity increase granted by the Heavy mod.")
            .defineInRange("modHeavyEffect", 0.1, 0.01, 1);
    private static final ForgeConfigSpec.DoubleValue AURIC_MOD_BARRIER = BUILDER
            .comment("The exponent reduction granted by the Barrier mod.",
                    "Warning - this can be immensely powerful if overtuned.")
            .defineInRange("modBarrierEffect", 0.05, 0.001, 0.5);
    private static final ForgeConfigSpec.DoubleValue AURIC_MOD_REJUVENATING = BUILDER
            .comment("The bonus health granted by the Rejuvenation mod.",
                    "One bonus heart = 2 health.")
            .defineInRange("modRejuvenatingEffect", 3, 0.1, 10);
    private static final ForgeConfigSpec.DoubleValue AURIC_MOD_REFOCUSED_ATTACK_SPEED = BUILDER
            .comment("The bonus attack speed granted by the Refocused mod.")
            .defineInRange("modRefocusedSpeedEffect", 0.1, 0.01, 1);
    private static final ForgeConfigSpec.DoubleValue AURIC_MOD_REFOCUSED_ATTACK_STRENGTH = BUILDER
            .comment("The bonus attack damage granted by the Refocused mod.")
            .defineInRange("modRefocusedDamageEffect", 1, 0.1, 5);

    private static final ForgeConfigSpec.Builder INTERNAL = BUILDER.pop().push("Internal");

    private static final ForgeConfigSpec.IntValue CONFIG_VERSION = BUILDER
            .comment("Used for resetting config defaults when systems are rebalanced. Do not change.")
            .defineInRange("configVersion", 0, 0, 1);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static double resonantToughness;
    public static double auricToughness;

    public static boolean shieldBreakCooldown;
    public static int shieldBreakMassDamage;
    public static int resonantMass;
    public static int hardenedMass;

    public static int boostDamage;
    public static boolean altBoostDamage;

    public static double boostStrength;
    public static double elytraBoostStrength;

    public static double attributeSoftCapFactor;

    public static double resonantModSpatial;
    public static double resonantModLucky;
    public static double hardenedModWarding;
    public static double hardenedModReinforced;
    public static double energeticModEmpowered;
    public static double energeticModEnhancing;
    public static double shapedModFloaty;
    public static double shapedModHeavy;
    public static double auricModBarrier;
    public static double auricModRejuvenating;
    public static double auricModRefocusedAttackSpeed;
    public static double auricModRefocusedAttackStrength;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        configVersionUpdate();
        resonantToughness = RESONANT_TOUGHNESS.get();
        auricToughness = AURIC_TOUGHNESS.get();

        shieldBreakCooldown = SHIELD_BREAK_COOLDOWN.get();
        shieldBreakMassDamage = SHIELD_BREAK_MASS_DAMAGE.get();
        resonantMass = RESONANT_MASS.get();
        hardenedMass = HARDENED_MASS.get();

        boostDamage = BOOST_DAMAGE.get();
        altBoostDamage = ALT_BOOST_DAMAGE.get();

        boostStrength = BOOST_STRENGTH.get();
        elytraBoostStrength = ELYTRA_BOOST_STRENGTH.get();

        attributeSoftCapFactor = ATTRIBUTE_SOFT_CAP_FACTOR.get();

        // modifiers
        resonantModSpatial = RESONANT_MOD_SPATIAL.get();
        resonantModLucky = RESONANT_MOD_LUCKY.get();
        hardenedModWarding = HARDENED_MOD_WARDING.get();
        hardenedModReinforced = HARDENED_MOD_REINFORCED.get();
        energeticModEmpowered = ENERGETIC_MOD_EMPOWERED.get();
        energeticModEnhancing = ENERGETIC_MOD_ENHANCING.get();
        shapedModFloaty = SHAPED_MOD_FLOATY.get();
        shapedModHeavy = SHAPED_MOD_HEAVY.get();
        auricModBarrier = AURIC_MOD_BARRIER.get();
        auricModRejuvenating = AURIC_MOD_REJUVENATING.get();
        auricModRefocusedAttackSpeed = AURIC_MOD_REFOCUSED_ATTACK_SPEED.get();
        auricModRefocusedAttackStrength = AURIC_MOD_REFOCUSED_ATTACK_STRENGTH.get();
    }

    private static void configVersionUpdate() {
        boolean change = v1();
        if (change) {
            CONFIG_VERSION.set(1);
            SPEC.save();
        }
    }

    private static boolean v1() {
        if (CONFIG_VERSION.get() < 1) {
            RESONANT_TOUGHNESS.set(0.5);
            AURIC_TOUGHNESS.set(2D);
            return true;
        }
        return false;
    }
}
