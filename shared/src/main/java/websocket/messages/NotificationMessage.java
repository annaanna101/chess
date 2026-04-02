package websocket.messages;

import com.google.gson.Gson;

public record NotificationMessage (Type type, String message) {
    public enum Type {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
