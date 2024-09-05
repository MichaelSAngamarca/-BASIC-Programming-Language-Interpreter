public class FloatNode extends Node{
    private float floatValue; // memeber to hold the value of the floatNode

    public FloatNode(float floatValue){
        this.floatValue = floatValue;
    }


    //Acessor to get the value of the float
    public float getValue()   {
        return floatValue;
    }

    /*/
   Override the toString method to simply return the valueOf the floatValue member
    */
    @Override
    public String toString() {
        return "FloatNode(" + floatValue + ")";
    }
}
