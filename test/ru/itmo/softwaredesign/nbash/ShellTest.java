package ru.itmo.softwaredesign.nbash;

import org.junit.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ShellTest {

    @Test
    public void testShell() throws IOException, InterruptedException {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        ProcessBuilder pb = new ProcessBuilder("java", "ru.itmo.softwaredesign.nbash.Main").directory(new File(path));
        Process ps = pb.start();
        BufferedWriter input = new BufferedWriter(new OutputStreamWriter(ps.getOutputStream()));

        String expected =
                "ƞBash> 1 1 4\n" +
                "ƞBash> ƞBash> ƞBash> ƞBash> exxiitt";

        input.write(
            "echo 123 | wc \n" +
            "a=ex\n" +
            "b=it\n" +
            "ExitMsg=exxiitt\n" +
            "$a$b\n" +
            "echo DO NOT PRINT THIS!"
        );
        Thread.sleep(2000);
        input.close();
        ps.waitFor();

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = ps.getInputStream().read(buffer)) != -1) {
            result.write(buffer, 0, length);
            if (length > 1) break;;
        }
        ps.destroy();

        assertEquals(expected.trim(), result.toString().trim());
    }
}