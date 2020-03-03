package ru.itmo.softwaredesign.nbash.executor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Task {

    protected final List<String> args;
    protected final StringBuffer stdOut = new StringBuffer();
    protected final StringBuffer stdErr = new StringBuffer();
    protected BufferedReader stdIn = null;
    protected Map<String, String> environment = new HashMap<>();

    public Task(List<String> args) {
        this.args = args;
    }

    public void setStdIn(BufferedReader reader) {
        stdIn = reader;
    }

    public void extentEnvironment(Map<String, String> environment) {
        this.environment.putAll(environment);
    }

    public StringBuffer getStdOut() {
        return stdOut;
    }

    public StringBuffer getStdErr() {
        return stdErr;
    }

    public abstract ExitCode execute();

    protected BufferedReader openFileOrStdin(String file) throws FileNotFoundException {
        return file != null ? new BufferedReader(new FileReader(file)) : stdIn;
    }

}