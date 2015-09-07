package edu.neumont.pro180.chess.core.controller;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;

import edu.neumont.pro180.chess.R;
import edu.neumont.pro180.chess.core.view.ChessBoardView;
import edu.neumont.pro180.chess.core.view.PlayerView;

/**
 * Simply wraps the Controller in an activity, so that it may run on Android.
 */
public class ChessActivity extends Activity {
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

        PlayerView dark = (PlayerView) findViewById(R.id.dark_player_view);
        dark.getLayoutParams().width = size.x;
        dark.getLayoutParams().height = playerViewHeight;
        dark.rotate();

        // Attach the player views to the board
        boardView.setLightPlayerView(light);
        boardView.setDarkPlayerView(dark);

        // Start a new controller with access to the boardView
        new Controller(boardView);
    }
}
