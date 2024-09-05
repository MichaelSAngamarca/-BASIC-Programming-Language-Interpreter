public class IfNode extends StatementNode {
    //private memebr that holds a condition value of BooleanExpresionNode type
    private BooleanExpressionNode condition;
    //private memeber that holds the then statement of StatementNode type.
    private StatementNode thenStatement;

    public IfNode(BooleanExpressionNode condition, StatementNode thenStatement){
        this.condition = condition;
        this.thenStatement = thenStatement;
    }

    public BooleanExpressionNode getCondition (){
        return condition;
    }

    public StatementNode getThenStatement(){
        return thenStatement;
    }

    /*/
    Override the toString method to simply return the valueOf the condition and the then Statement members
     */
    @Override
    public String toString() {
        return "IFNODE(" + condition + "," + thenStatement + ")";
    }
}
