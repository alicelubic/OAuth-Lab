package com.example.owlslubic.oauthlabroundii;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;

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
    private static final String TAG = "MainActivity";
    String mScreen_name;
    SharedPreferences mPrefs;
    ListView mListView;
    Button mButton;
    EditText mEditText;
    ArrayList<String> mTweetList;
    public static final String ACCESS_TOKEN = "twitter_access_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listview);
        mButton = (Button) findViewById(R.id.button);
        mEditText = (EditText) findViewById(R.id.edittext);
        mScreen_name = mEditText.getText().toString();
        mTweetList = new ArrayList<>();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mTweetList);
        mListView.setAdapter(adapter);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpClient client = new OkHttpClient();

                //now make our good ol tweet request
                Request newRequest = new Request.Builder()
                        .addHeader("Authorization", "Bearer " + mPrefs.getString(ACCESS_TOKEN,null))
                        .url("https://api.twitter.com/1.1/statuses/user_timeline.json?q=" + mScreen_name + "&count=1")//the count needs to be 20, but my twitter object isnt ready for that, so until i get the access token working....
                        .build();

                client.newCall(newRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i(TAG, "user_timeline request failed");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i(TAG, "user_timeline request succeeded");
                        if (!response.isSuccessful()) {
                            //bye
                        } else {
                            Gson gson = new Gson();
                            TwitterAPIResponse twitterResponse = gson.fromJson(response.body().string(), TwitterAPIResponse.class);
                            Log.i(TAG, "Twitter query response: " + twitterResponse.text);
                            String tweet = twitterResponse.getText() + "\n"+twitterResponse.getCreated_at();
                            mTweetList.add(tweet);




                        }
                    }
                });

            }
        });


        //get reference to sharedPrefs
        mPrefs = getSharedPreferences("my_shared_prefs", MODE_PRIVATE);
        //lets check the prefs to see if we have an access token
        if (!mPrefs.contains(ACCESS_TOKEN)) {
            Log.i(TAG, "Getting Access Token");
            doMyAuthentication();
        } else {
            Log.i(TAG, "Already have the Access Token");
            //and we store tha access token at the end of doMyAuthentication
        }



    }


    public void doMyAuthentication() {
        //get and concatenate my creds
        String consumer_key = "xvz1evFS4wEEPTGEFPHBog";
        String consumer_secret = "L8qq9PZyRg6ieKGEKhZolGC0vJWLw8iEJ88DRdyOg";
        String encodedKey = consumer_key + ":" + consumer_secret;
        //base64 encode it, get byte array from our encodedKey string
        String base64EncodedString = Base64.encodeToString(encodedKey.getBytes(), Base64.NO_WRAP);//these flags append to the base64 encoded string and change how it's formatted - usually it will be NO_WRAP unless otherwise sepcified
        Log.i(TAG, "base64encodedstring: " + base64EncodedString);


        //obtain bearer token
        //get client to make request
        OkHttpClient client = new OkHttpClient();

        //make body, and post request
        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build();

        Request apiRequest = new Request.Builder()
                .addHeader("Authorization", "Basic " + base64EncodedString)
                .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .post(requestBody)
                .url("https://api.twitter.com/oauth2/token")
                .build();
        //we have the request header and body and url, so lets make da call
        client.newCall(apiRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "Your API request failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "Your API request succeeded!");


                //tryna figure out why i can't get my access token!!
//                try {
//                    JSONObject accessToken = new JSONObject(response.body().string());
//                    Log.i(TAG, "response: "+ accessToken.getString("access_token"));
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                //make sure this was successful
                if (!response.isSuccessful()) {
                    //take a nap, never come back
                } else {

                    //THIS PART ISNT HAPPENING. WHY?
                    //GSON is gonna parse this for us so go make an object for it to use
                    Gson gson = new Gson();
                    //now parse the JSON into our GSON object
                    TwitterOAuthResponse oauthResponse = gson.fromJson(response.body().string(), TwitterOAuthResponse.class);//gson parses the data, and puts it in our object
                    Log.i(TAG, "Response: " + oauthResponse.getAccess_token());
                    //lets save our access token in shared prefs so we dont have to get it again
                    mPrefs.edit().putString(ACCESS_TOKEN, oauthResponse.getAccess_token()).commit();//commit ensure that it is stored in our shared preferences


                }

            }
        });
    }


}
