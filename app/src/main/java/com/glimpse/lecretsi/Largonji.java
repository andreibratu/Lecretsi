package com.glimpse.lecretsi;

public class Largonji
{
    ///TODO way to handle uppercase and lowercase
    private String normalText;
    private String codedText;
    private final String vowelsCase = "aeioulAEIOUL";

    Largonji(){}

    void setNormalText(String input) {
        normalText=input;
    }

    String getNormalText(){
        return normalText;
    }

    String getCcodedText(){
        return codedText;
    }

    ///TODO : handle words that start with uppercase
    ///TODO : handle words with uppercase in the middle
    String Algorithm(String input){
        if(vowelsCase.indexOf(input.charAt(0)) != -1){
            int auxPos=0;
            while(vowelsCase.indexOf(input.charAt(++auxPos))!= -1)
            input += input.charAt(auxPos)+'i';
            input = input.substring(0,auxPos-1)+'l'+input.substring(auxPos+1,input.length()-1);
            return input;
        }
        else{
            char auxChar = input.charAt(0);
            input = 'l' + input.substring(0,input.length()-1) + auxChar + 'i';
        }
        return input;
    }

    void setCodedText(){
        String buffer=normalText;
        while(!buffer.equals("")){
            int posNextWhiteSpace=buffer.indexOf(' ');
            if(posNextWhiteSpace!=-1){
                codedText += Algorithm(buffer.substring(0,posNextWhiteSpace-1));
                buffer = buffer.substring(posNextWhiteSpace+1,buffer.length()-1);
            }
            else{
                ///Buffer contains only the last word
                codedText += Algorithm(buffer);
                buffer = "";
            }
        }
    }

}
