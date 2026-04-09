package client.websocket;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public interface NotificationHandler {
    void notify(NotificationMessage notification);
    void notifyError(ErrorMessage error);
    void notifyLoadGame(LoadGameMessage message);
}
