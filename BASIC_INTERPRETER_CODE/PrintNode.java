import java.util.List;

public class PrintNode extends StatementNode {
    private List<Node> nodesToPrint; //List Member to store all the different PrintStatements we find

    public PrintNode(List<Node> nodesToPrint) {
        this.nodesToPrint = nodesToPrint;
    }

    public List<Node> getNodesToPrint() {
        return nodesToPrint;
    }

    /*/
    ToString Override to get the value of teh List of PrintNodes
     */
    @Override
    public String toString() {
        return "PrintNode(" + nodesToPrint + ")";
    }
}
