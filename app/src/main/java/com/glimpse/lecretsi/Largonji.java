package com.glimpse.lecretsi;
import java.util.Objects;

class Largonji
{
    /**
     *  This class handles the coding/decoding of the algorithm
    **/

    //TODO algorithmToNormal()
    private final static String VOWEL_CASE = "aeiouùûüÿàâæéèêëïîôœlAEIOUÙÛÜŸÀÂÆÉÈÊËÏÎÔŒL";
    private final static String POSSIBLE_EXCEPTION = "'-";
    private final static String PUNCTUATION = "{[()]}<>,.?/;:-+=!_1234567890";
    //To keep messages readable we chose not to codify these
    private static final String[] IGNORE_LIST =
            new String[] {"le", "la", "les", "je", "tu", "il", "elle", "on",
            "ils", "elles", "me", "te", "se", "en", "lui", "y",
            "nous", "vous", "leur", "moi", "toi", "elle", "soi", "eux", "l",
            "elles", "de", "du", "des", "j", "m", "t"};

    static private boolean isVowel(char x){
        return VOWEL_CASE.indexOf(x) != -1;
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

        for(String x: IGNORE_LIST) {
            if(Objects.equals(input.toLowerCase(), x))
                return input;
        }

        for(Character x: POSSIBLE_EXCEPTION.toCharArray() ) {
            Integer countExceptions = 0;
            String aux = input;

            while (aux.indexOf(x) != -1) {
                Integer pos = aux.indexOf(x);
                countExceptions++;
                aux = aux.substring(pos+1,aux.length());
            }

            if(countExceptions>=2) {
                //Constructions like Passes-les-me ; we don't codify the pronouns
                Integer posException = input.indexOf(x);
                return (algorithmToLargonji(input.substring(0,posException))+x)+
                        input.substring(posException+1,input.length());

            } else if(countExceptions==1) {
                // Cases like J'ai, m'apelle

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
        ///Remove beginning and ending whitespaces,tabs,empty lines
        input=input.trim();
        input=input.replaceAll("(?m)^\\s+$", "");
        String answer = "";

        //Handle punctuation,symbols,numbers
        for(Character x:PUNCTUATION.toCharArray())
            if (input.indexOf(x) != -1) {
                if (input.indexOf(x) == input.length() - 1)
                    //Exception at the end : madame?
                    return algorithmWrapper(input.substring(0, input.indexOf(x))) + x;
                if (input.indexOf(x) == 0)
                    //Exception at the start : ?madame
                    return x + algorithmWrapper(input.substring(1, input.indexOf(x)));

                //Exception in the middle : mad?ame
                //Handle parantheses nicely : ok{madame}
                return algorithmWrapper(input.substring(0, input.indexOf(x))) + x +
                        algorithmWrapper(input.substring(input.indexOf(x) + 1, input.length()));
            }

        ///Seperate words and send them to the actual algorithm
        int whereIsWhiteSpace = input.indexOf(' ');
        while(whereIsWhiteSpace!=-1 && input.length()>0) {
            String aux = input.substring(0,whereIsWhiteSpace);
            answer += algorithmToLargonji(aux) + ' ';
            input = input.substring(whereIsWhiteSpace + 1, input.length());
            whereIsWhiteSpace = input.indexOf(' ');
        }

        //Only one word left in the input
        answer += algorithmToLargonji(input);

        return answer;
    }
}
