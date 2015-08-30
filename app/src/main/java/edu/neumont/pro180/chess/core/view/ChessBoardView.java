package edu.neumont.pro180.chess.core.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.text.ParseException;

import edu.neumont.pro180.chess.R;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;

public class ChessBoardView extends SurfaceView implements View {
    TileProperty[][] tiles;
        // setColor - changes the background color
        // setPiece - adds a bitmap piece on top
    private SurfaceHolder holder;

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
        float tileSize = (float) (getWidth() / 8.0); // TODO: more casts ?, canvas.getWidth() ?

        canvas.drawColor(Color.BLACK);

        // TODO: probably need to encapsulate the tiles to know which tile is touched... would be useful for readMove()

        // Draw the tiles
        for (int i = 0; i < 8; i++) { // horizontal
            for (int j = 0; j < 8; j++) { // vertical
                Paint paint = new Paint();
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
    public Move readMove() throws ParseException {
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

    // void highlightMoves(List<Move> moves)
    // void displayBoard(Piece[][] board)
}
