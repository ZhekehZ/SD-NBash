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
                ParsedOptions parsedOpts = ParsedOptions.of(args);

                Pattern pattern = parsedOpts.ignoreCase ? Pattern.compile(parsedOpts.pattern, Pattern.CASE_INSENSITIVE)
                        : Pattern.compile(parsedOpts.pattern);

                BufferedReader reader;
                if (parsedOpts.fileName != null) {
                    reader = new BufferedReader(new FileReader(parsedOpts.fileName));
                } else {
                    reader = new BufferedReader(stdIn);
                }

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


        private enum GrepOptions {
            IGNORE_CASE("i", "ignore-case", false,
                    "Ignore case distinctions, so that characters that differ only in case match each other."
            ),
            AFTER_CONTEXT(
                    "A", "after-context", true,
                    "Print  <arg>  lines  of  trailing  context  after matching lines."
            ),
            WORD_REGEXP(
                    "w", "word-regexp", false,
                    " Select only those lines containing matches that form whole words. "
            );

            private final String opt;
            private final String longOpt;
            private final boolean hasArg;
            private final String description;

            GrepOptions(String opt, String longOpt, boolean hasArg, String description) {
                this.opt = opt;
                this.longOpt = longOpt;
                this.hasArg = hasArg;
                this.description = description;
            }

            public static void registerAll(Options opt) {
                for (GrepOptions option : GrepOptions.values()) {
                    opt.addOption(option.opt, option.longOpt, option.hasArg, option.description);
                }
            }
        }


        private static class ParsedOptions {
            private static final CommandLineParser parser = new DefaultParser();
            private static final HelpFormatter formatter = new HelpFormatter();
            private static final Options options = new Options();

            static {
                GrepOptions.registerAll(options);
            }

            final boolean ignoreCase;
            final int afterContext;
            final boolean wordRegexp;
            final String pattern;
            final String fileName;

            public ParsedOptions(boolean ignoreCase, int afterContext, boolean wordRegexp, String pattern, String fileName) {
                this.ignoreCase = ignoreCase;
                this.afterContext = afterContext;
                this.wordRegexp = wordRegexp;
                this.pattern = pattern;
                this.fileName = fileName;
            }

            private static ParsedOptions of(List<String> args) throws ParseException {
                String[] raw_args = new String[args.size()];
                args.toArray(raw_args);
                CommandLine cmd = parser.parse(options, raw_args);

                if (cmd.getArgs().length != 1 && cmd.getArgs().length != 2) {
                    throw new ParseException("Invalid arguments");
                }

                boolean ignoreCase = cmd.hasOption(GrepOptions.IGNORE_CASE.opt);
                int afterContext = 0;
                boolean wordRegexp = cmd.hasOption(GrepOptions.WORD_REGEXP.opt);
                String pattern = cmd.getArgs()[0];
                String fileName = cmd.getArgs().length > 1 ? cmd.getArgs()[1] : null;

                if (cmd.hasOption(GrepOptions.AFTER_CONTEXT.opt)) {
                    if (cmd.getOptionValue(GrepOptions.AFTER_CONTEXT.opt).matches("\\d+")) {
                        afterContext = Integer.parseInt(cmd.getOptionValue(GrepOptions.AFTER_CONTEXT.opt));
                    } else {
                        throw new ParseException("Invalid value for -" + GrepOptions.AFTER_CONTEXT.opt + " option");
                    }
                }

                return new ParsedOptions(ignoreCase, afterContext, wordRegexp, pattern, fileName);
            }

            public static void printHelp() {
                formatter.printHelp(" grep [OPTIONS] PATTERN FILE", options);
            }

        }

    }


}