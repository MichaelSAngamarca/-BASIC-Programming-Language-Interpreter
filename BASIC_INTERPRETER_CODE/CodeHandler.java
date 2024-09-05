import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class CodeHandler {
    // fileName contains the contents of the file we are reading from
    private String fileContents;

    // integer member in order to keep track of the index while traversing the file contents
    private int index = 0;

    public CodeHandler(String fileName){
        try{
            // Obtain the path of the file
            Path filePath = Paths.get(fileName);
            // In order to get the contents we can create an array of bytes and store the contents by using the readAllBytes method
            byte[] pathArray = Files.readAllBytes(filePath);
            //To make the contents readable we convert the array to a String
            this.fileContents = new String(pathArray);
            this.index = 0;
        }
        catch (IOException e) {
            //If we cant find a file we throw an exception that we cant read the file for goof coding practices.
            System.out.println("error reading file ");
            System.exit(1);
        }

    }

    //We check the condition that the given index in the parameter is within the bounds of the  length of the string
    //The reason we have to do this because if we are given an index that is out of bounds for the String we will get an error with the lexer
    char peek(int i){

        if(index + i < fileContents.length() && index + i >= 0){
            return fileContents.charAt(index+i);
        }
        else{
            throw new IndexOutOfBoundsException("Provided Index is Out of Bounds for the length of the document");
        }


    }

    //we get the index + i (the starting index plus the amount of characters we want to go to)
    //we use the substring method to simply start from the first index and go till the end index (peekindex)
    //if we see that the conditions in the if statement don't succeed we just return the rest of the string as is
    String peekString(int i ){
        int peekIndex = index + i;
        if(peekIndex < fileContents.length() && peekIndex > 0){
            return fileContents.substring(index, peekIndex);
        }
        return fileContents.substring(index);

    }

    //We want to print the character and then move the index to the next one.
    //To do this we want to use the charAt method provided in java  to obtain the character at out index and then increment the index.
    //If we see that the index is greater than the file length we throw an IndexOutOfBounds that we are outside the limits of the File.
    char getChar(){
        if(index < fileContents.length()){
            char currentChar = fileContents.charAt(index);
            index++;
            return currentChar;

        }
        else{
            throw new IndexOutOfBoundsException("Provided Index is Out of Bounds for the length of the document in GetChar");
        }


    }

    // all we do is add the given amount we want to move the index (i positions ) and add it to the existing index.
    void swallow(int i ){
        index += i;
    }

    //If we are at the end of the file that means that the index will be greater or equal to the length of the file so all we need to do is use a conditional return
    boolean isDone(){

        return index >= fileContents.length();

    }

    //return a substring of the document starting at a certain index provided in the parameter
    String remainder(){
        return fileContents.substring(index);
    }

}
