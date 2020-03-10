package ru.itmo.softwaredesign.nbash.executor;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.itmo.softwaredesign.nbash.executor.ExitCode.EXIT_SUCCESS;

/**
 * Tasks combined by the pipe operator
 */
class ComplexTask extends Task {

    private List<Task> tasks;

    public ComplexTask(List<List<String>> args, Map<String, String> environment) {
        super(null);

        tasks = args.stream()
                    .map(a -> TaskFactory.getTask(a, environment))
                    .collect(Collectors.toList());
    }

    /**
     * Sequentially runs each command, waits for it to complete, and copies the
     * output of the previous command to the input of the next one
     * StdErr is formed as the sum of all errors
     *
     * @return {@link ExitCode}
     */
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
