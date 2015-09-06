package edu.neumont.pro180.chess.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import edu.neumont.pro180.chess.R;
import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.model.Piece;

/**
 * The view in front of the board for the player.  It shows notifications such as "Your turn!"
 * It also holds your player's captured pieces and has the voice control button inside of it
 */
public class PlayerView extends LinearLayout {
//    private NotificationView notificationView;
    private TextView notificationView;
    private CapturedPieceView capturedPieceView;
    private Button voiceControl;

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.player_view, this);

        notificationView = (TextView) findViewById(R.id.notification_view);
//        notificationView = (NotificationView) findViewById(R.id.notification_view);
        capturedPieceView = (CapturedPieceView) findViewById(R.id.captured_piece_view);

//        System.out.println(notificationView);
//        voiceControl = (Button) findViewById(R.id.voice_control_button);
    }

    // CapturedPieceView
    public void setCapturedPieces(List<Piece> capturedPieces) {
        capturedPieceView.setCapturedPieces(capturedPieces);
    }

    // NotificationView text changing
    public void resetText() {
        notificationView.setText("");
    }
    public void notifyCheck() {
        notificationView.setText("Check!");
//        notificationView.notifyCheck();
    }

    public void notifyGameOver(Color result) {
        notificationView.setText("Game over");
//        notificationView.notifyGameOver(result);
    }

    public void notifyTurn() {
        notificationView.setText("Your turn!");
//        notificationView.notifyTurn();
    }
}