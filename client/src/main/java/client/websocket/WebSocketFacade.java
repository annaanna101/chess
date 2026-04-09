package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage messageType = new Gson().fromJson(message, ServerMessage.class);
                    switch (messageType.getServerMessageType()) {
                        case LOAD_GAME -> {
                            LoadGameMessage load = new Gson().fromJson(message, LoadGameMessage.class);
                            notificationHandler.notifyLoadGame(load);
                        }
                        case ERROR -> {
                            ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
                            notificationHandler.notifyError(error);
                        }
                        case NOTIFICATION -> {
                            NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                            notificationHandler.notify(notification);
                        }
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinedGame(String auth, Integer gameID) throws ResponseException{
        try {
            var connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(connect));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void makeMove(String auth, Integer gameID, ChessMove move) throws ResponseException{
        try {
            var command = new MakeMoveCommand(auth, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void leaveGame(String auth, Integer gameID) throws ResponseException{
        try {
            var leave = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(leave));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void resignGame(String auth, Integer gameID) throws ResponseException{
        try {
            var resign = new UserGameCommand(UserGameCommand.CommandType.RESIGN, auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(resign));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

}

