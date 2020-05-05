package ru.itmo.softwaredesign.nbash.executor.utils;

import org.junit.Test;
import ru.itmo.softwaredesign.nbash.executor.Task;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static ru.itmo.softwaredesign.nbash.executor.ExitCode.EXIT_FAILURE;
import static ru.itmo.softwaredesign.nbash.executor.ExitCode.IO_ERROR;

public class CatTest {

    @Test
    public void testCat() {
        Task catTask = (new Cat()).build(new ArrayList<>());
        String data = "hello\nworld!!";
        catTask.setStdIn(new BufferedReader(new StringReader(data)));
        catTask.execute();
        assertEquals(data, catTask.getStdOut().toString());
        assertEquals("", catTask.getStdErr().toString());
    }

    @Test
    public void testCatFile() throws IOException {
        String fileName = "test/ru/itmo/softwaredesign/nbash/executor/utils/file.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        File f = new File(fileName);
        byte[] bytes = Files.readAllBytes(f.toPath());
        String expected = new String(bytes, StandardCharsets.UTF_8);

        List<String> data = new ArrayList<>();
        data.add(fileName);
        Task catTask = (new Cat()).build(data);

        catTask.setStdIn(reader);
        catTask.execute();
        assertEquals(expected, catTask.getStdOut().toString());
        assertEquals("", catTask.getStdErr().toString());
    }

    @Test
    public void testCatInvalidArgs() {
        List<String> data = new ArrayList<>();
        data.add("fileName");
        data.add("error");
        Task catTask = (new Cat()).build(data);

        assertEquals(EXIT_FAILURE, catTask.execute());
        assertEquals("", catTask.getStdOut().toString());
        assertEquals("Invalid argument number", catTask.getStdErr().toString());
    }

    @Test
    public void testCatInvalidFile() {
        List<String> data = new ArrayList<>();
        data.add("NO_SUCH_FILE");
        Task catTask = (new Cat()).build(data);

        assertEquals(IO_ERROR, catTask.execute());
    }

}