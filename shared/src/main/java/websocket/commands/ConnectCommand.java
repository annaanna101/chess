package websocket.commands;


public class ConnectCommand extends UserGameCommand{

    public ConnectCommand(Integer gameID) {
        super(CommandType.CONNECT, null, gameID);
    }

}
