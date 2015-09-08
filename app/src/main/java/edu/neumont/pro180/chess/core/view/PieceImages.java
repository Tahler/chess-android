package edu.neumont.pro180.chess.core.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import edu.neumont.pro180.chess.R;
import edu.neumont.pro180.chess.core.model.Piece;

public class PieceImages {
    private Bitmap pl;
    private Bitmap rl;
    private Bitmap nl;
    private Bitmap bl;
    private Bitmap ql;
    private Bitmap kl;

    private Bitmap pd;
    private Bitmap rd;
    private Bitmap nd;
    private Bitmap bd;
    private Bitmap qd;
    private Bitmap kd;

    public PieceImages(Resources resources, Integer width) {
        pl = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.pl), width, width, false);
        rl = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.rl), width, width, false);
        nl = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.nl), width, width, false);
        bl = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.bl), width, width, false);
        ql = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.ql), width, width, false);
        kl = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.kl), width, width, false);
        pd = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.pd), width, width, false);
        rd = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.rd), width, width, false);
        nd = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.nd), width, width, false);
        bd = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.bd), width, width, false);
        qd = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.qd), width, width, false);
        kd = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.kd), width, width, false);
    }

    /**
     * @return An immutable Bitmap representing the piece
     */
    public Bitmap getPieceBitMap(Piece piece) {
        if (piece.getColor().equals(edu.neumont.pro180.chess.core.model.Color.LIGHT)) {
            switch (piece.getType()) {
                case PAWN:
                    return pl;
                case ROOK:
                    return rl;
                case KNIGHT:
                    return nl;
                case BISHOP:
                    return bl;
                case QUEEN:
                    return ql;
                case KING:
                    return kl;
            }
        } else {
            switch (piece.getType()) {
                case PAWN:
                    return pd;
                case ROOK:
                    return rd;
                case KNIGHT:
                    return nd;
                case BISHOP:
                    return bd;
                case QUEEN:
                    return qd;
                case KING:
                    return kd;
            }
        }
        return null;
    }
}
