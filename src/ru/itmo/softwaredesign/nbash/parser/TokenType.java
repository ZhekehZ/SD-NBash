package ru.itmo.softwaredesign.nbash.parser;

public enum TokenType {
    REGULAR_WORD,
    DOUBLE_QUOTED_WORD,
    SINGLE_QUOTED_WORD,
    PIPE_OPERATOR,
    ASSIGN_OPERATOR,
    DELIMITER
}