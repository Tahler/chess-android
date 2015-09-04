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
import edu.neumont.pro180.chess.core.model.Board;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;
import edu.neumont.pro180.chess.core.model.Tile;

public class ChessBoardView extends SurfaceView implements View, android.view.View.OnTouchListener {
    // For talking to the controller
    private View.Listener listener;

    // For drawing
    private SurfaceHolder holder;
    private Paint paint;

    // The two PlayerViews above and below the board.
    private PlayerView lightPlayerView;
    private PlayerView darkPlayerView;

    // The width in pixels of each tile
    private static int tileSize;
    private Piece[][] pieces; // cached, set from the controller call
    private Tile selectedTile; // Colored in blue
    private List<Tile> highlightedTiles; // Colored in yellow

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
        if (canvas == null) {
            System.out.println("Canvas was null");
            return;
        }
        Log.d("DRAW", "Redrawing the screen");
        canvas.drawColor(Color.BLACK);

        drawTiles(canvas);
        drawHighlights(canvas);
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

    private void drawHighlights(Canvas canvas) {
        if (selectedTile != null) highlightTile(selectedTile, Color.BLUE, canvas);
        if (highlightedTiles != null) {
            for (Tile end : highlightedTiles) {
                highlightTile(end, Color.YELLOW, canvas);
            }
            // TODO captures in red? might look dumb
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

    /**
     * Sets the pieces to be later drawn in the next possible display
     *
     * @param board The updated board
     */
    @Override
    public void displayBoard(Board board) {
        this.pieces = board.getPieces();
        Log.d("displayBoard()", "Received new Board");
        if (board.getCurrentTurnColor().equals(edu.neumont.pro180.chess.core.model.Color.LIGHT)) {
//            lightPlayerView.notifyTurn();
        } else {
//            darkPlayerView.notifyTurn();
        }
        lightPlayerView.setCapturedPieces(board.getLightCapturedPieces());
        darkPlayerView.setCapturedPieces(board.getDarkCapturedPieces());
        draw();
    }

    /**
     * Calls draw with the SurfaceHolder's canvas
     */
    private void draw() {
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void notifyCheck() {
//        lightPlayerView.notifyCheck();
//        darkPlayerView.notifyCheck();
        System.out.println("Check!");
    }

    @Override
    public void notifyGameOver(edu.neumont.pro180.chess.core.model.Color result) {
        // TODO
        System.out.println((result == null) ? "Stalemate!" : "Checkmate! The winner is " + result + "!");
    }

    /**
     * Sets the tiles to be highlighted, then redraws
     *
     * @param start The starting location
     * @param ends  A list of possible destinations
     */
    @Override
    public void highlightTiles(Tile start, List<Tile> ends) {
        Canvas canvas = holder.lockCanvas();
        // Set the tiles to be highlighted
        selectedTile = start;
        highlightedTiles = ends;
        draw(canvas);
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
        int borderWidth = 2;
        canvas.drawRect(new Rect(
                tile.x * tileSize + borderWidth,
                tile.y * tileSize + borderWidth,
                tile.x * tileSize + tileSize - borderWidth,
                tile.y * tileSize + tileSize - borderWidth
        ), paint);
    }

    private Tile from;
    private Tile to;
    @Override
    public boolean onTouch(android.view.View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Tile selected = getTileAt(event.getX(), event.getY());

            if (highlightedTiles == null || !highlightedTiles.contains(selected)) { // First touch (selecting the piece to move)
                from = getTileAt(event.getX(), event.getY());
                listener.tileSelected(from);
            } else { // Second touch (selecting the destination)
                to = getTileAt(event.getX(), event.getY());
                // Reset the highlights
                selectedTile = null;
                highlightedTiles.clear();
                // Send the move to the controller
                listener.moveSelected(new Move(from, to));
            }

            // Return true because the touch has been handled
            return true;
        }

        return false;
    }

    @Override
    public Piece.Type getPawnPromotion() {
        final String[] choices = new String[]{
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

    public void setLightPlayerView(PlayerView lightPlayerView) {
        this.lightPlayerView = lightPlayerView;
    }

    public void setDarkPlayerView(PlayerView darkPlayerView) {
        this.darkPlayerView = darkPlayerView;
    }

    private Tile getTileAt(float x, float y) {int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);

        try {
            return new Tile(tileX, tileY);
        } catch (IndexOutOfBoundsException e) {
            Log.e("", "Touched outside chess board");
            return null;
        }
    }

}
