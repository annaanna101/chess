package chess;

import java.util.ArrayList;
import java.util.List;

public class KingMoves {
    /** In Chess the King can move one space in eny direction
     *  This does include diagonals
     */
    private ChessBoard board;
    private ChessPosition position;
    private ChessPiece king;
    public KingMoves(ChessBoard board, ChessPosition position, ChessPiece king){
        this.board = board;
        this.king = king;
        this.position = position;
    }
    public List<ChessPosition> calculateMoves(){
        List<ChessPosition> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int[] directions = {-1,0,1};
        for (int dRow : directions) {
            for (int dCol : directions){
                if (dRow == 0 && dCol == 0){
                    continue;
                }
                int newRow = row +dRow;
                int newCol = col + dCol;
                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8){
                    continue;
                }
                ChessPosition newPosition = new ChessPosition(newRow,newCol);
                ChessPiece piece = board.getPiece(newPosition);
                if (piece == null){
                    moves.add(newPosition);
                } else if (piece.getTeamColor() != king.getTeamColor()) {
                    moves.add(newPosition);
                }
            }
        }
        return moves;
    }
}

