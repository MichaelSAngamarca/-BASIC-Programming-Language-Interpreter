import java.util.List;

public class InputNode extends StatementNode{
    // A private member to hold in a prompt of any value
    private Node prompt;
    //Private member to hold all the arguments following the prompt in an input statement
    private List<VariableNode> variables;

    public InputNode(Node prompt, List<VariableNode> variables){
        this.prompt = prompt;
        this.variables = variables;
    }

    public Node getPrompt(){
        return prompt;
    }
    public List<VariableNode> getVariables(){
        return variables;
    }
    /*/
   Override the toString method to simply return the valueOf the InputNode List member and the prompt Node Member
    */
    @Override
    public String toString() {
        return "InputNode(" + prompt + variables + ")";
    }
}
