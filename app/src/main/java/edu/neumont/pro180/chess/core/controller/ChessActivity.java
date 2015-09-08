package edu.neumont.pro180.chess.core.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Display;
import android.view.ViewGroup;

import java.util.List;

import edu.neumont.pro180.chess.R;
import edu.neumont.pro180.chess.core.model.Color;
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
            System.out.println(spokenText);
            // do stuff with the spoken text
//            controller.moveSelected(new Move());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
