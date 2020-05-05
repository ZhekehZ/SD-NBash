package ru.itmo.softwaredesign.nbash.executor;

public enum ExitCode {
    EXIT_SUCCESS,      // Successful run
    IO_ERROR,          // Io error
    INTERRUPT_ERROR,   // Interrupt error
    EXIT_FAILURE,      // Bad exit code
    COMMAND_NOT_FOUND, // Command not found exception
    EXIT_QUIT_SUCCESS, // Terminate with success
    EXIT_QUIT_FAIL;    // Terminate with an error


    /**
     * @param code -- program exit code
     * @return code representation
     */
    public static ExitCode getExit(int code) {
        return code == 0 ? EXIT_SUCCESS : EXIT_FAILURE;
    }
}
