package com.pepperspray.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.util.Identifier;

public class PepperSprayClient implements ClientModInitializer {
    /** Model that includes a hand holding the can (first-person view only). */
    public static final Identifier HAND_MODEL = new Identifier("pepperspray", "item/pepper_spray_hand");

    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(ctx -> ctx.addModels(HAND_MODEL));
    }
}
