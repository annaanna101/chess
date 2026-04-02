package websocket.messages;

import com.google.gson.Gson;
import model.GameD;
import model.GameSummary;

public record LoadGameMessage (Type type, String visitorName, GameSummary game){
    public enum Type {
        CONNECT,
        MAKE_MOVE,
    }
    public String toString() {
        return new Gson().toJson(this);
    }
}
