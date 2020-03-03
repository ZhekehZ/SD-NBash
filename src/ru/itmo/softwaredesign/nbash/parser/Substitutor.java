package ru.itmo.softwaredesign.nbash.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.itmo.softwaredesign.nbash.parser.ParsingResultStatus.FAIL;
import static ru.itmo.softwaredesign.nbash.parser.ParsingResultStatus.SUCCESS;
import static ru.itmo.softwaredesign.nbash.parser.TokenType.REGULAR_WORD;

public class Substitutor {

    private static final int MAX_ITER = 8;

    public static ParsingResult substituteAll(ParsingResult tokens, Map<String, String> environment) {
        if (tokens == null || environment == null) {
            return new ParsingResult(null, FAIL);
        }

        List<Token> parsed = null;

        if (tokens.getStatus() != SUCCESS) {
            return tokens;
        }

        parsed = new ArrayList<>();

        for (Token token : tokens.getTokens()) {
            switch (token.getType()) {
                case REGULAR_WORD:
                case DOUBLE_QUOTED_WORD:
                    String word = Substitutor.substitute(token.getStringRepr(), environment);
                    parsed.add(new Token(word, REGULAR_WORD));
                    break;

                case SINGLE_QUOTED_WORD:
                case PIPE_OPERATOR:
                case DELIMITER:
                case ASSIGN_OPERATOR:
                    parsed.add(token);
                    break;

                default:
                    return new ParsingResult(null, FAIL);
            }
        }

        return new ParsingResult(parsed, SUCCESS);
    }

    public static String substitute(String string, Map<String, String> environment) {
        StringBuilder builder = new StringBuilder();

        int prev = 0;
        int pos = string.indexOf('$');
        while (pos != -1) {
            builder.append(string, prev, pos);

            if (!(pos > 0 && string.charAt(pos - 1) == '\\')) {
                int endPos = pos + 1;
                while (endPos < string.length() && Character.isLetterOrDigit(string.charAt(endPos))) {
                    endPos++;
                }

                String key = string.substring(pos + 1, endPos);
                builder.append(environment.getOrDefault(key, ""));
                pos = endPos;
            }
            prev = pos;
            pos = string.indexOf('$', pos);
        }
        builder.append(string.substring(prev));

        return builder.toString();
    }

}