package com.me.cmitems.utils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class TickScheduler {

    public static final List<ClientTask> clientTasks = new ArrayList<>();
    public static final List<ServerTask> serverTasks = new ArrayList<>();

    static {
        ClientTickEvents.END_CLIENT_TICK.register(TickScheduler::tickClient);
        ServerTickEvents.END_WORLD_TICK.register(TickScheduler::tickServer);
    }

    private static void tickClient(MinecraftClient client) {
        Iterator<ClientTask> iterator = clientTasks.iterator();
        while (iterator.hasNext()) {
            ClientTask task = iterator.next();
            task.counter++;
            if (task.counter >= task.tick) {
                task.action.accept(client);
                task.counter = 0;
            }
        }
    }

    private static void tickServer(ServerWorld world) {
        for (ServerTask task : serverTasks) {
            task.counter++;
            if (task.counter >= task.tick) {
                task.action.accept(world);
                task.counter = 0;
            }
        }
    }

    public static class ClientTask {
        public final int tick;
        public final Consumer<MinecraftClient> action;
        public int counter = 0;

        public ClientTask(int tick, Consumer<MinecraftClient> action) {
            this.tick = tick;
            this.action = action;
        }
    }

    public static class ServerTask {
        public final int tick;
        public final Consumer<ServerWorld> action;
        public int counter = 0;

        public ServerTask(int tick, Consumer<ServerWorld> action) {
            this.tick = tick;
            this.action = action;
        }
    }
}