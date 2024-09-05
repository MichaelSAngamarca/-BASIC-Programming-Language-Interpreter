import java.util.List;

public class FunctionNode extends StatementNode{

    /*/
    private member that holds the value of the functionName of String type
     */
    private String functionName;
    /*
    private List memebr that holds the value of the arguments of Node Type
     */
    private List<Node> arguments;
    public FunctionNode(String functionName, List<Node> arguments){
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public String getFunctionName(){
        return functionName;
    }

    public List<Node> getArguments() {
        return arguments;
    }

    /*/
    Override the toString method to simply return the valueOf the FunctionName and Arugments List Members
     */
    @Override
    public String toString() {
        return "FUNCTIONNODE(" + functionName + "," + arguments;
    }


}
