public class GoSubNode extends StatementNode{

    /*/
    private member that holds the value for the indetifier of String type
     */
    private String identifier;

    public GoSubNode(String identifier){
        this.identifier = identifier;
    }
    public String getIdentifier(){
        return identifier;
    }

    /*/
    Override the toString method to simply return the valueOf the identifier member
     */
    @Override
    public String toString() {
        return "GOSUBNODE(" + identifier + ")";
    }
}
