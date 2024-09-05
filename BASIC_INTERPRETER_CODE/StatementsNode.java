import java.util.List;

public class StatementsNode extends StatementNode {
    private List<StatementNode> statementNodes; //List that contains all the statements we find in the file.

    public StatementsNode(List<StatementNode> statementNodes) {
        this.statementNodes = statementNodes;
    }

    public List<StatementNode> getStatementNodes() {
        return statementNodes;
    }

    /*/
    Override the toString method to simply return the valueOf the list of StatementNodes
     */
    @Override
    public String toString() {
        return "StatementsNode(" + statementNodes + ")";
    }
}
