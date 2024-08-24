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
            .defineInRange("resonantToughness", 0.1d, 0d, 10d);

    private static final ForgeConfigSpec.DoubleValue AURIC_TOUGHNESS = BUILDER
            .comment("Toughness gained per slotted auric crystal")
            .defineInRange("auricToughness", 0.5d, 0d, 10d);

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

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
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
    }
}
