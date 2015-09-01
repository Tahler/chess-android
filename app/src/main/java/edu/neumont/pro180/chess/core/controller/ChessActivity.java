package edu.neumont.pro180.chess.core.controller;

import android.app.Activity;
import android.os.Bundle;

import edu.neumont.pro180.chess.R;

/**
 * Simply wraps the Controller in an activity, so that it may run on Android.
 */
public class ChessActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

//        Controller controller = new Controller((View) findViewById(R.id.chess_board_view));
//        controller.play();
    }
}
