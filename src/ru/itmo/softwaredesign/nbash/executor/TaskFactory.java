package ru.itmo.softwaredesign.nbash.executor;


import ru.itmo.softwaredesign.nbash.executor.utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskFactory {

    private static final Map<String, TaskBuilder> internals = new HashMap<>();

    static {
        internals.put("echo", new Echo());
        internals.put("cat", new Cat());
        internals.put("wc", new Wc());
        internals.put("pwd", new Pwd());
        internals.put("exit", new Exit());
    }

    public static Task getTask(List<String> args) {
        if (args.size() == 0) {
            return null;
        }

        String name = args.get(0);

        if (internals.containsKey(name)) {
            return internals.get(name).build(args.subList(1, args.size()));
        }

        return new ExternalTaskImpl(args);
    }

}
