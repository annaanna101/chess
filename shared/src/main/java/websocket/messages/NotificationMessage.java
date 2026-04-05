package websocket.messages;


public class NotificationMessage extends ServerMessage {
    private final Type type;
    private final String message;

    public NotificationMessage (Type type, String message){
        super(ServerMessageType.NOTIFICATION);
        this.type = type;
        this.message = message;
    }
    public enum Type {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
