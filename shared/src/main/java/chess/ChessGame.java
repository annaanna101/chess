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
    ChessBoard gameBoard;

    public ChessGame() {
        this.gameBoard = new ChessBoard();
        this.gameBoard.resetBoard();

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
    private List<ChessPosition> getFriendlyPositions(TeamColor teamColor){
        List<ChessPosition> positions = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    positions.add(pos);
                }
            }
        }
        return positions;
    }
    private boolean escapesCheck(ChessMove move, TeamColor teamColor) {
        ChessBoard originalBoard = gameBoard;
        ChessBoard boardCopy = new ChessBoard(originalBoard);

        ChessPiece piece = boardCopy.getPiece(move.getStartPosition());

        if (move.getPromotionPiece() != null) {
            boardCopy.addPiece(
                    move.getEndPosition(),
                    new ChessPiece(piece.getTeamColor(), move.getPromotionPiece())
            );
        } else {
            boardCopy.addPiece(move.getEndPosition(), piece);
        }

        boardCopy.addPiece(move.getStartPosition(), null);
        gameBoard = boardCopy;

        boolean stillInCheck = isInCheck(teamColor);
        gameBoard = originalBoard;

        return !stillInCheck;
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
        if (piece == null){
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
            if (!inCheck) {
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
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        ChessPosition end = move.getEndPosition();
        Collection<ChessMove> legalMoves = validMoves(start);
        if(!legalMoves.contains(move)){
            throw new InvalidMoveException();
        }
        ChessPiece movedPiece = gameBoard.getPiece(start);
        if (movedPiece.getTeamColor() != teamTurn){
            throw new InvalidMoveException();
        }
        if (promotionPiece != null){
            ChessPiece promotedPiece = new ChessPiece(movedPiece.getTeamColor(),promotionPiece);
            gameBoard.addPiece(end,promotedPiece);
        } else {
            gameBoard.addPiece(end,movedPiece);
        }
        gameBoard.addPiece(start, null);
        if (teamTurn == TeamColor.WHITE){
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }
    }
    private ChessPosition findKingPosition(TeamColor teamColor){
        for (int row = 1; row <=8; row++){
            for (int col = 1; col <=8; col++){
                ChessPosition position = new ChessPosition(row,col);
                ChessPiece piece = gameBoard.getPiece(position);
                if (piece != null
                        && piece.getPieceType() == ChessPiece.PieceType.KING
                        && piece.getTeamColor() == teamColor){
                    return position;
                }
            }
        }
        return null;
    }
    private boolean pieceAttacksSquare(
            ChessPiece piece,
            ChessPosition position,
            ChessPosition target) {
        for (ChessMove move : piece.pieceMoves(gameBoard, position)) {
            if (move.getEndPosition().equals(target)){
                return true;
            }
        }
        return false;
    }
    private boolean isSquareAttacked (ChessPosition square, TeamColor defendingTeam){
        for (int row = 1; row <=8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row,col);
                ChessPiece piece = gameBoard.getPiece(position);
                if (piece == null){
                    continue;
                }
                if (piece.getTeamColor() == defendingTeam){
                    continue;
                }
                if (pieceAttacksSquare(piece, position, square)){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        if (kingPosition == null){
            return true;
        }
        return isSquareAttacked(kingPosition,teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)){
            return false;
        }
        for (ChessPosition pos : getFriendlyPositions(teamColor)){
            ChessPiece piece = gameBoard.getPiece(pos);
            for (ChessMove move : piece.pieceMoves(gameBoard, pos)){
                if (escapesCheck(move, teamColor)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)){
            return false;
        }
        if (teamColor == teamTurn) {
            Collection<ChessMove> legalMoves = new ArrayList<>();
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition position = new ChessPosition(row, col);
                    ChessPiece piece = gameBoard.getPiece(position);
                    if (piece != null && piece.getTeamColor() == teamColor) {
                        legalMoves = validMoves(position);
                    }
                }
            }
            return legalMoves.isEmpty();
        }
        return false;
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
