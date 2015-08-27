package edu.neumont.pro180.chess.core.view.io;

import edu.neumont.pro180.chess.core.model.Move;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoveParser {
    private static Pattern movementPattern = Pattern.compile("([a-h])([1-8])([a-h])([1-8])");

    /**
     * Parses and passes directives onto the board.
     * @param command The directive to be parsed. Example: d1d8
     * @return The resulting move
     * @throws ParseException Thrown if the directive cannot be parsed.
     */
    public Move parseCommand(String command) throws ParseException {
        Matcher matcher = movementPattern.matcher(command);

        if (matcher.find()) {
            return new Move(getColumn(matcher.group(1)), getRow(matcher.group(2)),
                    getColumn(matcher.group(3)), getRow(matcher.group(4)));
        } else {
            throw new ParseException(command + ": invalid syntax", 0);
        }
    }

    private Integer getRow(String row) {
        return (8 - Integer.valueOf(row));
    }

    private Integer getColumn(String column) {
        char c = column.charAt(0);
        int temp = (int) c;
        if (temp >= 97 && temp <= 122) return (temp - 97); // 97 is the value of ascii 'a'
        else return null; // Shouldn't happen
    }
}