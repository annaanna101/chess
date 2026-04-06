package websocket.commands;

public class LeaveGameCommand extends UserGameCommand{

    public LeaveGameCommand(Integer gameID) {
        super(CommandType.LEAVE, null, gameID);
    }

}
