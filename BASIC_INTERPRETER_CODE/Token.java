public class Token {
    public enum TokenType{
        /*
        Have Added New Values of Comma
         */
        WORD,NUMBER,ENDOFLINE,PRINT,READ,INPUT,DATA,GOSUB,FOR,TO,STEP,NEXT,RETURN,IF,THEN,FUNCTION,WHILE,END,DO,STRINGLITERAL,GREATOREQUAL,LESSOREQUAL,NOTEQUAL,EQUALS,GREATERTHAN,LESSTHAN,LPAREN,RPAREN,PLUS,MINUS,TIMES,DIVIED,LABEL,COMMA,RANDOM,LEFT$,RIGHT$,MID$,NUM$,VAL,VAL$

    }

    private TokenType typeOfToken;
    private String value;
    //Line Number Is Used to keep track of which line we are on in the contents of the file
    private int lineNumber;
    // characterPosition is used to keep track of were we are in the line of the file.
    private int characterPosition;

    //This Token constructor is needed in order to create tokens that do not contain a value such as ENDOFLINE tokens.
    //We Set the type of Token and its location at the LineNumber and characterPosition.
    public Token(TokenType tokenType, int lineNumber, int characterPosition){
        this.typeOfToken = tokenType;
        this.lineNumber = lineNumber;
        this.characterPosition = characterPosition;
    }

    // This Token Constructor Is needed in order to crate tokens that do contain a value such as WORD NUMBER tokens.
    // We set the type of Token is location at the LineNumber and CharacterPosition as well as setting its value
    public Token(TokenType tokenType, String value, int lineNumber, int characterPosition){
        this.typeOfToken = tokenType;
        this.lineNumber = lineNumber;
        this.characterPosition = characterPosition;
        this.value = value;
    }
    //token helper method for the parser in order to return the typeOfToken value

    public TokenType returnTokenType(){
        return typeOfToken;
    }

    //token helper method for teh parser in order to reut the value of the token in String form.
    public String returnToken(){
        return value;
    }


    //We Override Javas ToString method just to modify the output yo have the typeOFToken and its Value
    @Override
    public String toString(){
        if (value != null) {

            return typeOfToken + "(" + value + ")";

        } else {

            return typeOfToken.toString();
        }
    }


}
