import java.util.LinkedList;

public class Basic {
    public static void main(String[] args){
        if(args.length != 1){
            // If we see that the provided arguments are not 1 we throw an IllegalArgument exception as we only want 1 basic file
            throw new IllegalArgumentException("Please provied at least 1 and only 1 argument");
        }
        //we know that args[0] is the first entry in the array so we know that the first entry will be the file name
        String fileName = args[0];

        Lexer lexer = new Lexer();
        //We create the linked list of Tokens and lex the list with the file contents and store the tokens in the linked list in order to print out later
        LinkedList<Token> tokens = lexer.Lex(fileName);
        //Create new parser and take in the list of tokens
        Parser parser = new Parser(tokens);
        //Node result should be equal to the AST after we parse the linkedList
        Node result = parser.parse();
        //Print out the AST by calling toString
        Interpreter interpreter = new Interpreter((StatementsNode) result);
        interpreter.Interpret();
    }
}
