package top.prohect.psp.server.command.command4Module;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class YeetLeadDetachedByDistance {
    private static boolean on = true;


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandManager.literal("yeetleaddetachedbydistance").requires((source) -> {
            return source.hasPermissionLevel(2);
        })).executes((context) -> {
            return exec((ServerCommandSource) context.getSource());
        }));
    }


    private static int exec(ServerCommandSource source) {

        on = !on;
        if (on)
            source.sendFeedback(() -> {
                return Text.translatable("commands.yeetleaddetachedbydistance.enabled");
            }, true);
        else source.sendFeedback(() -> {
            return Text.translatable("commands.yeetleaddetachedbydistance.disabled");
        }, true);
        return 1;
    }


    public static boolean on() {
        return on;
    }
}
