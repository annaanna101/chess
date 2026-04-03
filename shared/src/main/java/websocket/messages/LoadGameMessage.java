package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;

public record LoadGameMessage (Type type, String visitorName, ChessGame game){
    public enum Type {
        LOAD_GAME,
        NOTIFICATION,
        ERROR
    }
    public String toString() {
        return new Gson().toJson(this);
    }
}
