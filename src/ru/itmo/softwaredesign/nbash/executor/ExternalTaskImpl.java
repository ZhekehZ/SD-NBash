package ru.itmo.softwaredesign.nbash.executor;

import java.io.*;
import java.util.List;
import java.util.Map;

import static ru.itmo.softwaredesign.nbash.executor.ExitCode.COMMAND_NOT_FOUND;
import static ru.itmo.softwaredesign.nbash.executor.ExitCode.INTERRUPT_ERROR;

/**
 * Task that references an external program
 */
class ExternalTaskImpl extends Task {

    private final ProcessBuilder externalCommand;

    public ExternalTaskImpl(List<String> args) {
        super(args);
        externalCommand = new ProcessBuilder(args);
    }

    @Override
    public void extendEnvironment(Map<String, String> environment) {
        externalCommand.environment().putAll(environment);
    }

    /**
     * Runs external program:
     * 1. starts new process
     * 2. copies stdIn to its standard input
     * 3. closes standard input
     * 4. waits for the program to finish
     *
     * @return {@link ExitCode}
     */
    @Override
    public ExitCode execute() {
        int errorCode;

        try {
            Process process = externalCommand.start();
            BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            if (stdIn != null) {
                BufferedWriter input = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

                String line = null;
                while ((line = stdIn.readLine()) != null) {
                    input.write(line);
                }
                input.flush();
                input.close();
            }

            errorCode = process.waitFor();

            String line = null;
            while (outReader.ready() && (line = outReader.readLine()) != null) {
                if (stdOut.length() > 0) {
                    stdOut.append('\n');
                }
                stdOut.append(line);
            }
            while (errReader.ready() && (line = errReader.readLine()) != null) {
                if (stdErr.length() > 0) {
                    stdErr.append('\n');
                }
                stdErr.append(line);
            }

        } catch (IOException e) {
            return COMMAND_NOT_FOUND;
        } catch (InterruptedException e) {
            return INTERRUPT_ERROR;
        }

        return ExitCode.getExit(errorCode);
    }
}
