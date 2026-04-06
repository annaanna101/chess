package websocket.commands;

import chess.ChessGame;

public class ResignCommand extends UserGameCommand{
    public ResignCommand(Integer gameID) {
        super(CommandType.RESIGN, null, gameID);
    }
}
