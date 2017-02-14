package com.glimpse.lecretsi;

import com.vdurmont.emoji.EmojiParser;
import java.util.Objects;

public class Largonji
{
    /**
     *  This class handles the coding/decoding of the algorithm
    **/

    //TODO algorithmToNormal()
    private final static String VOWEL_CASE = "aeiouùûüÿàâæéèêëïîôœlAEIOUÙÛÜŸ;ÀÂÆÉÈÊËÏÎÔŒL";
    private final static String POSSIBLE_EXCEPTION = "'-";
    private final static String PUNCTUATION = "{[()]}<>,.?/;:-+=!_1234567890$*&^:%#@!`~|";
    public static String LARGONJI_INVALID_INPUT = "invalid_input";
    //To keep messages readable we chose not to codify these
    private static final String[] IGNORE_LIST =
            new String[] {"le", "la", "les", "je", "tu", "il", "elle", "on",
            "ils", "elles", "me", "te", "se", "en", "lui", "y",
            "nous", "vous", "leur", "moi", "toi", "elle", "soi", "eux", "l",
            "elles", "de", "du", "des", "j", "m", "t", "un", "une"};

    static private boolean isVowel(char x){
        return VOWEL_CASE.indexOf(x) != -1;
    }

    private static boolean charIsL(Character x) {
        return java.lang.Character.toLowerCase(x)=='l';
    }

    private static boolean inputContainsOnlySymbols(String input) {
        for(Character x:input.toCharArray()) {
            if(PUNCTUATION.indexOf(x)==-1)
                return false;
        }
        return true;
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

        if(input.equals("")) return "";

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
                return (algorithmToLargonji(input.substring(0, posException)) + x) + input.charAt(posException+1) +
                        algorithmToLargonji(input.substring(posException + 2, input.length()));
            }
        }

        if( inputHasOnlyVowels(input) ) {
            encodedText = addLEncode( input.charAt(0) ) + input + 'i';
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

    static String algorithmWrapper(String input, Boolean toLargonji) {
        ///Remove beginning and ending whitespaces,tabs,empty lines
        input=input.trim();
        input=input.replaceAll("(?m)^\\s+$", "");
        input= EmojiParser.removeAllEmojis(input);

        if(input.equals("")) return "";
        if(inputContainsOnlySymbols(input)) return input;
        String answer = "";

        //Handle punctuation,symbols,numbers
        for(Character x:PUNCTUATION.toCharArray())
            if (input.indexOf(x) != -1) {
                if (input.indexOf(x) == input.length() - 1)
                    //Exception at the end : madame?
                    return (algorithmWrapper(input.substring(0, input.indexOf(x)),toLargonji) + x);
                if (input.indexOf(x) == 0)
                    //Exception at the start : ?madame
                    return (x + algorithmWrapper(input.substring(1, input.length()),toLargonji));

                //Exception in the middle : mad?ame
                //Handles parentheses nicely : ok{madame}
                return ( algorithmWrapper(input.substring(0, input.indexOf(x)),toLargonji) + x +
                        algorithmWrapper(input.substring(input.indexOf(x) + 1, input.length()),toLargonji));
            }

        ///Seperate words and send them to the actual algorithm
        int whereIsWhiteSpace = input.indexOf(' ');

        if(toLargonji) {
            //Normal to Largonji
            while(whereIsWhiteSpace!=-1 && input.length()>0) {
                String aux = input.substring(0,whereIsWhiteSpace);
                answer += algorithmToLargonji(aux) + ' ';
                input = input.substring(whereIsWhiteSpace + 1, input.length());
                whereIsWhiteSpace = input.indexOf(' ');
            }

            //Only one word left in the input
            answer += algorithmToLargonji(input);
        } else {
            //Largonji to Normal
            while(whereIsWhiteSpace!=-1 && input.length()>0) {
                String aux = input.substring(0,whereIsWhiteSpace);
                answer += algorithmToNormal(aux) + ' ';
                input = input.substring(whereIsWhiteSpace + 1, input.length());
                whereIsWhiteSpace = input.indexOf(' ');
            }

            //Only one word left in the input
            answer += algorithmToNormal(input);
        }
        return answer;
    }

    private static String algorithmToNormal(String input) {
        String answer;

        if(input.equals("")) return "";

        for(String x: IGNORE_LIST) {
            if(input.toLowerCase().equals(x))
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
                return (algorithmToNormal(input.substring(0,posException))+x)+
                        input.substring(posException+1,input.length());

            } else if(countExceptions==1) {
                // Cases like J'ai, m'apelle

                Integer posException = input.indexOf(x);
                return (algorithmToNormal(input.substring(0, posException)) + x) + input.charAt(posException+1) +
                        algorithmToNormal(input.substring(posException + 2, input.length()));
            }
        }

        if(inputHasOnlyVowels(input.substring(1,input.length()-2)))
            return  input.substring(1,input.length()-2);

        String aux = input.toLowerCase();
        int posSecondL = aux.substring(1).indexOf('l');
        Boolean isConsonantOnRightPos = !isVowel(input.charAt(aux.length()-2));
        Boolean wordEndsI = (aux.charAt(input.length()-1))=='i';

        if(isConsonantOnRightPos && wordEndsI) {
            if(posSecondL==-1) {
                answer = aux.charAt(aux.length()-2)+aux.substring(1,input.length()-2);

            } else {
                answer = aux.substring(1,posSecondL+1)+aux.charAt(input.length()-2)+input.substring(posSecondL+2,aux.length()-2);
            }
        } else {
            answer = LARGONJI_INVALID_INPUT;
        }
        return answer;
    }
}
