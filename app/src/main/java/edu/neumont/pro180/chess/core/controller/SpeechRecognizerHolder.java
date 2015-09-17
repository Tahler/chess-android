package edu.neumont.pro180.chess.core.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class SpeechRecognizerHolder {
    private static SpeechRecognizerHolder ourInstance = new SpeechRecognizerHolder();
    private static boolean ready = false;
    private static SpeechRecognizer recognizer;

    public static void initializeRecognizer(final Context context) {
        Log.d("Voice", "init");
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... voids) {
                try {
                    Log.d("Voice", "doInBackground");
                    Assets assets = new Assets(context);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    Log.d("Voice", e.toString());
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    ready = false;
                }
            }
        }.execute();
    }

    private static void setupRecognizer(File assetsDir) throws IOException{
        Log.d("Voice", "setup");
        recognizer = SpeechRecognizerSetup.defaultSetup().setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .getRecognizer();

    }

    public static SpeechRecognizer getRecognizer() {
        return recognizer;
    }
}
