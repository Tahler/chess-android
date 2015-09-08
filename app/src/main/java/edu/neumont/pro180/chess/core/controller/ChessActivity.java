package edu.neumont.pro180.chess.core.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;

import java.util.List;

import edu.neumont.pro180.chess.R;
import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.view.ChessBoardView;
import edu.neumont.pro180.chess.core.view.PlayerView;

/**
 * Simply wraps the Controller in an activity, so that it may run on Android.
 */
public class ChessActivity extends Activity implements SpeechRequestListener {

    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

        // Center the board
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        ChessBoardView boardView = (ChessBoardView) findViewById(R.id.chess_board_view);
        ViewGroup.LayoutParams params = boardView.getLayoutParams();
        params.height = size.x;
        params.width = size.x;
        int playerViewHeight = (size.y - size.x) / 2;

        // Set player views to correct size
        PlayerView light = (PlayerView) findViewById(R.id.light_player_view);
        light.getLayoutParams().width = size.x;
        light.getLayoutParams().height = playerViewHeight;
        light.setColor(Color.LIGHT);
        light.setListener(this);

        PlayerView dark = (PlayerView) findViewById(R.id.dark_player_view);
        dark.getLayoutParams().width = size.x;
        dark.getLayoutParams().height = playerViewHeight;
        dark.rotate();
        dark.setColor(Color.DARK);
        dark.setListener(this);

        // Attach the player views to the board
        boardView.setLightPlayerView(light);
        boardView.setDarkPlayerView(dark);

        // Start a new controller with access to the boardView
        controller = new Controller(boardView);
    }

    @Override
    public void speechRequested() {
        displaySpeechRecognizer();
    }

    // Copy pasted
    private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            Log.d("spoken_text", spokenText);
            // Do something with spokenText
            Move m = interpretMove(spokenText);
            if (m != null) {
                Log.d("spoken_text", m.toString());
                controller.moveSelected(m);
            }



        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Move interpretMove(String s) {
        String[] words = s.split(" ");
        if (words.length < 4 || words.length > 5) return null;
        int y1 = getColumn(words[0]);
        int x1 = getRow(words[1]);
        int y2, x2;
        if (words.length == 4) {
            y2 = getColumn(words[2]);
            x2 = getRow(words[3]);
        } else {
            y2 = getColumn(words[3]);
            x2 = getRow(words[4]);
        }
        if (x1 == -1 || y1 == -1 || y2 == -1 || x2 == -1) return null;
        return new Move(y1, x1, y2, x2);
    }

    private int getColumn(String s) {
        switch (s.toUpperCase()) {
            case "ALPHA":
            case "A":
                return 0;
            case "BRAVO":
            case "B":
                return 1;
            case "CHARLIE":
            case "C":
                return 2;
            case "DELTA":
            case "D":
                return 3;
            case "ECHO":
            case "E":
                return 4;
            case "FOXTROT":
            case "F":
                return 5;
            case "GOLF":
            case "G":
                return 6;
            case "HOTEL":
            case "H":
                return 7;
            default:
                return -1;
        }
    }

    private int getRow(String s) {
        if (s.length() == 2) s = "" + s.charAt(0);
        switch(s.toUpperCase()) {
            case "ONE":
            case "WON":
            case "1":
                return 7;
            case "TOO":
            case "TO":
            case "TWO":
            case "2":
                return 6;
            case "THREE":
            case "3":
                return 5;
            case "FOUR":
            case "FOR":
            case "4":
                return 4;
            case "FIVE":
            case "5":
                return 3;
            case "SIX":
            case "6":
                return 2;
            case "SEVEN":
            case "7":
                return 1;
            case "EIGHT":
            case "ATE":
            case "8":
                return 0;
            default:
                return -1;
        }
    }

}
