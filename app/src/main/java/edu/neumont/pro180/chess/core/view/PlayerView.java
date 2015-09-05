package edu.neumont.pro180.chess.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.List;

import edu.neumont.pro180.chess.core.model.Piece;

public class PlayerView extends LinearLayout {
    private NotificationView notification;
    private CapturedPieceView capturedPieces;
    private VoiceControlView voiceControl;


    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCapturedPieces(List<Piece> capturedPieces) {
        //this.capturedPieces = capturedPieces;
    }
}