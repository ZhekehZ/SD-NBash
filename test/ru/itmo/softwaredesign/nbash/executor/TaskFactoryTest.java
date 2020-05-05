package ru.itmo.softwaredesign.nbash.executor;

import org.junit.Test;
import ru.itmo.softwaredesign.nbash.executor.utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class TaskFactoryTest {

    @Test
    public void testTaskFactory() {
        String[] commands = {"cat", "wc", "echo", "exit", "pwd"};
        TaskBuilder[] impls = {new Cat(), new Wc(), new Echo(), new Exit(), new Pwd()};
        for (int i = 0; i < commands.length; i++) {
            testCommand(commands[i], impls[i]);
        }
    }

    private void testCommand(String name, TaskBuilder obj) {
        List<String> args = new ArrayList<>();
        args.add(name);
        Task t = TaskFactory.getTask(args, new HashMap<>());
        assertNotNull(t);
        assertEquals(obj.build(new ArrayList<>()).getClass(), t.getClass());
    }
}