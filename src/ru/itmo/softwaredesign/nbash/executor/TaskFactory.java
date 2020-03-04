package ru.itmo.softwaredesign.nbash.executor;


import com.sun.istack.internal.Nullable;
import ru.itmo.softwaredesign.nbash.executor.utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskFactory {

    // known internal commands
    private static final Map<String, TaskBuilder> internals = new HashMap<>();

    static {
        // Internal command builders
        internals.put("echo", new Echo());
        internals.put("cat", new Cat());
        internals.put("wc", new Wc());
        internals.put("pwd", new Pwd());
        internals.put("exit", new Exit());
    }

    /**
     * @param args list of arguments where the first argument is the command name
     * @return null if args is empty
     * Internal command Task if args[0] in {@link TaskFactory#internals}
     * External command Task otherwise
     */
    @Nullable
    public static Task getTask(List<String> args) {
        if (args.size() == 0) {
            return new Task() {
                @Override
                public ExitCode execute() {
                    return ExitCode.COMMAND_NOT_FOUND;
                }
            };
        }

        String name = args.get(0);

        if (internals.containsKey(name)) {
            return internals.get(name).build(args.subList(1, args.size()));
        }

        return new ExternalTaskImpl(args);
    }

}
