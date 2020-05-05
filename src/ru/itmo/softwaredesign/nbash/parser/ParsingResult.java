package ru.itmo.softwaredesign.nbash.parser;

import java.util.List;

public class ParsingResult {
    private final List<Token> tokens;
    private final ParsingResultStatus status;

    /**
     * @param tokens -- list of tokens or null if parsing failed
     * @param status -- {@link ParsingResultStatus}
     */
    public ParsingResult(List<Token> tokens, ParsingResultStatus status) {
        this.tokens = tokens;
        this.status = status;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public ParsingResultStatus getStatus() {
        return status;
    }

    /**
     * @return true if the input is not finished yet
     */
    public boolean isWaitingStatus() {
        switch (status) {
            case SUCCESS:
            case FAIL:
                return false;

            case DOUBLE_QUOTE_WAITING:
            case SINGLE_QUOTE_WAITING:
            case PIPE_WAITING:
            default:
                return true;
        }
    }

}
