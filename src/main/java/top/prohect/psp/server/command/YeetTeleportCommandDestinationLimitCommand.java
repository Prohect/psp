package top.prohect.psp.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class YeetTeleportCommandDestinationLimitCommand{
    private static boolean yeetTPLimit = true;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandManager.literal("yeettplimit").requires((source) -> {
            return source.hasPermissionLevel(2);
        })).executes((context) -> {
            return execute((ServerCommandSource) context.getSource());
        }));
    }

    private static int execute(ServerCommandSource source) {

        yeetTPLimit = !yeetTPLimit;
        if (yeetTPLimit)
            source.sendFeedback(() -> {
                return Text.translatable("commands.yeetteleportlimit.enabled");
            }, true);
        else source.sendFeedback(() -> {
            return Text.translatable("commands.yeetteleportlimit.disabled");
        }, true);
        return 1;
    }

    public static boolean yeetTPLimit() {
        return yeetTPLimit;
    }

    public static void setYeetTPLimit(boolean yeetTPLimit) {
        YeetTeleportCommandDestinationLimitCommand.yeetTPLimit = yeetTPLimit;
    }
}