package ru.itmo.softwaredesign.nbash;

import ru.itmo.softwaredesign.nbash.executor.ExitCode;
import ru.itmo.softwaredesign.nbash.executor.Task;
import ru.itmo.softwaredesign.nbash.executor.TaskFactory;
import ru.itmo.softwaredesign.nbash.parser.Parser;
import ru.itmo.softwaredesign.nbash.parser.ParsingResult;
import ru.itmo.softwaredesign.nbash.parser.ParsingResultStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.itmo.softwaredesign.nbash.executor.ExitCode.*;
import static ru.itmo.softwaredesign.nbash.parser.ParsingResultStatus.SUCCESS;


/**
 * Shell class
 */
public class Shell {

    // Colors for output
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BRIGHT_RED = "\u001B[91m";
    private static final String ANSI_BRIGHT_PURPLE = "\u001B[95m";
    private final Map<String, String> environment = new HashMap<>();
    private final BufferedReader console;

    {
        console = new BufferedReader(new InputStreamReader(System.in));

        // Default environment
        environment.put("ExitMsg", ANSI_BRIGHT_PURPLE + "Bye!" + ANSI_RESET);
    }

    /**
     * Main shell loop. Exit when Ctrl+D is entered or `exit` command calls
     */
    public void run() {

        while (true) {
            System.out.print(getPrompt());

            String rawCommand = readNextLine();
            if (rawCommand == null) {
                rawCommand = "exit";
            }

            ParsingResult parsingResult = Parser.parse(rawCommand, null, environment);
            while (parsingResult.isWaitingStatus()) {
                System.out.print(getWaitingMsg(parsingResult.getStatus()));
                rawCommand = readNextLine();
                parsingResult = Parser.parse(rawCommand, parsingResult, environment);
            }

            if (parsingResult.getStatus() == SUCCESS) {
                if (processIfAssignment(parsingResult)) {
                    continue;
                }

                List<List<String>> command = Parser.tokensToCommand(parsingResult.getTokens());

                Task ext = TaskFactory.getComplexTask(command, environment);
                ExitCode code = ext.execute();

                if (code != EXIT_SUCCESS && code != EXIT_QUIT_SUCCESS) {
                    System.out.println(ANSI_BRIGHT_PURPLE
                            + "Execution failed ("
                            + code.toString()
                            + ")" + ANSI_RESET);
                }

                String output = String.join("\n", ext.getStdOut());
                String stderr = String.join("\n", ext.getStdErr());
                if (!output.isEmpty()) {
                    System.out.println(ext.getStdOut());
                }
                if (!stderr.isEmpty()) {
                    System.out.println(ANSI_BRIGHT_RED + ext.getStdErr() + ANSI_RESET);
                }

                if (code == EXIT_QUIT_SUCCESS || code == EXIT_QUIT_FAIL) {
                    break;
                }
            } else {
                System.out.println(ANSI_BRIGHT_RED + "Parsing error!" + ANSI_RESET);
            }

        }
    }


    /**
     * Reads next line from standard input.
     * Returns `exit 1` call when read fails
     *
     * @return line from stdin
     */
    private String readNextLine() {
        String command = null;
        try {
            command = console.readLine();
        } catch (IOException e) {
            return "exit 1";
        }
        return command;
    }


    /**
     * @return prompt string representation
     */
    String getPrompt() {
        return "\u019EBash> ";
    }


    /**
     * Extends context if an operation is an assignment
     *
     * @param parsed -- paring result
     * @return true if operation is assignment
     */
    private boolean processIfAssignment(ParsingResult parsed) {
        Map.Entry<String, String> entry = Parser.getSubstitutionIfAssignment(parsed);
        if (entry != null) {
            environment.put(entry.getKey(), entry.getValue());
            return true;
        }
        return false;
    }


    /**
     * Returns a string with information about the expected character
     * when the quote or pipe is not closed
     *
     * @param status -- status after parsing
     * @return prompt string representation
     */
    String getWaitingMsg(ParsingResultStatus status) {
        switch (status) {
            case PIPE_WAITING:
                return "pipe> ";
            case SINGLE_QUOTE_WAITING:
                return "quote> ";
            case DOUBLE_QUOTE_WAITING:
                return "dquote> ";
            default:
                return "";
        }
    }

}
