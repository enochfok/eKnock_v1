package com.example.ivanchow.eknock_v1;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private String inputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        resultText  = (TextView)findViewById(R.id.textViewResult);
    }

    public void onButtonClick(View v){
        if(v.getId() == R.id.imageButton){
            promptSpeechInput();
        }
    }
    public ToneAnalysis getTone(String bam){
        IamOptions options = new IamOptions.Builder()
                .apiKey("rlJ4zjrOU9mXG1SQw8ekGWyHmN7XfNSytFM_-cCpugl4")
                .build();

        ToneAnalyzer toneAnalyzer = new ToneAnalyzer("2017-09-21", options);
        toneAnalyzer.setEndPoint("https://gateway.watsonplatform.net/tone-analyzer/api");

        String text = bam;

        ToneOptions toneOptions = new ToneOptions.Builder()
                .text(text)
                .build();

        ToneAnalysis toneAnalysis = toneAnalyzer.tone(toneOptions).execute();
        //for()
        Log.w("tone", toneAnalysis.toString());
        return toneAnalysis;
    }
    public void promptSpeechInput(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something!");

        try{
            startActivityForResult(i, 100);
        }
        catch(ActivityNotFoundException a){
            Toast.makeText(MainActivity.this, "Sorry, your device does not support this feature!", Toast.LENGTH_LONG).show();
        }
    }

    public void onActivityResult(int request_code, int result_code, Intent i){
        super.onActivityResult(request_code, result_code, i);

        switch(request_code){
            case 100: if(result_code == RESULT_OK && i != null){
                ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                resultText.setText(result.get(0));
                getTone(result.get(0));
            }
                break;
        }
    }
}
