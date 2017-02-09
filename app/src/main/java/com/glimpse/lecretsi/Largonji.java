package com.glimpse.lecretsi;
import java.lang.String;
import java.util.ArrayList;
import java.util.Objects;

class Largonji
{
    /**
     *  This class handles the coding/decoding of the algorithm
    **/

    //TODO algorithmToNormal()
    private static final String vowelsCase = "aeiouùûüÿàâæéèêëïîôœlAEIOUÙÛÜŸÀÂÆÉÈÊËÏÎÔŒL";
    private static final String possibleException = "'-";
    private static final String[] ignoreList =
            new String[] {"le", "la", "les", "je", "tu", "il", "elle", "on",
            "ils", "elles", "me", "te", "se", "en", "lui", "y",
            "nous", "vous", "leur", "moi", "toi", "elle", "soi", "eux", "l",
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


    private static String algorithmToLargonji(String input){
        String encodedText;

        for(String x:ignoreList) {
            if(Objects.equals(input.toLowerCase(), x))
                return input;
        }

        for(Character x:possibleException.toCharArray() ) {
            Integer countExceptions = 0;
            String aux = input;

            while (aux.indexOf(x) != -1) {
                Integer pos = aux.indexOf(x);
                countExceptions++;
                aux = aux.substring(pos+1,aux.length());
            }

            if(countExceptions>=2) {

                Integer posException = input.indexOf(x);
                return (algorithmToLargonji(input.substring(0,posException))+x)+
                        input.substring(posException+1,input.length());

            } else if(countExceptions==1) {

                Integer posException = input.indexOf(x);
                return (algorithmToLargonji(input.substring(0, posException)) + x) +
                        algorithmToLargonji(input.substring(posException + 1, input.length()));
            }
        }

        if( inputHasOnlyVowels(input) ) {
            encodedText = addLEncode( input.charAt(0) ) + input;
            return encodedText;
        }

        if( isVowel(input.charAt(0) ) || charIsL(input.charAt(0)) ){
            int charToReplaceIndex;

            for(charToReplaceIndex=0; charToReplaceIndex < input.length() &&
                        isVowel(input.charAt(charToReplaceIndex) ) ; charToReplaceIndex++ );

            encodedText = addLEncode(input.charAt(0)) + input.substring(0,charToReplaceIndex) +
                    addLEncode(input.charAt(charToReplaceIndex)) +
                    input.substring(charToReplaceIndex+1,input.length() ) +
                    input.charAt( charToReplaceIndex ) + 'i';
        }

        else{
            char auxChar = input.charAt(0);
            encodedText = addLEncode(auxChar) + input.substring(1,input.length()) +
                    Character.toLowerCase(auxChar) + 'i';
        }

        if( charIsL( encodedText.charAt(0) ) && charIsL(encodedText.charAt(1) ) )
            encodedText = encodedText.charAt(0)+encodedText.substring(2,encodedText.length());
        return encodedText;
    }

    static String algorithmWrapper(String input) {
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
