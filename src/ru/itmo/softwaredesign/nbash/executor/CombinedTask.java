package ru.itmo.softwaredesign.nbash.executor;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.itmo.softwaredesign.nbash.executor.ExitCode.EXIT_SUCCESS;

public class CombinedTask extends Task {

    private List<Task> tasks;

    public CombinedTask(List<List<String>> args, Map<String, String> environment) {
        super(null);

        tasks = args.stream().map(TaskFactory::getTask).collect(Collectors.toList());
        tasks.forEach(task -> task.extentEnvironment(environment));
    }

    @Override
    public ExitCode execute() {
        ExitCode code;

        StringBuffer inputOutput = new StringBuffer();

        if (stdIn != null) {
            inputOutput.append(stdIn.lines());
        }

        for (Task task : tasks) {
            BufferedReader reader = new BufferedReader(new StringReader(inputOutput.toString()));
            task.setStdIn(reader);

            if ((code = task.execute()) != EXIT_SUCCESS) {
                stdErr.append(task.getStdErr());
                return code;
            }

            inputOutput = task.getStdOut();
            stdErr.append(task.getStdErr());
        }

        stdOut.append(inputOutput);
        return EXIT_SUCCESS;
    }
}
