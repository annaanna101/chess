package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class DrawBoard {
    static final String UNICODE_ESCAPE = "\u001b";
    private static final String SET_TEXT_COLOR = UNICODE_ESCAPE + "[38;5;";
    static final String SET_BG_COLOR = UNICODE_ESCAPE + "[48;5;";

    private static final int BOARD_SIZE = 8;

    public static final String SET_BG_COLOR_BLACK = SET_BG_COLOR + "0m";
    public static final String SET_TEXT_COLOR_WHITE = SET_TEXT_COLOR + "15m";
    public static final String SET_TEXT_COLOR_BLACK = SET_TEXT_COLOR + "0m";
    public static final String SET_TEXT_COLOR_DARK_GREEN = SET_TEXT_COLOR + "22m";
    public static final String SET_BG_COLOR_DARK_GREEN = SET_BG_COLOR + "22m";

    public static final String SET_TEXT_COLOR_BEIGE = SET_TEXT_COLOR + "115m";
    public static final String SET_BG_COLOR_BEIGE = SET_BG_COLOR + "115m";

    public static final String SET_BG_HIGHLIGHTED_LIGHT = SET_BG_COLOR + "148m";
    public static final String SET_TEXT_HIGHLIGHTED_LIGHT = SET_TEXT_COLOR + "148m";
    public static final String SET_BG_HIGHLIGHTED_DARK = SET_BG_COLOR + "40m";
    public static final String SET_TEXT_HIGHLIGHTED_DARK = SET_TEXT_COLOR + "40m";

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

    private boolean isHighlight = false;

    static void drawCorrectBoard(String color, ChessGame game, ChessPosition start) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        ChessBoard board = game.getBoard();
        Collection<ChessMove> moves = game.validMoves(start);

        out.print(ERASE_SCREEN);
        if (color.toUpperCase().contains("WHITE") || color.toUpperCase().contains("OBSERVE")){
            drawHeaders(out);
            drawBoard(out, board, moves);
            drawHeaders(out);
        } else if (color.toUpperCase().contains("BLACK")){
            drawHeadersFlipped(out);
            drawBoardFlipped(out, board, moves);
            drawHeadersFlipped(out);
        }
        reset(out);
    }

    private static void drawBoard(PrintStream out, ChessBoard board, Collection<ChessMove> moves) {
        int rowCount = 8;
        for (int row = 0; row < BOARD_SIZE; row++) {
            drawRow(out, row, rowCount, board, moves);
            rowCount--;
        }
    }

    private static void drawBoardFlipped(PrintStream out, ChessBoard board, Collection<ChessMove> moves) {
        for (int row = BOARD_SIZE - 1; row >= 0; row--) {
            drawRowFlipped(out, row, board, moves);
        }
    }

    private static void drawRowFlipped(PrintStream out, int row, ChessBoard board, Collection<ChessMove> moves) {
        int displayRow = BOARD_SIZE - row;

        header(out);
        out.print(" " + displayRow + " ");

        for (int col = BOARD_SIZE - 1; col >= 0; col--) {
            rowHelper(out, row, col, board, moves);
        }

        header(out);
        out.print(" " + displayRow + " ");

        reset(out);
        out.println();
    }
    private static void rowHelper(PrintStream out, int row, int col, ChessBoard board, Collection<ChessMove> moves) {
        boolean light = (row + col) % 2 == 0;
        boolean highlight = false;
        ChessPiece piecePos = board.getPiece(new ChessPosition(row+1, col+1));
        String piece = getPiece(piecePos);

        for (ChessMove move: moves){
            ChessPosition end = move.getEndPosition();
            int endRow = end.getRow();
            int endCol = end.getColumn();
            if (row+1 == endRow && col+1 == endCol){
                highlight = true;
                break;
            }
            if (row+1 == move.getStartPosition().getRow() && col+1 == move.getStartPosition().getColumn()){
                highlight = true;
                break;
            }
        }
        if (highlight){
            if (light) {
                highlightLightSquare(out);
            } else {
                highlightDarkSquare(out);
            }
        } else {
            if (light) {
                lightSquare(out);
            } else {
                darkSquare(out);
            }
        }
        setPieceColor(out, piecePos);
        out.print(piece);

    }

    private static void drawRow(PrintStream out, int row, int rowCount, ChessBoard board, Collection<ChessMove> moves) {

        header(out);
        out.print(" " + rowCount + " ");

        for (int col = 0; col < BOARD_SIZE; col++) {
            rowHelper(out, row, col, board, moves);
        }
        header(out);
        out.print(" " + rowCount + " ");

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

    private static String getPiece(ChessPiece piece) {
        if (piece == null) return EMPTY;

        switch(piece.getPieceType()){
            case QUEEN -> {
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            }
            case KING -> {
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            }
            case PAWN -> {
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
            }
            case ROOK -> {
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            }
            case KNIGHT -> {
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            }
            case BISHOP -> {
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            }
            default -> {
                return EMPTY;
            }
        }
    }
    private static void setPieceColor(PrintStream out, ChessPiece piece) {
        if (piece == null) {
            return;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            out.print(SET_TEXT_COLOR_WHITE);
        } else {
            out.print(SET_TEXT_COLOR_BLACK);
        }
    }

    private static void darkSquare(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_DARK_GREEN);
    }

    private static void lightSquare(PrintStream out) {
        out.print(SET_BG_COLOR_BEIGE);
        out.print(SET_TEXT_COLOR_BEIGE);
    }

    private static void header(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void reset(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }
    private static void highlightLightSquare(PrintStream out) {
        out.print(SET_BG_HIGHLIGHTED_LIGHT);
        out.print(SET_TEXT_HIGHLIGHTED_LIGHT);
    }
    private static void highlightDarkSquare(PrintStream out) {
        out.print(SET_BG_HIGHLIGHTED_DARK);
        out.print(SET_TEXT_HIGHLIGHTED_DARK);
    }
}