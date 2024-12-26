package FileHandler;

import java.io.*;

public class FileHandler {
    private File file;
    private int currentRow = 1;
    private int currentColumn = 0;
    private int currentCharacter, previousCharacter;
    private String rowWithError;
    BufferedReader fileReader;

    public FileHandler (File f) throws IOException{
        file = f;
        fileReader = new BufferedReader(new FileReader(file));
    }

    public int nextCharacter() throws IOException{
        previousCharacter = currentCharacter;
        currentCharacter = fileReader.read();
        currentColumn = currentColumn +1;
        if (previousCharacter == '\n'){
            currentRow = currentRow +1;
            currentColumn = 1;
        }
        if (currentCharacter == '\r'){
            currentCharacter = fileReader.read();
        }
        return currentCharacter;
    }
    public int getCurrentRow(){
        return currentRow;
    }
    public int getCurrentCharacter(){
        return currentCharacter;
    }
    public int getCurrentColumn(){
        return currentColumn;
    }
    public String getLineWithError() throws IOException{
        BufferedReader fileReaderForLineError = new BufferedReader(new FileReader(file));
        int rowNumber = 1;
        while (rowNumber < currentRow){
            fileReaderForLineError.readLine();
            rowNumber = rowNumber +1;
        }
        rowWithError = fileReaderForLineError.readLine();
        if (rowWithError == null){
            rowWithError = "";
        }
        return rowWithError;
    }




}
