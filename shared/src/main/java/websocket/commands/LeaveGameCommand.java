package websocket.commands;

public class LeaveGameCommand extends UserGameCommand{
    private final String teamColor;

    public LeaveGameCommand(String teamColor, Integer gameID) {
        super(CommandType.LEAVE, null, gameID);
        this.teamColor = teamColor;
    }

    public String getTeamColor() {
        return teamColor;
    }

}
