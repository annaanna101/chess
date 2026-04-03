package client.websocket;

import com.google.gson.Gson;
import model.GameSummary;
import jakarta.websocket.*;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

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
                    NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            //might have to add Runtime to ResponseException thingy as done in either Chess client or Server Facade.
            throw new ResponseException(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinedGame(String visitorName, GameSummary game) throws ResponseException{
        try {
            //case CONNECT -> connect(session, username, (ConnectCommand) command);
            var action = new LoadGameMessage(LoadGameMessage.Type.CONNECT, visitorName, game);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void makeMove(String visitorName, GameSummary game) throws ResponseException{
        try {
            //make move command
            var action = new LoadGameMessage(LoadGameMessage.Type.CONNECT, visitorName, game);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void leaveGame(String visitorName, GameSummary game) throws ResponseException{
        try {
            //leave game command
            var action = new LoadGameMessage(LoadGameMessage.Type.CONNECT, visitorName, game);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void resignGame(String visitorName, GameSummary game) throws ResponseException{
        try {
            //resign command
            var action = new LoadGameMessage(LoadGameMessage.Type.CONNECT, visitorName, game);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

}

