public class ForNode extends StatementNode {

    /*/
    private memebr that holds the assignmentNode value
     */
    private AssignmentNode assignmentNode;
    /*/
    private member that holds the endValue of Node type
     */
    private Node endValue;
    /*/
    private member that holds the step value of Node Type (Integer Node)
     */
    private Node stepValue;  // Optional, defaults to 1 if not provided

    public ForNode(AssignmentNode assignmentNode, Node endValue, Node stepValue) {
        this.assignmentNode = assignmentNode;
        this.endValue = endValue;
        this.stepValue = stepValue != null ? stepValue : new IntegerNode(1); // Default step to 1 if not provided
    }

    public AssignmentNode getAssignmentNode() {
        return assignmentNode;
    }

    public Node getEndValue() {
        return endValue;
    }

    public Node getStepValue() {
        return stepValue;
    }

    /*/
    Override the toString method to simply return the valueOf the assignmentNode, endValue, and stepValue members
     */
    @Override
    public String toString() {
        return "FORNODE(" + assignmentNode + ", " + endValue + ", " + stepValue + ")";
    }
}
