package ru.itmo.softwaredesign.nbash.parser;

class Token {

    private final String data;
    private final TokenType type;

    /**
     * @param data -- Token string representation
     *             or null for operators or delimiter
     * @param type -- {@link TokenType}
     */
    public Token(String data, TokenType type) {
        this.data = data;
        this.type = type;
    }

    public TokenType getType() {
        return type;
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

