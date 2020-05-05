package ru.itmo.softwaredesign.nbash.parser;

enum TokenType {
    REGULAR_WORD,       // Word without the modifiers
    DOUBLE_QUOTED_WORD, // Word in double quotes
    SINGLE_QUOTED_WORD, // Word in single quotes
    PIPE_OPERATOR,      // Pipe (`|`) operator
    ASSIGN_OPERATOR,    // Assign (`=`) operator
    DELIMITER           // Blank symbols (\s+)
}