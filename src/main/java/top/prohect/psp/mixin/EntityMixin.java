package top.prohect.psp.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.prohect.psp.server.command.command4Module.YeetTeleportCommandDestinationLimitCommand;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public double prevY;
    @Shadow
    public double prevX;
    @Shadow
    public double prevZ;

    /**
     * makes server not limiting the position for a player.
     * originally it clamps the y besides -3.0E7 and 3.0E7,
     * now unlimited
     * <p>
     * ignore the warning if there were
     */
    @Inject(method = "updatePosition", at = @At("HEAD"), cancellable = true)
    public void updatePosition(double x, double y, double z, CallbackInfo ci) {
        if (YeetTeleportCommandDestinationLimitCommand.on() && ((Entity) (Object) this) instanceof ServerPlayerEntity) {
            this.prevX = x;
            this.prevY = y;
            this.prevZ = y;
            this.setPosition(x, y, z);
            ci.cancel();
        }
    }

    @Shadow
    public abstract void setPosition(double d, double y, double e);
}
