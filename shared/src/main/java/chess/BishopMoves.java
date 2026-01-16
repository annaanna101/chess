package chess;

import java.util.ArrayList;
import java.util.List;

public class BishopMoves {
    private ChessBoard board;
    private ChessPosition position;
    private ChessPiece bishop;
    public BishopMoves(ChessBoard board, ChessPosition position, ChessPiece bishop){
        this.board = board;
        this.bishop = bishop;
        this.position = position;
    }
    public List<ChessPosition> calculateMoves(){
        List<ChessPosition> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int[] directions = {-1,1};
        for (int dRow : directions) {
            for (int dCol : directions){
                int newRow = row +dRow;
                int newCol = col + dCol;
                while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8){
                    ChessPosition newPosition = new ChessPosition(newRow,newCol);
                    ChessPiece peice = board.getPiece(newPosition);
                    if (peice == null){
                        moves.add(newPosition);
                    } else if (peice.getTeamColor() != bishop.getTeamColor()) {
                        moves.add(newPosition);
                        break;
                    }else {
                        break;
                    }
                    newRow = newRow +dRow;
                    newCol = newCol +dCol;
                }
            }
        }
        return moves;
    }
}
