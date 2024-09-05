import java.util.LinkedList;
import java.util.List;

public class DataNode extends StatementNode{

    //Private List Member To Hold all Arguments for a DataNode
    List<Node> dataList = new LinkedList<>();

    public DataNode(List<Node> dataList){
        this.dataList = dataList;
    }

    public List<Node> getDataList(){
        return dataList;
    }
    /*/
   Override the toString method to simply return the valueOf the DataNode List member
    */
    @Override
    public String toString() {
        return "DataNode( " + dataList + ")";
    }
}
