package websocket.commands;

import chess.ChessMove;

public class ConnectCommand extends UserGameCommand{
    private final String teamColor;
    private final Integer gameID;

    public ConnectCommand(String teamColor, Integer gameID) {
        this.teamColor = teamColor;
        this.gameID = gameID;
    }

    public String getTeamColor() {
        return teamColor;
    }

    public Integer getGameID() {
        return gameID;
    }
}
