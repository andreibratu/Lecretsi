package com.glimpse.lecretsi;
import java.lang.String;
import java.lang.reflect.Array;
import com.glimpse.lecretsi.Phrase;

public class Largonji
{
    /**
     *  This class will handle the coding/decoding of the algorithm
     *  Class is to be used by its static members
     */


    //TODO algorithmToNormal()
    // TODO *J'en* parle
    private static final String vowelsCase = "aeiouùûüÿàâæéèêëïîôœlAEIOUÙÛÜŸÀÂÆÉÈÊËÏÎÔŒL";
    private static final String possibleException = "'-";
    private static final String[] ignoreList =
            new String[] {"le", "la", "les", "je", "tu", "il", "elle", "on",
            "ils", "elles", "me", "te", "se", "en", "lui", "y",
            "nous", "vous", "leur", "moi", "toi", "lui", "elle", "soi", "eux",
            "elles", "de", "du", "des", "j"};

    /*
        I'll leave these here in case we need them
        "-le", "-la", "-les", "-lui", "-leur", "-moi", "-toi",
            "-nous", "-vous", "-y", "-z-y", "-z-en", "-m", "-m'", "-t'",
            "-m'en", "-moi-z-en", "-en-moi", "m'en", "-en-toi", "-en-nous", "-en-vous",
            "-en-lui", "-en-leur", "-en-la", "-en-le", "-en-les",
     */

    static private boolean isVowel(char x){
        return vowelsCase.indexOf(x) != -1;
    }

    private static boolean charIsL(Character x) {
        return java.lang.Character.toLowerCase(x)=='l';
    }

    private static boolean inputHasOnlyVowels(String input) {
        for(char x:input.toCharArray()) if (!isVowel(x)) return false;

        return true;
    }

    private static char addLEncode(char x){
        /** Add 'L' if letter is uppercase or else 'l' */
        if(java.lang.Character.isUpperCase(x)) return 'L';
        return 'l';
    }

    static String algorithmToLargonji(String input){
        String encodedText;

        if( inputHasOnlyVowels(input) ) {
            encodedText = addLEncode( input.charAt(0) ) + input;
            return encodedText;
        }

        for(String x:ignoreList) {
            if(input.toLowerCase()==x) return input;
        }

        for(Character x:possibleException.toCharArray() ) {
            if( input.indexOf(x) != -1 )
                if( !charIsL(input.charAt(0)) ) return input.substring( 0,input.indexOf(x) )+
                        algorithmToLargonji( input.substring(input.indexOf(x)+1,input.length() ) );
                else {
                    String aux =
                            algorithmToLargonji( input.substring(input.indexOf(x)+1,input.length()));
                    return input.substring( 0,input.indexOf(x) ) + aux.substring(1,aux.length());
                }
        }

        if( isVowel(input.charAt(0) ) || charIsL(input.charAt(0)) ){
            int charToReplaceIndex;

            for(charToReplaceIndex=0;
                charToReplaceIndex < input.length() &&
                        isVowel(input.charAt(charToReplaceIndex) ) ; charToReplaceIndex++ );

            encodedText = addLEncode(input.charAt(0)) + input.substring(0,charToReplaceIndex) +
                    addLEncode(input.charAt(charToReplaceIndex)) +
                    input.substring(charToReplaceIndex+1,input.length() ) +
                    input.charAt( charToReplaceIndex ) + 'i';
        }
        else{
            char auxChar = input.charAt(0);
            encodedText = addLEncode(auxChar) + input.substring(1,input.length()) + auxChar + 'i';
        }
        if( charIsL( encodedText.charAt(0) ) && charIsL(encodedText.charAt(1) ) )
            encodedText = encodedText.charAt(0)+encodedText.substring(2,encodedText.length());
        return encodedText;
    }

    public static String algorithmWrapper(String input) {
        String answer = "";
        int whereIsWhiteSpace = input.indexOf(' ');
        while(whereIsWhiteSpace!=-1 && input.length()>0) {
            String aux = input.substring(0,whereIsWhiteSpace);
            answer += algorithmToLargonji(aux) + ' ';
            input = input.substring(whereIsWhiteSpace + 1, input.length());
            whereIsWhiteSpace = input.indexOf(' ');
        }

        answer += algorithmToLargonji(input);

        return answer;
    }
}
