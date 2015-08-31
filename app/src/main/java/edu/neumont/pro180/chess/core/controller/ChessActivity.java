package edu.neumont.pro180.chess.core.controller;

import android.app.Activity;
import android.os.Bundle;

import edu.neumont.pro180.chess.R;

/**
 * The controller.
 * Instructs the interface to interact with the user and retrieve moves.
 * Validates moves before executing them on the board.
 */
public class ChessActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

//        Engine controller = new Engine((View) findViewById(R.id.chess_board_view));
//        controller.play();
    }
}
