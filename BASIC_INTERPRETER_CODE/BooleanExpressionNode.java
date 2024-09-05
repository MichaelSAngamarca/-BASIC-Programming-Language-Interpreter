public class BooleanExpressionNode extends StatementNode{
    /*/
    private member that holds the first half of a condition of Node type
     */
    private Node left;
    /*/
    private member that holds the right half of a condiiton if needed of Node Tpye
     */
    private Node right;
    /*/
    private member that holds the TokenType of the operator
     */
    private Token.TokenType operator;

    public BooleanExpressionNode(Node left, Node right, Token.TokenType operator){
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public Node getLeft(){
        return left;
    }

    public Node getRight(){
        return right;
    }

    public Token.TokenType getOperator(){
        return operator;
    }

    /*/
    Override the toString method to simply return the valueOf the left, rightm and Operator members
     */
    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }
}
