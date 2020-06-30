package com.example.burunduk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    TextView text_help, text_resilt;
    ImageButton VoiceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_help = findViewById(R.id.text_help);
        text_resilt = findViewById(R.id.text_result);
        VoiceBtn = findViewById(R.id.voiceBtn);

        VoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_rotate();
                speak();
            }
        });
    }

    private void button_rotate() {
        VoiceBtn.setRotation(3600);
        VoiceBtn.animate().rotation(0).setDuration(5000);
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите!");
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null){
            switch (requestCode){
                case 10:
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    text_help.setVisibility(View.INVISIBLE);
                    if(text.contains("погода в городе")){
                        find_weather(text);
                    }
                    if(text.contains("Привет")){
                        text_resilt.setText("Добрый день");
                        text_resilt.setVisibility(View.VISIBLE);
                    }
                    if(text.contains("Открой карты")){
                        Intent m = new Intent();
                        PackageManager manager1 = getPackageManager();
                        m = manager1.getLaunchIntentForPackage("com.google.android.apps.maps");
                        m.addCategory(Intent.CATEGORY_LAUNCHER);
                        startActivity(m);
                    }
                    if(text.contains("Открой браузер")){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                        startActivity(browserIntent);
                    }
            }
        }
    }

    private void find_weather(String text) {

        String city_word = text.substring(text.lastIndexOf(" ")+1);
        StringBuffer url = new StringBuffer("http://api.openweathermap.org/data/2.5/weather?q=&appid=5c1dc45847307301cbcb0cca69ef8ef2&lang=ru&units=metric");
        url.insert(49, city_word);
        String new_url = url.toString();


        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, new_url, null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_object = response.getJSONObject("main");
                    String city = response.getString("name");
                    String temp = String.valueOf(main_object.getInt("temp"));

                    JSONArray weather_array = response.getJSONArray("weather");
                    JSONObject d_object = weather_array.getJSONObject(0);
                    String description = d_object.getString("description");

                    JSONObject wind = response.getJSONObject("wind");
                    String speed = String.valueOf(wind.getInt("speed"));

                    text_resilt.setText("Погода в городе "+city+":\n"+firstUpperCase(description) +"\n"
                            +"Температура: "+temp+" °C\n"
                            +"Скорость ветра: "+speed+" м/c");
                    /*
                    Погода в городе сity:
                    Description
                    Температура: temp°C
                    Скорость ветра: speed м/c
                    */
                    text_resilt.setVisibility(View.VISIBLE);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        Context context;
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }

    //замена первой буквы на заглавную для description
    public String firstUpperCase(String word){
        if(word == null || word.isEmpty()) return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}