package edu.neumont.pro180.chess.core.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

import edu.neumont.pro180.chess.R;
import edu.neumont.pro180.chess.core.model.Piece;

public class PlayerView extends SurfaceView {
    private SurfaceHolder holder;
    private static int displaySize;
    private List<Piece> capturedPieces;
    private Paint paint;

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        capturedPieces = new ArrayList<>();
        holder = getHolder();

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Canvas canvas = holder.lockCanvas(null);
                displaySize = (int) (getWidth() / 8.0 * 0.60D);
                paint = new Paint();
                holder.unlockCanvasAndPost(canvas);
                draw();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            }
        });


    }

    @Override
    public void draw(Canvas canvas) {

        canvas.drawColor(Color.LTGRAY);

        drawPieces(canvas);
    }

    private void drawPieces(Canvas canvas) {
        int count = 0;
        int y = 0;
        int overflow = (getRootView().getWidth() / displaySize) - 1;
        for (Piece p : capturedPieces) {
            if (count > overflow) {
                y += displaySize;
                count = 0;
            }
            Bitmap unscaled = getPieceBitMap(p);
            Bitmap scaled = Bitmap.createScaledBitmap(unscaled, displaySize, displaySize, false);
            canvas.drawBitmap(scaled, count * displaySize, y, paint);
            count++;
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

    public void setCapturedPieces(List<Piece> capturedPieces) {
        this.capturedPieces = capturedPieces;
        draw();
    }

    private void draw() {
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }
}
