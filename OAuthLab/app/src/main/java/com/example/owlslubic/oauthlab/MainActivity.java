package com.example.owlslubic.oauthlab;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    ListView mListView;
    Button mButton;
    EditText mEditText;
    String mUserInput;
    private static String mToken;
    public static final String CONSUMER_KEY = "8yTxWf26e73uo7EmDKJxRHXg0";
    public static final String CONSUMER_SECRET = "Srjm9rdbWrLanZrSfKPNjkIOBHkNbU140dTLEPyfAWOVa46uE0";
    public static final String TAG = "MainActivity";

    public static final String KEY_AND_SECRET_CONCAT = CONSUMER_KEY + ":" + CONSUMER_SECRET;

    public static String KEY_BASE64;
    public String screen_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listview);
        mButton = (Button) findViewById(R.id.button);
        mEditText = (EditText) findViewById(R.id.edittext);
       mUserInput =  mEditText.getText().toString();
        ArrayList<String> tweets = new ArrayList<>();


            //base64 encoding my key
        byte[] concatArray = KEY_AND_SECRET_CONCAT.getBytes();
        KEY_BASE64 = Base64.encodeToString(concatArray, Base64.DEFAULT);

        /**this is what im doing along with the lesson with alan**/
        //request must have Headers for Authorization and Content-Type
        //form body must only be grant-type=client_credentials

        //the api request









        /**end of what we did in lesson**/
        //once we have the token, then we gotta make the actual request for data...
        //then parse the JSON response  to get the tweet content/date/time etc
        //then with that data, we runOnUiThread (cuz its been running in the background) and set it to the listview or something....... right?



        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + getToken())
                .url("https://api.twitter.com/1.1/statuses/user_timeline.json?" + screen_name + "=twitterapi&count=20")//somehow, somewhere i will set screen_name to be whatever the input from the edittext yields
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: Something failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("JSON parsing done bad");
                String responseString = response.body().string();

                try {
                    JSONArray resultArray = new JSONArray(responseString);
                    //i'm pretty sure the root element is an array, based on the example response, but since I can't get the actual request to happen, I cannot be sure
                    //so this section is just a bit of guesswork
                    JSONObject object = resultArray.getJSONObject(0);//buttttttt i'm lookin to get the first 20 tweets, so I know i'm not just gonna use index 0
                    String tweetText = object.getString("text");
                    String createdAt = object.getString("created_at");
                    //ok i know that there are a lot more objects to navigate down the rabbit hole but i put these things so you can see what i'm getting at
                    //and i will set the text and createdAt to a simple_list_item_2 in my listview maybe?

                    //and do some sort of tweets.add(tweetText + createdAt), because that list is what i will feed to my arrayadapter

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //i guess this is where i will set the adapter and pass it the list of tweet info........?
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //this should return the string token
    private String getToken() {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("Authorization", "Basic " + KEY_BASE64)
                .add("Content_Type", "application/x-www-form-urlencoded")
                .add("charset", "UTF-8")
                .add("grant_type", "client_credentials")
                .build();
        Request request = new Request.Builder()
                .url("https://api.twitter.com/oauth2/token")//don't think this is right
                .post(formBody)
                .build();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: request failed", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }
                        String responseString = response.body().string();
                        Log.i(TAG, "onResponse: " + responseString);

                        try {
                            JSONObject result = new JSONObject(responseString);
                            mToken = result.getString("access_token");
                            Log.i(TAG, "onResponse: access token - " + mToken);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        return mToken;
    }


}
