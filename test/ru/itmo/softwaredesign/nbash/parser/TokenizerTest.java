package ru.itmo.softwaredesign.nbash.parser;

import org.junit.Test;

import static org.junit.Assert.*;

public class TokenizerTest {

    private final TokenType TTR = TokenType.REGULAR_WORD;
    private final TokenType TTS = TokenType.SINGLE_QUOTED_WORD;
    private final TokenType TTD = TokenType.DOUBLE_QUOTED_WORD;
    private final TokenType TTP = TokenType.PIPE_OPERATOR;
    private final TokenType TTA = TokenType.ASSIGN_OPERATOR;
    private final TokenType DEL = TokenType.DELIMITER;

    @Test
    public void testTokenizeAAAABBBBCCCC() {
        String str = "aaaa bbbb cccc";
        String[] expected = new String[]{"aaaa", null, "bbbb", null, "cccc"};
        TokenType[] expectedTypes = new TokenType[]{TTR, DEL, TTR, DEL, TTR};
        testTokenizeSimple(str, expected, expectedTypes);
    }

    @Test
    public void testTokenizeKCombinator() {
        String str = "a -> b -> a";
        String[] expected = new String[]{"a", null, "->", null, "b", null, "->", null, "a"};
        TokenType[] expectedTypes = new TokenType[]{TTR, DEL, TTR, DEL, TTR, DEL, TTR, DEL, TTR};
        testTokenizeSimple(str, expected, expectedTypes);
    }

    @Test
    public void testTokenizeSpaces() {
        String str = "hello                                world";
        TokenType[] expectedTypes = new TokenType[]{TTR, DEL, TTR};
        String[] expected = new String[]{"hello", null, "world"};
        testTokenizeSimple(str, expected, expectedTypes);
    }

    @Test
    public void testTokenizeAssign() {
        String str = "hello=world";
        TokenType[] expectedTypes = new TokenType[]{TTR, TTA, TTR};
        String[] expected = new String[]{"hello", null, "world"};
        testTokenizeSimple(str, expected, expectedTypes);
    }

    @Test
    public void testTokenizePipe() {
        String str = "hello|world";
        TokenType[] expectedTypes = new TokenType[]{TTR, TTP, TTR};
        String[] expected = new String[]{"hello", null, "world"};
        testTokenizeSimple(str, expected, expectedTypes);
    }

    @Test
    public void testTokenizeEmpty() {
        String str = "";
        TokenType[] expectedTypes = new TokenType[]{TTR};
        String[] expected = new String[]{""};
        testTokenizeSimple(str, expected, expectedTypes);
    }

    @Test
    public void testTokenizeSingleQuotes() {
        String str = "hello 'world' dfkgjf'its   me'";
        TokenType[] expectedTypes = new TokenType[]{TTR, DEL, TTS, DEL, TTR, TTS, TTR};
        String[] expected = new String[]{"hello", null, "world", null, "dfkgjf", "its   me", ""};
        testTokenizeSimple(str, expected, expectedTypes);
    }

    @Test
    public void testTokenizeDoubleQuotes() {
        String str = "hello \"world\" dfkgjf\"its   me\"";
        TokenType[] expectedTypes = new TokenType[]{TTR, DEL, TTD, DEL, TTR, TTD, TTR};
        String[] expected = new String[]{"hello", null, "world", null, "dfkgjf", "its   me", ""};
        testTokenizeSimple(str, expected, expectedTypes);
    }

    @Test
    public void testTokenizeQuotes() {
        String str = "hello \"world\" 'here' \"w'e\" 'a\"re' !!!!";
        TokenType[] expectedTypes = new TokenType[]{TTR, DEL, TTD, DEL, TTS, DEL, TTD, DEL, TTS, DEL, TTR};
        String[] expected = new String[]{"hello", null, "world", null, "here", null, "w'e", null, "a\"re", null, "!!!!"};
        testTokenizeSimple(str, expected, expectedTypes);
    }

    @Test
    public void testTokenizeBackSlash() {
        String str = "test \\\"String \"String\" ?";
        TokenType[] expectedTypes = new TokenType[]{TTR, DEL, TTR, DEL, TTD, DEL, TTR};
        String[] expected = new String[]{"test", null, "\\\"String", null, "String", null, "?"};
        testTokenizeSimple(str, expected, expectedTypes);
    }

    @Test
    public void testTokenizeAll() {
        String str = "Abb=\"cccc\" | 'dd' | lel \" a | b | c \" elele ";
        TokenType[] expectedTypes = new TokenType[]{TTR, TTA, TTD, DEL, TTP, DEL, TTS, DEL, TTP, DEL, TTR, DEL, TTD, DEL, TTR, DEL};
        String[] expected = new String[]{"Abb", null, "cccc", null, null, null, "dd", null, null, null, "lel", null, " a | b | c ", null, "elele", null};
        testTokenizeSimple(str, expected, expectedTypes);
    }

    @Test
    public void testContinueTokenizing() {
        for (int i : new Integer[]{2, 3, 7, 15, 30}){
            String str = "Abb=\"cccc\" | 'dd' | lel \" a | b | c \" elele ";
            TokenType[] expectedTypes = new TokenType[]{TTR, TTA, TTD, DEL, TTP, DEL, TTS, DEL, TTP, DEL, TTR, DEL, TTD, DEL, TTR, DEL};
            String[] expected = new String[]{"Abb", null, "cccc", null, null, null, "dd", null, null, null, "lel", null, " a | b | c ", null, "elele", null};
            testContinueTokenizing(str, expected, expectedTypes, i);
        }
    }

    @Test
    public void testContinueTokenizingFail() {
        ParsingResult result = Tokenizer.continueTokenization(new ParsingResult(null, ParsingResultStatus.FAIL), "kek");
        assertEquals(ParsingResultStatus.FAIL, result.getStatus());
    }

    @Test
    public void testUnclosedQuote() {
        String str = "hehehehe \" kdfjgfg";
        ParsingResult result = Tokenizer.tokenizeString(str);
        assertTrue(result.isWaitingStatus());
        assertEquals(ParsingResultStatus.DOUBLE_QUOTE_WAITING, result.getStatus());
    }

    @Test
    public void testUnclosedDuote() {
        String str = "hehehehe  ' \" kdfjgfg";
        ParsingResult result = Tokenizer.tokenizeString(str);
        assertTrue(result.isWaitingStatus());
        assertEquals(ParsingResultStatus.SINGLE_QUOTE_WAITING, result.getStatus());
    }

    @Test
    public void testUnclosedPipe() {
        String str = "hehehehe  ' \" ' kdfjgfg | ";
        ParsingResult result = Tokenizer.tokenizeString(str);
        assertTrue(result.isWaitingStatus());
        assertEquals(ParsingResultStatus.PIPE_WAITING, result.getStatus());
    }

    @Test
    public void testReTokenize() {
        String str = "Abb=\"cccc\" | 'dd' | lel \" a | b | c \" elele ";
        ParsingResult parsed = Tokenizer.tokenizeString(str);
        assertEquals(ParsingResultStatus.SUCCESS, parsed.getStatus());
        ParsingResult retokenized = Tokenizer.reTokenize(parsed.getTokens());
        assertEquals(ParsingResultStatus.SUCCESS, retokenized.getStatus());

        ParsingResult retokenized2 = Tokenizer.reTokenize(retokenized.getTokens());
        assertEquals(ParsingResultStatus.SUCCESS, retokenized2.getStatus());
        for (int i = 0; i < retokenized.getTokens().size(); i++) {
            assertEquals(retokenized.getTokens().get(i).getStringRepr(),
                    retokenized2.getTokens().get(i).getStringRepr());
        }
    }

    private void testTokenizeSimple(String str, String[] expected, TokenType[] expectedTypes) {
        ParsingResult result = Tokenizer.tokenizeString(str);
        assertEquals(result.getStatus(), ParsingResultStatus.SUCCESS);
        assertEquals(expected.length, result.getTokens().size());

        for (int i = 0; i < expected.length; i++) {
            Token tok = result.getTokens().get(i);
            assertEquals(tok.getType(), expectedTypes[i]);
            assertEquals(new Token(expected[i], expectedTypes[i]).getStringRepr(), tok.getStringRepr());
        }
    }

    private void testContinueTokenizing(String str, String[] expected, TokenType[] expectedTypes, int splitAt) {
        ParsingResult result = Tokenizer.tokenizeString(str.substring(0, splitAt));
        result = Tokenizer.continueTokenization(result, str.substring(splitAt));
        assertEquals(result.getStatus(), ParsingResultStatus.SUCCESS);
        assertEquals(expected.length, result.getTokens().size());
        assertFalse(result.isWaitingStatus());

        for (int i = 0; i < expected.length; i++) {
            Token tok = result.getTokens().get(i);
            assertEquals(tok.getType(), expectedTypes[i]);
            assertEquals(new Token(expected[i], expectedTypes[i]).getStringRepr(), tok.getStringRepr());
        }
    }

}