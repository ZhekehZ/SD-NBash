package ru.itmo.softwaredesign.nbash.parser;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SubstitutorTest {

    private final TokenType TTR = TokenType.REGULAR_WORD;
    private final TokenType TTP = TokenType.PIPE_OPERATOR;
    private final TokenType DEL = TokenType.DELIMITER;
    Map<String, String> env;

    @Before
    public void setUp() throws Exception {
        env = new HashMap<>();
    }

    @Test
    public void testSubstituteUnexpectedArg() {
        assertEquals(ParsingResultStatus.FAIL, Substitutor.substituteAll(null, env).getStatus());
        assertEquals(ParsingResultStatus.FAIL, Substitutor.substituteAll(
                new ParsingResult(new ArrayList<>(), ParsingResultStatus.SUCCESS),
                null).getStatus());
    }

    @Test
    public void testSubstituteAll() {
        String str = "$a$b";
        String[] expectedStr = {"exit"};
        TokenType[] expectedType = {TTR};

        env.put("a", "ex");
        env.put("b", "it");
        testSubstituteHelper(str, expectedType, expectedStr);
    }

    @Test
    public void testSubstituteAllWithPipes() {
        String str = "cat $file | wc -c";
        String[] expectedStr = {"cat", null, "my_file.txt", null, null, null, "wc", null, "-c"};
        TokenType[] expectedType = {TTR, DEL, TTR, DEL, TTP, DEL, TTR, DEL, TTR};
        env.put("file", "my_file.txt");
        testSubstituteHelper(str, expectedType, expectedStr);
    }

    @Test
    public void testSubstituteComplex() {
        String str = "ABCDE$aFGH ABCDE\"$a\"FGH 'ABCDE$aFGH' $a";
        String[] expectedStr = {"ABCDE", null, "ABCDEkekFGH", null, "ABCDE$aFGH", null, "kek"};
        TokenType[] expectedType = {TTR, DEL, TTR, DEL, TTR, DEL, TTR};
        env.put("a", "kek");

        testSubstituteHelper(str, expectedType, expectedStr);
    }

    @Test
    public void testSubstitute() {
        String str = "hello$world";
        env.put("world", ", kek!");
        assertEquals("hello, kek!", Substitutor.substitute(str, env));
    }

    @Test
    public void testSubstituteDoubleWord() {
        String str = "$hello$world";
        env.put("world", ", kek!");
        env.put("hello", "lol");
        assertEquals("lol, kek!", Substitutor.substitute(str, env));
    }

    @Test
    public void testSubstituteDoubleComplexExpression() {
        String str = "$hello$world = $lol \"$kek\" '$cheburek'";
        env.put("world", ", kek!");
        env.put("hello", "lol");
        env.put("lol", "abc");
        env.put("kek", "woohoo");
        env.put("cheburek", "None");
        assertEquals("lol, kek! = abc \"woohoo\" 'None'", Substitutor.substitute(str, env));
    }

    private void testSubstituteHelper(String str, TokenType[] expectedType, String[] expectedStr) {
        ParsingResult result = Tokenizer.tokenizeString(str);
        assertEquals(ParsingResultStatus.SUCCESS, result.getStatus());
        result = Substitutor.substituteAll(result, env);
        assertEquals(ParsingResultStatus.SUCCESS, result.getStatus());
        List<Token> tokens = result.getTokens();

        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < expectedStr.length; i++) {
            sb1.append(new Token(expectedStr[i], expectedType[i]).getStringRepr());
        }
        for (Token tok : tokens) {
            sb2.append(tok.getStringRepr());
        }
        assertEquals(sb1.toString(), sb2.toString());
    }

}