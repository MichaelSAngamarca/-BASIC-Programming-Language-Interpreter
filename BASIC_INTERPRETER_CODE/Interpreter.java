import java.util.*;

public class Interpreter {
    /*/
    statementListNode is the parsed statementList from the parser previously that is used to traverse the ast
     */
    private StatementsNode statementListNode;

    /*/
    Que that is used to store the dataNodes/Statements in the ast while traversing
     */

    private Queue<Node> dataQueue = new LinkedList<>();
    /*/
    HashMap used to store the values of the LabelStatementNodes while traversing the ast
     */
    private HashMap<String, LabeledStatementNode> labeledStatementNodeMap = new HashMap<>();

    private Map<String, Integer> stringIntegerMap = new HashMap<>();
    private Map<String, Float> stringFloatMap = new HashMap<>();
    private Map<String, String> stringMap = new HashMap<>();
    /*/
    boolean member in order to control the flow of the program
     */
    private boolean loop;
    /*
    private Member that is used in order to keep track of the currentStatement we are evaluating in our interpreter
     */
    private StatementNode currentStatement;
    /*/
    private member stack that is used to store all the varying nodes and values to be used to control the flow of values we keep throught interpreting
     */
    private Stack<StatementNode> statementNodeStack;
    /*/
    Random value that is used to generate a random number in the random method
     */
    private Random rand;


    /*/
    construct that takes in the parsed StatementList as well as calls the methods to traverse the ast for data and label nodes before running
     */
    public Interpreter(StatementsNode statementListNode) {
        this.statementListNode = statementListNode;
        this.statementNodeStack = new Stack<>();
        walkDataAst();
        walkLabelAst();
        walkNext();
    }

    /*/
    We have changed Interpret to know loop through the program to control the flow
    we simply loop over the statementNodes and call executeStatement on each one passing in the currentStatement we are on
    we then set the value after evaluation to the next node in the statement list and so on
     */
    public void Interpret() {
        loop = true;
        List<StatementNode> statementNodes = statementListNode.getStatementNodes();
        currentStatement = statementNodes.get(0);
        while (loop && currentStatement != null) {
            executeStatement(currentStatement);
            currentStatement = currentStatement.getNext();
        }
    }

    /*/
    executeStatement is used in order to evaluate the indivdual nodes in each statement in a statementList
    we simply check if the statement is an instance of varying nodes and calling the appropriate evaluate method
     */
    public void executeStatement(StatementNode statement) {
        if (statement instanceof EndNode) {
            evaluateEnd();
        }
        else if (statement instanceof PrintNode) {
            evaluatePrint((PrintNode) statement);
        } else if (statement instanceof ReadNode) {
            evaluateRead((ReadNode) statement);
        } else if (statement instanceof InputNode) {
            evaluateInput((InputNode) statement);
        } else if (statement instanceof AssignmentNode) {
            evaluateAssignment((AssignmentNode) statement);
        } else if (statement instanceof IfNode) {
            evaluateIf((IfNode) statement);
        } else if (statement instanceof GoSubNode) {
            evaluateGosub((GoSubNode) statement);
        } else if (statement instanceof ReturnNode) {
            evaluateReturn();
        } else if (statement instanceof NextNode) {
            evaluateNext();
        } else if (statement instanceof ForNode) {
            evaluateFor((ForNode) statement);
        }
    }

    /*/
    walkDataAst is used to traverse the AST in search for DataStatements/Nodes and add them to a Queue
    We traverse the StatementList and check to see nodes that are instances of DataNodes
    When done we create a list of these nodes and obtain the value from the statements
    At the end we traverse each individual value and add it to the queue
    at the end return the Queue of data nodes
     */
    public Queue<Node> walkDataAst() {
        for (StatementNode node : statementListNode.getStatementNodes()) {
            if (node instanceof DataNode) {
                DataNode dataNode = (DataNode) node;
                List<Node> dataList = dataNode.getDataList();
                for (Node data : dataList) {
                    dataQueue.offer(data);
                }
            }
        }
        return dataQueue;
    }

    /*/
    walkNest is used in order to setUp the linkedList of values in order to easily traverse the nodes in our statementList
    we simply create a new list of statementNodes and store the values of our statementNode
    We loop through the statementNodes and set the currentNode to the first statementNode and set the nextNode to the value ahead etc
    We simply set the last value to null in order to signify that we are at the end of the list
     */
    public void walkNext() {
        List<StatementNode> statementNodes = statementListNode.getStatementNodes();
        for (int i = 0; i < statementNodes.size() - 1; i++) {
            StatementNode currentNode = statementNodes.get(i);
            StatementNode nextNode = statementNodes.get(i + 1);
            currentNode.setNext(nextNode);
        }
        StatementNode lastNode = statementNodes.get(statementNodes.size() - 1);
        lastNode.setNext(null);
    }

    /*/
    walkLabelAst similar to walkDataAst is used to traverse the AST and search for labeledStatementNodes
    We traverse the StatementList AST and get the values of these label nodes  and insert them in the hash map with its name and its values.
    At the end we return the HashMap of values of labeledStatementNodes
     */
    public HashMap<String, LabeledStatementNode> walkLabelAst() {
        for (StatementNode node : statementListNode.getStatementNodes()) {
            if (node instanceof LabeledStatementNode) {
                LabeledStatementNode labelNode = (LabeledStatementNode) node;
                String label = labelNode.getLabelString();
                labeledStatementNodeMap.put(label, labelNode);
            }
        }
        return labeledStatementNodeMap;
    }

    /*/
    Return a new Random Integer
     */
    public int random() {
        return rand.nextInt();
    }

    /*/
    Return a substring that starts at the beginning and goes until we reach the integer parameter characters
     */
    public static String left(String data, int characters) {
        return data.substring(0, characters);
    }

    /*/
    Return a substring that begins at the end and goes until we reach the value from subtracting the length of the string from the given character parameter
     */

    public static String right(String data, int characters) {
        return data.substring(data.length() - characters);
    }

    /*/
    Return a substring that starts from a given index and goes until we reach a specific count given in teh second parameter
     */
    public static String mid(String data, int startIndex, int count) {
        int endIndex = startIndex + count;

        return data.substring(startIndex, endIndex);
    }

    /*/
    Return the value of an integer parameter as a String
     */
    public static String numToString(int num) {
        return String.valueOf(num);
    }

    /*
    Return the value of a float parameter as a String
     */
    public static String numToString(float num) {
        return String.valueOf(num);
    }

    /*/
    Return the value of a String parameter and parse it as a Integer
     */
    public static int valToInt(String value) {
        return Integer.parseInt(value);
    }

    /*/
    Return thew value of a String parameter and parse it as a Float
     */
    public static float valToFloat(String value) {
        return Float.parseFloat(value);
    }

    /*/
    evaluateInteger takes in a Node value and is used to evaluate Integer Node or mathOpNodes in the ast
    We first evaluate to see if the node if an instance of a IntegerNode and set the value of our node parameter to it and returns its value
    If we encounter a math opNode we first set the value of the parameter to a new MathOpNode then recursively call evaluateInteger to obtain the leftValue and rightValue of the Expression
    Using a switch statement we go through the mathNode mathToken enum found and return the answer from the arithmetic done in the switch case
     */
    public int evaluateInteger(Node node) {
        if (node instanceof IntegerNode) {
            IntegerNode intNode = (IntegerNode) node;
            return intNode.getValue();
        } else if (node instanceof VariableNode) {
            String variableName = ((VariableNode) node).getVariableName();
            if (stringIntegerMap.containsKey(variableName)) {
                return stringIntegerMap.get(variableName);
            } else {
                throw new RuntimeException("Variable not found: " + variableName);
            }
        } else if (node instanceof MathOpNode) {
            MathOpNode mathNode = (MathOpNode) node;
            int leftValue = evaluateInteger(mathNode.getLeft());
            int rightValue = evaluateInteger(mathNode.getRight());
            switch (mathNode.mathToken) {
                case ADD:
                    return leftValue + rightValue;
                case SUBTRACT:
                    return leftValue - rightValue;
                case MULTIPLY:
                    return leftValue * rightValue;
                case DIVIDE:
                    if (rightValue == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    return leftValue / rightValue;
                default:
                    throw new IllegalArgumentException("Unknown operation: " + mathNode.mathToken);
            }
        } else if (node instanceof FunctionNode) {
            FunctionNode functionNode = (FunctionNode) node;
            String functionName = functionNode.getFunctionName();
            List<Node> parameters = functionNode.getArguments();

            if (functionName.equalsIgnoreCase("RANDOM")) {
                for (Node param : parameters) {
                    evaluateInteger(param);
                }
                return random();
            } else {
                throw new IllegalArgumentException("Calling a function that does not return a integer type");
            }
        }
        throw new IllegalArgumentException("Node is not an instance of integer node, mathOpNode, Or FunctionNode that returns an integer type");
    }

    /*/
    evaluateFloat follows the exact saem structure as evaluateInteger with float types
     */
    public float evaluateFloat(Node node) {
        if (node instanceof FloatNode) {
            return ((FloatNode) node).getValue();
        } else if (node instanceof VariableNode) {

            String variableName = ((VariableNode) node).getVariableName();
            if (stringFloatMap.containsKey(variableName)) {
                return stringFloatMap.get(variableName);
            }
        } else if (node instanceof MathOpNode) {

            MathOpNode mathNode = (MathOpNode) node;
            float leftValue = evaluateFloat(mathNode.getLeft());
            float rightValue = evaluateFloat(mathNode.getRight());

            switch (mathNode.mathToken) {
                case ADD:
                    return leftValue + rightValue;
                case SUBTRACT:
                    return leftValue - rightValue;
                case MULTIPLY:
                    return leftValue * rightValue;
                case DIVIDE:
                    if (rightValue == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    return leftValue / rightValue;
                default:
                    throw new IllegalArgumentException("Unknown operation: " + mathNode.mathToken);
            }
        } else if (node instanceof FunctionNode) {

            FunctionNode functionNode = (FunctionNode) node;
            String functionName = functionNode.getFunctionName();
            List<Node> arguments = functionNode.getArguments();

            if (functionName.equalsIgnoreCase("VAL%")) {

                if (arguments.size() != 1) {
                    throw new IllegalArgumentException("VAL% function requires exactly one argument");
                }

                Node argumentNode = arguments.get(0);
                String stringValue = evaluateString(argumentNode);

                try {

                    return Float.parseFloat(stringValue);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid float value provided in VAL% function");
                }
            } else {

                throw new IllegalArgumentException("Calling a function that does not return a float type or is not VAL%");
            }
        }


        throw new IllegalArgumentException("Node is not an instance of float node, math op node, function node with VAL% or variable node with float type");
    }

    /*/
    evulateString is used in order to evualate string nodes for print and output
    We simply check if the node is an instance of a string node and get its value and return it
     */
    public String evaluateString(Node node) {
        if (node instanceof StringNode) {
            StringNode stringNode = (StringNode) node;
            return stringNode.getStringValue();
        }
        throw new IllegalArgumentException("Node is not an instance of stringnode");
    }


    /*/
    evaluteRead is used in order to evaluate readNodes and set their values to the hashmap members in order to be used to read the data of a datanoce
    We simply check if the Map for each specific value is in the hashmap and if not add its value to the hashMap
    We then attempt to parse its value using the function methods created before such as valTOFloat and valToInt
    If these values don't exist we create and add these values to the map.
     */

    public void evaluateRead(ReadNode readNode) {
        List<VariableNode> variables = readNode.getListOfVariables();
        for (VariableNode variable : variables) {
            if (dataQueue.isEmpty()) {
                throw new RuntimeException("Data Queue Is Empty");
            }

            String dataValue = String.valueOf(dataQueue.poll());

            String variableName = variable.getVariableName();

            if (stringIntegerMap.containsKey(variableName)) {
                int intValue = valToInt(dataValue);
                stringIntegerMap.put(variableName, intValue);
            } else if (stringFloatMap.containsKey(variableName)) {
                float floatValue = valToFloat(dataValue);
                stringFloatMap.put(variableName, floatValue);
            } else if (stringMap.containsKey(variableName)) {
                stringMap.put(variableName, dataValue);
            } else {
                try {
                    int intValue = valToInt(dataValue);
                    stringIntegerMap.put(variableName, intValue);
                } catch (NumberFormatException e) {
                    try {
                        float floatValue = valToFloat(dataValue);
                        stringFloatMap.put(variableName, floatValue);
                    } catch (NumberFormatException ex) {
                        stringMap.put(variableName, dataValue);
                    }
                }
            }
        }
    }

    /*/
    in evaluateAssignment we use this in order to evulate any assignmentNodes and set the variableName and its value to the hasmap of its according type
    we check these values are instances of a specific node and if so call the appropriate evaluate Function and put this value into the hashmap of its accordin type
    We throw an error in case none of these values exist in the assigned variable at runtime.
     */

    public void evaluateAssignment(AssignmentNode assignmentNode) {
        VariableNode variableNode = assignmentNode.getVariableNode();
        String variableName = variableNode.getVariableName();
        Node valueNode = assignmentNode.getValueNode();
        if (valueNode instanceof IntegerNode || valueNode instanceof MathOpNode || valueNode instanceof FunctionNode) {
            int intValue = evaluateInteger(valueNode);
            stringIntegerMap.put(variableName, intValue);
        } else if (valueNode instanceof FloatNode) {
            float floatValue = evaluateFloat(valueNode);
            stringFloatMap.put(variableName, floatValue);
        } else if (valueNode instanceof StringNode) {
            String stringValue = evaluateString(valueNode);
            stringMap.put(variableName, stringValue);
        } else {
            throw new IllegalArgumentException("Unsupported value node type in assignment");
        }
    }

    /*/
    evaluateInput is used to evaluate inputNodes and obtain the value for the node
    We first obtain the string of the prompt in the first parameter
    we iterate through the list of varaiblenodes and by using a scanner let the user input a value
    we then check the final character in the parameter and see if it contains an appropriate output type
    we handle each of these cases and add the value to the appropriate hashmap
    we handle exceptions in case an incorrect value is inputted at runtime
     */
    public void evaluateInput(InputNode inputNode) {
        Node promptNode = inputNode.getPrompt();
        String promptString = evaluateString(promptNode);

        List<VariableNode> variables = inputNode.getVariables();

        Scanner scanner = new Scanner(System.in);

        System.out.print(promptString + ": ");
        for (VariableNode variableNode : variables) {
            String userInput = scanner.nextLine();

            String variableName = variableNode.getVariableName();

            char lastChar = variableName.charAt(variableName.length() - 1);

            if (lastChar == '$') {
                stringMap.put(variableName, userInput);
            } else if (lastChar == '%') {
                try {
                    float floatValue = Float.parseFloat(userInput);
                    stringFloatMap.put(variableName, floatValue);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid type for float parameter " + variableName);
                }
            } else {
                try {
                    int intValue = Integer.parseInt(userInput);
                    stringIntegerMap.put(variableName, intValue);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid type for integer parameter " + variableName);
                }
            }
            if (variables.indexOf(variableNode) != variables.size() - 1) {
                System.out.print(promptString + ": ");
            }
        }
    }

    /*/
    in evaluateIf, we determine the condition for an if statement and call a separate evaluate function in order to determine the type of sign in the statement
    Following that if there exists a condition we can simply get the statement condition and obtain the label that points after the condition from the labelHashMap
     */
    public void evaluateIf(IfNode ifNode) {
        BooleanExpressionNode condition = ifNode.getCondition();
        boolean conditionResult = evaluateBoolean(condition);
        if (conditionResult) {
            StatementNode thenStatement = ifNode.getThenStatement();
            executeStatement(thenStatement);
        }
    }

    /*/
    evaluateFor is used in order o evaluate for nodes and obtain teh values and control the flow of looping
    We first obtain the assignmentNode, the variableName, the Initial value, the endvalue, and the final value for the loop
    We first set the default integer step to 1 if it is not defined or givven a  value previously in the setup
    We then set the currentValue to the initial value and then checl that of the varibleName exists in the key and if not insert it into the map to store
    We then examine for nextNodes in order to make increment the step for the loop
    We finally push the forNode and its evaluated value to the stack.
     */

    public void evaluateFor(ForNode forNode) {
        AssignmentNode assignmentNode = forNode.getAssignmentNode();
        String variableName = assignmentNode.getVariableNode().getVariableName();
        int initialValue = evaluateInteger(assignmentNode.getValueNode());

        Node endValue = forNode.getEndValue();
        int finalValue = evaluateInteger(endValue);

        int step = 1;
        if (forNode.getStepValue() != null) {
            step = evaluateInteger(forNode.getStepValue());
        }

        int currentValue = initialValue;
        if (!stringIntegerMap.containsKey(variableName)) {
            stringIntegerMap.put(variableName, initialValue);
        } else {
            currentValue = stringIntegerMap.get(variableName) + step;
            stringIntegerMap.put(variableName, currentValue);
        }
        if (step > 0 && currentValue > finalValue) {
            int nextCount = 1;
            StatementNode nextStatement = forNode.getNext();
            while (nextCount != 0 && nextStatement != null) {
                if (nextStatement instanceof NextNode) {
                    nextCount--;
                } else if (nextStatement instanceof ForNode) {
                    nextCount++;
                }
                nextStatement = nextStatement.getNext();
            }
            currentStatement = nextStatement.getNext();
        } else {
            statementNodeStack.push(forNode);
        }

    }


    /*/
    evaluateBoolean evaluates any BooleanExpression node and attempts to obtain the value of the condition in any statement which requires a condition
    We first obtain the left and right signs of the condition by calling the methods get left and get right and obtain the type of operator by calling getOperator
    to obtain the values in the condition we get call what's left of the expression by evaluating the integer and getting wahts after the condition by evaluating wahts to the right
    We then can use a switch statement and return the boolean value of the expression depending on the type of condition token
    If none appears we return an appropriate error
     */
    public boolean evaluateBoolean(Node node){
        if(node instanceof BooleanExpressionNode){
            BooleanExpressionNode booleanExpressionNode = (BooleanExpressionNode) node;
            Node leftNode = booleanExpressionNode.getLeft();
            Node rightNode = booleanExpressionNode.getRight();
            Token.TokenType operator = booleanExpressionNode.getOperator();

            int leftValue = evaluateInteger(leftNode);
            int rightValue = evaluateInteger(rightNode);

            switch (operator) {
                case EQUALS:
                    return leftValue == rightValue;
                case NOTEQUAL:
                    return leftValue != rightValue;
                case GREATERTHAN:
                    return leftValue > rightValue;
                case LESSTHAN:
                    return leftValue < rightValue;
                case GREATOREQUAL:
                    return leftValue >= rightValue;
                case LESSOREQUAL:
                    return leftValue <= rightValue;
                default:
                    throw new IllegalArgumentException("Unknown comparison operator: " + operator);
            }
        } else {
            throw new IllegalArgumentException("Node is not a boolean expression");
        }
    }
    /*/
    evaluateGosub if used to evaluate goSub nodes and obtain their labels to obtain their name and value
    we first obtain the identifier from the goSubNode identifier
    we then check if that identifier exists within the label hashmap
    if it does we can get the label from the label hashmap and get the statement that follows
    we then call executeStatement on that statement we get from the label hashmap
     */
    public void evaluateGosub(GoSubNode goSubNode) {
        String label = goSubNode.getIdentifier();
        if (labeledStatementNodeMap.containsKey(label)) {
            LabeledStatementNode labeledStatementNode = labeledStatementNodeMap.get(label);
            StatementNode statementNode = labeledStatementNode.getLabelReference();
            executeStatement(statementNode);
        } else {
            throw new RuntimeException("Label not found: " + label);
        }
    }


    /*/
    evaluateReturn is used to evaluateReturnNodes
    we simply do this by first checking if the statementNodeStack is empty and if not pop the top of the stack and setting that next statment replacing the currentStatement
    if not we throw an error that there is no previous statement to return to
     */
    public void evaluateReturn() {
        if (!statementNodeStack.isEmpty()) {
            StatementNode nextStatement = statementNodeStack.pop();
            currentStatement.setNext(nextStatement);
        } else {
            throw new RuntimeException("No previous statement to return to");
        }
    }

    /*/
    evaluateNext is used in order to check an evalute Next nodes to be used in for nodes if nodes etc
    we first check that if teh stack is empty and if not we pop the top of the stack and set that value to the next value in our currentStatement
    if we see that we have reached the end we simply end the loop by calling evaluateEnd
     */
    public void evaluateNext() {
        if (!statementNodeStack.isEmpty()) {
            StatementNode nextStatement = statementNodeStack.pop();
            currentStatement.setNext(nextStatement);
        }
        else {
            evaluateEnd();
        }
    }


    /*
    evaluateEnd simply sets the loop too false to signal that the program must end and to stop interpreting
     */
    public void evaluateEnd(){
        loop = false;
    }
    /*/
    evaluatePrint is used in order to print out the values of specific nodes such aas INtegernOdes mAthopNodes FunctionsNode that return values and FloatNodes.
    We first check if the value of the printNode paramtere is null and throw an error.
    We iterate through the list of nodes in the print node and check if these nodes are instances of the specific nodes that can print a value
    While iterating we simply print to the console the value.
     */
    public void evaluatePrint(PrintNode printNode) {
        if (printNode == null) {
            throw new RuntimeException("PrintNode cannot be null");
        }
        List<Node> nodesToPrint = printNode.getNodesToPrint();
        for (Node node : nodesToPrint) {
            if (node instanceof MathOpNode) {
                int result = evaluateInteger(node);
                System.out.println(result);
            }
            else if (node instanceof VariableNode) {
                VariableNode varNode = (VariableNode) node;
                String variableName = varNode.getVariableName();
                if (stringIntegerMap.containsKey(variableName)) {
                    System.out.println(stringIntegerMap.get(variableName));
                } else if (stringFloatMap.containsKey(variableName)) {
                    System.out.println(stringFloatMap.get(variableName));
                } else if (stringMap.containsKey(variableName)) {
                    System.out.println(stringMap.get(variableName));
                } else {
                    throw new RuntimeException("Variable not found: " + variableName);
                }
            } else if (node instanceof IntegerNode || node instanceof FunctionNode) {
                int intResult = evaluateInteger(node);
                System.out.println(intResult);
            } else if (node instanceof FloatNode) {
                float floatResult = evaluateFloat(node);
                System.out.println(floatResult);
            } else if (node instanceof StringNode) {
                String stringResult = evaluateString(node);
                System.out.println(stringResult);
            }
        }
    }

}
