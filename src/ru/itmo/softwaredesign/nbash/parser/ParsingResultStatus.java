package ru.itmo.softwaredesign.nbash.parser;

public enum ParsingResultStatus {
    SUCCESS,
    SINGLE_QUOTE_WAITING,
    DOUBLE_QUOTE_WAITING,
    PIPE_WAITING,
    FAIL
}