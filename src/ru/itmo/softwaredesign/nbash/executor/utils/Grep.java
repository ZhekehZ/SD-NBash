package ru.itmo.softwaredesign.nbash.executor.utils;


import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import ru.itmo.softwaredesign.nbash.executor.ExitCode;
import ru.itmo.softwaredesign.nbash.executor.Task;
import ru.itmo.softwaredesign.nbash.executor.TaskBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import static ru.itmo.softwaredesign.nbash.executor.ExitCode.EXIT_FAILURE;
import static ru.itmo.softwaredesign.nbash.executor.ExitCode.EXIT_SUCCESS;

public class Grep implements TaskBuilder {

    public Task build(List<String> args) {
        return new GrepImpl(args);
    }


    private static class GrepImpl extends Task {

        private static class CommandLine {
            @Option(name = "-i", usage = "ignore-case")
            public boolean ignoreCase = false;

            @Option(name = "-w", usage = "word-regexp")
            public boolean wordRegexp = false;

            @Option(name = "-A", usage = "after-context")
            public int lines = 0;

            @Argument(index = 0, required = true, usage = "pattern")
            public String pattern;

            @Argument(index = 1, required = true, usage = "file")
            public String file = null;

            private CommandLine() {}

            public static CommandLine parseArgs(List<String> args) {
                CommandLine parsed = new CommandLine();
                CmdLineParser parser = new CmdLineParser(parsed);

                try {
                    parser.parseArgument(args);
                } catch (CmdLineException e) {
                    return null;
                }

                return parsed;
            }
        }

        protected GrepImpl(List<String> args) {
            super(args);
        }

        @Override
        public ExitCode execute() {
            CommandLine parsedArgs = CommandLine.parseArgs(args);
            if (parsedArgs == null) {
                return EXIT_FAILURE;
            }

            if (parsedArgs.file == null) {
                return EXIT_FAILURE;
            }

            try {
                Pattern pattern = parsedArgs.ignoreCase ? Pattern.compile(parsedArgs.pattern.toLowerCase(),
                        Pattern.CASE_INSENSITIVE)
                        : Pattern.compile(parsedArgs.pattern);

                BufferedReader reader = new BufferedReader(new FileReader(parsedArgs.file));
                int linesFromMatched = 0;

                StringJoiner localBuffer = new StringJoiner("\n");

                String line = null;
                while ((line = reader.readLine()) != null) {
                    boolean matched = false;
                    if (parsedArgs.wordRegexp) {
                        for (String word : line.split("\\s+")) {
                            matched |= pattern.matcher(word).matches();
                        }
                    } else {
                        matched = pattern.matcher(line).find();
                    }
                    if (matched || linesFromMatched < parsedArgs.lines) {
                        localBuffer.add(line);
                    }
                    linesFromMatched = matched ? 0 : linesFromMatched + 1;
                }

                stdOut.append(localBuffer.toString());

            } catch (FileNotFoundException e) {
                return EXIT_FAILURE;
            } catch (IOException e) {
                return EXIT_FAILURE;
            }

            return EXIT_SUCCESS;
        }
    }


}