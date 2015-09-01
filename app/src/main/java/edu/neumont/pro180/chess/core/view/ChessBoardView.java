package edu.neumont.pro180.chess.core.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import edu.neumont.pro180.chess.R;
import edu.neumont.pro180.chess.core.model.Board;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;
import edu.neumont.pro180.chess.core.model.Tile;

public class ChessBoardView extends SurfaceView implements View, android.view.View.OnTouchListener {
    private View.Listener listener;
    private SurfaceHolder holder;
    private static float tileSize;
    private Piece[][] pieces; // cached, set from the controller call

    public ChessBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Canvas canvas = holder.lockCanvas(null);
                onDraw(canvas);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}
        });
        this.setOnTouchListener(this);
//        BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        tileSize = (float) (getWidth() / 8.0);
        Log.d("TILE_SIZE", String.valueOf(tileSize));

        // Draw the tiles
        Paint paint = new Paint();
        for (int i = 0; i < 8; i++) { // horizontal
            for (int j = 0; j < 8; j++) { // vertical
                paint.setColor(((i + j) % 2 == 0) ? Color.WHITE : Color.GRAY);
                canvas.drawRect(
                        i * tileSize,
                        j * tileSize,
                        i * tileSize + tileSize,
                        j * tileSize + tileSize,
                        paint);
                if (pieces != null) {
                    Piece piece = pieces[i][j]; // j, i?
                    if (piece != null) {
                        Bitmap pieceBitMap = getPieceBitMap(piece);
                        canvas.drawBitmap(pieceBitMap, i * tileSize, j * tileSize, paint); // Replace paint with null?
                    }
                }
            }
        }
    }

    private Bitmap getPieceBitMap(Piece piece) {
        switch (piece.getType()) {

        }
        return null;
    }

    @Override
    public void displayBoard(Piece[][] pieces) {
        this.pieces = pieces;
    }

    @Override
    public void notifyIsInCheck() {
        // TODO notify the user of check
    }

    @Override
    public void highlightMoves(List<Move> moves) {
        // TODO call highlightTile on all move ends in yellow, start in blue, (..captures in red?)
    }

    public void highlightTile(Tile tile, Color color) {

    }

    @Override
    public Move readMove() {
        return null;
    }

    @Override
    public Piece.Type getPawnPromotion() {
        final String[] choices = new String[] {
                "Queen",
                "Knight",
                "Rook",
                "Bishop"
        };
        final String[] choice = new String[1];

        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle(R.string.pawn_promotion)
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choice[0] = choices[which];
                    }
                })
                .create()
                .show();

        switch (choice[0]) {
            case "Queen":
                return Piece.Type.QUEEN;
            case "Knight":
                return Piece.Type.KNIGHT;
            case "Rook":
                return Piece.Type.ROOK;
            case "Bishop":
                return Piece.Type.BISHOP;
            default:
                return null;
        }
    }

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private Tile from;
    private Tile to;
    @Override
    public boolean onTouch(android.view.View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (from == null) { // First touch (selecting the piece to move)
                from = getTileAt(event.getX(), event.getY());
                if (listener != null) listener.tileSelected(from);
            } else { // Second touch (selecting the destination)
                to = getTileAt(event.getX(), event.getY());
                // TODO: Send the move to the controller ( new Move(from, to) )
            }

            // Return true because the touch has been handled
            return true;
        }

        return false;
    }

    private Tile getTileAt(float x, float y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);

        Log.d("TileTouched", tileX + ", " + tileY);

        try {
            return new Tile(tileX, tileY);
        } catch (IndexOutOfBoundsException e) {
            Log.e("", "Touched outside chess board");
            return null;
        }
    }
}
