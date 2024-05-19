package top.prohect.psp.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.LeadItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.prohect.psp.server.EntityHoldingEntityMap;
import top.prohect.psp.server.PspServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(MobEntityRenderer.class)
public abstract class MobEntityRendererMixin<T extends MobEntity, M extends EntityModel<T>>
        extends LivingEntityRenderer<T, M> {
    public MobEntityRendererMixin(EntityRendererFactory.Context ctx, M model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "renderLeash", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void renderLeash(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider provider, E holdingEntity, CallbackInfo ci) {
        List<EntityHoldingEntityMap> entityHoldingEntityMaps = PspServer.entityHoldingEntityMaps;
        EntityHoldingEntityMap entityHoldingEntityMap = EntityHoldingEntityMap.getOrAddEntityHoldingEntityMapByWorld(entity.getWorld());
        if (entityHoldingEntityMap.entityHoldingMap.containsKey(entity)) {
            AtomicBoolean flag = new AtomicBoolean(false);
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.getHandItems().forEach(itemStack -> {
                    if (itemStack.getItem() instanceof LeadItem) flag.set(true);
                });
            }
            if (!flag.get()) {
                ci.cancel();
            }
        }
    }
}
