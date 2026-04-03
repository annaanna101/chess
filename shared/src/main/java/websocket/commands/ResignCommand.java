package websocket.commands;

public class ResignCommand extends UserGameCommand{
    private final Integer gameID;

    public ResignCommand(Integer gameID) {
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}
