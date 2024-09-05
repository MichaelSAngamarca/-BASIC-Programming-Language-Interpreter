public class IntegerNode extends Node {
    //Member to hold the Value of An Integer Node
    private int integerValue;

    public IntegerNode(int value){
        this.integerValue = value;
    }

    //Accessor to get the value of the integer
    public int getValue(){
        return integerValue;
    }

    /*/
    Override the toString method to simply return the valueOf the integerValue member
     */
    @Override
    public String toString() {
        return "IntegerNode(" + integerValue + ")";
    }
}
