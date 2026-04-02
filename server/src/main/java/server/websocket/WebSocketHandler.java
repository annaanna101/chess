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
import org.jetbrains.annotations.NotNull;
import webSocketMessages.Action;
import webSocketMessages.Notification;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
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
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
//        try {
//            LoadGameMessage action = new Gson().fromJson(ctx.message(), LoadGameMessage.class);
//            String teamColor;
//            if (action.visitorName().equals(action.game().getWhiteUsername())){
//                teamColor = "WHITE";
//            } else {
//                teamColor = "BLACK";
//            }
//            switch (action.type()) {
//                case CONNECT -> connect(action.visitorName(), ctx.session, teamColor, action.game().getGameID());
//                case MAKE_MOVE -> makeMove(action.visitorName(), ctx.session);
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
        int gameId = -1;
        Session session = wsMessageContext.session;

        try {
            UserGameCommand command = Serializer.fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());
            saveSession(gameId, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (UnauthorizedException ex) {
            sendMessage(session, gameId, new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session, gameId, new ErrorMessage("Error: " + ex.getMessage()));
        }

    }

    private void sendMessage(Session session, int gameId, ErrorMessage errorMessage) {

    }

    private void resign(Session session, String username, ResignCommand command) {
    }

    private void saveSession(int gameId, Session session) {
    }

    private String getUsername(String authToken) {
    }

    private void leaveGame(Session session, String username, LeaveGameCommand command) {
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