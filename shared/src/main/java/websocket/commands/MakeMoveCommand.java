package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    private ChessMove move;

    public MakeMoveCommand(String username, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, username, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
}
