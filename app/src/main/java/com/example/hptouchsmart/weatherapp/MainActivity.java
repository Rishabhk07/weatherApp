package com.example.hptouchsmart.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
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


    public void findWeather(View view){

        Log.i("City Name", cityName.getText().toString());



        InputMethodManager mgr  = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);;
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        String encodedURL = null;
        try {
            encodedURL = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedURL + "&APPID=db46ada5169f0f48e073d0f7329a608e");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"could not endcode ",Toast.LENGTH_LONG).show();
        }





    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText)findViewById(R.id.editText);
        resultTextView = (TextView) findViewById(R.id.resultTextView);

    }


    public class DownloadTask extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... urls) {



            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection)url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current  = (char)data;

                    result += current;

                    data = reader.read();

                }

                return  result;

            } catch (MalformedURLException e) {
                Toast.makeText(getApplicationContext(), "Could not find the city ",Toast.LENGTH_LONG).show();
            }catch (IOException e){
                Toast.makeText(getApplicationContext(), "Could not find the city ",Toast.LENGTH_LONG).show();
            }catch(Exception e ){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "internet not connected ",Toast.LENGTH_LONG).show();
            }



            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject jsonObject  = null;
            String message = "";

            try {
                jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather");

                //this here we find the temp from making an object from main to temp object

                JSONObject tempObject = jsonObject.getJSONObject("main");

                String temp =  tempObject.getString("temp");

                float tempe =  Float.parseFloat(temp);

                tempe = tempe - 273;

                temp  = Float.toString(tempe);

                Log.i("temp",temp);

                //till here

                // this here starts to show the weather condition

                JSONArray arr = new JSONArray(weatherInfo);

                for(int i = 0 ; i < arr.length() ; i++){

                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String descrip = jsonPart.getString("description");

                    Log.i("main",main);
                    Log.i("description",descrip);

                    if(main != "" && descrip != ""){
                        message += main + ": " + descrip + "\r\n" + "Temperature: " + temp;

                    }

                    if(message != ""){
                        resultTextView.setText(message);
                    }/*else{
                        Toast.makeText(getApplicationContext(), "Could not find the city ",Toast.LENGTH_LONG).show();
                        resultTextView.setText(null);
                    }*/


                }





            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Could not find the city ",Toast.LENGTH_LONG).show();
            }


        }
    }
}
