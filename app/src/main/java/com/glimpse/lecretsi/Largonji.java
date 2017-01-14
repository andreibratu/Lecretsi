package com.glimpse.lecretsi;
import java.lang.String;
import com.glimpse.lecretsi.Phrase;

public class Largonji
{
    /**
     *  This class will handle the coding/decoding of the algorithm
     *  A single object should be initialised once at start of the app and be destroyed at app close
     */
    //TODO algorithmToNormal()
    //TODO Tie the Largonji class to the DB

    static private boolean isVowel(char x){
        final String vowelsCase = "aeioulAEIOUL";
        return vowelsCase.indexOf(x) != -1;
    }

    private static char addLEncode(char x){
        /** Add 'L' if letter is uppercase or else 'l' */
        if(java.lang.Character.isUpperCase(x)) return 'L';
        return 'l';
    }

    public static String algorithmToLargonji(String input){
        String encodedText = input;
        if( isVowel(input.charAt(0) ) ){
            int startOfSequenceToBeAdded=0;
            int endOfTheSequenceToBeAdded;

            //Select a whole consonant cluster
            while( isVowel(input.charAt( ++startOfSequenceToBeAdded ) ) );
            endOfTheSequenceToBeAdded=startOfSequenceToBeAdded;
            while( isVowel( input.charAt( ++endOfTheSequenceToBeAdded ) ) );

            encodedText = input.substring(0,startOfSequenceToBeAdded-1) + addLEncode(input.charAt(startOfSequenceToBeAdded)) +
                    input.substring(endOfTheSequenceToBeAdded+1,input.length()-1) +
                    input.substring(startOfSequenceToBeAdded, endOfTheSequenceToBeAdded) + 'i';

        }
        else{
            char auxChar = input.charAt(0);
            encodedText = addLEncode(auxChar) + input.substring(0,input.length()-1) + auxChar + 'i';
        }
        Phrase.updateDatabase(input, encodedText);
        return encodedText;
    }
}
