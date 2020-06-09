package com.example.burunduk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    TextView text_help, text_voice;
    TextView weather_city, weather_temp, weather_description;
    ImageButton mVoiceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_help = findViewById(R.id.text_help);
        text_voice = findViewById(R.id.text_voice);
        weather_city = findViewById(R.id.weather_city);
        weather_temp = findViewById(R.id.weather_temp);
        weather_description = findViewById(R.id.weather_description);
        mVoiceBtn = findViewById(R.id.voiceBtn);

        mVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
    }

    private void find_weather() {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=Taganrog&appid=5c1dc45847307301cbcb0cca69ef8ef2&lang=ru&units=metric";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String city = response.getString("name");
                    String temp = String.valueOf(main_object.getInt("temp"));
                    String description = object.getString("description");

                    weather_city.setText(city);
                    weather_city.setVisibility(View.VISIBLE);
                    weather_temp.setText(temp);
                    weather_temp.setVisibility(View.VISIBLE);
                    weather_description.setText(description);
                    weather_description.setVisibility(View.VISIBLE);
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

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите!");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        text_help.setVisibility(View.INVISIBLE);
        text_voice.setVisibility(View.VISIBLE);
        text_voice.setText(result.get(0));
        find_weather();

    }
}