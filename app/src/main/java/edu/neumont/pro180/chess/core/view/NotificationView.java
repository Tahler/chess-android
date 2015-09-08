package edu.neumont.pro180.chess.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.neumont.pro180.chess.R;
import edu.neumont.pro180.chess.core.model.Color;

public class NotificationView extends LinearLayout {
    // two textviews
    TextView centerView;
    TextView rightView;

    public NotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.notification_view, this);
        //centerView = (TextView) findViewById(R.id.center_notification);
        //rightView = (TextView) findViewById(R.id.right_notification);
        //centerView.setText("center");
        //rightView.setText("right");
    }

    public void notifyCheck() {
        // Set the right side's textview to say "Check!"
    }

    public void notifyGameOver(Color result) {
        // Set the main text to say what is below.
        System.out.println((result == null) ? "Stalemate!" : "Checkmate! The winner is " + result + "!");
    }

    public void resetText() {
        // Clear the TextViews
    }

    public void notifyTurn() {
        // Set text to "Your turn!"
    }
}
