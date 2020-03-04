package ru.itmo.softwaredesign.nbash.parser;

public enum ParsingResultStatus {
    SUCCESS,                // Successful parsing
    SINGLE_QUOTE_WAITING,   // There is an unclosed single quote
    DOUBLE_QUOTE_WAITING,   // There is an unclosed double quote
    PIPE_WAITING,           // There is nothing to the right of pipe
    FAIL                    // Parsing failed
}