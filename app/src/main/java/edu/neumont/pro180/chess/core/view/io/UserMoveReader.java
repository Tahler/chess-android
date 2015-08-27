package edu.neumont.pro180.chess.core.view.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UserMoveReader extends MoveReader {
    public UserMoveReader() {
        super(new BufferedReader(new InputStreamReader(System.in)));
    }
}
