package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;

public class LoadGameMessage extends ServerMessage{
    private final String visitorName;
    private final ChessGame game;

    public LoadGameMessage (String visitorName, ChessGame game) {
        super(ServerMessage.ServerMessageType.LOAD_GAME);
        this.visitorName = visitorName;
        this.game = game;
    }
    public String getVisitorName() {
        return visitorName;
    }

    public ChessGame getGame() {
        return game;
    }
}
