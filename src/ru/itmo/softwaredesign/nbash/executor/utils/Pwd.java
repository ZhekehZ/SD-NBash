package ru.itmo.softwaredesign.nbash.executor.utils;

import ru.itmo.softwaredesign.nbash.executor.ExitCode;
import ru.itmo.softwaredesign.nbash.executor.Task;
import ru.itmo.softwaredesign.nbash.executor.TaskBuilder;

import java.nio.file.Paths;
import java.util.List;

import static ru.itmo.softwaredesign.nbash.executor.ExitCode.EXIT_SUCCESS;

public class Pwd implements TaskBuilder {

    public Task build(List<String> args) {
        return new PwdImpl(args);
    }

}

class PwdImpl extends Task {
    protected PwdImpl(List<String> args) {
        super(args);
    }

    @Override
    public ExitCode execute() {
        stdOut.append(Paths.get("").toAbsolutePath().toString());
        if (!args.isEmpty()) {
            stdErr.append("pwd: args ignored")
                  .append(String.join(", ", args.toString()));
        }
        return EXIT_SUCCESS;
    }
}