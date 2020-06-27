package com.voiceassistant.translate;

import android.content.Context;
import android.util.Log;

import androidx.core.util.Consumer;

import com.voiceassistant.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TranslateToString {
    public static void getTranslate(Context context, final String lang, final String text, final Consumer<String> callback){
        TranslateApi api = TranslateService.getApi();
        Call<Translate> call = api.getTranslate(lang, text);

        call.enqueue(new Callback<Translate>() {
            @Override
            public void onResponse(Call<Translate> call, Response<Translate> response) {
                Translate translate = response.body();
                if (translate!=null) {
                    //запрос выполнен успешно
                    callback.accept(translate.text.get(0));
                }
                else callback.accept(context.getString(R.string.translate_error));
            }

            @Override
            public void onFailure(Call<Translate> call, Throwable t) {
                Log.v("TRANSLATE", t.getMessage());
                callback.accept(context.getString(R.string.translate_error));
            }
        });

    }

}
