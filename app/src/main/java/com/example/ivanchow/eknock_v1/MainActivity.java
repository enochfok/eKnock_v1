package com.example.ivanchow.eknock_v1;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;

import java.util.ArrayList;
import java.util.Locale;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.lang.Math;


public class MainActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private String token;
    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    private static final int REQUEST_CODE = 123;
    private static final String REDIRECT_URI = "eknock://callback";

    TextView resultText;
    TextView resultText2;
    TextView resultText3;
    TextView prompt1;
    TextView prompt2;
    TextView prompt3;

    ImageButton spotify;
    ImageView mainLogo;
    ImageButton cmha_button;
    ImageButton mainButton;

    TextView finalText;


    String prompt[];
    int index;
    double finalMoodRating;
    String finalLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueue = Volley.newRequestQueue(this);


        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder("adfc82ad02ec49268333af2c85ebab71", AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);



        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        resultText  = (TextView)findViewById(R.id.textViewResult);
        resultText2 = (TextView)findViewById(R.id.textViewResult2);
        resultText3 = (TextView)findViewById(R.id.textViewResult3);

        prompt1 = (TextView)findViewById(R.id.prompt1);
        prompt2 = (TextView)findViewById(R.id.prompt2);
        prompt3 = (TextView)findViewById(R.id.prompt3);

        prompt1.setVisibility(TextView.INVISIBLE);
        prompt2.setVisibility(TextView.INVISIBLE);
        prompt3.setVisibility(TextView.INVISIBLE);

        finalText = (TextView)findViewById(R.id.textFinal) ;
        finalText.setVisibility(TextView.INVISIBLE);

        spotify = (ImageButton)findViewById(R.id.spotify);
        spotify.setVisibility(TextView.INVISIBLE);

        mainLogo = (ImageView)findViewById(R.id.imageView2);
        //mainLogo.setBackgroundColor(Color.rgb(0,0,0));

        mainButton = (ImageButton)findViewById(R.id.imageButton);
        cmha_button = (ImageButton) findViewById(R.id.cmha_button);

        index = 0;
        prompt = new String[]{"Hello! How are you feeling?", "Go on, I'd like to hear more", "What else can you tell me?", ""};
        finalMoodRating = 0;
        finalLink = "";
    }

    public void onButtonClick(View v){
        mainButton.setImageResource(R.drawable.the_last_door);

        index = 0;
        spotify.setVisibility(TextView.INVISIBLE);

        prompt2.setVisibility(TextView.INVISIBLE);
        prompt3.setVisibility(TextView.INVISIBLE);

        resultText.setText("");
        resultText2.setText("");
        resultText3.setText("");

        finalText.setVisibility(TextView.INVISIBLE);

        if(v.getId() == R.id.imageButton){
            promptSpeechInput(prompt[index]);
        }
        prompt1.setVisibility(TextView.VISIBLE);
    }

    public void spotify_Direct(View v){
        Uri uri = Uri.parse(finalLink); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void linkToSite(View v){
        Uri uri = Uri.parse("https://cmha.ca/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    public double getTone(String bam){
        IamOptions options = new IamOptions.Builder()
                .apiKey("rlJ4zjrOU9mXG1SQw8ekGWyHmN7XfNSytFM_-cCpugl4")
                .build();

        ToneAnalyzer toneAnalyzer = new ToneAnalyzer("2017-09-21", options);
        toneAnalyzer.setEndPoint("https://gateway.watsonplatform.net/tone-analyzer/api");

        String text = bam;

        ToneOptions toneOptions = new ToneOptions.Builder()
                .text(text)
                .build();
        double currentHighest = 0;
        String currentMood = "";
        ToneAnalysis toneAnalysis = toneAnalyzer.tone(toneOptions).execute();
        for(int i = 0; i < toneAnalysis.getDocumentTone().getTones().size(); i++){
            if(toneAnalysis.getDocumentTone().getTones().get(i).getScore() > currentHighest){
                String testString = toneAnalysis.getDocumentTone().getTones().get(i).getToneId();
                if(testString.equals("joy") || testString.equals("anger") || testString.equals("fear") || testString.equals("sadness")) {
                    currentMood = testString;
                    currentHighest = toneAnalysis.getDocumentTone().getTones().get(i).getScore();
                } else {
                    continue;
                }
            }
        }
        double finalResult = 0;
        if(currentMood.equals("joy")) {
            finalResult = 0.75 + 0.25 * currentHighest - Math.random()/10;
            index = 0;
        } else if (currentMood.equals("anger")) {
            finalResult = 0.5 + 0.25 * currentHighest - Math.random()/10;
            index = 0;
        } else if (currentMood.equals("fear")) {
            finalResult = 0.5 - 0.25 * currentHighest + Math.random()/10;
            index = 0;
        } else if (currentMood.equals("sadness")) {
            finalResult = 0.251 - 0.25 * currentHighest + Math.random()/10;
            index = 0;
        } else {
            index++;
            if(index == 1){
                prompt2.setVisibility(TextView.VISIBLE);
            }
            else if(index == 2){
                prompt3.setVisibility(TextView.VISIBLE);
            }
            promptSpeechInput(prompt[index]);
        }
        return finalResult;
    }
    public void promptSpeechInput(String p){
        if (index == 3){
            index = 0;
            jsonParse(1.0);
        }
        else {
            Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, p);

            try{
               startActivityForResult(i, 100);

            }
            catch(ActivityNotFoundException a){
                Toast.makeText(MainActivity.this, "Sorry, your device does not support this feature!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onActivityResult(int request_code, int result_code, Intent i){
        super.onActivityResult(request_code, result_code, i);

        if (request_code == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(result_code, i);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    token = response.getAccessToken();
                    Log.e("debug", token);
                    // Handle successful response
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.e("debug", response.getError());
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.e("debug", "what");
                    // Handle other cases
            }
        }

        switch(request_code){
            case 100: if(result_code == RESULT_OK && i != null){
                ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(index == 0){
                    resultText.setText(result.get(0));
                    double currentValue = getTone(result.get(0));
                    jsonParse(currentValue);
                    finalMoodRating = currentValue;
                    if(finalMoodRating != 0){
                        finalText.setVisibility(TextView.VISIBLE);
                        spotify.setVisibility(TextView.VISIBLE);

                        mainButton.setImageResource(R.drawable.door_closed);

                    }
                }
                else if(index == 1){
                    resultText2.setText(result.get(0));
                    double currentValue = getTone(result.get(0));
                    jsonParse(currentValue);
                    finalMoodRating = currentValue;
                    if(finalMoodRating != 0){
                        finalText.setVisibility(TextView.VISIBLE);
                        spotify.setVisibility(TextView.VISIBLE);

                        mainButton.setImageResource(R.drawable.door_closed);

                    }
                }
                else{
                    resultText3.setText(result.get(0));
                    double currentValue = getTone(result.get(0));
                    jsonParse(currentValue);
                    finalMoodRating = currentValue;
                    finalText.setVisibility(TextView.VISIBLE);
                    spotify.setVisibility(TextView.VISIBLE);

                    mainButton.setImageResource(R.drawable.door_closed);

                }


            }
                break;
        }
    }
    private void jsonParse(double valence) {


        String url = "https://api.spotify.com/v1/recommendations?target_valence=" + valence + "&seed_genres=pop, hip_hop, classical, hard_rock";

        // this is the test json file
        //String url = "https://api.myjson.com/bins/7i8o0";
        //String token = "BQCRqH8mNoo5Hiij_O1lkoPYXqVjyW1Z82TN66MqYyZG_i_fXSeyTz3bbiNeSeiIxyh5cxocfYzDrry-LMuO_t5xngWcCUDnSyhiHNtQ4_oXj2g1lzIMLOOWFm1DdxplKchGZ-nA1d1tMw";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("debug", response.toString());
                            JSONArray jsonArray = response.getJSONArray("tracks");


                            JSONObject albumObj = jsonArray.getJSONObject(0);
                            JSONObject extObj = albumObj.getJSONObject("external_urls");
                            String link = extObj.getString("spotify");
                            finalLink = link;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");

                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        mQueue.add(request);
    }
}
