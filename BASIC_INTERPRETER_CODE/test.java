/*
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import static org.junit.Assert.*;
public class test {

    @Test
    public void testLexer() throws IOException {
        File tempFile = createTempFile("testIntegerAndString", "5 hello \n 5.23 \n 8.5 \n 7.77.78.1 \n My$NameIsMiguel");
        LinkedList<Token> list = new Lexer().Lex(tempFile.getAbsolutePath());
        assertEquals("NUMBER(5)", list.get(0).toString());
        assertEquals("WORD(HELLO)", list.get(1).toString());
        assertEquals("ENDOFLINE", list.get(2).toString());
        assertEquals("NUMBER(5.23)", list.get(3).toString());
        assertEquals("ENDOFLINE", list.get(4).toString());
        assertEquals("NUMBER(8.5)", list.get(5).toString());
        assertEquals("ENDOFLINE", list.get(6).toString());
        assertEquals("NUMBER(7.77)", list.get(7).toString());
        assertEquals("NUMBER(.78)", list.get(8).toString());
        assertEquals("NUMBER(.1)", list.get(9).toString());
        assertEquals("ENDOFLINE", list.get(10).toString());
        assertEquals("WORD(MY$)", list.get(11).toString());
        assertEquals("WORD(NAMEISMIGUEL)", list.get(12).toString());
    }
    @Test
    public void testSymbols() throws IOException {
        File tempFile = createTempFile("testSymbols", "+ - * / ( ) < > = >= <= <>");
        LinkedList<Token> list = new Lexer().Lex(tempFile.getAbsolutePath());
        assertEquals("PLUS", list.get(0).toString());
        assertEquals("MINUS", list.get(1).toString());
        assertEquals("TIMES", list.get(2).toString());
        assertEquals("DIVIED", list.get(3).toString());
        assertEquals("LPAREN", list.get(4).toString());
        assertEquals("RPAREN", list.get(5).toString());
        assertEquals("LESSTHAN", list.get(6).toString());
        assertEquals("GREATERTHAN", list.get(7).toString());
        assertEquals("EQUALS", list.get(8).toString());
        assertEquals("GREATOREQUAL",list.get(9).toString());
        assertEquals("LESSOREQUAL", list.get(10).toString());
        assertEquals("NOTEQUAL", list.get(11).toString());
    }

    @Test
    public void testCodeHandler() throws IOException {
        // Create a temporary file with some content
        String fileName = "tempFile.txt";
        String fileContent = "This is a test file content.";
        Files.write(Paths.get(fileName), fileContent.getBytes());
        // Create a CodeHandler instance
        CodeHandler codeHandler = new CodeHandler(fileName);
        // Test peek method
        assertEquals('T', codeHandler.peek(0));
        assertEquals('h', codeHandler.peek(1));
        assertEquals('i', codeHandler.peek(2));
        assertEquals('s', codeHandler.peek(3));

        assertThrows(IndexOutOfBoundsException.class, () -> codeHandler.peek(-1));  // Test out of bounds
        // Test peekString method
        assertEquals("This", codeHandler.peekString(4));
        assertEquals("This is a test", codeHandler.peekString(14));

        // Test swallow method
        codeHandler.swallow(6);
        assertEquals('s', codeHandler.getChar());  // Make sure index has moved
        // Test isDone method
        assertFalse(codeHandler.isDone());  // Not done yet
        codeHandler.swallow(fileContent.length() - 1);  // Move to the end
        assertTrue(codeHandler.isDone());  // Should be done now
    }

    @Test
    public void testParserStatementWithAssignment() throws IOException {
        File tempFile = createTempFile("testParserStatement", "x = x + 5 - 7 * 9 + 4");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        assertEquals("StatementsNode([AssignmentNode(VariableNode(X), MathOpNode(MathOpNode(MathOpNode(VariableNode(X) + IntegerNode(5)) - MathOpNode(IntegerNode(7) * IntegerNode(9))) + IntegerNode(4)))])",result.toString());
    }

    @Test
    public void testParserWithPrintStatementAndAssignment() throws IOException {
        File tempFile = createTempFile("testParserWithPrintStatementAndAssignment","x = 5 + 5 - 6 * 1\n" + "PRINT x");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        assertEquals("StatementsNode([AssignmentNode(VariableNode(X), MathOpNode(MathOpNode(IntegerNode(5) + IntegerNode(5)) - MathOpNode(IntegerNode(6) * IntegerNode(1)))), PrintNode([VariableNode(X)])])",result.toString());
    }

    @Test
    public void testParserPrintListStatement() throws IOException {
        File tempFile = createTempFile("testPrintListStatement","PRINT X,Y,Z");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        assertEquals("StatementsNode([PrintNode([VariableNode(X), VariableNode(Y), VariableNode(Z)])])",result.toString());
    }

    @Test
    public void testParserStringNodeStatement() throws IOException {
        File tempFile = createTempFile("testParserStringNodeStatement","PRINT \"Hello World!\"");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = (parser).parse();
        assertEquals("StatementsNode([PrintNode([StringNode(\"Hello World!\")])])",result.toString());
    }

    @Test
    public void testParserReadNodeStatement() throws IOException {
        File tempFile = createTempFile("testParserReadNodeStatement","READ X,Y,Z");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = (parser).parse();
        assertEquals("StatementsNode([ReadNode([VariableNode(X), VariableNode(Y), VariableNode(Z)])])",result.toString());
    }

    @Test
    public void testParserDataStatement() throws IOException{
        File tempFile = createTempFile("testParserDataStatement","DATA \"Hello World\",100,1.23");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = (parser).parse();
        assertEquals("StatementsNode([DataNode( [StringNode(\"Hello World\"), IntegerNode(100), FloatNode(1.23)])])",result.toString());

    }

    @Test
    public void testParserWithInputStatement() throws IOException{
        File tempFile = createTempFile("testParserWithInputStatement","INPUT \"Hello What Is Your Name?\",X,Y,Z");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = (parser).parse();
        assertEquals("StatementsNode([InputNode(StringNode(\"Hello What Is Your Name?\")[VariableNode(X), VariableNode(Y), VariableNode(Z)])])",result.toString());
    }

    @Test
    public void testParserMultipleNodesAndStatementsReadDataInput() throws IOException{
        File tempFile = createTempFile("testMultipleNodesAndStatements","READ X,Y,Z\n" +
                "DATA \"Hello World\",10,1.1\n" +
                "PRINT \"Hey Data Node!\"\n" +
                "INPUT \"Hey Print Node!\",X,Y,Z");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = (parser).parse();
        assertEquals("StatementsNode([ReadNode([VariableNode(X), VariableNode(Y), VariableNode(Z)]), DataNode( [StringNode(\"Hello World\"), IntegerNode(10), FloatNode(1.1)]), PrintNode([StringNode(\"Hey Data Node!\")]), InputNode(StringNode(\"Hey Print Node!\")[VariableNode(X), VariableNode(Y), VariableNode(Z)])])",result.toString());
    }
    @Test
    public void testLabelANdFUnction() throws IOException {
        File tempFile = createTempFile("testLabel", "FIZZ: PRINT \"FIZZ\"");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = (parser).parse();
        assertEquals("StatementsNode([LabeledStatementNode(FIZZ, PrintNode([StringNode(\"FIZZ\")]))])", result.toString());
    }
    @Test
    public void testParserForStatementDefaultStep() throws IOException {
        File tempFile = createTempFile("testParserForStatementDefaultStep","FOR A = 1 TO 100");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        assertEquals("StatementsNode([FORNODE(AssignmentNode(VariableNode(A), IntegerNode(1)), IntegerNode(100), IntegerNode(1))])",result.toString());
    }
    @Test
    public void parserTestParserForStatement() throws IOException {
        File tempFile = createTempFile("testParserForStatement", "FOR i = 0 TO 10 STEP 12");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        assertEquals("StatementsNode([FORNODE(AssignmentNode(VariableNode(I), IntegerNode(0)), IntegerNode(10), IntegerNode(12))])",result.toString());
    }

    @Test
    public void parserTestWhileStatement() throws IOException {
        File tempFile = createTempFile("testwhilesatement","WHILE A < 1 endLabel");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        assertEquals("StatementsNode([WHILENODE((VariableNode(A) LESSTHAN IntegerNode(1)),ENDLABEL)])",result.toString());
    }

    @Test
    public void parserTestIfStatementAndGoSub() throws IOException {
        File tempFile = createTempFile("testIfstatementandgosub", "IF A <> 1 THEN GOSUB X");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        assertEquals("StatementsNode([IFNODE((VariableNode(A) NOTEQUAL IntegerNode(1)),GOSUBNODE(X))])",result.toString());
    }

    @Test
    public void parserTestEnd() throws IOException {
        File tempFile = createTempFile("testend","END");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        assertEquals("StatementsNode([ENDNODE])",result.toString());
    }

    @Test
    public void parserTestReturn() throws IOException {
        File tempFile = createTempFile("testend","RETURN");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        assertEquals("StatementsNode([RETURNNODE])",result.toString());
    }

    @Test
    public void parserTestNext() throws IOException {
        File tempFile = createTempFile("testnext","NEXT X ");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        assertEquals("StatementsNode([NextNode(X)])",result.toString());
    }

    @Test
    public void parserTestExampleCode() throws IOException {
        File tempFile = createTempFile("parsertestexamplecode", "FOR A = 1 TO 100\n" +
                "IF (A/3)*3 > A-1 THEN GOSUB FIZZ\n" +
                "IF (A/5)*5 > A-1 THEN GOSUB BUZZ\n" +
                "NEXT A\n" +
                "END\n" +
                "FIZZ: PRINT \"FIZZ\"\n" +
                "RETURN\n" +
                "BUZZ: PRINT \"BUZZ\"\n" +
                "RETURN\n");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        assertEquals("StatementsNode([FORNODE(AssignmentNode(VariableNode(A), IntegerNode(1)), IntegerNode(100), IntegerNode(1)), IFNODE((MathOpNode(MathOpNode(VariableNode(A) / IntegerNode(3)) * IntegerNode(3)) GREATERTHAN MathOpNode(VariableNode(A) - IntegerNode(1))),GOSUBNODE(FIZZ)), IFNODE((MathOpNode(MathOpNode(VariableNode(A) / IntegerNode(5)) * IntegerNode(5)) GREATERTHAN MathOpNode(VariableNode(A) - IntegerNode(1))),GOSUBNODE(BUZZ)), NextNode(A), ENDNODE, LabeledStatementNode(FIZZ, PrintNode([StringNode(\"FIZZ\")])), RETURNNODE, LabeledStatementNode(BUZZ, PrintNode([StringNode(\"BUZZ\")])), RETURNNODE])",result.toString());
    }
    @Test
    public void interpreterTestDataQueue() throws IOException {
        File tempFile = createTempFile("interpretertestdataqueue","DATA 10,35,23");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        Interpreter interpreter = new Interpreter((StatementsNode) result);
        assertEquals("[IntegerNode(10), IntegerNode(35), IntegerNode(23), IntegerNode(10), IntegerNode(35), IntegerNode(23)]",interpreter.walkDataAst().toString());
    }

    @Test
    public void interpreterTestLabelHashMap() throws IOException {
        File tempFile = createTempFile("interpreterTestLabelhashMap","HELLO: PRINT \"HELLO WORLD\"\n" +
                "WORLD: PRINT \"WORLD\"");
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        Interpreter interpreter = new Interpreter((StatementsNode) result);
        assertEquals("{HELLO=LabeledStatementNode(HELLO, PrintNode([StringNode(\"HELLO WORLD\")])), WORLD=LabeledStatementNode(WORLD, PrintNode([StringNode(\"WORLD\")]))}",interpreter.walkLabelAst().toString());
    }
    @Test
    public void interpreterTestLeft(){
        assertEquals("Hel",Interpreter.left("Hel",3));
    }

    @Test
    public void interpreterTestRight(){
        assertEquals("rld",Interpreter.right("Hello World",3));
    }

    @Test
    public void interpreterTestMid(){
        assertEquals("ban",Interpreter.mid("Albany",2,3));
    }

    @Test
    public void interpreterTestNum(){
        assertEquals("3",Interpreter.numToString(3));
    }

    @Test
    public void interpreterTestNumFloat(){
        assertEquals("3.75",Interpreter.numToString(3.75F));
    }

    @Test
    public void interpreterValToInt(){
        assertEquals(100,Interpreter.valToInt("100"));
    }
    @Test
    public void interpreterValToFloat(){
        assertEquals(95.5,Interpreter.valToFloat("95.5"),0.05);
    }
    @Test
    public void interpreterTestEvaluateInteger() throws IOException {
        // step 1: create the temp file similar to other unit tests and add the contest of our test file
        File tempFile = createTempFile("interpreterTestLabelhashMap", "X = 7\n" +
                "Y = 5\n" +
                "PRINT X + Y");

        // step 2: redirect the output stream to a byte array to get the printed output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        //step 3: continue the lexing,parsing, and interpreting as usual
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        Interpreter interpreter = new Interpreter((StatementsNode) result);
        interpreter.Interpret();

        // step 4: reset the output stream for other tests.
        System.setOut(originalOut);

        // step 5 get the expected output of 12
        String expectedOutput = "12\n"; // output should be the sum of 7 and 5
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void interpreterTestEvaluateIntegerMoreComplex() throws IOException {
        // step 1: create the temp file similar to other unit tests and add the contest of our test file
        File tempFile = createTempFile("interpretertestintegermorecomplex", "X = 3 * 8 + 8 / 9\n" +
                "Y = 3 * 9 * 4 * 3\n" +
                "\n" +
                "PRINT X * Y + 10");

        // step 2: redirect the output stream to a byte array to get the printed output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        //step 3: continue the lexing,parsing, and interpreting as usual
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        Interpreter interpreter = new Interpreter((StatementsNode) result);
        interpreter.Interpret();

        // step 4: reset the output stream for other tests.
        System.setOut(originalOut);

        // step 5 get the expected output of 12
        String expectedOutput = "7786\n"; //expected output is the sum for the given math equation
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void interpreterTestPrint() throws IOException {
        // step 1: create the temp file similar to other unit tests and add the contest of our test file
        File tempFile = createTempFile("interpretertestprint", "PRINT \"Hello World\"");

        // step 2: redirect the output stream to a byte array to get the printed output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        //step 3: continue the lexing,parsing, and interpreting as usual
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        Interpreter interpreter = new Interpreter((StatementsNode) result);
        interpreter.Interpret();

        System.setOut(originalOut);

        String expectedOutput = "Hello World\n";
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void intrepterCODE_TEST_FizzBuzzTest() throws IOException {
        // step 1: create the temp file similar to other unit tests and add the contest of our test file
        File tempFile = createTempFile("fizzBuzzTestIntrepter", "FOR A = 1 TO 100\n" +
                "IF (A/3)*3 > A-1 THEN GOSUB FIZZ\n" +
                "IF (A/5)*5 > A-1 THEN GOSUB BUZZ\n" +
                "NEXT A\n" +
                "FIZZ: PRINT \"FIZZ\"\n" +
                "RETURN\n" +
                "BUZZ: PRINT \"BUZZ\"\n" +
                "RETURN\n");

        // step 2: redirect the output stream to a byte array to get the printed output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        //step 3: continue the lexing,parsing, and interpreting as usual
        LinkedList<Token> lexer = new Lexer().Lex(tempFile.getAbsolutePath());
        Parser parser = new Parser(lexer);
        Node result = parser.parse();
        Interpreter interpreter = new Interpreter((StatementsNode) result);
        interpreter.Interpret();

        System.setOut(originalOut);

        String expectedOutput = "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "BUZZ\n" +
                "FIZZ\n" +
                "FIZZ\n" +
                "BUZZ\n";
        assertEquals(expectedOutput, outputStream.toString());
    }

    private File createTempFile(String fileName, String content) throws IOException {
        File tempFile = File.createTempFile(fileName, ".txt");
        tempFile.deleteOnExit();

        try(FileWriter writer = new FileWriter(tempFile)){
            writer.write(content);

        }
        return tempFile;

    }
}
*/