package ru.itmo.softwaredesign.nbash.executor.utils;

import ru.itmo.softwaredesign.nbash.executor.ExitCode;
import ru.itmo.softwaredesign.nbash.executor.Task;
import ru.itmo.softwaredesign.nbash.executor.TaskBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import static ru.itmo.softwaredesign.nbash.executor.ExitCode.*;

public class Wc implements TaskBuilder {

    @Override
    public Task build(List<String> args) {
        return new WcImpl(args);
    }


    private static class WcImpl extends Task {

        public WcImpl(List<String> args) {
            super(args);
        }

        @Override
        public ExitCode execute() {

            if (args.size() > 1) {
                stdErr.append("Invalid argument number");
                return EXIT_FAILURE;
            }

            try (BufferedReader br = openFileOrStdin(args.isEmpty() ? null : args.get(0))) {

                int words = 0, lines = 0, bytes = 0;

                String line;
                while ((line = br.readLine()) != null) {
                    lines++;
                    String[] aWords = line.split("\\s+");
                    words += aWords.length;
                    for (String word : aWords) {
                        if (word.length() == 0) {
                            words--;
                        }
                    }
                    bytes += line.length();
                }

                stdOut.append(lines)
                        .append(' ')
                        .append(words)
                        .append(' ')
                        .append(bytes + lines);

            } catch (IOException e) {
                return IO_ERROR;
            }

            return EXIT_SUCCESS;
        }
    }


}

