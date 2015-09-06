package edu.neumont.pro180.chess.core.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

import edu.neumont.pro180.chess.R;
import edu.neumont.pro180.chess.core.model.Piece;

public class CapturedPieceView extends SurfaceView {
    private SurfaceHolder holder;
    private static int displaySize;
    private List<Piece> capturedPieces;
    private Paint paint;

    public CapturedPieceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        capturedPieces = new ArrayList<>();
        holder = getHolder();

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Canvas canvas = holder.lockCanvas(null);

                displaySize = (int) (getHeight()/2);
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
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.LTGRAY);
        drawPieces(canvas);
        canvas.rotate(180);
    }

    private void drawPieces(Canvas canvas) {
        int[] xPos = {0, 0, displaySize, displaySize*2, displaySize*3, displaySize*4};
        int y;
        int x;
        for (Piece p : capturedPieces) {
            Bitmap unscaled = getPieceBitMap(p);

            Bitmap scaled = Bitmap.createScaledBitmap(unscaled, displaySize, displaySize, false);
            if (p.getType().equals(Piece.Type.PAWN)) {
                y = 0;
                x = xPos[0];
                xPos[0] += displaySize;
            }
            else {
                y = displaySize;
                switch (p.getType()) {
                    case ROOK:
                        x = xPos[1];
                        xPos[1] = xPos[1] + 7*displaySize;
                        break;
                    case KNIGHT:
                        x = xPos[2];
                        xPos[2] = xPos[2] + 5*displaySize;
                        break;
                    case BISHOP:
                        x = xPos[3];
                        xPos[3] = xPos[3] + 3*displaySize;
                        break;
                    case QUEEN:
                        x = xPos[4];
                        xPos[4] += 5;
                        break;
                    case KING:
                        x = xPos[5];
                        break;
                    default:
                        x = -1;
                }
            }
            canvas.drawBitmap(scaled, x, y, paint);
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
