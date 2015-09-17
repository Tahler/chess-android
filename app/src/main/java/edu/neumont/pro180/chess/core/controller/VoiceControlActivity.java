package edu.neumont.pro180.chess.core.controller;


import android.app.Activity;
import android.os.Bundle;

import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;


public class VoiceControlActivity extends Activity implements RecognitionListener {
    private SpeechRecognizer recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recognizer = SpeechRecognizerHolder.getRecognizer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recognizer.addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        recognizer.removeListener(this);
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {

    }

    @Override
    public void onResult(Hypothesis hypothesis) {

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {

    }






}
