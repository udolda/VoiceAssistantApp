package com.voiceassistant;

import com.voiceassistant.htmlParsing.ParsingHtmlService;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void holiday_answer(){
        try {
            System.out.println( ParsingHtmlService.getHolidays("20 марта 2021"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void answer_holiday(){
        System.out.println( AI.getDate("какой праздник 25.2"));
    }
}