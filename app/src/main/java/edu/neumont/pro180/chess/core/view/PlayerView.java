package edu.neumont.pro180.chess.core.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import edu.neumont.pro180.chess.R;
import edu.neumont.pro180.chess.core.controller.SpeechRequestListener;
import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;

/**
 * The view in front of the board for the player.  It shows notifications such as "Your turn!"
 * It also holds your player's captured pieces and has the voice control button inside of it
 */
public class PlayerView extends LinearLayout {
    private SpeechRequestListener speechRequestListener;

    private TextView centerNotification;
    private TextView rightNotification;
    private CapturedPieceView capturedPieceView;
    private ImageButton voiceControlButton;

    private Color color;
    // true if it is this player's turn
    private boolean isTurn;

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.player_view, this);

        centerNotification = (TextView) findViewById(R.id.center_notification);
        rightNotification = (TextView) findViewById(R.id.right_notification);
        capturedPieceView = (CapturedPieceView) findViewById(R.id.captured_piece_view);

        capturedPieceView = (CapturedPieceView) findViewById(R.id.captured_piece_view);
        capturedPieceView.setZOrderOnTop(true);
        capturedPieceView.getHolder().setFormat(PixelFormat.TRANSPARENT);

        voiceControlButton = (ImageButton) findViewById(R.id.voice_control_button);
        voiceControlButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Only pressed if it is this player's turn
                if (speechRequestListener != null && isTurn) speechRequestListener.speechRequested();
            }
        });
    }

    public void rotate() {
        this.setRotation(180f);
        capturedPieceView.setRotation(180f);
        capturedPieceView.rotate();
    }


    // CapturedPieceView
    public void setCapturedPieces(List<Piece> capturedPieces) {
        capturedPieceView.setCapturedPieces(capturedPieces);
    }

    // NotificationView text changing
    public void resetText() {
        centerNotification.setText("");
        rightNotification.setText("");
        isTurn = false;
        voiceControlButton.setEnabled(false);
        this.invalidate();

    }
    public void notifyCheck() {
        rightNotification.setText("Check!");
    }

    public void notifyGameOver(Color result) {
        if (result == null)centerNotification.setText("Stale mate!");
        else if (result.equals(color)) centerNotification.setText("You win!");
        else centerNotification.setText("You lose!");
        rightNotification.setText("");
        System.out.println("Printing Game over");
    }

    public void notifyTurn() {
        isTurn = true;
        voiceControlButton.setEnabled(true);
        centerNotification.setText("Your turn!");
    }

    public void notifySpeechError() {
        if (isTurn) centerNotification.setText("Error interpreting voice, try again.");
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setListener(SpeechRequestListener speechRequestListener) {
        this.speechRequestListener = speechRequestListener;
    }

    public void notifyInvalidMove(Move move) {
        if (isTurn) centerNotification.setText("The move from " + move.getStart() + " to " + move.getEnd() + " is invalid, try again.");
    }
}