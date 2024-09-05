public class WhileNode extends StatementNode{

    //private member that holds a condition value of BooleanExpresionNode type
    private BooleanExpressionNode condition;
    /*/
    private member that holds the value of the endlable of String type
     */
    private String endLabel;

    public WhileNode(BooleanExpressionNode condition, String endLabel){
        this.condition = condition;
        this.endLabel = endLabel;

    }
    public BooleanExpressionNode getCondition(){
        return condition;
    }

    public String getEndLabel(){
        return endLabel;
    }
    /*/
    Override the toString method to simply return the valueOf the condition and the endLabel members
     */
    @Override
    public String toString() {
        return "WHILENODE(" + condition + "," + endLabel + ")";
    }
}
