package edu.neumont.pro180.chess.core.view;

import android.content.res.Resources;
import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

import edu.neumont.pro180.chess.core.model.Piece;

public class PieceImagesFactory {
    private static Map<Integer, PieceImages> pieceImagesMap = new HashMap<>();

    public static Bitmap getPieceBitMap(Resources resources, Integer tileSize, Piece piece) {
        // if no set exists yet, create one
        if (!pieceImagesMap.containsKey(tileSize)) {
            pieceImagesMap.put(tileSize, new PieceImages(resources, tileSize));
        }
        return pieceImagesMap.get(tileSize).getPieceBitMap(piece);
    }
}
