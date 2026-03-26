package com.me.cmitems.utils;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static void commandWithArgs(String name, Consumer<String[]> action, List<List<String>> args, List<ArgumentType<Object>> argumentTypes) {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
            dispatcher.register(createLiteralWithArgs(name, action,  args, argumentTypes));
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

    /**
     *
     * @param name Name of the command
     * @param action What it will do
     * @param args List of argument lists. Each inner list represents a set of arguments for a specific subcommand or variation of the command. The outer list allows for multiple variations of the command, each with its own set of arguments.
     * <pre>
     *     {@code
     *          Example:
     *          createLiteralWithArgs("fruit",
     *          () -> {System.out.println("Hello")},
     *          List.of(
     *              List.of("banana", "apple"),
     *              List.of(1, 2)
     *          ),
     *          List.of(ArgumentType<String>, ArgumentType<Integer>)
     *          Command:
     *          /name <banana|apple> <1|2>
     *     }
     * </pre>
     */
    public static LiteralArgumentBuilder<FabricClientCommandSource> createLiteralWithArgs(
            String name,
            Consumer<String[]> action,
            List<List<String>> args,
            List<ArgumentType<Object>> argumentTypes
    ) {
        LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommandManager.literal(name);

        ArgumentBuilder<FabricClientCommandSource, ?> current = root;
        List<String> argNames = new ArrayList<>();

        for (int i = 0; i < args.size(); i++) {
            List<String> possibilities = args.get(i);
            ArgumentType<Object> type = argumentTypes.get(i);

            String argName = "arg" + i;
            argNames.add(argName);

            RequiredArgumentBuilder<FabricClientCommandSource, Object> argument =
                    ClientCommandManager.argument(argName, type)
                            .suggests((ctx, builder) -> {
                                String typed = builder.getRemaining().toLowerCase();

                                for (String option : possibilities) {
                                    if (option.toLowerCase().startsWith(typed)) {
                                        builder.suggest(option);
                                    }
                                }

                                return builder.buildFuture();
                            });

            current.then(argument);
            current = argument;
        }

        current.executes(ctx -> {
            String[] values = new String[argNames.size()];

            for (int i = 0; i < argNames.size(); i++) {
                values[i] = ctx.getArgument(argNames.get(i), Object.class).toString();
            }

            action.accept(values);
            return 1;
        });

        return root;
    }


}
