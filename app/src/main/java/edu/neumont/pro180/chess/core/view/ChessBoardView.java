package edu.neumont.pro180.chess.core.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;
import edu.neumont.pro180.chess.core.model.Tile;

public class ChessBoardView extends SurfaceView implements View, android.view.View.OnTouchListener {
    private View.Listener listener;
    private SurfaceHolder holder;
    private static float tileSize;

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
            }
        }
//        canvas.drawBitmap(myChar, 10, 10, null);
    }

    @Override
    public void notifyIsInCheck() {

    }

    @Override
    public void highlightMoves(List<Move> moves) {

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
            Log.d("Touched", "");

            if (from == null) { // First touch (selecting the piece to move)
                from = getTileAt(event.getX(), event.getY());
                if (listener != null) listener.tileSelected(new Tile(0, 0));
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
        float tileX = x / tileSize;
        float tileY = y / tileSize;

        Log.d("X, Y", x + ", " + y);

        return null;
    }
}
