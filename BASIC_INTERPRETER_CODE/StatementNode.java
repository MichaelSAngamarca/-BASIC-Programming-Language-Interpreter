import java.sql.Statement;
import java.util.List;

public abstract class StatementNode extends Node {
    /*/
    private member that is used in order to hold the next statement in our statement list node
     */
    private StatementNode next;

    /*/
    get the next value in our statement node
     */
    public StatementNode getNext(){
        return next;
    }

    /*/
    set the next value of our statementNode
     */
    public void setNext(StatementNode next){
        this.next = next;
    }
}
