package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor teamTurn = TeamColor.WHITE;
    ChessBoard gameBoard = new ChessBoard();

    public ChessGame() {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, gameBoard);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team){
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = gameBoard.getPiece(startPosition);
        if (piece == null || piece.getTeamColor() != teamTurn){
            return new ArrayList<>();
        }
        Collection<ChessMove> rawMoves = piece.pieceMoves(gameBoard,startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();
        ChessBoard originalBoard = gameBoard;
        for (ChessMove moves : rawMoves){
            ChessBoard boardCopy = new ChessBoard(originalBoard);
            ChessPiece sPiece = boardCopy.getPiece(moves.getStartPosition());
            if (moves.getPromotionPiece() != null){
                boardCopy.addPiece(
                        moves.getEndPosition(),
                        new ChessPiece(sPiece.getTeamColor(),moves.getPromotionPiece())
                );
            } else {
                boardCopy.addPiece(moves.getEndPosition(), sPiece);
            }
            boardCopy.addPiece(moves.getStartPosition(), null);
            gameBoard = boardCopy;
            boolean inCheck = isInCheck(sPiece.getTeamColor());
            gameBoard = originalBoard;
            if (!inCheck){
                legalMoves.add(moves);
            }
        }
        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPiece.PieceType piece = move.getPromotionPiece();
        ChessPosition end = move.getEndPosition();
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int row = 1; row <=8; row++){
            for (int col = 1; col <=8; col++){
                ChessPosition position = new ChessPosition(row,col);
                ChessPiece piece = gameBoard.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor){
                    kingPosition = position;
                    break;
                }
            }
        }
        if (kingPosition == null){
            return true;
        }
        for (int row = 1; row <=8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row,col);
                ChessPiece piece = gameBoard.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor){
                    Collection<ChessMove> moves = piece.pieceMoves(gameBoard,position);
                    for (ChessMove move : moves){
                        ChessPosition endPos = move.getEndPosition();
                        if (endPos.equals(kingPosition)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        /* Things to check for:
        1. Make sure the king is in check.
        2. Try every legal move for the team in check
        3. (If there is a move) simulate the move then re-check for check
            - make a deep copy of the board and then check the move.
        4. if any move escapes check -> not checkmate
            - king moves, blocking the check, capturing the attacking piece.
        5. If no escape moves exists -> checkmate
         */
        ChessBoard originalBoard = gameBoard;
        if (isInCheck(teamColor)){
            // find all friendly pieces
            List <ChessPosition> friendlyPosition = new ArrayList<>();
            for (int row = 1; row <= 8; row++){
                for (int col = 1; col <=8; col++){
                    ChessPosition position = new ChessPosition(row,col);
                    ChessPiece piece = gameBoard.getPiece(position);
                    if (piece != null && piece.getTeamColor() == teamColor){
                        friendlyPosition.add (position);
                    }
                }
            }
            // generate every legal move for those pieces
            Collection<ChessMove> moves = new ArrayList<>();
            for (ChessPosition pos : friendlyPosition){
                ChessPiece piece1 = gameBoard.getPiece(pos);
                Collection<ChessMove> legalMoves = piece1.pieceMoves(gameBoard,pos);
                moves.addAll(legalMoves);
            }
            // simulate each move (one at a time)
            for (ChessMove simMoves : moves){
                ChessBoard boardCopy = new ChessBoard(originalBoard);
                ChessMove sMove = simMoves;
                ChessPiece piece2 = boardCopy.getPiece(sMove.getStartPosition());
                if (sMove.getPromotionPiece() != null) {
                    boardCopy.addPiece(
                            sMove.getEndPosition(),
                            new ChessPiece(piece2.getTeamColor(),sMove.getPromotionPiece())
                    );
                } else {
                    boardCopy.addPiece(sMove.getEndPosition(), piece2);
                }
                boardCopy.addPiece(sMove.getStartPosition(), null);
                gameBoard = boardCopy;
                // re-check for check
                boolean stillInCheck = isInCheck(teamColor);
                gameBoard = originalBoard;
                if (!stillInCheck){
                    // if no longer in check, return false
                    return false;
                }
            }
            // if every move checked and still in check return true
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
