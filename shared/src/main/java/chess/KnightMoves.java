package chess;

import java.util.ArrayList;
import java.util.List;

public class KnightMoves {
    private ChessBoard board;
    private ChessPosition position;
    private ChessPiece knight;

    public KnightMoves(ChessBoard board, ChessPosition position, ChessPiece knight) {
        this.knight = knight;
        this.board = board;
        this.position = position;
    }

    public List<ChessPosition> calculateMoves() {
        List<ChessPosition> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int[] secondDirections = {-1, 1};
        int[] firstDirections = {-2, 2};
        for (int dRow : firstDirections) {
            for (int dCol : secondDirections) {
                int newRow = row + dRow;
                int newCol = col + dCol;
                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                    continue;
                }
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);
                if (piece == null) {
                    moves.add(newPosition);
                } else if (piece.getTeamColor() != knight.getTeamColor()) {
                    moves.add(newPosition);
                }
            }
        }
        for (int dCol : firstDirections) {
            for (int dRow : secondDirections) {
                int newRow = row + dRow;
                int newCol = col + dCol;
                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                    continue;
                }
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);
                if (piece == null) {
                    moves.add(newPosition);
                } else if (piece.getTeamColor() != knight.getTeamColor()) {
                    moves.add(newPosition);
                }
            }
        }
        return moves;
    }
}
