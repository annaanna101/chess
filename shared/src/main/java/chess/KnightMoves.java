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
        int row = position.getRow();
        int col = position.getColumn();
        List<ChessPosition> moves = new ArrayList<>();
        int[][] directions = {
                { 2 , 1 }, { 2, -1 },
                { -2, 1 }, { -2, -1 },
                { 1, 2 }, { 1, -2 },
                { -1, 2 }, { -1, -2 }
        };
        for (int[] d: directions){
            int newRow = row + d[0];
            int newCol = col + d[1];
            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                continue;
            }

            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            ChessPiece piece = board.getPiece(newPosition);

            if (piece == null || piece.getTeamColor() != knight.getTeamColor()) {
                moves.add(newPosition);
            }
        }

        return moves;
    }
}
