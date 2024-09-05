public class StringNode extends Node {
    //Private String memebr to store the value of the StringNode
    private String stringValue;

    public StringNode(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    /*/
   Override the toString method to simply return the valueOf the stringValue member
    */
    @Override
    public String toString() {
        return "StringNode(\"" + stringValue + "\")";
    }
}
