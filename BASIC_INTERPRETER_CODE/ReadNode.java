import java.util.LinkedList;
import java.util.List;

public class ReadNode extends StatementNode{
    //Private List Member to hold the list of variables
    List<VariableNode> listOfVariables = new LinkedList<>();

    public ReadNode(List<VariableNode> listOfVariables){
        this.listOfVariables = listOfVariables;
    }

    public List<VariableNode> getListOfVariables(){
        return listOfVariables;
    }
    /*/
   Override the toString method to simply return the valueOf the ReadNode List member
    */
    @Override
    public String toString() {
        return "ReadNode(" + listOfVariables + ")";
    }
}
