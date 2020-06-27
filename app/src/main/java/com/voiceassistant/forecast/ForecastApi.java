package com.voiceassistant.forecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForecastApi {
    @GET("/current?access_key=98ce4449ba47b872f50b879862471ea4")
    Call<Forecast> getCurrentWeather(@Query("query") String city);
}
