package com.me.cmitems;

import com.me.cmitems.utils.TickScheduler;

import java.util.function.Consumer;

import static com.me.cmitems.utils.TickScheduler.tasks;

public class Register {
    /**
     * Registers a tick event that executes an action every specified number of ticks.
     * @param tick The number of ticks after which the action should be executed.
     * @param action The action to execute. It receives a lambda to unregister itself.
     */
    public static void onTick(int tick, Consumer<String[]> action) {
        tasks.add(new TickScheduler.ScheduledTask(tick, done -> action.accept(new String[0])));
    }
}
