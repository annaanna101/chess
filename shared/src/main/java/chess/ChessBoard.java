package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard(ChessBoard other) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                this.squares[row][col] = other.squares[row][col];
            }
        }
    }

    public ChessBoard() {

    }


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                squares[row][col] = null;
            }
        }
        for (int col = 1; col <= 8; col++){
            ChessPosition position = new ChessPosition(2, col);
            ChessPiece pawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            addPiece(position,pawn);
        }
        for (int col = 1; col <= 8; col++){
            ChessPosition position = new ChessPosition(7, col);
            ChessPiece pawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            addPiece(position,pawn);
        }
        for (int col = 1; col <= 8; col ++){
            if (col == 1 || col == 8) {
                ChessPosition position = new ChessPosition(1, col);
                ChessPiece rook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                addPiece(position, rook);
                ChessPosition bposition = new ChessPosition(8, col);
                ChessPiece brook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
                addPiece(bposition, brook);
            }
            if (col == 2 || col == 7) {
                ChessPosition position = new ChessPosition(1, col);
                ChessPiece knight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                addPiece(position, knight);
                ChessPosition bposition = new ChessPosition(8, col);
                ChessPiece bknight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                addPiece(bposition, bknight);
            }
            if (col == 3 || col == 6) {
                ChessPosition position = new ChessPosition(1, col);
                ChessPiece bishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                addPiece(position, bishop);
                ChessPosition bposition = new ChessPosition(8, col);
                ChessPiece bbishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                addPiece(bposition, bbishop);
            }
            if (col == 4) {
                ChessPosition position = new ChessPosition(1, col);
                ChessPiece queen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
                addPiece(position, queen);
                ChessPosition bposition = new ChessPosition(8, col);
                ChessPiece bqueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
                addPiece(bposition, bqueen);
            }
            if (col == 5) {
                ChessPosition position = new ChessPosition(1, col);
                ChessPiece king = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
                addPiece(position, king);
                ChessPosition bposition = new ChessPosition(8, col);
                ChessPiece bking = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
                addPiece(bposition, bking);
            }
        }
    }
}
