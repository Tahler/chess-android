package edu.neumont.pro180.chess.core.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
    private Paint paint;
    private static int tileSize;
    private Piece[][] pieces; // cached, set from the controller call

    public ChessBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                tileSize = (int) (getWidth() / 8.0);
                Log.d("TILE_SIZE", String.valueOf(tileSize));

                paint = new Paint();

                draw();
            }

            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {}
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}
        });
        setOnTouchListener(this);
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) return;
        Log.d("DRAW", "Redrawing the screen");
        canvas.drawColor(Color.BLACK);

        drawTiles(canvas);
        drawPieces(canvas);
    }
    private void drawTiles(Canvas canvas) {
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
    }
    private void drawPieces(Canvas canvas) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces != null) {
                    Piece piece = pieces[j][i];
                    if (piece != null) {
                        // TODO store these bitmaps from the start

                        // This is immutable, and not scaled to the size of the tile
                        Bitmap unscaled = getPieceBitMap(piece);

                        // Create a scaled instance of the above
                        Bitmap scaled = Bitmap.createScaledBitmap(unscaled, tileSize, tileSize, false);
                        canvas.drawBitmap(scaled, i * tileSize, j * tileSize, paint);
                    }
                }
            }
        }
    }

    /**
     * @return An immutable Bitmap representing the piece
     */
    private Bitmap getPieceBitMap(Piece piece) {
        if (piece.getColor().equals(edu.neumont.pro180.chess.core.model.Color.LIGHT)) {
            switch (piece.getType()) {
                case PAWN:
                    return BitmapFactory.decodeResource(getResources(), R.drawable.pl);
                case ROOK:
                    return BitmapFactory.decodeResource(getResources(), R.drawable.rl);
                case KNIGHT:
                    return BitmapFactory.decodeResource(getResources(), R.drawable.nl);
                case BISHOP:
                    return BitmapFactory.decodeResource(getResources(), R.drawable.bl);
                case QUEEN:
                    return BitmapFactory.decodeResource(getResources(), R.drawable.ql);
                case KING:
                    return BitmapFactory.decodeResource(getResources(), R.drawable.kl);
            }
        } else {
            switch (piece.getType()) {
                case PAWN:
                    return BitmapFactory.decodeResource(getResources(), R.drawable.pd);
                case ROOK:
                    return BitmapFactory.decodeResource(getResources(), R.drawable.rd);
                case KNIGHT:
                    return BitmapFactory.decodeResource(getResources(), R.drawable.nd);
                case BISHOP:
                    return BitmapFactory.decodeResource(getResources(), R.drawable.bd);
                case QUEEN:
                    return BitmapFactory.decodeResource(getResources(), R.drawable.qd);
                case KING:
                    return BitmapFactory.decodeResource(getResources(), R.drawable.kd);
            }
        }
        return null;
    }

    @Override
    public void displayBoard(Piece[][] pieces) {
        updatePieces(pieces);
    }

    public void updatePieces(Piece[][] pieces) {
        Log.d("displayBoard()", "Received new Piece[][] set");
        this.pieces = pieces;
    }

    private void draw() {
        Canvas canvas = holder.lockCanvas(null);
        if (canvas != null) {
            draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void notifyIsInCheck() {
        // TODO notify the user of check
    }

    @Override
    public void highlightTiles(Tile start, List<Tile> ends) {
        Canvas canvas = holder.lockCanvas();

        drawTiles(canvas);

        highlightTile(start, Color.BLUE, canvas);
        for (Tile end : ends) {
            highlightTile(end, Color.YELLOW, canvas);
        }
        // TODO captures in red? might look dumb

        drawPieces(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    /**
     * Redirected to highlightTile(Tile, int, Canvas) with the canvas argument as the
     * SurfaceHolder's locked canvas. holder.unlockCanvasAndPost(canvas) is called at the end.
     */
    public void highlightTile(Tile tile, int color) {
        Canvas canvas = holder.lockCanvas();
        highlightTile(tile, color, canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    public void highlightTile(Tile tile, int color, Canvas canvas) {
        paint.setColor(Color.BLACK);
        canvas.drawRect(new Rect(
                tile.x * tileSize,
                tile.y * tileSize,
                tile.x * tileSize + tileSize,
                tile.y * tileSize + tileSize
            ), paint);

        paint.setColor(color);
        int padding = 2;
        canvas.drawRect(new Rect(
                tile.x * tileSize + padding,
                tile.y * tileSize + padding,
                tile.x * tileSize + tileSize - padding,
                tile.y * tileSize + tileSize - padding
            ), paint);
    }

    @Override
    public Move readMove() {
        final Tile[] from = {null};
        final Tile[] to = {null};

        Thread readMove = new Thread(new Runnable() {
            @Override
            public void run() {
                while (ChessBoardView.this.from == null && ChessBoardView.this.to == null); // TODO: bad practice? it's waiting until onTouch finds a move

                // Store what onTouch() found
                from[0] = ChessBoardView.this.from;
                to[0] = ChessBoardView.this.to;

                // Reset for onTouch later
                ChessBoardView.this.from = null;
                ChessBoardView.this.to = null;

                notify();
            }
        });

        readMove.start();

        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Send the move back to the controller
        return new Move(from[0], to[0]);
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
            // For testing
//            if (listener != null) listener.tileSelected(getTileAt(event.getX(), event.getY()));

            if (from == null) { // First touch (selecting the piece to move)
                from = getTileAt(event.getX(), event.getY());
                if (listener != null) listener.tileSelected(from);
            } else { // Second touch (selecting the destination)
                to = getTileAt(event.getX(), event.getY());
            }

            // Return true because the touch has been handled
            return true;
        }

        return false;
    }

    private Tile getTileAt(float x, float y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);

        try {
            return new Tile(tileX, tileY);
        } catch (IndexOutOfBoundsException e) {
            Log.e("", "Touched outside chess board");
            return null;
        }
    }
}
