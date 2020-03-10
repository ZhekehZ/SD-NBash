package ru.itmo.softwaredesign.nbash.executor;


import com.sun.istack.internal.Nullable;
import ru.itmo.softwaredesign.nbash.executor.utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskFactory {

    // known internal commands
    private static final Map<String, TaskBuilder> internals = new HashMap<>();

    private final static Task notFoundTask = new Task() {
        @Override
        public ExitCode execute() {
            return ExitCode.COMMAND_NOT_FOUND;
        }
    };

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
    static Task getTask(List<String> args, Map<String, String> environment) {
        if (args.size() == 0) {
            return notFoundTask;
        }
        return getDirectTask(args.get(0), args.subList(1, args.size()), environment);
    }


    public static Task getComplexTask(List<List<String>> args, Map<String, String> environment) {
        return new ComplexTask(args, environment);
    }


    public static Task getDirectTask(String taskName, List<String> args, Map<String, String> environment) {
        Task task = null;
        if (internals.containsKey(taskName)) {
            task = internals.get(taskName).build(args);
        } else {
            args.add(taskName);
            task = new ExternalTaskImpl(args);
        }
        if (task != null) {
            task.extendEnvironment(environment);
        }
        return task;
    }

}
