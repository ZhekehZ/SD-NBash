package ru.itmo.softwaredesign.nbash.executor;

public enum ExitCode {
    EXIT_SUCCESS,
    IO_ERROR,
    INTERRUPT_ERROR,
    EXIT_FAILURE;

    public static ExitCode getExit(int code) {
        return code == 0 ? EXIT_SUCCESS : EXIT_FAILURE;
    }
}
