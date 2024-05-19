package top.prohect.psp.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.prohect.psp.server.command.command4Module.OptimizeLead;
import top.prohect.psp.server.command.command4Module.YeetLeadDetachedByDistance;

@Mixin(PathAwareEntity.class)
public abstract class PathAwareEntityMixin {

    @Inject(method = "updateLeash", at = @At("HEAD"), cancellable = true)
    private void updateLeash(CallbackInfo ci) {
        if (YeetLeadDetachedByDistance.on()) {
            try {
                PathAwareEntity pathAwareEntity = (PathAwareEntity) (Object) this;
                if (pathAwareEntity.leashNbt != null) {
                    pathAwareEntity.readLeashNbt();
                }
                if (pathAwareEntity.getHoldingEntity() == null) {
                    return;
                }
                if (!pathAwareEntity.isAlive() || !pathAwareEntity.getHoldingEntity().isAlive()) {
                    pathAwareEntity.detachLeash(true, true);
                }
                Entity entity = pathAwareEntity.getHoldingEntity();
                if (entity != null && entity.getWorld() == pathAwareEntity.getWorld()) {
                    pathAwareEntity.setPositionTarget(entity.getBlockPos(), 5);
                    float f = pathAwareEntity.distanceTo(entity);
                    if (pathAwareEntity instanceof TameableEntity && ((TameableEntity) pathAwareEntity).isInSittingPose()) {
                        if (f > 10.0f) {
                            pathAwareEntity.detachLeash(true, true);
                        }
                        return;
                    }
                    pathAwareEntity.updateForLeashLength(f);
                    if (!OptimizeLead.on() && f > 10.0f) {
                        pathAwareEntity.detachLeash(true, true);
                        pathAwareEntity.goalSelector.disableControl(Goal.Control.MOVE);
                    } else if (f > 6.0f) {
                        double d = (entity.getX() - pathAwareEntity.getX()) / (double) f;
                        double e = (entity.getY() - pathAwareEntity.getY()) / (double) f;
                        double g = (entity.getZ() - pathAwareEntity.getZ()) / (double) f;
                        pathAwareEntity.setVelocity(pathAwareEntity.getVelocity().add(Math.copySign(d * d * 0.4, d), Math.copySign(e * e * 0.4, e), Math.copySign(g * g * 0.4, g)));
                        pathAwareEntity.limitFallDistance();
                    } else if (pathAwareEntity.shouldFollowLeash() && !pathAwareEntity.isPanicking()) {
                        pathAwareEntity.goalSelector.enableControl(Goal.Control.MOVE);
                        float h = 2.0f;
                        Vec3d vec3d = new Vec3d(entity.getX() - pathAwareEntity.getX(), entity.getY() - pathAwareEntity.getY(), entity.getZ() - pathAwareEntity.getZ()).normalize().multiply(Math.max(f - 2.0f, 0.0f));
                        pathAwareEntity.getNavigation().startMovingTo(pathAwareEntity.getX() + vec3d.x, pathAwareEntity.getY() + vec3d.y, pathAwareEntity.getZ() + vec3d.z, pathAwareEntity.getFollowLeashSpeed());
                    }
                }
            } finally {
                ci.cancel();
            }
        }
    }

//    @Redirect(method = "updateLeash", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/mob/PathAwareEntity;distanceTo(Lnet/minecraft/entity/Entity;)F"))
//    private float distanceTo(Entity e){
//        if (OptimizeLead.on())
//        return 1E-7F;
//        else {
//            PathAwareEntity that = (PathAwareEntity)(Object)this;
//            float f = (float)(that.getX() - e.getX());
//            float g = (float)(that.getY() - e.getY());
//            float h = (float)(that.getZ() - e.getZ());
//            return MathHelper.sqrt(f * f + g * g + h * h);
//        }
//    }
}
