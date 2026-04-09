package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class DrawBoard {;
    private static final String SET_TEXT_COLOR = UNICODE_ESCAPE + "[38;5;";
    static final String SET_BG_COLOR = UNICODE_ESCAPE + "[48;5;";

    private static final int BOARD_SIZE = 8;

    public static final String SET_BG_COLOR_BLACK = SET_BG_COLOR + "0m";
    public static final String SET_TEXT_COLOR_WHITE = SET_TEXT_COLOR + "15m";
    public static final String SET_TEXT_COLOR_BLACK = SET_TEXT_COLOR + "0m";
    public static final String SET_BG_COLOR_DARK_GREEN = SET_BG_COLOR + "22m";

    public static final String SET_BG_COLOR_BEIGE = SET_BG_COLOR + "115m";

    public static final String SET_BG_HIGHLIGHTED_LIGHT = SET_BG_COLOR + "148m";
    public static final String SET_BG_HIGHLIGHTED_DARK = SET_BG_COLOR + "40m";

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


    static void drawCorrectBoard(String color, ChessGame game, ChessPosition start, HighlightState state) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        ChessBoard board = game.getBoard();
        Collection<ChessMove> moves = null;
        if (start != null && state == HighlightState.HIGHLIGHT){
            moves = game.validMoves(start);
        }
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
        int bRow = 8 - row;
        int bCol = col + 1;
        ChessPosition cPos = new ChessPosition(bRow, bCol);
        ChessPiece piecePos = board.getPiece(cPos);
        String piece = getPiece(piecePos);
        if (moves != null){
            for (ChessMove move: moves){
                ChessPosition end = move.getEndPosition();
                ChessPosition start = move.getStartPosition();
                if (cPos.equals(start) || cPos.equals(end)){
                    highlight = true;
                    break;
                }
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
                if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    return WHITE_QUEEN;
                }
                return BLACK_QUEEN;
            }
            case KING -> {
                if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    return WHITE_KING;
                }
                return BLACK_KING;
            }
            case PAWN -> {
                if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    return WHITE_PAWN;
                }
                return BLACK_PAWN;
            }
            case ROOK -> {
                if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    return WHITE_ROOK;
                }
                return BLACK_ROOK;
            }
            case KNIGHT -> {
                if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    return WHITE_KNIGHT;
                }
                return BLACK_KNIGHT;
            }
            case BISHOP -> {
                if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    return WHITE_BISHOP;
                }
                return BLACK_BISHOP;
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
    }

    private static void lightSquare(PrintStream out) {
        out.print(SET_BG_COLOR_BEIGE);
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
    }
    private static void highlightDarkSquare(PrintStream out) {
        out.print(SET_BG_HIGHLIGHTED_DARK);
    }
}