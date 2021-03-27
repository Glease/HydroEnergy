package com.sinthoras.hydroenergy.mixins.client;

import net.minecraft.client.renderer.ActiveRenderInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ActiveRenderInfo.class)
public class MixinsActiveRenderInfo {

    @Inject(method = "getBlockAtEntityViewpoint", at = @At(value = "INVOKE_ASSIGN", target = ""))
    public static void handleHEWater(CallbackInfo callbackInfo) {

    }
}
