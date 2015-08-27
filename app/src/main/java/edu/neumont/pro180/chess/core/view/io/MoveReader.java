package edu.neumont.pro180.chess.core.view.io;

import edu.neumont.pro180.chess.core.model.Move;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Tyler Berry on 8/7/2015.
 */
public class MoveReader implements AutoCloseable {
    private final MoveParser moveParser;
    private final BufferedReader reader;

    public MoveReader(BufferedReader reader) {
        this.reader = reader;
        this.moveParser = new MoveParser();
    }

    /**
     * Returns the resulting move from a string
     * @param line The command to be parsed (e.g. d1d8)
     * @return The resulting move
     */
    public Move parseLine(String line) throws ParseException {
        return moveParser.parseCommand(line);
    }

    public Move readLine() throws ParseException {
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (line != null) line = line.toLowerCase().trim();
        else return null;

        return parseLine(line);
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }
}
