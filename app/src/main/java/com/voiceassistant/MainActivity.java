package com.voiceassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.voiceassistant.dataBase.DBHelper;
import com.voiceassistant.dataBase.MessageEntity;
import com.voiceassistant.messageView.MessageListAdapter;
import com.voiceassistant.messageView.Message;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    protected TextToSpeech textToSpeech;
    protected Button sendButton;
    protected EditText questionText;
    protected RecyclerView chatMessageList;
    protected MessageListAdapter messageListAdapter;
    protected LinearLayoutManager layoutManager;
    private boolean IsLastItemVisible = false;
    SharedPreferences sPref;
    public static final String APP_PREFERENCES = "mySettings";
    private boolean isDay = true;
    private String THEME = "THEME";
    private DBHelper dbHelper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ссылка для работы AI
        AI.setContext(this);

        //тема из файла
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        isDay  = sPref.getBoolean(THEME, true);
        //смена темы, если сохранили тёмную
        if (!isDay) getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        //БД
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        dbHelper.onCreate(database);

        messageListAdapter = new MessageListAdapter();
        //Открытие приложения после полного его закрытия
        Log.i("DB", savedInstanceState==null? "open":"not open");

        if (savedInstanceState==null) {
            Log.i("DB", "creating cursor");
            Cursor cursor = database.query(DBHelper.TABLE_MESSAGES, null, null,null,null,null,null);

            if (cursor.moveToFirst()){
                Log.i("DB",  "start reading message");
                int messageIndex  = cursor.getColumnIndex(DBHelper.FIELD_MESSAGE);
                int dateIndex = cursor.getColumnIndex(DBHelper.FIELD_DATE);
                int sendIndex = cursor.getColumnIndex(DBHelper.FIELD_SEND);
                do {
                    MessageEntity entity = new MessageEntity(
                            cursor.getString(messageIndex),
                            cursor.getString(dateIndex),
                            cursor.getInt(sendIndex));
                    Message message = new Message(entity);
                    Log.i("DB", "get message " + message.text);
                    messageListAdapter.messageList.add(message);
                } while (cursor.moveToNext());
            }
            cursor.close();
            //добавление сообщений из БД
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sendButton = findViewById(R.id.sendButton);
        questionText = findViewById(R.id.questionField);
        chatMessageList = findViewById(R.id.chatMessageList);

        chatMessageList.setLayoutManager(new LinearLayoutManager(this));
        chatMessageList.setAdapter(messageListAdapter);
        layoutManager = (LinearLayoutManager) chatMessageList.getLayoutManager();

        if (messageListAdapter.getItemCount()>0) {
            chatMessageList.scrollToPosition(messageListAdapter.messageList.size() -1);
            IsLastItemVisible = true;
        }

        //cобытие при пролистывании
        chatMessageList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy!=0) IsLastItemVisible = layoutManager.findLastVisibleItemPosition() == chatMessageList.getAdapter().getItemCount() - 1;
            }
        });

        //событие при изменении размеров экрана, при открытии клавиатуры
        chatMessageList.addOnLayoutChangeListener(new RecyclerView.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (IsLastItemVisible && oldBottom > bottom)
                {
                    chatMessageList.post(() -> chatMessageList.scrollToPosition(messageListAdapter.messageList.size() -1));
                }
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status!=TextToSpeech.ERROR){
                    Log.i("LocaleDef", Locale.getDefault().toLanguageTag());
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });
    }

    //сохранение данных
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("chat", messageListAdapter.messageList.toArray());
        outState.putInt("first_visible_item_rv",layoutManager.findFirstVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }

    // восстановление данных
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Object[] messages = (Object[]) savedInstanceState.getSerializable("chat");
        for (Object message: messages) {
            messageListAdapter.messageList.add((Message) message);
        }
        messageListAdapter.notifyDataSetChanged();
        chatMessageList.scrollToPosition(savedInstanceState.getInt("first_visible_item_rv"));
    }

    //при полной остановке приложения
    @Override
    protected void onStop() {
        Log.i("Doing", "on Stop");
        //сохранение рабочей темы
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(THEME, isDay);
        editor.apply();

        //работа с бд
        database.delete(DBHelper.TABLE_MESSAGES, null, null);
        for (Message item:
                messageListAdapter.messageList) {
            MessageEntity entity = new MessageEntity(item);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.FIELD_MESSAGE, entity.text);
            contentValues.put(DBHelper.FIELD_SEND, entity.isSend);
            contentValues.put(DBHelper.FIELD_DATE, entity.date);
            database.insert(DBHelper.TABLE_MESSAGES, null, contentValues);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        database.close();
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.day_settings:
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                isDay = true;
                break;

            case R.id.night_settings:
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                isDay = false;
                break;

            case R.id.clear_dialog:
                messageListAdapter.messageList.clear();
                messageListAdapter.notifyDataSetChanged();
                break;

            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendButtonOnClick(View view) {
        String text = questionText.getText().toString().trim();
        if (text.length()>0) {
            questionText.getText().clear();
            sendButton.setEnabled(false);
            messageListAdapter.messageList.add(new Message(text, true));
            messageListAdapter.notifyDataSetChanged();
            chatMessageList.scrollToPosition(messageListAdapter.messageList.size() -1);

            AI.getAnswer(text, answer -> {
                textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH,null,null);

                messageListAdapter.messageList.add(new Message(answer, false));
                messageListAdapter.notifyDataSetChanged();

                chatMessageList.scrollToPosition(messageListAdapter.messageList.size() -1);
                sendButton.setEnabled(true);
                IsLastItemVisible = true;

            });
        }
    }

}
