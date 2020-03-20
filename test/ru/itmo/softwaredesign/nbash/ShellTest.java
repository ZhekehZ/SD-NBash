package ru.itmo.softwaredesign.nbash;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class ShellTest {

    @Test
    public void testShell() {
        String inputString =
                "echo 123 | wc \n" +
                "a=ex\n" +
                "b=it\n" +
                "ExitMsg=exxiitt\n" +
                "$a$b\n" +
                "echo DO NOT PRINT THIS!";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        System.setIn(new ByteArrayInputStream(inputString.getBytes()));
        System.setOut(new PrintStream(outputStream));

        Shell console = new Shell();
        console.run();

        String expected =
                "ƞBash> 1 1 4\n" +
                        "ƞBash> ƞBash> ƞBash> ƞBash> exxiitt";

        assertEquals(expected.trim(), outputStream.toString().trim());
    }
}