package chess;

import java.util.ArrayList;
import java.util.List;

public class RookMoves {
    private ChessBoard board;
    private ChessPosition position;
    private ChessPiece rook;
    public RookMoves(ChessBoard board, ChessPosition position, ChessPiece rook){
        this.board = board;
        this.rook = rook;
        this.position = position;
    }
    public List<ChessPosition> calculateMoves(){
        List<ChessPosition> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int[] directions = {-1,1};
        for (int dRow : directions) {
            int newRow = row + dRow;
            while (newRow >= 1 && newRow <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, col);
                ChessPiece piece = board.getPiece(newPosition);
                if (piece == null) {
                    moves.add(newPosition);
                } else if (piece.getTeamColor() != rook.getTeamColor()) {
                    moves.add(newPosition);
                    break;
                } else {
                    break;
                }
                newRow = newRow + dRow;
            }
        }
        for (int dCol : directions){
            int newCol = col + dCol;
            while (newCol >= 1 && newCol <=8){
                ChessPosition newPosition = new ChessPosition(row,newCol);
                ChessPiece piece = board.getPiece(newPosition);
                if (piece == null){
                    moves.add(newPosition);
                } else if (piece.getTeamColor() != rook.getTeamColor()) {
                    moves.add(newPosition);
                    break;
                }else {
                    break;
                }
                newCol = newCol +dCol;
            }
        }
        return moves;
    }
}
