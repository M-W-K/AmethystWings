package com.m_w_k.amethystwings.api.util;

public enum WingsAction {
    SHIELD, ELYTRA, BOOST, NONE;

    public boolean isNone() {
        return this == NONE;
    }
}
