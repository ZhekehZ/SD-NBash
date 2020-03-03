package ru.itmo.softwaredesign.nbash.executor;

import java.io.*;
import java.util.List;
import java.util.Map;

import static ru.itmo.softwaredesign.nbash.executor.ExitCode.INTERRUPT_ERROR;
import static ru.itmo.softwaredesign.nbash.executor.ExitCode.IO_ERROR;

public class ExternalTaskImpl extends Task {

    private final ProcessBuilder processBuilder;

    public ExternalTaskImpl(List<String> args) {
        super(args);
        processBuilder = new ProcessBuilder(args);
    }

    public ExternalTaskImpl extendEnvironment(Map<String, String> environment) {
        processBuilder.environment().putAll(environment);
        return this;
    }

    @Override
    public void extentEnvironment(Map<String, String> environment) {
        processBuilder.environment().putAll(environment);
    }

    @Override
    public ExitCode execute() {
        int errorCode = -1;

        try {
            Process process = processBuilder.start();
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
            return IO_ERROR;
        } catch (InterruptedException e) {
            return INTERRUPT_ERROR;
        }

        return ExitCode.getExit(errorCode);
    }
}
