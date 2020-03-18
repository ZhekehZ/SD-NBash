package ru.itmo.softwaredesign.nbash.executor.utils;

import org.junit.Test;
import ru.itmo.softwaredesign.nbash.executor.Task;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static ru.itmo.softwaredesign.nbash.executor.ExitCode.EXIT_SUCCESS;

public class EchoTest {

    @Test
    public void testEcho() {
        Task catTask = (new Echo()).build(new ArrayList<>());
        String data = "";
        catTask.setStdIn(new BufferedReader(new StringReader(data)));
        assertEquals(EXIT_SUCCESS, catTask.execute());
    }


    @Test
    public void testEcho2Args() {
        List<String> args = new ArrayList<>();
        args.add("hello");
        args.add("world");

        Task catTask = (new Echo()).build(args);

        String expected = "hello world";
        assertEquals(EXIT_SUCCESS, catTask.execute());
        assertEquals(expected, catTask.getStdOut().toString());
    }
}