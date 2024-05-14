package top.prohect.psp.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.prohect.psp.server.command.YeetTeleportCommandDestinationLimitCommand;

@Mixin(CommandManager.class)
public class CommandManagerMixin {

    @Final
    @Shadow
    private CommandDispatcher<ServerCommandSource> dispatcher;

    @Inject(method = "<init>",at = @At("RETURN"))
    private void MixinInit(CommandManager.RegistrationEnvironment environment, CommandRegistryAccess commandRegistryAccess, CallbackInfo ci){
        YeetTeleportCommandDestinationLimitCommand.register(this.dispatcher);
    }
}
