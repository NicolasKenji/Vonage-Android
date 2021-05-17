package com.example.vonageapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    EditText textRoomName;
    Button btnEntrar;
    Button btnCriar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCriar = (Button)findViewById(R.id.btnCriar);
        btnEntrar  = (Button)findViewById(R.id.btnEntrar);
        textRoomName  = (EditText)findViewById(R.id.textRoomName);


        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinRoom();
            }
        });

        btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRoom();
            }
        });
    }

    private void createRoom(){
        String url = "https://vonagenicolas.herokuapp.com/generate_room/"+textRoomName.getText();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        joinRoom();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error Create Room", error.toString());

                    }
                });
        queue.add(jsonObjectRequest);
    }

    private void joinRoom(){
        String url = "https://vonagenicolas.herokuapp.com/join_room/"+textRoomName.getText();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Intent video = new Intent(MainActivity.this, VideoCall.class);
                            video.putExtra("token", response.get("token").toString());
                            video.putExtra("sessionToken", response.get("session_id").toString());
                            video.putExtra("apiKey", response.get("api_key").toString());
                            startActivity(video);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error Join Room", error.toString());
                    }
                });
        queue.add(jsonObjectRequest);
    }
}