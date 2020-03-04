package ru.itmo.softwaredesign.nbash.parser;

import java.util.ArrayList;
import java.util.List;

import static ru.itmo.softwaredesign.nbash.parser.ParsingResultStatus.*;
import static ru.itmo.softwaredesign.nbash.parser.TokenType.*;

/**
 * Provides static functions for tokenization
 */
class Tokenizer {
    /**
     * @param string -- string for tokenization
     * @return {@link ParsingResult}
     */
    public static ParsingResult tokenizeString(String string) {
        List<Token> tokenList = new ArrayList<>();
        State state = State.REGULAR;
        int anchor = 0;

        for (int pos = 0; pos < string.length(); pos++) {
            char symbol = string.charAt(pos);

            switch (symbol) {
                case '\\':
                    pos++;
                    break;


                case '\"':
                case '\'':
                    State exceptState = symbol == '\"' ? State.SINGLE_QUOTES
                            : State.DOUBLE_QUOTES;
                    State symbolState = symbol == '\"' ? State.DOUBLE_QUOTES
                            : State.SINGLE_QUOTES;
                    TokenType tokenType = symbol == '\'' ? SINGLE_QUOTED_WORD
                            : DOUBLE_QUOTED_WORD;

                    if (state != exceptState) {
                        String tokenString = string.substring(anchor, pos);
                        anchor = pos + 1;
                        if (state == symbolState) {
                            tokenList.add(new Token(tokenString, tokenType));
                            state = State.REGULAR;
                        } else {
                            tokenList.addAll(splitWord(tokenString));
                            state = symbolState;
                        }
                    }
                    break;


                case '|':
                case '=':
                    TokenType operatorType = symbol == '|' ? PIPE_OPERATOR
                            : ASSIGN_OPERATOR;
                    if (state == State.REGULAR) {
                        tokenList.addAll(splitWord(string.substring(anchor, pos)));
                        anchor = pos + 1;
                        tokenList.add(new Token(null, operatorType));
                    }
                    break;

            }
        }

        List<Token> rest = new ArrayList<>();
        if (state == State.REGULAR) {
            rest = splitWord(string.substring(anchor));
        } else {
            rest.add(new Token(string.substring(anchor), REGULAR_WORD));
        }

        tokenList.addAll(rest);

        boolean lastIsPipe = false;
        for (int i = 1; i < 3 && i < tokenList.size(); i++) {
            Token tok = tokenList.get(tokenList.size() - i);
            lastIsPipe = tok.getType() == PIPE_OPERATOR;
            if (tok.getType() != DELIMITER || lastIsPipe) {
                break;
            }
        }

        if (rest.isEmpty()) {
            tokenList.add(new Token("", REGULAR_WORD));
        }

        if (state == State.SINGLE_QUOTES) {
            return new ParsingResult(tokenList, SINGLE_QUOTE_WAITING);
        }
        if (state == State.DOUBLE_QUOTES) {
            return new ParsingResult(tokenList, DOUBLE_QUOTE_WAITING);
        }
        if (lastIsPipe) {
            return new ParsingResult(tokenList, PIPE_WAITING);
        }
        return new ParsingResult(tokenList, SUCCESS);
    }


    /**
     * @param prev       earlier tokenized string prefix
     * @param nextString rest of the string
     * @return the result of tokenization , as if the entire string was
     * passed entirely to the {@link Tokenizer#tokenizeString(String)} function
     */
    public static ParsingResult continueTokenization(ParsingResult prev, String nextString) {
        List<Token> tokens;
        ParsingResultStatus status = prev.getStatus();

        if (status == FAIL) {
            return prev;
        }

        tokens = prev.getTokens();

        Token lastToken = tokens.remove(tokens.size() - 1);
        nextString = lastToken.getStringRepr() + nextString;

        if (status == DOUBLE_QUOTE_WAITING) {
            nextString = '\"' + nextString;
        } else if (status == SINGLE_QUOTE_WAITING) {
            nextString = '\'' + nextString;
        }

        ParsingResult parsedNextString = tokenizeString(nextString);
        if (parsedNextString.getStatus() == FAIL) {
            return parsedNextString;
        } else {
            tokens.addAll(parsedNextString.getTokens());
            return new ParsingResult(tokens, parsedNextString.getStatus());
        }
    }

    /**
     * Converts tokens into string and tokenize it
     *
     * @param tokens -- list of tokens
     * @return retokenized string
     */
    public static ParsingResult reTokenize(List<Token> tokens) {
        if (tokens == null) {
            return new ParsingResult(null, FAIL);
        }
        StringBuilder builder = new StringBuilder();
        for (Token token : tokens) {
            builder.append(token.getStringRepr());
        }

        return tokenizeString(builder.toString());
    }


    /**
     * @param string -- A string of regular
     *               (in this sense : {@link TokenType#REGULAR_WORD}) words
     * @return list of regular words splitted by delimiter {@link TokenType#DELIMITER}
     */
    private static List<Token> splitWord(String string) {
        List<Token> words = new ArrayList<>();
        for (String word : string.split("\\s+")) {
            if (word.length() > 0) {
                words.add(new Token(word, REGULAR_WORD));
                words.add(new Token(null, DELIMITER));
            }
        }

        if (string.length() > 0 && Character.isWhitespace(string.charAt(0))) {
            words.add(0, new Token(null, DELIMITER));
        }
        if (words.size() > 0 && !Character.isWhitespace(string.charAt(string.length() - 1))) {
            words.remove(words.size() - 1);
        }

        return words;
    }


    /**
     * Tokenizer states
     */
    private enum State {
        REGULAR,        // Reading regular word
        DOUBLE_QUOTES,  // Reading word in double quotes
        SINGLE_QUOTES   // Reading word in single quotes
    }
}
