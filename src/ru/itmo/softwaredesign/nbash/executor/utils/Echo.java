package ru.itmo.softwaredesign.nbash.executor.utils;

import ru.itmo.softwaredesign.nbash.executor.ExitCode;
import ru.itmo.softwaredesign.nbash.executor.Task;
import ru.itmo.softwaredesign.nbash.executor.TaskBuilder;

import java.util.List;

import static ru.itmo.softwaredesign.nbash.executor.ExitCode.EXIT_SUCCESS;

public class Echo implements TaskBuilder {

    public Task build(List<String> args) {
        return new EchoImpl(args);
    }

    private static class EchoImpl extends Task {
        protected EchoImpl(List<String> args) {
            super(args);
        }

        @Override
        public ExitCode execute() {
            stdOut.append(String.join(" ", args));
            return EXIT_SUCCESS;
        }
    }

}

