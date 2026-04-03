package websocket.commands;

public class LeaveGameCommand extends UserGameCommand{
    private final String teamColor;
    private final Integer gameID;

    public LeaveGameCommand(String teamColor, Integer gameID) {
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
