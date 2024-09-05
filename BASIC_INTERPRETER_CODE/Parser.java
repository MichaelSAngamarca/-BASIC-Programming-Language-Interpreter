import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Parser {
    private TokenManager tokenManager; //Initalize a new tokenManger for examining the list
    private LinkedList<Token> parserTokens = new LinkedList<>();

    /*/
    Constructor that takes the LinkedList as a parameter and sets a new tokenManager that takes in the LinkedList of the tokens
     */
    public Parser(LinkedList<Token> parserTokens){
        this.tokenManager = new TokenManager(parserTokens);
    }

    /*/
    Accept Separators examines the list for ENDOFLINE tokens and if they are present within the list
    We use a flag to keep track of if we find a Separator, if we do, we can update the flag and return accordingly
     */
    public boolean AcceptSeperators(){
        boolean foundSeperators = false;
        while(tokenManager.MatchAndRemove(Token.TokenType.ENDOFLINE).isPresent()){
            foundSeperators = true;
        }
        return foundSeperators;
    }

    /*/
    We have changed parse in this instance to call statement which now will call expression to build out multiple statements
     */
    public Node parse() {
        return Statements();
    }

    /*/
    We create a LinkedList of StatementNodes in order to store all the statement and their indentifiers.
    We call Statement which will build out single statements until we reach the end of the file by seeing if the next time we traverse the file it is null
    At the end we return the LinkedList of Statements
     */
    public Node Statements() {
        List<StatementNode> statementNodes = new LinkedList<>();
        StatementNode statement = Statement();
        while (statement != null) {
            statementNodes.add(statement);
            AcceptSeperators();
            statement = Statement();
        }
        return new StatementsNode(statementNodes);
    }

    /*/
    In Statement, we want to examine if in our file we come across two key identifiers, a PRINT token and an EQUAL token
    If we encounter a print that means we need to create a new PrintNode as everything following will be what we want to output
    If we encounter a EQUAL token that means we want to create an AssignmentNode as that means we are creating a new Variable which assigns itself a value.
    We can do this by just examining ahead to see if either Token exists (Keeping in mind normally where values could appear) in the file and calling the appropriate methods when needed
    If we examine that none of these exist ahead that means there is no appropriate node needed and can return null
    **UPDATE: We have now added more conditions that allow us to call the correct method to build out the new ast node for IF,WHILE,FOR,NEXT,END,GOSUB,LABEL,and FUNCTION
     */

    public StatementNode Statement() {
        Optional<Token> printToken = tokenManager.MatchAndRemove(Token.TokenType.PRINT);
        Optional<Token> readToken = tokenManager.MatchAndRemove(Token.TokenType.READ);
        Optional<Token> inputToken = tokenManager.MatchAndRemove(Token.TokenType.INPUT);
        Optional<Token> dataToken = tokenManager.MatchAndRemove(Token.TokenType.DATA);
        Optional<Token> returnToken = tokenManager.MatchAndRemove(Token.TokenType.RETURN);
        Optional<Token> goSubToken = tokenManager.MatchAndRemove(Token.TokenType.GOSUB);
        Optional<Token> forToken = tokenManager.MatchAndRemove(Token.TokenType.FOR);
        Optional<Token> nextToken = tokenManager.MatchAndRemove(Token.TokenType.NEXT);
        Optional<Token> endToken = tokenManager.MatchAndRemove(Token.TokenType.END);
        Optional<Token> ifToken = tokenManager.MatchAndRemove(Token.TokenType.IF);
        Optional<Token> whileToken = tokenManager.MatchAndRemove(Token.TokenType.WHILE);
        Optional<Token> functionToken = tokenManager.MatchAndRemove(Token.TokenType.FUNCTION);
        Optional<Token> assignmentToken = tokenManager.Peek(1);
        Optional<Token> labelToken = tokenManager.MatchAndRemove(Token.TokenType.LABEL);

        if (labelToken.isPresent()) {
            String label = labelToken.get().returnToken();
            Optional<Token> tokenAhead = tokenManager.Peek(1);
            if (tokenAhead.isPresent() && tokenAhead.get().returnTokenType() != Token.TokenType.ENDOFLINE) {
                Node statement = Statement();
                return new LabeledStatementNode(label, (StatementNode) statement);
            } else {
                return null; // Incomplete labeled statement
            }
        }
        else if (printToken.isPresent()) {
            return printStatement();
        } else if (readToken.isPresent()) {
            return readStatement();
        } else if (inputToken.isPresent()) {
            return inputStatement();
        } else if (dataToken.isPresent()) {
            return dataStatement();
        } else if (forToken.isPresent()) {
            return forStatement();
        } else if (ifToken.isPresent()) {
            return ifStatement();
        } else if (whileToken.isPresent()) {
            return whileStatement();
        } else if (assignmentToken.isPresent() && assignmentToken.get().returnTokenType() == Token.TokenType.EQUALS) {
            return Assignment();
        }
        else if (goSubToken.isPresent()) {
            return goSubNode();
        } else if (returnToken.isPresent()) {
            return returnStatement();
        } else if (endToken.isPresent()) {
            return endStatement();
        } else if (nextToken.isPresent()) {
            return NextStatement();
        }
        // If none of the specific statements are found, return null
        return null;

    }


    /*
    printStatement builds out everything to the right of a PRINT identifier.
    to build out the print statement we first call expression to build out everything to the right of a PRINT identifier and add it to the LinkedList
    However, we need to check that in a printStatement, we can print out multiple values signified by a COMMA token.
    We can do this by examining and looping ahead and seeing if there exists a comma token ahead, if so, call PrintList() to build out the multiple values we would be printing that are followed by a Comma.
    At the end we simply need to create a new PrintNode and add that value to the LinkedList of Statements.
     */
    public PrintNode printStatement() {
        List<Node> nodesToPrint = new LinkedList<>();
        Node expression = expression();
        nodesToPrint.add(expression);
        while (tokenManager.MatchAndRemove(Token.TokenType.COMMA).isPresent()) {
            return printList(nodesToPrint);
        }
        return new PrintNode(nodesToPrint);
    }


    /*/
    PrintList handles the condition if a Print Identifier is taking in multiple statements.
    To do this, we can take the LinkedList created in PrintStatement and continuously loop while there is a COMMA token ahead, calling expression to keep building out the entire expression
    At the end of this we Return a new PrintNode which takes in the entire LinkedList of PrintNodes to then be sent to the StatementsNode.
     */

    public PrintNode printList(List<Node> nodesToPrint) {
        while (true) {
            Node expression = expression();
            nodesToPrint.add(expression);
            if (!tokenManager.MatchAndRemove(Token.TokenType.COMMA).isPresent()) {
                break;
            }
        }
        return new PrintNode(nodesToPrint);
    }


    /*/
    Assignment Handles the condition from Statement in which we come across an Assignment (Ex X = 3) in our File
    We want to examine if there is first a WORD token ahead in the file which signals the first part of an Assignment and get its value and then create a new VariableNode for it.
    We similarly check ahead from that if there exists a EQUALS token ahead, If there does not exist one that means it's not a complete Assignment and therfore should throw an error.
    However, if we do see that the EQUAL does Exist to the right of the VariableNode, that means we can call expression and build out everything that will be ahead of the assignment.
    At the end we create a new AssignmentNode which takes in the VariableName and its value to the right of the Equal Sign.
     */
    public AssignmentNode Assignment() {
        Optional<Token> identifierToken = tokenManager.MatchAndRemove(Token.TokenType.WORD);
        if (!identifierToken.isPresent()) {
            throw new RuntimeException("Identifier expected for variable in assignment");
        }
        String variableName = identifierToken.get().returnToken();
        VariableNode variableNode = new VariableNode(variableName);

        Optional<Token> assignmentToken = tokenManager.MatchAndRemove(Token.TokenType.EQUALS);
        if (!assignmentToken.isPresent()) {
            throw new RuntimeException("= expected in assignment");
        }
        Node valueNode = expression();
        if (valueNode == null) {
            throw new RuntimeException("Expression expected");
        }

        return new AssignmentNode(variableNode, valueNode);
    }

    /*/
    ReadStatement identifies read tokens and builds out a comma speerated list of all values we want to read from
    We are able to do a similar process as in print list by examining if a comma exists ahead and continuly looping to build out the list
    At the end we return a new ReadNode that contains a linked list of variables that we want to read from
     */
    public ReadNode readStatement() {
        List<VariableNode> variables = new LinkedList<>();
        boolean continueParsing = true;
        while (continueParsing) {
            Optional<Token> wordToken = tokenManager.MatchAndRemove(Token.TokenType.WORD);
            if (wordToken.isPresent()) {
                variables.add(new VariableNode(wordToken.get().returnToken()));
            } else {
                throw new RuntimeException("Variable expected in READ statement");
            }
            if (!tokenManager.MatchAndRemove(Token.TokenType.COMMA).isPresent()) {
                continueParsing = false;
            }
        }

        return new ReadNode(variables);
    }

    /*/
    DataNode builds out a comma Speperated List of differing Nodes of Float,Integer,and String Type
    Similar to print list we can continue looping while we see a comma token exists to the right of the DATA identifier
    To Examine strings we can simply call match and remove to obtains its value and check if it is present, if so we can create a new StringNode
    To examine the differing integer or float node we can obtain the string of the next token and see if that value contains a decimal point
    If so we can create a new FloatNode if not create a new integer node and return a new DataNode of the linked list of values
     */
    public DataNode dataStatement() {
        List<Node> dataValues = new LinkedList<>();

        boolean continueParsing = true;
        while (continueParsing) {
            Optional<Token> stringToken = tokenManager.MatchAndRemove(Token.TokenType.STRINGLITERAL);
            Optional<Token> numberToken = tokenManager.MatchAndRemove(Token.TokenType.NUMBER);


            if (stringToken.isPresent()) {
                dataValues.add(new StringNode(stringToken.get().returnToken()));
            } else if (numberToken.isPresent()) {
                String numberValue = numberToken.get().returnToken();
                if (numberValue.contains(".")) {
                    dataValues.add(new FloatNode(Float.parseFloat(numberValue)));
                } else {
                    dataValues.add(new IntegerNode(Integer.parseInt(numberValue)));
                }
            } else {

                continueParsing = false;
            }


            if (!tokenManager.MatchAndRemove(Token.TokenType.COMMA).isPresent()) {
                continueParsing = false;
            }
        }
        return new DataNode(dataValues);
    }

    /*/
    InputNode returns a list of prompts and VariableNodes
    To Parse the prompt we check to see if a stringliteral token exists ahead and if so call a new StringNode for that value of the prompt
    Similar to printList we continue looping while we see that a comma token exists ahead
    To parse the VaraibleNodes we can follow a similar pattern as in the VaribaleNode method by checking if a word token exists ahead in teh list and if so create a varaible node token
    at the end return a new InputNode that holds both the parsed prompt and list of VariableNodes
     */
    public InputNode inputStatement() {

        Node prompt;
        Optional<Token> stringToken = tokenManager.MatchAndRemove(Token.TokenType.STRINGLITERAL);
        if (stringToken.isPresent()) {
            prompt = new StringNode(stringToken.get().returnToken());
        } else {
            Optional<Token> variableToken = tokenManager.MatchAndRemove(Token.TokenType.WORD);
            if (variableToken.isPresent()) {
                prompt = new VariableNode(variableToken.get().returnToken());
            } else {
                throw new RuntimeException("Prompt string or variable expected in INPUT statement");
            }
        }

        if (!tokenManager.MatchAndRemove(Token.TokenType.COMMA).isPresent()) {
            throw new RuntimeException("Comma expected after INPUT prompt");
        }

        List<VariableNode> variables = new LinkedList<>();
        boolean continueParsing = true;
        while (continueParsing) {
            Optional<Token> wordToken = tokenManager.MatchAndRemove(Token.TokenType.WORD);
            if (wordToken.isPresent()) {
                variables.add(new VariableNode(wordToken.get().returnToken()));
            } else {
                throw new RuntimeException("Variable expected in INPUT statement");
            }


            if (!tokenManager.MatchAndRemove(Token.TokenType.COMMA).isPresent()) {
                continueParsing = false;
            }
        }

        return new InputNode(prompt, variables);
    }

    /*/
    returnStatement retursn a new ReturnNode
     */
    public ReturnNode returnStatement(){
        return new ReturnNode();
    }

    /*/
    endStatement returns a new EndNode
     */
    public EndNode endStatement(){
        return new EndNode();
    }

    /*/
    goSubNode returns a new node of GoSub type
    GoSub contains an identifier of string type to identfity the node
    we first check if the indetnfier exists ahead of the GoSubNode and return null if not
    at the end reutnr a new GoSubNode
     */
    public GoSubNode goSubNode(){
        Optional<Token> identifierToken = tokenManager.MatchAndRemove(Token.TokenType.WORD);
        if (!identifierToken.isPresent()) {
            return null;
        }
        String identifier = identifierToken.get().returnToken();
        return new GoSubNode(identifier);
    }

    /*/
    forStatement returns a new ForNode
    A for node consists of an assignmentNode as well as the end value an the stepvalue
    we get the Assignment value and check ahead if the syntax is correct and return null if not (if TO exists)
    we get the endValue get calling expression
    to get the step value we examine the value by calling expression, if there is no specifed step value we can set the value to 1
    at the end return a new for node
     */
    public ForNode forStatement() {
        AssignmentNode assignmentNode = Assignment();
        if (!tokenManager.MatchAndRemove(Token.TokenType.TO).isPresent()) {
            return null;
        }

        Node endValue = expression();

        Node stepValue = null;
        if (tokenManager.MatchAndRemove(Token.TokenType.STEP).isPresent()) {
            stepValue = expression();
        }

        return new ForNode(assignmentNode, (IntegerNode) endValue, (IntegerNode) stepValue);
    }

    /*/
    NextStatement returns a new NextNode with a String Identifier
    We get the value of the indetifier by checking if a word token exists ahead and assigning the value to a String and if not return null
    at the end return a new NextNode
     */
    public NextNode NextStatement(){
        Optional<Token> nextToken = tokenManager.MatchAndRemove(Token.TokenType.WORD);
        if(!nextToken.isPresent()){
            return null;
        }
        String variableName = nextToken.get().returnToken();
        return new NextNode(variableName);
    }

    /*/
    BooleanExpression is used to check for COnditionalOperators for Nodes such as While and If
    We first want to get the left side of the expression and assign it to a node
    We then want to exmaine to see what the token ahead is if any exists returning null if not
    We get the value of the right operaend and then call expression to get what's ahead
    We want to call expression so obtain eeverthing to the right of the condition
    at the end we return a new BooleanExpressionNode
     */
    public BooleanExpressionNode BooleanExpression() {
        Node leftOperand = expression();

        Optional<Token> operatorToken = tokenManager.MatchAndRemove(Token.TokenType.GREATERTHAN);
        if (!operatorToken.isPresent()) {
            operatorToken = tokenManager.MatchAndRemove(Token.TokenType.GREATOREQUAL);
        }
        if (!operatorToken.isPresent()) {
            operatorToken = tokenManager.MatchAndRemove(Token.TokenType.LESSTHAN);
        }
        if (!operatorToken.isPresent()) {
            operatorToken = tokenManager.MatchAndRemove(Token.TokenType.LESSOREQUAL);
        }
        if (!operatorToken.isPresent()) {
            operatorToken = tokenManager.MatchAndRemove(Token.TokenType.NOTEQUAL);
        }
        if (!operatorToken.isPresent()) {
            operatorToken = tokenManager.MatchAndRemove(Token.TokenType.EQUALS);
        }
        if (!operatorToken.isPresent()) {
            return null;
        }

        Token.TokenType operator = operatorToken.get().returnTokenType();
        Node rightOperand = expression();

        return new BooleanExpressionNode (leftOperand, rightOperand, operator);
    }

    /*/
    ifStatement returns a new IfNode that contains a condition and a statement ahead
    We first get the condition by calling boolean expression
    We want to get the statement ahead of the THEN keyword by calling Statement after checking wetter or not the syntax is correct and returning null if not
    at the end return a new IfNode
     */

    public IfNode ifStatement() {
        BooleanExpressionNode condition = BooleanExpression();
        // Check for presence of THEN or skip to the statement
        if (!tokenManager.MatchAndRemove(Token.TokenType.THEN).isPresent()) {
            throw new RuntimeException("THEN expected after IF condition");
        }
        // Now parse the statement after THEN
        StatementNode thenStatement = Statement();
        if (thenStatement == null) {
            throw new RuntimeException("Missing statement after IF condition");
        }
        return new IfNode(condition, thenStatement);
    }


    /*
    whileSatatement returns a WhileNode that contains a condition and an endlabel
    We get the condition by calling BooleanExpression
    we want to get the endLabel by seeing if any word tokens exist and setting the value to a string named endLabel
    at the end return a new WhileNode
     */
    public WhileNode whileStatement() {
        BooleanExpressionNode condition = BooleanExpression();
        Optional<Token> endWhileToken = tokenManager.MatchAndRemove(Token.TokenType.WORD);
        String endLabel;
        if (endWhileToken.isPresent()) {
            endLabel = endWhileToken.get().returnToken();
        } else {
            return null;
        }

        return new WhileNode(condition, endLabel);
    }

    public FunctionNode functionInvocation(String functionName) {
        if (functionName.equals("RANDOM")) {
            return new FunctionNode(functionName, new LinkedList<>());
        } else if (functionName.equals("LEFT$")) {
            List<Node> parameters = new LinkedList<>();
            if (!tokenManager.MatchAndRemove(Token.TokenType.LPAREN).isPresent()) {
                throw new RuntimeException("Expected '(' after LEFT$");
            }
            parameters.add(expression());
            if (!tokenManager.MatchAndRemove(Token.TokenType.COMMA).isPresent()) {
                throw new RuntimeException("Expected ',' after parameter in LEFT$");
            }
            parameters.add(expression());
            if (!tokenManager.MatchAndRemove(Token.TokenType.RPAREN).isPresent()) {
                throw new RuntimeException("Expected ')' after LEFT$ parameters");
            }
            return new FunctionNode(functionName, parameters);
        } else if (functionName.equals("RIGHT$")) {
            List<Node> parameters = new LinkedList<>();
            if (!tokenManager.MatchAndRemove(Token.TokenType.LPAREN).isPresent()) {
                throw new RuntimeException("Expected '(' after RIGHT$");
            }
            parameters.add(expression());
            if (!tokenManager.MatchAndRemove(Token.TokenType.COMMA).isPresent()) {
                throw new RuntimeException("Expected ',' after parameter in RIGHT$");
            }
            parameters.add(expression());
            if (!tokenManager.MatchAndRemove(Token.TokenType.RPAREN).isPresent()) {
                throw new RuntimeException("Expected ')' after RIGHT$ parameters");
            }
            return new FunctionNode(functionName, parameters);
        } else if (functionName.equals("MID$")) {
            List<Node> parameters = new LinkedList<>();
            if (!tokenManager.MatchAndRemove(Token.TokenType.LPAREN).isPresent()) {
                throw new RuntimeException("Expected '(' after MID$");
            }
            parameters.add(expression());
            if (!tokenManager.MatchAndRemove(Token.TokenType.COMMA).isPresent()) {
                throw new RuntimeException("Expected ',' after first parameter in MID$");
            }
            parameters.add(expression());
            if (!tokenManager.MatchAndRemove(Token.TokenType.COMMA).isPresent()) {
                throw new RuntimeException("Expected ',' after second parameter in MID$");
            }
            parameters.add(expression());
            if (!tokenManager.MatchAndRemove(Token.TokenType.RPAREN).isPresent()) {
                throw new RuntimeException("Expected ')' after MID$ parameters");
            }
            return new FunctionNode(functionName, parameters);
        } else if (functionName.equals("NUM$")) {
            List<Node> parameters = new LinkedList<>();
            if (!tokenManager.MatchAndRemove(Token.TokenType.LPAREN).isPresent()) {
                throw new RuntimeException("Expected '(' after NUM$");
            }
            parameters.add(expression());
            if (!tokenManager.MatchAndRemove(Token.TokenType.RPAREN).isPresent()) {
                throw new RuntimeException("Expected ')' after NUM$ parameter");
            }
            return new FunctionNode(functionName, parameters);
        } else if (functionName.equals("VAL")) {
            List<Node> parameters = new LinkedList<>();
            if (!tokenManager.MatchAndRemove(Token.TokenType.LPAREN).isPresent()) {
                throw new RuntimeException("Expected '(' after VAL");
            }
            parameters.add(expression());
            if (!tokenManager.MatchAndRemove(Token.TokenType.RPAREN).isPresent()) {
                throw new RuntimeException("Expected ')' after VAL parameter");
            }
            return new FunctionNode(functionName, parameters);
        } else if (functionName.equals("VAL%")) {
            List<Node> parameters = new LinkedList<>();
            if (!tokenManager.MatchAndRemove(Token.TokenType.LPAREN).isPresent()) {
                throw new RuntimeException("Expected '(' after VAL%");
            }
            parameters.add(expression());
            if (!tokenManager.MatchAndRemove(Token.TokenType.RPAREN).isPresent()) {
                throw new RuntimeException("Expected ')' after VAL% parameter");
            }
            return new FunctionNode(functionName, parameters);
        }

        return null;
    }







    /*/
    functionInvocation allows us to find pre defined functions and parse them
    we first want to get the functionName by chaking for word tokens and setting a string equal to its value
    we then iteratre through  a linked list of parameters exmaining how many parameters for each specific function
    we build out the paramteres by calling expression in order to get everything insinde the paramteres of each method (if they reqiure parameters
    we continue parsing untill we reach the end using a flag to keep track of the loop through the linked list and return a new FunctionNode
    at the end
     */

    /*/
    expression() is meant to examining the list for Plus and Minus tokens and create Arithmetic expression tokens based on the token we find
    We can create Optionals for both plus and minus and can call MatchAndRemove in order to store the values of these tokens in the Optinonal.
    if we find that a value is present (plus or minus) we can call term() on the right node of the AST and return a new Math Token based accordingly.
    At the end we can return the left node of the AST.
     */

    public Node expression() {
        Node left = term();
        while (tokenManager.MoreTokens()) {
            Optional<Token> plus = tokenManager.MatchAndRemove(Token.TokenType.PLUS);
            Optional<Token> minus = tokenManager.MatchAndRemove(Token.TokenType.MINUS);
            if (plus.isPresent()) {
                Node right = term();
                left = new MathOpNode(MathOpNode.TypeOfMathToken.ADD, left, right);
            }
            else if (minus.isPresent()) {
                Node right = term();
                left = new MathOpNode(MathOpNode.TypeOfMathToken.SUBTRACT, left, right);
            }
            else {
                break;
            }
        }
        return left;
    }



    /*/
    Similarly To expression, term we examine the list in search for Multiplication and Division Tokens.
    We can create Optionals for both times and divied  and can call MatchAndRemove in order to store the values of these tokens in the Optinonal.
    if we find that a value is present (times  or divied) we can call factor() on the right node of the AST and return a new Math Token based accordingly.
    At the end we can return the left node of the AST.

     */
    private Node term() {
        Node left = factor();
        while(tokenManager.MoreTokens()){
            Optional<Token>times = tokenManager.MatchAndRemove(Token.TokenType.TIMES);
            Optional<Token>divied = tokenManager.MatchAndRemove(Token.TokenType.DIVIED);
            if(times.isPresent()){
                Node right = factor();
                left = new MathOpNode(MathOpNode.TypeOfMathToken.MULTIPLY,left,right);
            }
            else if(divied.isPresent()){
                Node right = term();
                left = new MathOpNode(MathOpNode.TypeOfMathToken.DIVIDE,left,right);
            }
            else{
                break;
            }
        }
        return left;

    }

    /*/
    factor returns the leaf of the AST.
    First we can create an Optional of MatchAndRemove for a Token of Number Type.
    We can then examine a String that contains the tokens of the next. If we see that a value contains a decimal point
    we can then know that it is a float note and create a node as such by Parsing the value.
    else we know that it has to be an integer node.
    We want to examine parenthesis tokens from ( to ) as we know that a ( means a new expression and an ) means the end of one
    If we see a LPAREN token that means that we can call the expression method to build out the new expression inside the parenthesis
    if we see a RPAREN that means we can just return teh build out expression.
    **UPDATE We have now given factor the ability to check for WORD tokens which are treated like variables (Ex X,Y,Z) in which we return a new VariableNode with the value of the WORD Token
    At the end we can throw Exceptions as needed and appropriate
    **UPDATE we have now added to factor the ability to handle StinrgLiteral Tokens by the indetifier ""
     */

    private Node factor() {
        Optional<Token> nextToken = tokenManager.MatchAndRemove(Token.TokenType.NUMBER);
        Optional<Token> function = tokenManager.MatchAndRemove(Token.TokenType.FUNCTION);
        if (function.isPresent()) {
            return functionInvocation(function.get().returnToken());
        } else if (nextToken.isPresent()) {
            String tokenValue = nextToken.get().returnToken();
            if (tokenValue.contains(".")) {
                return new FloatNode(Float.parseFloat(tokenValue));
            } else {
                return new IntegerNode(Integer.parseInt(tokenValue));
            }
        } else if (tokenManager.MatchAndRemove(Token.TokenType.LPAREN).isPresent()) {
            Node expressionResult = expression();
            if (tokenManager.MatchAndRemove(Token.TokenType.RPAREN).isPresent()) {
                return expressionResult;
            } else {
                return null;
            }
        } else {
            Optional<Token> wordToken = tokenManager.MatchAndRemove(Token.TokenType.WORD);
            if (wordToken.isPresent()) {
                String variableName = wordToken.get().returnToken();
                return new VariableNode(variableName);
            } else {
                Optional<Token> stringToken = tokenManager.MatchAndRemove(Token.TokenType.STRINGLITERAL);
                if (stringToken.isPresent()) {
                    String stringValue = stringToken.get().returnToken();
                    return new StringNode(stringValue);
                } else {
                    throw new RuntimeException("Invalid expression");
                }
            }
        }
    }


}