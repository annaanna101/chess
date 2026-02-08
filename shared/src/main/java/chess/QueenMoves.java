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
    }
}
