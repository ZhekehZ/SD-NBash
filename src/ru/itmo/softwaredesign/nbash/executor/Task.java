package ru.itmo.softwaredesign.nbash.executor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Task {
    protected final List<String> args;                           // Call arguments
    protected final StringBuffer stdOut = new StringBuffer();
    protected final StringBuffer stdErr = new StringBuffer();
    protected final Map<String, String> environment = new HashMap<>(); // Local environment
    protected BufferedReader stdIn = null;

    public Task(List<String> args) {
        this.args = args;
    }

    public Task() {
        args = new ArrayList<>();
    }

    public void setStdIn(BufferedReader reader) {
        stdIn = reader;
    }

    public void extendEnvironment(Map<String, String> environment) {
        this.environment.putAll(environment);
    }

    public StringBuffer getStdOut() {
        return stdOut;
    }

    public StringBuffer getStdErr() {
        return stdErr;
    }

    public abstract ExitCode execute();

    /**
     * @param file -- path to file
     * @return Reader from file if file is not null
     * Reader form standard input otherwise
     * @throws FileNotFoundException
     */
    protected BufferedReader openFileOrStdin(String file) throws FileNotFoundException {
        return file != null ? new BufferedReader(new FileReader(file)) : stdIn;
    }

}