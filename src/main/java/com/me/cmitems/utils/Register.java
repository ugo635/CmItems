package com.me.cmitems.utils;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.server.world.ServerWorld;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.me.cmitems.utils.TickScheduler.clientTasks;
import static com.me.cmitems.utils.TickScheduler.serverTasks;

public class Register {

    /**
     * Registers a tick event that executes an action every specified number of ticks.
     * @param tick The number of ticks after which the action should be executed.
     * @param action The action to execute. It receives a lambda to unregister itself.
     */
    public static void onClientTick(int tick, Consumer<String[]> action) {
        clientTasks.add(new TickScheduler.ClientTask(tick, done -> action.accept(new String[0])));
    }

    public static void onServerTick(int tick, Consumer<ServerWorld> action) {
        serverTasks.add(new TickScheduler.ServerTask(tick, action));
    }

    /**
     * Registers a command with the specified name and aliases.
     * The action is executed when the command is invoked, with the provided arguments.
     *
     * @param name The name of the command.
     * @param aliases Optional aliases for the command.
     * @param action The action to execute when the command is invoked. (Lambda function)
     */
    public static void command(String name, Consumer<String[]> action, String... aliases) {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
            dispatcher.register(createLiteral(name, action));
            for (String alias : aliases) dispatcher.register(createLiteral(alias, action));
        }));
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> createLiteral(String name, Consumer<String[]> action) {
        return ClientCommandManager.literal(name)
                .executes(context -> { action.accept(new String[0]); return 1; })
                .then(ClientCommandManager.argument("args", StringArgumentType.greedyString())
                        .executes(context -> {
                            String argsString = StringArgumentType.getString(context, "args");
                            String[] args = Arrays.stream(argsString.split(" "))
                                    .filter(s -> !s.isEmpty())
                                    .toArray(String[]::new);
                            action.accept(args);
                            return 1;
                        }));
    }


}
