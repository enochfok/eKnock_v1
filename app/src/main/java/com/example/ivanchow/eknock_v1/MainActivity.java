package com.example.ivanchow.eknock_v1;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView resultText;
    TextView resultText2;
    TextView resultText3;
    TextView prompt1;
    TextView prompt2;
    TextView prompt3;

    ImageButton spotify;
    ImageView mainLogo;


    String prompt[];
    int index;
    double finalMoodRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        spotify = (ImageButton)findViewById(R.id.spotify);
        spotify.setVisibility(TextView.INVISIBLE);

        mainLogo = (ImageView)findViewById(R.id.imageView2);
        //mainLogo.setBackgroundColor(Color.rgb(0,0,0));

        index = 0;
        prompt = new String[]{"Hello! How are you feeling?", "Go on, I'd like to hear more", "What else can you tell me?", ""};
        finalMoodRating = 0;
    }

    public void onButtonClick(View v){
        index = 0;
        spotify.setVisibility(TextView.INVISIBLE);

        prompt2.setVisibility(TextView.INVISIBLE);
        prompt3.setVisibility(TextView.INVISIBLE);

        resultText.setText("");
        resultText2.setText("");
        resultText3.setText("");

        if(v.getId() == R.id.imageButton){
            promptSpeechInput(prompt[index]);
        }
        prompt1.setVisibility(TextView.VISIBLE);
    }

    public void spotify_Direct(View v){
        System.out.println("Hi");
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
            finalResult = 0.75 + 0.25 * currentHighest;
            index = 0;
        } else if (currentMood.equals("anger")) {
            finalResult = 0.5 + 0.25 * currentHighest;
            index = 0;
        } else if (currentMood.equals("fear")) {
            finalResult = 0.25 - 0.25 * currentHighest;
            index = 0;
        } else if (currentMood.equals("sadness")) {
            finalResult = 0.5 - 0.25 * currentHighest;
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
            resultText2.setText(String.valueOf(1.0));
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

        switch(request_code){
            case 100: if(result_code == RESULT_OK && i != null){
                ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(index == 0){
                    resultText.setText(result.get(0));
                    finalMoodRating = getTone(result.get(0));
                    if(finalMoodRating != 0){
                        spotify.setVisibility(TextView.VISIBLE);
                    }
                }
                else if(index == 1){
                    resultText2.setText(result.get(0));
                    finalMoodRating = getTone(result.get(0));
                    if(finalMoodRating != 0){
                        spotify.setVisibility(TextView.VISIBLE);
                    }
                }
                else{
                    resultText3.setText(result.get(0));
                    spotify.setVisibility(TextView.VISIBLE);
                }


            }
                break;
        }
    }
}
