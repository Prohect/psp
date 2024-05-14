package top.prohect.psp.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.prohect.psp.server.command.YeetTeleportCommandDestinationLimitCommand;

import java.util.Set;

@Mixin(TeleportCommand.class)
public abstract class TeleportCommandMixin {

    @Final
    @Shadow
    private static SimpleCommandExceptionType INVALID_POSITION_EXCEPTION;

    /**
     * make the tp command not limiting the target position
     * and
     */
    @Inject(method = "teleport", at = @At("HEAD"), cancellable = true)
    private static void teleport(ServerCommandSource source, Entity target, ServerWorld world, double x, double y, double z, Set<PositionFlag> movementFlags, float yaw, float pitch, TeleportCommand.LookTarget facingLocation, CallbackInfo ci) throws CommandSyntaxException {
        BlockPos blockPos = BlockPos.ofFloored(x, y, z);
        try {
            if (YeetTeleportCommandDestinationLimitCommand.isYeetTPLimit()) {
                exec(source, target, world, x, y, z, movementFlags, yaw, pitch, facingLocation);
            } else if (!World.isValid(blockPos)) {
                throw INVALID_POSITION_EXCEPTION.create();
            } else {
                exec(source, target, world, x, y, z, movementFlags, yaw, pitch, facingLocation);
            }
        } finally {
            ci.cancel();
        }
    }

    @Unique
    private static void exec(ServerCommandSource source, Entity target, ServerWorld world, double x, double y, double z, Set<PositionFlag> movementFlags, float yaw, float pitch, TeleportCommand.LookTarget facingLocation) {
        float f = MathHelper.wrapDegrees(yaw);
        float g = MathHelper.wrapDegrees(pitch);
        if (target.teleport(world, x, y, z, movementFlags, f, g)) {
            if (facingLocation != null) {
                facingLocation.look(source, target);
            }


            if (target instanceof LivingEntity livingEntity) {
                if (!livingEntity.isFallFlying()) {
                    target.setVelocity(target.getVelocity().multiply(1.0, 0.0, 1.0));
                    target.setOnGround(true);
                }
            }


            if (target instanceof PathAwareEntity pathAwareEntity) {
                pathAwareEntity.getNavigation().stop();
            }

        }
        target.fallDistance = 0f;
    }
}
