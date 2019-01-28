package com.example.android.valutaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String[] list_data = {"EUR_KZT", "USD_KZT", "RUB_KZT"};
    private String url = "http://192.168.1.2:8080/valuta/getCurrency?fromTo=%s";

    public TextView convert_view;
    public TextView digit_view;
    public Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Converted currency
        convert_view = findViewById(R.id.convert_txtview);

        //Digit of currency on KZT
        digit_view = findViewById(R.id.digit_txtview);

        //adapter
        int item = android.R.layout.simple_spinner_item;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, item, list_data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //spinner
        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        //title
        spinner.setPrompt("Valuta");

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {

                final String selectedItem = parent.getSelectedItem().toString();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format(url, selectedItem))
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            final String usd = object.get("rate").toString();

                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    convert_view.setText(selectedItem);
                                    digit_view.setText(usd);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
