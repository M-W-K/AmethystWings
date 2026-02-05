package com.m_w_k.amethystwings.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public class ShadowingKeyMapping extends KeyMapping {
    protected final @NotNull KeyMapping shadowed;
    protected final @NotNull BooleanSupplier shouldShadow;

    public ShadowingKeyMapping(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier,
                               InputConstants.Type inputType, int keyCode, String category,
                               @NotNull KeyMapping shadowed, @NotNull BooleanSupplier shouldShadow) {
        super(description, keyConflictContext, keyModifier, inputType, keyCode, category);
        this.shadowed = shadowed;
        this.shouldShadow = shouldShadow;
    }

    @Override
    public void setDown(boolean p_90846_) {
        super.setDown(p_90846_);
        if (shouldShadow.getAsBoolean()) {
            shadowed.setDown(p_90846_);
        }
    }


}
