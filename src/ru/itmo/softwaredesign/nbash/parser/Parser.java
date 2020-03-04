package ru.itmo.softwaredesign.nbash.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.itmo.softwaredesign.nbash.parser.ParsingResultStatus.FAIL;
import static ru.itmo.softwaredesign.nbash.parser.ParsingResultStatus.SUCCESS;
import static ru.itmo.softwaredesign.nbash.parser.TokenType.ASSIGN_OPERATOR;
import static ru.itmo.softwaredesign.nbash.parser.TokenType.DELIMITER;
import static ru.itmo.softwaredesign.nbash.parser.Tokenizer.continueTokenization;
import static ru.itmo.softwaredesign.nbash.parser.Tokenizer.tokenizeString;

public class Parser {

    /**
     * Tokenizes string and applies substitutions
     *
     * @param string      -- input string
     * @param prefix      -- parsed string prefix
     * @param environment -- known variables
     * @return parsed string (including prefix)
     */
    public static ParsingResult parse(String string, ParsingResult prefix, Map<String, String> environment) {
        ParsingResult tokens = prefix == null ? tokenizeString(string)
                : continueTokenization(prefix, string);
        if (tokens.getStatus() != SUCCESS) {
            return tokens;
        }

        return Substitutor.substituteAll(tokens, environment);
    }


    /**
     * @param tokens -- result of string parsing
     * @return tokens string representation
     */
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

    /**
     * @param parsed -- parsed string
     * @return substitution A:=B if string has the form like `A=B`
     */
    public static Map.Entry<String, String> getSubstitutionIfAssignment(ParsingResult parsed) {
        if (parsed.getStatus() == FAIL) {
            return null;
        }
        List<Token> tokens = parsed.getTokens();
        if (tokens.size() == 3
                && tokens.get(0).getType() != DELIMITER
                && tokens.get(1).getType() == ASSIGN_OPERATOR
                && tokens.get(2).getType() != DELIMITER
        ) {
            return new HashMap.SimpleEntry<>(tokens.get(0).getStringRepr(), tokens.get(2).getStringRepr());
        }
        return null;
    }

}
