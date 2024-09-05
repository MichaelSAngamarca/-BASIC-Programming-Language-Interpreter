import java.util.HashMap;
import java.util.LinkedList;

public class Lexer {
    //integer that is used to keep track of the position of a line in the file
    private int linePosition;
    // integer that is used to keep track of what line we are in starting. We start at 1 as we cant have a file start at 0
    private int lineNumber;
    LinkedList<Token> tokens;
    HashMap<String, Token.TokenType> map; // HashMap responsible for holding keywords
    HashMap<String, Token.TokenType> twoCharacterMap; // HashMap respectables for holding symbols of two characters
    HashMap<Character,Token.TokenType> oneCharacterMap; //HashMap responsible for holding symbols of one character

    HashMap<String, Token.TokenType> keyWords;
    //Constructor Initializes the required members such as line number and position for the lexer and calls all methods that populate each hashmap
    public Lexer(){
        this.lineNumber = 1;
        this.linePosition = 0;
        this.tokens = new LinkedList<>();
        this.map = new HashMap<>();
        this.twoCharacterMap = new HashMap<>();
        this.oneCharacterMap = new HashMap<>();
        this.keyWords = new HashMap<>();
        populateMap();
        populateOneCharacterMap();
        populateTwoCharacterMap();
        populateKnownFunctions();
    }
    public LinkedList<Token> Lex(String fileName) {
        CodeHandler handler = new CodeHandler(fileName); // need to create a new instance of a CodeHandler in order to gain access to the methods to traverse through the file
        while(!handler.isDone()){
            char currentChar = handler.peek(0); // We want to look what's ahead but not take what's ahead,so we set the current character to be one ahead so the lexer knows what character is coming up

            switch (currentChar){ // Using a switch statement for readability in order to avoid excess if and if else statements
                case '\t', ' ' -> { // we first examine the case that there is white space and or tabs in the line we are in
                    handler.swallow(1); // we want to swallow (move the index by one) if we see either white space of a tab as we know that this is not a character that is readable
                    linePosition++;

                }
                case '\n' -> { // examine the case that we encounter a new line while traversing the file
                    tokens.add(new Token(Token.TokenType.ENDOFLINE, linePosition, lineNumber)); // When we encounter a new line that means that we have reached the end of the previous ine meaning we can create a ENDOFLINE token to signify this
                    handler.swallow(1);
                    lineNumber++;
                    linePosition = 0; // reset the line position to begin at the first character of the new line

                }
                case '\r' ->{
                    linePosition++;
                    handler.swallow(1);
                }
                case'"' ->{
                    Token stringLiteral = handleStringLiteral(handler);
                    tokens.add(stringLiteral);
                }
                default -> { // the default case is used to signify if we have reached a readable character of a word or number type
                    if(Character.isLetter(currentChar) || currentChar == '_'){ // if the lexer sees that the next character is a Letter and or contains a "_" that means we can add it to the Linked list by calling the processWord method
                        tokens.add(ProcessWord(handler));
                    } else if (Character.isDigit(currentChar) || currentChar == '.') { // Similarly if the lexer sees that the next character is a Digit and or Contains a '.', that means we can add it to the linked list by calling the processNumber method
                        tokens.add(ProcessNumber(handler));
                    }
                    else{ // If we see that the next symbol is neither a word or letter it has to be a symbol so we call the handleSymbols method
                        handleSymbols(handler);
                    }
                }
            }
        }
        return tokens;

    }

    //ProcesWord is used to look in the file and Process words that we encounter. We do this by methods of StringBuilding and condition  checking returning a new Token at the end that contains the value of WORD to the linked list

    public Token ProcessWord(CodeHandler handler){
        StringBuilder wordBuilder = new StringBuilder();
        int position = linePosition;
        while(!handler.isDone()) {
            char currentChar = handler.peek(0);
            if (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
                wordBuilder.append(handler.getChar());
                linePosition++;
            } else if (currentChar == '$' || currentChar == '%') {
                wordBuilder.append(handler.getChar());
                linePosition++;
                break;
            }
            else if(handler.peek(0) == ':'){
                handler.swallow(1);
                return new Token(Token.TokenType.LABEL, wordBuilder.toString(), lineNumber, linePosition);
            }
            else {
                break;
            }
        }
        String word = wordBuilder.toString().toUpperCase();
        Token.TokenType tokenType = map.getOrDefault(word, keyWords.getOrDefault(word, Token.TokenType.WORD));
        return map.containsKey(word) || keyWords.containsKey(word)
                ? new Token(tokenType, word,lineNumber, linePosition)
                : new Token(tokenType, word, lineNumber, linePosition);
    }
    public Token ProcessNumber(CodeHandler handler){ // ProcessNumer is used to look in the file and Process numbers that we encounter. We do this ny methods of StringBuilding and condition checking returning a new Token at the end that contains the value pof NUMBEr to the linked list
        StringBuilder numberBuilder = new StringBuilder();
        int startPosition = linePosition;
        boolean hasDecimal = false; // flag to keep track of whether we have encountered the first decimal point. If another decimal point is encountered, we start a new token for the remaining part of the number.
        while(!handler.isDone()){
            char currentChar = handler.peek(0);
            if(Character.isDigit(currentChar)){ // If we see that the nextCharacter ahead is a digit we can append to the StringBuilder and increment to line position in order to add the digit to the token
                numberBuilder.append(handler.getChar());
                linePosition++;
            } else if (currentChar == '.' && !hasDecimal) { // If we see that the nextCharacter is a decimal point and that the flag is True then that means we have to separate the tokens as we have more then 1 decimal point and need to create new tokens for each instance after the 1st decimal point we see.
                numberBuilder.append(handler.getChar());
                linePosition++;
                hasDecimal = true;

            }
            else{
                break;
            }

        }
        return new Token(Token.TokenType.NUMBER, numberBuilder.toString(), lineNumber, startPosition);
    }

    private Token handleStringLiteral(CodeHandler handler) {

        StringBuilder literalBuilder = new StringBuilder(); //String builder that is used to build out the string literal
        int position = linePosition;

        // We skip the opening quote in order to avoid reading the quote with the value
        handler.swallow(1);

        while (!handler.isDone()) {
            char currentChar = handler.getChar();

            if (currentChar == '\"') {
                // If a double quote is encountered, it marks the end of the string literal
                break;
            } else if (currentChar == '\\') {
                // If we find a backslash that means we check the next character
                char nextChar = handler.getChar();
                if (nextChar == '\"') {
                    // If the next character is a double quote, append a double quote to the string literal
                    literalBuilder.append('\"');
                }
            } else {
                // Append the current character to the string literal
                literalBuilder.append(currentChar);
            }

            linePosition++;
        }

        // Create and return a Token for the string literal and getting the value for it by calling the to string method.
        return new Token(Token.TokenType.STRINGLITERAL, literalBuilder.toString(), lineNumber, position);
    }
    //method in order to handle incoming symbols both double and single symbols such and + - * / and <= and >=
    private void handleSymbols(CodeHandler handler) {
        char currentChar = handler.peek(0);

        // Check if the current character is a two-character symbol of both < > or = type, so we can construct the two character symbol
        if (currentChar == '<' || currentChar == '>' || currentChar == '=') {
            char nextChar = handler.peek(1);

            // Construct the two-character symbol
            String twoCharSymbol = Character.toString(currentChar) + Character.toString(nextChar);

            // Check if the new constructed symbol is in the towCharacter hashmap and if so we can creat a token of that type using the built-in hash get method
            if (twoCharacterMap.containsKey(twoCharSymbol)) {
                // Add the corresponding token to the list
                tokens.add(new Token(twoCharacterMap.get(twoCharSymbol), linePosition, lineNumber));
                handler.swallow(2);
                linePosition += 2; // Increment line position by 2 as the symbol is of length of 2
                return;
            }
        }

        // Check if the current character is a one-character symbol is in the oneCharacterMap
        if (oneCharacterMap.containsKey(currentChar)) {
            // create a new token similar to the twoCharacterSymbol method by using the hashmap get method
            tokens.add(new Token(oneCharacterMap.get(currentChar), linePosition, lineNumber));
            handler.swallow(1);
            linePosition++; // Increment line position by 1
        }
    }




    //Helper method in order to populate the map with keywords.
    private void populateMap(){
        map.put("PRINT", Token.TokenType.PRINT);
        map.put("READ", Token.TokenType.READ);
        map.put("INPUT", Token.TokenType.INPUT);
        map.put("DATA", Token.TokenType.DATA);
        map.put("GOSUB", Token.TokenType.GOSUB);
        map.put("FOR", Token.TokenType.FOR);
        map.put("TO", Token.TokenType.TO);
        map.put("STEP", Token.TokenType.STEP);
        map.put("NEXT", Token.TokenType.NEXT);
        map.put("RETURN", Token.TokenType.RETURN);
        map.put("IF", Token.TokenType.IF);
        map.put("THEN", Token.TokenType.THEN);
        map.put("FUNCTION", Token.TokenType.FUNCTION);
        map.put("WHILE", Token.TokenType.WHILE);
        map.put("END", Token.TokenType.END);
        map.put("DO", Token.TokenType.DO);
        map.put("LABEL", Token.TokenType.LABEL);

    }

    private void populateKnownFunctions(){
        keyWords.put("RANDOM", Token.TokenType.FUNCTION);
        keyWords.put("MID$", Token.TokenType.FUNCTION);
        keyWords.put("LEFT$", Token.TokenType.FUNCTION);
        keyWords.put("RIGHT$", Token.TokenType.FUNCTION);
        keyWords.put("NUM$", Token.TokenType.FUNCTION);
        keyWords.put("VAL", Token.TokenType.FUNCTION);
        keyWords.put("VAL%", Token.TokenType.FUNCTION);
    }

    //Helper method that populated the twoCharacterMap for two character symbols
    private void populateTwoCharacterMap(){
        twoCharacterMap.put(">=", Token.TokenType.GREATOREQUAL);
        twoCharacterMap.put("<=", Token.TokenType.LESSOREQUAL);
        twoCharacterMap.put("<>", Token.TokenType.NOTEQUAL);
    }

    // Helper method that populates the oneCharacterMap for one character symbols.
    private void populateOneCharacterMap(){
        oneCharacterMap.put('=', Token.TokenType.EQUALS);
        oneCharacterMap.put('<', Token.TokenType.LESSTHAN);
        oneCharacterMap.put('>', Token.TokenType.GREATERTHAN);
        oneCharacterMap.put('+', Token.TokenType.PLUS);
        oneCharacterMap.put('-', Token.TokenType.MINUS);
        oneCharacterMap.put('*', Token.TokenType.TIMES);
        oneCharacterMap.put('/', Token.TokenType.DIVIED);
        oneCharacterMap.put('(', Token.TokenType.LPAREN);
        oneCharacterMap.put(')', Token.TokenType.RPAREN);
        oneCharacterMap.put(',',Token.TokenType.COMMA);
    }
}