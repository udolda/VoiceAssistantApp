package com.voiceassistant.htmlParsing;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;

public class ParsingHtmlService {
    private static final String URL = "http://mirkosmosa.ru/holiday/2020";

    /***
     * Все праздники в этот день
     * @param date дата, праздники которой надо выдать
     * @return список всех праздников
     * @throws IOException ошибка с парсингом сайта
     */

    public static ArrayList<String> getHolidays(String date) throws Exception {
        Document document = Jsoup.connect(URL).get();
        Element body = document.body();
        Element day =  body.getElementsContainingOwnText(date).first();
        Elements holidays = day.parent().parent().select("li");
        ArrayList<String> holidays_str = new ArrayList<>();
        for (Element element: holidays) {
            holidays_str.add(element.selectFirst("a").text());
        }
        return  holidays_str;
    }

}
