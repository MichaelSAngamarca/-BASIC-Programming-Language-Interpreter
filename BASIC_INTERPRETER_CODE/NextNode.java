public class NextNode extends StatementNode {
    /*/
    private member that holds the value of the varaible name identifier of string type
     */
    private String variableName;

    public NextNode(String variableName){
        this.variableName = variableName;
    }

    public String getVariableName(){
        return variableName;
    }
    /*/
    Override the toString method to simply return the valueOf the varaibleName members
     */
    @Override
    public String toString() {
        return "NextNode(" + variableName + ')';
    }
}
