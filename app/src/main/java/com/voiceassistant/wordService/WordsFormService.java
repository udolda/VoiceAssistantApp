package com.voiceassistant.wordService;

public class WordsFormService {
    /**
     * Вовзвращает правильную форму существительного  после числительного
     * @param t число
     * @param word слово именительном падеже
     * @return
     */

    public static String getGoodWordFormAfterNum(int t, String word, WordGender gen){
        t = Math.abs(t);
        if (gen==WordGender.MALE_GENDER){
            if (t>=11 && t<=14) return word + "ов";
            int r = t%10;
            if (r==1) return  word;
            if (r>=2 && r<=4) return word + "а";
            return word + "ов";
        }

        if (gen == WordGender.FEMALE_GENDER){
            String mainForm = word.substring(0,word.length() - 2);
            if (t>=11 && t<=14) return mainForm;
            int r = t%10;
            if (r==1) return  word;
            if (r>=2 && r<=4) return mainForm + "и";
            return mainForm;
        }
        return word;
    }

}
