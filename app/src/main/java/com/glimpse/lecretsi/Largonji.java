package com.glimpse.lecretsi;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;

public class Largonji
{
    /**
     *  This class will handle the coding/decoding of the algorithm
     *  A single object should be initialised once at start of the app and be destroyed at app close
     */
    //TODO algorithmToNormal()
    //TODO Tie the Largonji class to the DB
    private List<String> normalText;
    private List<String> encodedText = new ArrayList<>();

    Largonji() {}

    public String getNormalText(){
        String aux = null;
        for(String x:normalText){
            aux += (x+" ");
        }
        assert aux!=null;
        return aux.substring(0,aux.length()-2); //Eliminate final white space
    }

    public String getLargonjiText(){
        String aux = null;
        for(String x:encodedText){
            aux += (x+" ");
        }
        assert aux!=null;
        return aux.substring(0,aux.length()-2); //Eliminate final white space

    }

    static private boolean isVowel(char x){
        final String vowelsCase = "aeioulAEIOUL";
        return vowelsCase.indexOf(x) != -1;
    }

    private char addLEncode(char x){
        /** Add 'L' if letter is uppercase or else 'l' */
        if(java.lang.Character.isUpperCase(x)) return 'L';
        return 'l';
    }

    private String algorithmToLargonji(String input){
        if( isVowel(input.charAt(0) ) ){
            int startOfSequenceToBeAdded=0;
            int endOfTheSequenceToBeAdded;

            //Select a whole consonant cluster
            while( isVowel(input.charAt( ++startOfSequenceToBeAdded ) ) );
            endOfTheSequenceToBeAdded=startOfSequenceToBeAdded;
            while( isVowel( input.charAt( ++endOfTheSequenceToBeAdded ) ) );

            input += input.substring(startOfSequenceToBeAdded,endOfTheSequenceToBeAdded)+'i';
            input = input.substring(0,startOfSequenceToBeAdded-1) + addLEncode(input.charAt(startOfSequenceToBeAdded)) +
                    input.substring(endOfTheSequenceToBeAdded+1,input.length()-1) +
                    input.substring(startOfSequenceToBeAdded, endOfTheSequenceToBeAdded) + 'i';

        }
        else{
            char auxChar = input.charAt(0);
            input = addLEncode(auxChar) + input.substring(0,input.length()-1) + auxChar + 'i';
        }
        return input;
    }
}
