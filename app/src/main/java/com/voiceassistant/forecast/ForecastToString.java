package com.voiceassistant.forecast;

import android.content.Context;
import android.util.Log;

import androidx.core.util.Consumer;

import com.voiceassistant.R;
import com.voiceassistant.wordService.WordGender;
import com.voiceassistant.wordService.WordsFormService;
import com.voiceassistant.translate.TranslateToString;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastToString {
    public static void getForecast(Context context, final String city, final Consumer<String> callback){
        ForecastApi api = ForecastService.getApi();
        Call<Forecast> call = api.getCurrentWeather(city);

        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                Forecast result = response.body();

                if (result!=null && result.current !=null){
                    Log.i("Forecast", "get answer: ");
                    Log.i("Language", Locale.getDefault().getLanguage());
                    if (Locale.getDefault().getLanguage().equals("ru"))
                    {
                        String descr ="In the street " +  result.current.weather_descriptions.get(0);
                        Log.i("Forecast", "Description: " + descr);
                        TranslateToString.getTranslate(context,"en-ru", descr, new Consumer<String>() {
                            @Override
                            public void accept(String s) {
                                String descr = "";
                                if (!s.equals( context.getString(R.string.translate_error))) descr = s;
                                String answer = String.format("%s %s %d %s. %s", context.getString(R.string.now_in_city), city, result.current.temperature, WordsFormService.getGoodWordFormAfterNum(result.current.temperature, "градус", WordGender.MALE_GENDER), descr);
                                callback.accept(answer);
                            }
                        });
                    }
                    else if (Locale.getDefault().getLanguage().equals("en")) {
                        String answer = String.format("%s %s %d°. %s", context.getString(R.string.now_in_city), city, result.current.temperature, result.current.weather_descriptions.get(0));
                        callback.accept(answer);
                    }
                    else callback.accept(context.getString(R.string.weather_error));
                }
                else callback.accept(context.getString(R.string.weather_error));
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                Log.v("WEATHER", t.getMessage());
                callback.accept(context.getString(R.string.internet_error));
            }
        });
    }

}
