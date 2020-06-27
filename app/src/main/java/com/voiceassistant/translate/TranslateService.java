package com.voiceassistant.translate;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TranslateService {
    public static TranslateApi getApi(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://translate.yandex.net/api/v1.5/tr.json/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(TranslateApi.class);
    }

}
