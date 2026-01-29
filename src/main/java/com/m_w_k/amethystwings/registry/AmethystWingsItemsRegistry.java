package com.m_w_k.amethystwings.registry;

import com.m_w_k.amethystwings.AmethystWingsConfig;
import com.m_w_k.amethystwings.CrystalStats;
import com.m_w_k.amethystwings.api.TotemBehavior;
import com.m_w_k.amethystwings.item.WingsCrystalItem;
import com.m_w_k.amethystwings.item.WingsItem;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.m_w_k.amethystwings.AmethystWingsMod.MODID;
import static com.m_w_k.amethystwings.api.util.WingsAction.*;
import static com.m_w_k.amethystwings.datagen.AmethystWingsModelProvider.*;

public class AmethystWingsItemsRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> WINGS = ITEMS.register("wings_controller", () -> new WingsItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON), 6));
    public static final RegistryObject<Item> PRIMITIVE_WINGS = ITEMS.register("primitive_wings_controller", () -> new WingsItem(new Item.Properties().stacksTo(1).fireResistant(), 2));

    public static final RegistryObject<Item> TREATED_AMETHYST = ITEMS.register("treated_amethyst", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RESONANT_AMETHYST = ITEMS.register("resonant_amethyst",
            () -> new WingsCrystalItem(crystalProps(60), new CrystalStats(RESONANT).mass(() -> AmethystWingsConfig.resonantMass).attributeContributions(() -> resonantAttribute(0)).action(ELYTRA, SHIELD, BOOST)));
    public static final RegistryObject<Item> STURDY_RESONANT_AMETHYST = ITEMS.register("resonant_amethyst.sturdy",
            () -> new WingsCrystalItem(crystalPropsSturdy(60), new CrystalStats(RESONANT).mass(() -> AmethystWingsConfig.resonantMass).attributeContributions(() -> resonantAttribute(1)).action(ELYTRA, SHIELD, BOOST)));
    public static final RegistryObject<Item> SPATIAL_RESONANT_AMETHYST = ITEMS.register("resonant_amethyst.spatial",
            () -> new WingsCrystalItem(crystalProps(60), new CrystalStats(RESONANT).mass(() -> AmethystWingsConfig.resonantMass).attributeContributions(() -> resonantAttribute(2)).action(ELYTRA, SHIELD, BOOST)));
    public static final RegistryObject<Item> LUCKY_RESONANT_AMETHYST = ITEMS.register("resonant_amethyst.lucky",
            () -> new WingsCrystalItem(crystalProps(60), new CrystalStats(RESONANT).mass(() -> AmethystWingsConfig.resonantMass).attributeContributions(() -> resonantAttribute(3)).action(ELYTRA, SHIELD, BOOST)));

    public static final RegistryObject<Item> HARDENED_AMETHYST = ITEMS.register("hardened_amethyst",
            () -> new WingsCrystalItem(crystalProps(150), new CrystalStats(HARDENED).priority(10).mass(() -> AmethystWingsConfig.hardenedMass).action(SHIELD)));
    public static final RegistryObject<Item> STURDY_HARDENED_AMETHYST = ITEMS.register("hardened_amethyst.sturdy",
            () -> new WingsCrystalItem(crystalPropsSturdy(150), new CrystalStats(HARDENED).priority(10).mass(() -> AmethystWingsConfig.hardenedMass).action(SHIELD)));
    public static final RegistryObject<Item> WARDING_HARDENED_AMETHYST = ITEMS.register("hardened_amethyst.warding",
            () -> new WingsCrystalItem(crystalProps(150), new CrystalStats(HARDENED).priority(10).mass(() -> AmethystWingsConfig.hardenedMass).attributeContributions(AmethystWingsItemsRegistry::wardingAttribute).action(SHIELD)));
    public static final RegistryObject<Item> REINFORCED_HARDENED_AMETHYST = ITEMS.register("hardened_amethyst.reinforced",
            () -> new WingsCrystalItem(crystalProps(150), new CrystalStats(HARDENED).priority(10).mass(() -> AmethystWingsConfig.hardenedMass).shatterMult(() -> AmethystWingsConfig.hardenedModReinforced).action(SHIELD)));

    public static final RegistryObject<Item> ENERGETIC_AMETHYST = ITEMS.register("energetic_amethyst",
            () -> new WingsCrystalItem(crystalProps(150), new CrystalStats(ENERGETIC).priority(10).action(BOOST)));
    public static final RegistryObject<Item> STURDY_ENERGETIC_AMETHYST = ITEMS.register("energetic_amethyst.sturdy",
            () -> new WingsCrystalItem(crystalPropsSturdy(150), new CrystalStats(ENERGETIC).priority(10).action(BOOST)));
    public static final RegistryObject<Item> EMPOWERED_ENERGETIC_AMETHYST = ITEMS.register("energetic_amethyst.empowered",
            () -> new WingsCrystalItem(crystalProps(150), new CrystalStats(ENERGETIC).priority(10).boostBonus(() -> AmethystWingsConfig.energeticModEmpowered).action(BOOST)));
    public static final RegistryObject<Item> ENHANCING_ENERGETIC_AMETHYST = ITEMS.register("energetic_amethyst.enhancing",
            () -> new WingsCrystalItem(crystalProps(150), new CrystalStats(ENERGETIC).priority(10).attributeContributions(AmethystWingsItemsRegistry::enhancingAttribute).action(BOOST)));

    public static final RegistryObject<Item> SHAPED_AMETHYST = ITEMS.register("shaped_amethyst",
            () -> new WingsCrystalItem(crystalProps(150), new CrystalStats(SHAPED).priority(10).action(ELYTRA)));
    public static final RegistryObject<Item> STURDY_SHAPED_AMETHYST = ITEMS.register("shaped_amethyst.sturdy",
            () -> new WingsCrystalItem(crystalPropsSturdy(150), new CrystalStats(SHAPED).priority(10).action(ELYTRA)));
    public static final RegistryObject<Item> FLOATY_SHAPED_AMETHYST = ITEMS.register("shaped_amethyst.floaty",
            () -> new WingsCrystalItem(crystalProps(150), new CrystalStats(SHAPED).priority(10).attributeContributions(() -> shapedAttribute(true)).action(ELYTRA)));
    public static final RegistryObject<Item> HEAVY_SHAPED_AMETHYST = ITEMS.register("shaped_amethyst.heavy",
            () -> new WingsCrystalItem(crystalProps(150), new CrystalStats(SHAPED).priority(10).attributeContributions(() -> shapedAttribute(false)).action(ELYTRA)));

    public static final RegistryObject<Item> AURIC_AMETHYST = ITEMS.register("auric_amethyst",
            () -> new WingsCrystalItem(new Item.Properties().fireResistant(), new CrystalStats(AURIC).attributeContributions(() -> auricAttribute(0))));
    public static final RegistryObject<Item> BARRIER_AURIC_AMETHYST = ITEMS.register("auric_amethyst.barrier",
            () -> new WingsCrystalItem(new Item.Properties().fireResistant(), new CrystalStats(AURIC).attributeContributions(() -> auricAttribute(1))));
    public static final RegistryObject<Item> REJUVENATING_AURIC_AMETHYST = ITEMS.register("auric_amethyst.rejuvenating",
            () -> new WingsCrystalItem(new Item.Properties().fireResistant(), new CrystalStats(AURIC).attributeContributions(() -> auricAttribute(2))));
    public static final RegistryObject<Item> REFOCUSED_AURIC_AMETHYST = ITEMS.register("auric_amethyst.refocused",
            () -> new WingsCrystalItem(new Item.Properties().fireResistant(), new CrystalStats(AURIC).attributeContributions(() -> auricAttribute(3))));

    public static final RegistryObject<Item> TOTEMIC_AMETHYST = ITEMS.register("totemic_amethyst",
            () -> new WingsCrystalItem(new Item.Properties(), new CrystalStats(TOTEMIC).special(TotemBehavior.IDENTIFIER, TotemBehavior.NORMAL)));
    public static final RegistryObject<Item> VENGEFUL_TOTEMIC_AMETHYST = ITEMS.register("totemic_amethyst.vengeful",
            () -> new WingsCrystalItem(new Item.Properties(), new CrystalStats(TOTEMIC).special(TotemBehavior.IDENTIFIER, TotemBehavior.VENGEFUL)));
    public static final RegistryObject<Item> MERCIFUL_TOTEMIC_AMETHYST = ITEMS.register("totemic_amethyst.merciful",
            () -> new WingsCrystalItem(new Item.Properties(), new CrystalStats(TOTEMIC).special(TotemBehavior.IDENTIFIER, TotemBehavior.MERCIFUL)));
    public static final RegistryObject<Item> NOURISHING_TOTEMIC_AMETHYST = ITEMS.register("totemic_amethyst.nourishing",
            () -> new WingsCrystalItem(new Item.Properties(), new CrystalStats(TOTEMIC).special(TotemBehavior.NOURISHING, null)));

    private static Item.Properties crystalProps(int durability) {
        return new Item.Properties().durability(durability);
    }
    private static Item.Properties crystalPropsSturdy(int durability) {
        return new Item.Properties().durability(durability * 3);
    }

    private static Object2DoubleMap<Attribute> resonantAttribute(int variant) {
        return switch (variant) {
            case 2 -> {
                Object2DoubleMap<Attribute> ret = new Object2DoubleArrayMap<>();
                ret.put(ForgeMod.ENTITY_REACH.get(), AmethystWingsConfig.resonantModSpatial);
                ret.put(ForgeMod.BLOCK_REACH.get(), AmethystWingsConfig.resonantModSpatial);
                ret.put(Attributes.ARMOR_TOUGHNESS, AmethystWingsConfig.resonantToughness);
                yield ret;
            }
            case 3 -> {
                Object2DoubleMap<Attribute> ret = new Object2DoubleArrayMap<>();
                ret.put(Attributes.LUCK, AmethystWingsConfig.resonantModLucky);
                ret.put(Attributes.ARMOR_TOUGHNESS, AmethystWingsConfig.resonantToughness);
                yield ret;
            }
            default -> Object2DoubleMaps.singleton(Attributes.ARMOR_TOUGHNESS, AmethystWingsConfig.resonantToughness);

        };
    }

    private static Object2DoubleMap<Attribute> wardingAttribute() {
        return Object2DoubleMaps.singleton(AmethystWingsAttributeRegistry.WARDING.get(), AmethystWingsConfig.hardenedModWarding);
    }

    private static Object2DoubleMap<Attribute> enhancingAttribute() {
        Object2DoubleMap<Attribute> ret = new Object2DoubleArrayMap<>();
        ret.put(Attributes.MOVEMENT_SPEED, AmethystWingsConfig.energeticModEnhancing);
        ret.put(ForgeMod.SWIM_SPEED.get(), AmethystWingsConfig.energeticModEnhancing);
        return ret;
    }

    private static Object2DoubleMap<Attribute> shapedAttribute(boolean floaty) {
        return Object2DoubleMaps.singleton(ForgeMod.ENTITY_GRAVITY.get(), floaty ? -AmethystWingsConfig.shapedModFloaty : AmethystWingsConfig.shapedModHeavy);
    }

    private static Object2DoubleMap<Attribute> auricAttribute(int variant) {
        Object2DoubleMap<Attribute> ret = new Object2DoubleArrayMap<>();
        if (variant == 3) {
            ret.put(Attributes.ATTACK_SPEED, AmethystWingsConfig.auricModRefocusedAttackSpeed);
            ret.put(Attributes.ATTACK_DAMAGE, AmethystWingsConfig.auricModRefocusedAttackStrength);
        } else {
            ret.put(Attributes.ARMOR_TOUGHNESS, AmethystWingsConfig.auricToughness);
            ret.put(Attributes.KNOCKBACK_RESISTANCE, 0.1);
        }
        if (variant == 1) {
            ret.put(AmethystWingsAttributeRegistry.BARRIER.get(), AmethystWingsConfig.auricModBarrier);
        } else if (variant == 2) {
            ret.put(Attributes.MAX_HEALTH, AmethystWingsConfig.auricModRejuvenating);
        }
        return ret;
    }
}
