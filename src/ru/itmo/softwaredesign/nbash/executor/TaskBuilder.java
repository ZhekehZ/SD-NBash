package ru.itmo.softwaredesign.nbash.executor;

import java.util.List;

public interface TaskBuilder {

    Task build(List<String> args);

}
