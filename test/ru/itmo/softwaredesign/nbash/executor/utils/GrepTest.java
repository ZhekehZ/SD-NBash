package ru.itmo.softwaredesign.nbash.executor.utils;

import org.junit.Test;
import ru.itmo.softwaredesign.nbash.executor.ExitCode;
import ru.itmo.softwaredesign.nbash.executor.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class GrepTest {

    private final String fileName = "test/ru/itmo/softwaredesign/nbash/executor/utils/fileGrep.txt";

    @Test
    public void testGrepTest1() {
        List<String> args = Arrays.asList("aaaa", fileName);
        String expected = "test AAAAAaaaaa test\n" +
                          "test aaaa test";

        Task grepTask = new Grep().build(args);
        grepTask.execute();

        assertEquals(expected, grepTask.getStdOut().toString());
    }

    @Test
    public void testGrepTest2() {
        List<String> args = Arrays.asList("-w", "aaaa", fileName);
        String expected = "test aaaa test";

        Task grepTask = new Grep().build(args);
        grepTask.execute();

        assertEquals(expected, grepTask.getStdOut().toString());
    }

    @Test
    public void testGrepTest3() {
        List<String> args = Arrays.asList("-i", "AAAA", fileName);
        String expected = "test AAAAAaaaaa test\n" +
                          "test aaaa test";

        Task grepTask = new Grep().build(args);
        grepTask.execute();

        assertEquals(expected, grepTask.getStdOut().toString());
    }


    @Test
    public void testGrepTest4() {
        List<String> args = Arrays.asList("-A", "3", "hello", fileName);
        String expected = "hello WorldWithSuffix\n" +
                          "line 1\n" +
                          "line 2\n" +
                          "line 3";

        Task grepTask = new Grep().build(args);
        grepTask.execute();

        assertEquals(expected, grepTask.getStdOut().toString());
    }

    @Test
    public void testGrepTest5() {
        List<String> args = Arrays.asList("-A", "kek", "hello", fileName);

        Task grepTask = new Grep().build(args);
        assertEquals(ExitCode.EXIT_FAILURE, grepTask.execute());
    }
}