public class MathOpNode extends Node{
    public enum TypeOfMathToken{
        ADD,SUBTRACT,MULTIPLY,DIVIDE
    }

    //Member to hold the Type of arithmetic token
    public TypeOfMathToken mathToken;
    //Node that represents the left node of the AST
    private Node left;
    //Node that represents the right node of the AST
    private Node right;

    public MathOpNode(TypeOfMathToken mathToken, Node left, Node right){
        this.left = left;
        this.right = right;
        this.mathToken = mathToken;

    }

    public Node getLeft(){
        return left;
    }
    public Node getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "MathOpNode"+"(" + left.toString() + " " + operationToString() + " " + right.toString() + ")";
    }

    /*/
    Helper method that uses a switch case in order to determine the associated symbol for the Operation Enum.
     */
    private String operationToString(){
        switch (mathToken) {
            case ADD:
                return "+";
            case SUBTRACT:
                return "-";
            case MULTIPLY:
                return "*";
            case DIVIDE:
                return "/";
            default:
                throw new IllegalArgumentException("Unknown operation: " + mathToken);
        }
    }
}

