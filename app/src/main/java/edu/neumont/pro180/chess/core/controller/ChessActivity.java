package edu.neumont.pro180.chess.core.controller;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;

import edu.neumont.pro180.chess.R;
import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.view.CapturedPieceView;
import edu.neumont.pro180.chess.core.view.ChessBoardView;
import edu.neumont.pro180.chess.core.view.View;

/**
 * Simply wraps the Controller in an activity, so that it may run on Android.
 */
public class ChessActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

        View view = (View) findViewById(R.id.chess_board_view);
        Controller controller = new Controller(view);
        setupBoard();
    }

    private void setupBoard() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        ChessBoardView board = (ChessBoardView) findViewById(R.id.chess_board_view);
        ViewGroup.LayoutParams params = board.getLayoutParams();
        params.height = size.x;
        params.width = size.x;
        board.setLayoutParams(params);
        CapturedPieceView dark = (CapturedPieceView) findViewById(R.id.captured_dark);
        dark.setC(Color.DARK);
        CapturedPieceView light = (CapturedPieceView) findViewById(R.id.captured_light);
        light.setC(Color.LIGHT);
        ViewGroup.LayoutParams captureDark = dark.getLayoutParams();
        ViewGroup.LayoutParams captureLight = light.getLayoutParams();
        captureDark.width = size.x;
        //captureDark.height = size.x/4;
        captureLight.width = size.x;
        //captureLight.height = size.x/4;
        board.addCapturedPieceView(dark);
        board.addCapturedPieceView(light);
    }
}
