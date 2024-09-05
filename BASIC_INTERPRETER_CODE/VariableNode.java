public class VariableNode extends StatementNode {

    private String variableName; //Member to Store the name of our VariableNode

    public VariableNode(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

    /*/
    Override the toString method to simply return the valueOf the variableName assoicated with the VariableNode
     */
    @Override
    public String toString() {
        return "VariableNode(" + variableName + ")";
    }
}
