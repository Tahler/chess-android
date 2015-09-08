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
        String prompt = "Speak your move";
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt);
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
            } else {
                controller.notifySpeechError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Move interpretMove(String s) {
        //Trying an iterative method
        s = s.replace(" ", "");
        s = s.replace("-", "");
        s = s.replace("*", "");
        int y1 = -1, x1 = -1, y2 = -1, x2 = -1;
        int startIndex = 0;
        int endIndex = 0;
        try {
            while (y1 == -1) {
                y1 = getColumn(s.substring(startIndex, ++endIndex));
            }
            startIndex = endIndex;

            while (x1 == -1) {
                x1 = getRow(s.substring(startIndex, ++endIndex));
            }

            startIndex = endIndex;

            while (y2 == -1) {
                y2 = getColumn(s.substring(startIndex, ++endIndex));
                int delimit = checkForDelimiter(s.substring(startIndex, endIndex));
                if (delimit != -1) {
                    startIndex += delimit;
                    endIndex = startIndex;
                }
            }

            startIndex = endIndex;

            while (x2 == -1) {
                x2 = getRow(s.substring(startIndex, ++endIndex));
            }

            Move m = new Move(y1, x1, y2, x2);
            Log.d("spoken_text", m.toString());
            return m;
        } catch (IndexOutOfBoundsException e) {
            Log.d("spoken_text", "Out of bounds!");
            return null;
        }
    }

    private int getColumn(String s) {
        switch (s.toUpperCase()) {
            case "ALPHA":
            case "ALFA":
            case "HOUR":
            case "OUTOF":
                return 0;
            case "BRAVO":
            case "RADO":
            case "50":
                return 1;
            case "CHARLIE":
                return 2;
            case "DELTA":
                return 3;
            case "ECHO":
            case "IGO":
            case "AGO":
            case "80":
                return 4;
            case "FOXTROT":
                return 5;
            case "GOLF":
                return 6;
            case "HOTEL":
                return 7;
            default:
                return -1;
        }
    }

    private int getRow(String s) {
        switch(s.toUpperCase()) {
            case "ONE":
            case "WON":
            case "1":
            case "01":
                return 7;
            case "TOO":
            case "TO":
            case "TWO":
            case "2":
            case "02":
                return 6;
            case "THREE":
            case "3":
            case "03":
                return 5;
            case "FOUR":
            case "FOR":
            case "BEFORE":
            case "4":
            case "04":
                return 4;
            case "FIVE":
            case "5":
            case "05":
                return 3;
            case "SIX":
            case "6":
            case "06":
            case "STICK":
                return 2;
            case "SEVEN":
            case "7":
            case "07":
                return 1;
            case "EIGHT":
            case "ATE":
            case "8":
            case "08":
                return 0;
            default:
                //Check again with the first char just in case
                if (s.length() == 2) {
                    return (getRow("" + s.charAt(0)));
                }
                return -1;
        }
    }

    private int checkForDelimiter(String s) {
        switch (s) {
            case "2":
                return 1;
            case "to":
                return 2;
            case "two":
            case "too":
                return 4;
        }
        return -1;
    }

}
