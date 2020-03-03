package ru.itmo.softwaredesign.nbash.executor.utils;

import org.junit.Test;
import ru.itmo.softwaredesign.nbash.executor.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static ru.itmo.softwaredesign.nbash.executor.ExitCode.EXIT_FAILURE;
import static ru.itmo.softwaredesign.nbash.executor.ExitCode.IO_ERROR;

public class WcTest {

    @Test
    public void testWc() {
        Task wcTask = (new Wc()).build(new ArrayList<>());
        String data = "hello\nworld!!";
        wcTask.setStdIn(new BufferedReader(new StringReader(data)));
        wcTask.execute();
        assertEquals("2 2 14", wcTask.getStdOut().toString());
        assertEquals("", wcTask.getStdErr().toString());
    }

    @Test
    public void testWcFile() throws IOException {
        String fileName = "test/ru/itmo/softwaredesign/nbash/executor/utils/file.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String expected = "2 2 13";

        List<String> data = new ArrayList<>();
        data.add(fileName);
        Task wcTask = (new Wc()).build(data);

        wcTask.setStdIn(reader);
        wcTask.execute();
        assertEquals(expected, wcTask.getStdOut().toString());
        assertEquals("", wcTask.getStdErr().toString());
    }

    @Test
    public void testWcInvalidArgs() {
        List<String> data = new ArrayList<>();
        data.add("fileName");
        data.add("error");
        Task wcTask = (new Cat()).build(data);

        assertEquals(EXIT_FAILURE, wcTask.execute());
        assertEquals("", wcTask.getStdOut().toString());
        assertEquals("Invalid argument number", wcTask.getStdErr().toString());
    }

    @Test
    public void testWcInvalidFile() {
        List<String> data = new ArrayList<>();
        data.add("NO_SUCH_FILE");
        Task wcTask = (new Wc()).build(data);

        assertEquals(IO_ERROR, wcTask.execute());
    }

}