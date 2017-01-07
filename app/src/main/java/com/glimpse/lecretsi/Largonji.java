package com.glimpse.lecretsi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Largonji
{
    private List<String> normalText;
    private List<String> encodedText = new ArrayList<>();

    Largonji(String input){
        assert input!=null;
        /** Sets the 'normal message' and calls the encode function for it */
        normalText = new ArrayList<>(Arrays.asList(input.split(" ") ) );
        setCodedText();
    }

    public String getNormalText(){
        String aux = null;
        for(String x:normalText){
            aux += (x+" ");
        }
        assert aux!=null;
        return aux.substring(0,aux.length()-2);//Eliminate final white space
    }

    public String getCodedText(){
        String aux = null;
        for(String x:encodedText){
            aux += (x+" ");
        }
        assert aux!=null;
        return aux.substring(0,aux.length()-2);//Eliminate final white space

    }

    private void setCodedText(){
        for(String x:normalText) {
            encodedText.add( algorithm(x) );
        }
    }

    static private boolean isVowel(char x){
        final String vowelsCase = "aeioulAEIOUL";
        return vowelsCase.indexOf(x) != -1;
    }

    static private char addLEncode(char x){
        /** Add 'L' if letter is uppercase or else 'l' */
        if(java.lang.Character.isUpperCase(x)) return 'L';
        return 'l';
    }

    static private String algorithm(String input){
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
