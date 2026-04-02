package server.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.Action;
import webSocketMessages.Notification;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            LoadGameMessage action = new Gson().fromJson(ctx.message(), LoadGameMessage.class);
            String teamColor;
            if (action.visitorName().equals(action.game().getWhiteUsername())){
                teamColor = "WHITE";
            } else {
                teamColor = "BLACK";
            }
            switch (action.type()) {
                case CONNECT -> connect(action.visitorName(), ctx.session, teamColor, action.game().getGameID());
                case MAKE_MOVE -> makeMove(action.visitorName(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(String visitorName, Session session, String teamColor, Integer gameID) throws IOException {
        connections.add(gameID, session);
        var message = String.format("%s joined the game as %s", visitorName, teamColor);
        var notification = new NotificationMessage(NotificationMessage.Type.CONNECT, message);
        connections.broadcast(session, notification);
    }

    private void makeMove(String visitorName, Session session) throws IOException {
        var message = String.format("%s left the shop", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(session, notification);
        connections.remove(session);
    }

}