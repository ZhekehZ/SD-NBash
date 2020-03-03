package ru.itmo.softwaredesign.nbash.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.itmo.softwaredesign.nbash.parser.ParsingResultStatus.SUCCESS;
import static ru.itmo.softwaredesign.nbash.parser.Tokenizer.continueTokenization;
import static ru.itmo.softwaredesign.nbash.parser.Tokenizer.tokenizeString;

public class Parser {

    public static ParsingResult parse(String string, ParsingResult prefix, Map<String, String> environment) {
        ParsingResult tokens = prefix == null ? tokenizeString(string)
                                              : continueTokenization(prefix, string);
        if (tokens.getStatus() != SUCCESS) {
            return tokens;
        }

        ParsingResult parsedString = Substitutor.substituteAll(tokens, environment);
        if (parsedString.getStatus() != SUCCESS) {
            return parsedString;
        }

        return parsedString;
    }

    public static List<List<String>> tokensToCommand(List<Token> tokens) {
        List<List<String>> pipedCommands = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        List<String> command = new ArrayList<>();
        for (Token token : tokens) {
            switch (token.getType()) {
                case PIPE_OPERATOR:
                    if (builder.length() > 0) {
                        command.add(builder.toString());
                        builder = new StringBuilder();
                    }
                    pipedCommands.add(command);
                    command = new ArrayList<>();
                    break;

                case DELIMITER:
                    if (builder.length() > 0) {
                        command.add(builder.toString());
                        builder = new StringBuilder();
                    }
                    break;

                default:
                    builder.append(token.getStringRepr());
            }
        }
        if (builder.length() > 0) {
            command.add(builder.toString());
        }
        if (command.size() > 0) {
            pipedCommands.add(command);
        }

        return pipedCommands;
    }

}
