package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawBoard {
    static final String UNICODE_ESCAPE = "\u001b";
    private static final String SET_TEXT_COLOR = UNICODE_ESCAPE + "[38;5;";
    static final String SET_BG_COLOR = UNICODE_ESCAPE + "[48;5;";

    private static final int BOARD_SIZE = 8;

    public static final String SET_BG_COLOR_BLACK = SET_BG_COLOR + "0m";
    public static final String SET_BG_COLOR_LIGHT_GREY = SET_BG_COLOR + "242m";
    public static final String SET_TEXT_COLOR_WHITE = SET_TEXT_COLOR + "15m";
    public static final String SET_TEXT_COLOR_BLACK = SET_TEXT_COLOR + "0m";
    public static final String SET_TEXT_COLOR_DARK_GREY = SET_TEXT_COLOR + "235m";

    public static final String EMPTY = "   ";

    public static final String WHITE_KING = " K ";
    public static final String WHITE_QUEEN = " Q ";
    public static final String WHITE_BISHOP = " B ";
    public static final String WHITE_KNIGHT = " N ";
    public static final String WHITE_ROOK = " R ";
    public static final String WHITE_PAWN = " P ";

    public static final String BLACK_KING = " k ";
    public static final String BLACK_QUEEN = " q ";
    public static final String BLACK_BISHOP = " b ";
    public static final String BLACK_KNIGHT = " n ";
    public static final String BLACK_ROOK = " r ";
    public static final String BLACK_PAWN = " p ";

    static void drawCorrectBoard(String color) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        if (color.toUpperCase().contains("WHITE") || color.toUpperCase().contains("OBSERVE")){
            drawHeaders(out);
            drawBoard(out);
            drawHeaders(out);
        } else if (color.toUpperCase().contains("BLACK")){
            drawHeadersFlipped(out);
            drawBoardFlipped(out);
            drawHeadersFlipped(out);
        }
        reset(out);
    }

    private static void drawBoard(PrintStream out) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            drawRow(out, row);
        }
    }

    private static void drawBoardFlipped(PrintStream out) {
        for (int row = BOARD_SIZE - 1; row >= 0; row--) {
            drawRowFlipped(out, row);
        }
    }

    private static void drawRowFlipped(PrintStream out, int row) {
        int displayRow = BOARD_SIZE - row;

        header(out);
        out.print(" " + displayRow + " ");

        for (int col = BOARD_SIZE - 1; col >= 0; col--) {
            rowHelper(out, row, col);
        }

        header(out);
        out.print(" " + displayRow + " ");

        reset(out);
        out.println();
    }
    private static void rowHelper(PrintStream out, int row, int col) {
        boolean dark = (row + col) % 2 == 0;
        String piece = getPiece(row, col);

        if (dark) {
            darkSquare(out);
        } else {
            lightSquare(out);
        }

        setPieceColor(out, piece);
        out.print(piece);

    }

    private static void drawRow(PrintStream out, int row) {
        int displayRow = row + 1;

        header(out);
        out.print(" " + displayRow + " ");

        for (int col = 0; col < BOARD_SIZE; col++) {
            rowHelper(out, row, col);
        }
        header(out);
        out.print(" " + displayRow + " ");

        reset(out);
        out.println();
    }
    private static void drawHeaders(PrintStream out) {
        String[] headers = {"a","b","c","d","e","f","g","h"};
        header(out);
        out.print("   ");


        for (String h : headers) {
            header(out);
            out.print(" " + h + " ");
        }
        header(out);
        out.print("   ");

        reset(out);
        out.println();
    }
    private static void drawHeadersFlipped(PrintStream out) {
        String[] headers = {"h","g","f","e","d","c","b","a"};
        header(out);
        out.print("   ");

        for (String h : headers) {
            header(out);
            out.print(" " + h + " ");
        }
        header(out);
        out.print("   ");

        reset(out);
        out.println();
    }

    private static String getPiece(int row, int col) {
        if (row == 0){
            return switch (col) {
                case 0, 7 -> BLACK_ROOK;
                case 1, 6 -> BLACK_KNIGHT;
                case 2, 5 -> BLACK_BISHOP;
                case 3 -> BLACK_QUEEN;
                case 4 -> BLACK_KING;
                default -> EMPTY;
            };
        }

        if (row == 1) {
            return BLACK_PAWN;
        }

        if (row == 6) {
            return WHITE_PAWN;
        }
//
        if (row == 7){
            return switch (col) {
                case 0, 7 -> WHITE_ROOK;
                case 1, 6 -> WHITE_KNIGHT;
                case 2, 5 -> WHITE_BISHOP;
                case 3 -> WHITE_QUEEN;
                case 4 -> WHITE_KING;
                default -> EMPTY;
            };
        }

        return EMPTY;
    }
    private static void setPieceColor(PrintStream out, String piece) {
        if (piece.equals(EMPTY)) {
            return;
        }
        if (Character.isUpperCase(piece.trim().charAt(0))) {
            out.print(SET_TEXT_COLOR_WHITE);
        } else {
            out.print(SET_TEXT_COLOR_GREEN);
        }
    }

    private static void darkSquare(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private static void lightSquare(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void header(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void reset(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

}