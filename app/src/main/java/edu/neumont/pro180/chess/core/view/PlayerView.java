package edu.neumont.pro180.chess.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.model.Piece;

/**
 * The view in front of the board for the player.  It shows notifications such as "Your turn!"
 * It also holds your player's captured pieces and has the voice control button inside of it
 */
public class PlayerView extends LinearLayout {
    private NotificationView notificationView;
    private CapturedPieceView capturedPieceView;
    private Button voiceControl;

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        notificationView = new NotificationView(context, attrs);
        capturedPieceView = new CapturedPieceView(context, attrs);
//        notificationView = (NotificationView) findViewById(R.id.notification_view);
//        capturedPieceView = (CapturedPieceView) findViewById(R.id.captured_piece_view);
//        voiceControl = (Button) findViewById(R.id.voice_control_button);
    }

    // CapturedPieceView
    public void setCapturedPieces(List<Piece> capturedPieces) {
        capturedPieceView.setCapturedPieces(capturedPieces);
    }

    // NotificationView text changing
    public void resetText() {
        notificationView.resetText();
    }
    public void notifyCheck() {
        notificationView.notifyCheck();
    }
    public void notifyGameOver(Color result) {
        notificationView.notifyGameOver(result);
    }
    public void notifyTurn() {
        notificationView.notifyTurn();
    }
}