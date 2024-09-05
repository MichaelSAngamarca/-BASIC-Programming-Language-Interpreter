public class LabeledStatementNode extends StatementNode{

    /*/
    private member that holds the label name of string type
     */
    private String labelString;
    /*/
    private member that holds the refrence tonthe label of StatementNode type
     */
    private StatementNode labelReference;

    public LabeledStatementNode(String labelString, StatementNode labelReference){
        this.labelString = labelString;
        this.labelReference = labelReference;

    }

    public String getLabelString(){
        return labelString;
    }

    public StatementNode getLabelReference() {
        return labelReference;
    }

    /*/
    Override the toString method to simply return the valueOf the labelString and labelReference members
     */
    @Override
    public String toString() {
        return "LabeledStatementNode(" + labelString + ", " + labelReference + ")";
    }
}
