package ru.itmo.softwaredesign.nbash.executor.utils;

import org.junit.Test;
import ru.itmo.softwaredesign.nbash.executor.ExitCode;
import ru.itmo.softwaredesign.nbash.executor.Task;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.itmo.softwaredesign.nbash.executor.ExitCode.EXIT_SUCCESS;


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
    public void testInvalidArgType() {
        List<String> args = Arrays.asList("-A", "kek", "hello", fileName);

        Task grepTask = new Grep().build(args);
        assertEquals(ExitCode.EXIT_FAILURE, grepTask.execute());
    }

    @Test
    public void testInvalidFlag() {
        List<String> args = Arrays.asList("-Z", "hello", fileName);

        Task grepTask = new Grep().build(args);
        assertEquals(ExitCode.EXIT_FAILURE, grepTask.execute());
    }

    @Test
    public void testAbsentArg() {
        List<String> args = Arrays.asList("-A", "hello", fileName);

        Task grepTask = new Grep().build(args);
        assertEquals(ExitCode.EXIT_FAILURE, grepTask.execute());
    }

    @Test
    public void testFlagArgument() {
        List<String> args = Arrays.asList("-i", "-1", "hello", fileName);

        Task grepTask = new Grep().build(args);
        assertEquals(ExitCode.EXIT_FAILURE, grepTask.execute());
    }

    @Test
    public void testStdIn() {
        List<String> args = Arrays.asList("-i", "-A", "1", "lol");

        String input = "hellol\n" +
                "line\n" +
                "other line\n" +
                "one more line\n" +
                "llllllLoLlllll\n" +
                "again\n" +
                "stop it\n" +
                "heh\n" +
                "lolO\n" +
                "OlOl\n" +
                "should be printed\n" +
                "end\n";

        String expected = "hellol\n" +
                "line\n" +
                "llllllLoLlllll\n" +
                "again\n" +
                "lolO\n" +
                "OlOl\n" +
                "should be printed";

        Task grepTask = new Grep().build(args);
        grepTask.setStdIn(new BufferedReader(new StringReader(input)));

        assertEquals(EXIT_SUCCESS, grepTask.execute());
        assertEquals(expected, grepTask.getStdOut().toString());
    }
}