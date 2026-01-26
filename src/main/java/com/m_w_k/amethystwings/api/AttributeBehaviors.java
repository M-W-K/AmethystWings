package com.m_w_k.amethystwings.api;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;

import java.util.HashSet;
import java.util.Set;

public class AttributeBehaviors {

    public static final Set<Attribute> MULTIPLICATIVE_ATTRIBUTES = new HashSet<>();

    static {
        MULTIPLICATIVE_ATTRIBUTES.add(Attributes.MOVEMENT_SPEED);
        MULTIPLICATIVE_ATTRIBUTES.add(ForgeMod.SWIM_SPEED.get());
        MULTIPLICATIVE_ATTRIBUTES.add(ForgeMod.ENTITY_GRAVITY.get());
    }

    public static AttributeModifier.Operation getType(Attribute attribute) {
        return MULTIPLICATIVE_ATTRIBUTES.contains(attribute) ? AttributeModifier.Operation.MULTIPLY_TOTAL : AttributeModifier.Operation.ADDITION;
    }
}
