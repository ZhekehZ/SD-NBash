package ru.itmo.softwaredesign.nbash.executor.utils;

import ru.itmo.softwaredesign.nbash.executor.ExitCode;
import ru.itmo.softwaredesign.nbash.executor.Task;
import ru.itmo.softwaredesign.nbash.executor.TaskBuilder;

import java.util.List;

import static ru.itmo.softwaredesign.nbash.executor.ExitCode.*;

public class Exit implements TaskBuilder {

    public Task build(List<String> args) {
        return new ExitImpl(args);
    }

    private static class ExitImpl extends Task {
        protected ExitImpl(List<String> args) {
            super(args);
        }

        @Override
        public ExitCode execute() {
            if (args.size() > 1) {
                stdErr.append("Invalid argument number");
                return EXIT_FAILURE;
            }
            if (!args.isEmpty() && args.get(0).equals("1")) {
                return EXIT_QUIT_FAIL;
            }
            System.out.println(environment.getOrDefault("ExitMsg", ""));
            return EXIT_QUIT_SUCCESS;
        }
    }

}
