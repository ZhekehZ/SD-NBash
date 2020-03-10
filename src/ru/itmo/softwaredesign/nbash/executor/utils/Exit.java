package ru.itmo.softwaredesign.nbash.executor.utils;

import ru.itmo.softwaredesign.nbash.executor.ExitCode;
import ru.itmo.softwaredesign.nbash.executor.Task;
import ru.itmo.softwaredesign.nbash.executor.TaskBuilder;

import java.util.List;

import static ru.itmo.softwaredesign.nbash.executor.ExitCode.EXIT_SUCCESS;

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
            System.out.println(environment.getOrDefault("ExitMsg", ""));
            System.exit(0);
            return EXIT_SUCCESS;
        }
    }

}
