package ru.itmo.softwaredesign.nbash.executor.utils;

import ru.itmo.softwaredesign.nbash.executor.ExitCode;
import ru.itmo.softwaredesign.nbash.executor.Task;
import ru.itmo.softwaredesign.nbash.executor.TaskBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static ru.itmo.softwaredesign.nbash.executor.ExitCode.*;

public class Cat implements TaskBuilder {

    @Override
    public Task build(List<String> args) {
        return new CatImpl(args);
    }
}

class CatImpl extends Task {

    public CatImpl(List<String> args) {
        super(args);
    }

    @Override
    public ExitCode execute() {

        if (args.size() > 1) {
            stdErr.append("Invalid argument number");
            return EXIT_FAILURE;
        }

        try (BufferedReader br = openFileOrStdin(args.isEmpty() ? null : args.get(0))) {
            stdOut.append(br.lines().collect(Collectors.joining("\n")));
        } catch (IOException e) {
            return IO_ERROR;
        }

        return EXIT_SUCCESS;
    }
}
