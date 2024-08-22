package com.m_w_k.amethystwings.registry;

import com.m_w_k.amethystwings.AmethystWingsConfig;
import com.m_w_k.amethystwings.item.WingsCrystalItem;
import com.m_w_k.amethystwings.item.WingsItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.m_w_k.amethystwings.AmethystWingsMod.MODID;
import static com.m_w_k.amethystwings.api.util.WingsAction.*;
import static com.m_w_k.amethystwings.datagen.AmethystWingsModelProvider.*;

public class AmethystWingsItemsRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> WINGS = ITEMS.register("wings_controller", () -> new WingsItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> TREATED_AMETHYST = ITEMS.register("treated_amethyst", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RESONANT_AMETHYST = ITEMS.register("resonant_amethyst",
            () -> new WingsCrystalItem(crystalProps(60), (byte) 0, () -> (byte) AmethystWingsConfig.resonantMass, () -> AmethystWingsConfig.resonantToughness, RESONANT, ELYTRA, SHIELD, BOOST));
    public static final RegistryObject<Item> HARDENED_AMETHYST = ITEMS.register("hardened_amethyst",
            () -> new WingsCrystalItem(crystalProps(150), (byte) 10, () -> (byte) AmethystWingsConfig.hardenedMass, HARDENED, SHIELD));
    public static final RegistryObject<Item> ENERGETIC_AMETHYST = ITEMS.register("energetic_amethyst",
            () -> new WingsCrystalItem(crystalProps(150), (byte) 10, (byte) 5, ENERGETIC, BOOST));
    public static final RegistryObject<Item> SHAPED_AMETHYST = ITEMS.register("shaped_amethyst",
            () -> new WingsCrystalItem(crystalProps(150), (byte) 10, (byte) 5, SHAPED, ELYTRA));
    public static final RegistryObject<Item> AURIC_AMETHYST = ITEMS.register("auric_amethyst",
            () -> new WingsCrystalItem(new Item.Properties().fireResistant(), (byte) 0, (byte) 5, () -> AmethystWingsConfig.auricToughness, AURIC, NONE));

    private static Item.Properties crystalProps(int durability) {
        return new Item.Properties().durability(durability);
    }
}
