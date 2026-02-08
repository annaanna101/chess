package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMoves {
    private ChessBoard board;
    private ChessPosition position;
    private ChessPiece queen;
    public QueenMoves(ChessBoard board, ChessPosition position, ChessPiece bishop){
        this.board = board;
        this.queen = bishop;
        this.position = position;
    }
    public List<ChessPosition> calculateMoves(){
        List<ChessPosition> moves = new ArrayList<>();
        BishopMoves bishopMoves = new BishopMoves(board,position,queen);
        moves.addAll(bishopMoves.calculateMoves());
        RookMoves rookMoves = new RookMoves(board,position,queen);
        moves.addAll(rookMoves.calculateMoves());
        return moves;
//        int row = position.getRow();
//        int col = position.getColumn();
//        int[] directions = {-1,1};
//        for (int dRow : directions) {
//            for (int dCol : directions){
//                int newRow = row +dRow;
//                int newCol = col + dCol;
//                while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8){
//                    ChessPosition newPosition = new ChessPosition(newRow,newCol);
//                    ChessPiece peice = board.getPiece(newPosition);
//                    if (peice == null){
//                        moves.add(newPosition);
//                    } else if (peice.getTeamColor() != queen.getTeamColor()) {
//                        moves.add(newPosition);
//                        break;
//                    }else {
//                        break;
//                    }
//                    newRow = newRow +dRow;
//                    newCol = newCol +dCol;
//                }
//            }
//        }
//        for (int dRow : directions) {
//            int newRow = row + dRow;
//            while (newRow >= 1 && newRow <= 8) {
//                ChessPosition newPosition = new ChessPosition(newRow, col);
//                ChessPiece piece = board.getPiece(newPosition);
//                if (piece == null) {
//                    moves.add(newPosition);
//                } else if (piece.getTeamColor() != queen.getTeamColor()) {
//                    moves.add(newPosition);
//                    break;
//                } else {
//                    break;
//                }
//                newRow = newRow + dRow;
//            }
//        }
//        for (int dCol : directions){
//            int newCol = col + dCol;
//            while (newCol >= 1 && newCol <=8){
//                ChessPosition newPosition = new ChessPosition(row,newCol);
//                ChessPiece piece = board.getPiece(newPosition);
//                if (piece == null){
//                    moves.add(newPosition);
//                } else if (piece.getTeamColor() != queen.getTeamColor()) {
//                    moves.add(newPosition);
//                    break;
//                }else {
//                    break;
//                }
//                newCol = newCol +dCol;
//            }
//        }
//        return moves;
    }
}
