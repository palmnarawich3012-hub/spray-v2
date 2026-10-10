package com.pepperspray.mixin;

import com.pepperspray.PepperSprayItem;
import com.pepperspray.client.PepperSprayClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    /** In first person, swap to the model that has a hand holding the spray. */
    @ModifyVariable(
        method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
        at = @At("HEAD"),
        argsOnly = true)
    private BakedModel pepperspray$handModel(BakedModel model, ItemStack stack, ModelTransformationMode mode,
                                             boolean leftHanded, MatrixStack matrices,
                                             VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.getItem() instanceof PepperSprayItem && mode.isFirstPerson()) {
            return MinecraftClient.getInstance().getBakedModelManager().getModel(PepperSprayClient.HAND_MODEL);
        }
        return model;
    }
}
