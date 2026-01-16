package chess;

import java.util.ArrayList;
import java.util.List;

public class PawnMoves {
    /** In Chess the King can move one space in eny direction
     *  This does include diagonals
     */
    private ChessBoard board;
    private ChessPosition position;
    private ChessPiece pawn;
    public PawnMoves(ChessBoard board, ChessPosition position, ChessPiece pawn){
        this.board = board;
        this.pawn = pawn;
        this.position = position;
    }
    public List<ChessPosition> calculateMoves(){
        List<ChessPosition> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int directions = (pawn.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1: -1;
        /*
            Things to check for:
                Start of game: (Starting row) Pawns can move two spaces
                Can only move forward if space is empty
                Can only move diagonally up if capturing enemy piece
                When pawns reach the end of the board (white = row 8, black = row 1),
                    they can become anything but pawn and king
         */
        int oneForwardRow = row + directions;
        if (oneForwardRow >= 1 && oneForwardRow <= 8){
            ChessPosition oneForward = new ChessPosition(oneForwardRow,col);
            if (board.getPiece(oneForward) == null){
                moves.add(oneForward);

                boolean isStartingRow =
                        (pawn.getTeamColor() == ChessGame.TeamColor.WHITE && row == 2) ||
                                (pawn.getTeamColor() == ChessGame.TeamColor.BLACK && row ==7);
                if (isStartingRow){
                    int twoForwardRow = row + 2*directions;
                    ChessPosition twoForward = new ChessPosition(twoForwardRow, col);
                    if (board.getPiece(twoForward)==null){
                        moves.add(twoForward);
                    }
                }
            }
        }
        for (int dc : new int[]{-1,1}){
            int newCol = col + dc;
            int newRow = row + directions;
            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol >8){
                continue;
            }
            ChessPosition diagonal = new ChessPosition(newRow,newCol);
            ChessPiece target = board.getPiece(diagonal);
            if (target != null && target.getTeamColor() != pawn.getTeamColor()){
                moves.add(diagonal);
            }
        }
        return moves;
    }
}
