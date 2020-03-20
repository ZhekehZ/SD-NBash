package ru.itmo.softwaredesign.nbash.executor.utils;


import org.apache.commons.cli.*;
import ru.itmo.softwaredesign.nbash.executor.ExitCode;
import ru.itmo.softwaredesign.nbash.executor.Task;
import ru.itmo.softwaredesign.nbash.executor.TaskBuilder;

import java.io.BufferedReader;
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

        protected GrepImpl(List<String> args) {
            super(args);
        }

        @Override
        public ExitCode execute() {
            try {
                ParsedOptions parsedOpts = new ParsedOptions(args);

                Pattern pattern = parsedOpts.ignoreCase ? Pattern.compile(parsedOpts.pattern, Pattern.CASE_INSENSITIVE)
                        : Pattern.compile(parsedOpts.pattern);

                BufferedReader reader = new BufferedReader(new FileReader(parsedOpts.fileName));
                int linesFromMatched = parsedOpts.afterContext;

                StringJoiner localBuffer = new StringJoiner("\n");

                String line;
                while ((line = reader.readLine()) != null) {
                    boolean matched = false;
                    if (parsedOpts.wordRegexp) {
                        for (String word : line.split("\\s+")) {
                            matched |= pattern.matcher(word).matches();
                        }
                    } else {
                        matched = pattern.matcher(line).find();
                    }
                    if (matched || linesFromMatched < parsedOpts.afterContext) {
                        localBuffer.add(line);
                    }
                    linesFromMatched = matched ? 0 : linesFromMatched + 1;
                }

                stdOut.append(localBuffer.toString());

            } catch (ParseException e) {
                ParsedOptions.printHelp();
                return EXIT_FAILURE;
            } catch (IOException e) {
                return EXIT_FAILURE;
            }

            return EXIT_SUCCESS;
        }

        private static class ParsedOptions {
            private static final CommandLineParser parser = new DefaultParser();
            private static final HelpFormatter formatter = new HelpFormatter();
            private static final Options options = new Options();

            static {
                options.addOption("i", "ignore-case", false,
                        "Ignore case distinctions, so that characters that " +
                                "differ only in case match each other.");
                options.addOption("A", "after-context", true,
                        "Print  <arg>  lines  of  trailing  context  after matching lines.");
                options.addOption("w", "word-regexp", false,
                        " Select only those lines containing matches that form whole words. ");
            }

            final boolean ignoreCase;
            final int afterContext;
            final boolean wordRegexp;
            final String pattern;
            final String fileName;

            ParsedOptions(List<String> args) throws ParseException {
                String[] raw_args = new String[args.size()];
                args.toArray(raw_args);
                CommandLine cmd = parser.parse(options, raw_args);

                if (cmd.getArgs().length != 2) {
                    throw new ParseException("Invalid arguments");
                }

                pattern = cmd.getArgs()[0];
                fileName = cmd.getArgs()[1];

                ignoreCase = cmd.hasOption("i");
                if (cmd.hasOption("A")) {
                    if (cmd.getOptionValue("A").matches("\\d+")) {
                        afterContext = Integer.parseInt(cmd.getOptionValue("A"));
                    } else {
                        throw new ParseException("Invalid value for -A option");
                    }
                } else {
                    afterContext = 0;
                }
                wordRegexp = cmd.hasOption("w");
            }

            public static void printHelp() {
                formatter.printHelp(" grep [OPTIONS] PATTERN FILE", options);
            }
        }
    }


}