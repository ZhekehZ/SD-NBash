package ru.itmo.softwaredesign.nbash.parser;

public class Token {

    private final String data;
    private final TokenType type;

    public TokenType getType() {
        return type;
    }

    public Token(String data, TokenType type) {
        this.data = data;
        this.type = type;
    }

    /**
     * @return token string representation
     */
    public String getStringRepr() {
        switch (type) {
            case REGULAR_WORD:
            case DOUBLE_QUOTED_WORD:
            case SINGLE_QUOTED_WORD:
                return data;
            case PIPE_OPERATOR:
                return "|";
            case ASSIGN_OPERATOR:
                return "=";
            case DELIMITER:
                return " ";
            default:
                return null;
        }
    }

}

