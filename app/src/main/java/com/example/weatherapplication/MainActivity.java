package com.example.weatherapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultTextView;
    public void buttonClicked(View view){

        Log.i("cityName",cityName.getText().toString());

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);

        try {
            String EncodedUrl = URLEncoder.encode(cityName.getText().toString(),"UTF-8");

            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q="+EncodedUrl+"&appid=17bf9b05924b0ce83c310e84c7e0a109");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Could Not find the Weather",Toast.LENGTH_LONG).show();
        }


    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            String result ="";
            URL url;
            HttpURLConnection urlconnection = null;
            try {
                url = new URL(urls[0]);

                urlconnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlconnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data !=-1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (MalformedURLException me) {
                me.printStackTrace();

            }catch(IOException e){
                e.printStackTrace();
            }
            catch(Exception e){
                Toast.makeText(getApplicationContext(),"Could Not find the Weather",Toast.LENGTH_LONG).show();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                String message = "";

                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo =  jsonObject.getString("weather");

                JSONArray arr = new JSONArray(weatherInfo);

                for(int i=0;i<arr.length();i++){
                    JSONObject jspart = arr.getJSONObject(i);

                    String main = "";
                    String description = "";

                    main = jspart.getString("main");
                    description = jspart.getString("description");
                    if(main!="" && description != ""){

                        message += main+ ":" + description + "\n";
                    }
                }
                if (message != "") {

                    resultTextView.setText(message);
                }else{
                    Toast.makeText(getApplicationContext(),"Could Not find the Weather",Toast.LENGTH_LONG).show();
                }



            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"Could Not find the Weather",Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);





    }
}