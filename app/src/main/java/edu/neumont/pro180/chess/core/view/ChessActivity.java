package edu.neumont.pro180.chess.core.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;

import java.text.ParseException;

import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;
import edu.neumont.pro180.chessandroid.R;

public class ChessActivity extends Activity implements View, SurfaceHolder.Callback {
    private ChessBoardView board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);
    }

    @Override
    public Move readMove() throws ParseException {
        return null;
    }

    @Override
    public Piece.Type getPawnPromotion() {
        // TODO: some dialog
        return null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
