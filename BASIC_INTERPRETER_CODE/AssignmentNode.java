public class AssignmentNode extends StatementNode {
    private VariableNode variableNode; //Member to store VariableNode for which is being assigned
    private Node valueNode; // Member to store the value associated with the VariableNode

    public AssignmentNode(VariableNode variableNode, Node valueNode) {
        this.variableNode = variableNode;
        this.valueNode = valueNode;
    }

    public VariableNode getVariableNode() {
        return variableNode;
    }

    public Node getValueNode() {
        return valueNode;
    }

    /*/
    Override the toString method to simply return the valueOf the VariableNode and valueNode member
     */
    @Override
    public String toString() {
        return "AssignmentNode(" + variableNode + ", " + valueNode + ")";
    }
}
