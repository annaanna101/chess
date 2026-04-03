package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameD;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private MySqlDataAccess dao;
    private Gson gson = new Gson();
    public PlayerState state;

    public WebSocketHandler(MySqlDataAccess dao) {
        this.dao = dao;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception{
        int gameId = -1;
        Session session = wsMessageContext.session;

        try {
            UserGameCommand command = gson.fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());
            saveSession(gameId, session);

            if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE){
                command = gson.fromJson(wsMessageContext.message(), MakeMoveCommand.class);

            }

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (UnauthorizedException ex) {
            sendMessage(session, new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session, new ErrorMessage("Error: " + ex.getMessage()));
        }

    }

    private void makeMove(Session session, String username, MakeMoveCommand command) throws IOException, DataAccessException {
        ChessMove move = command.getMove();
        Integer gameID = command.getGameID();
        GameD game = dao.getGame(gameID);
        ChessGame chessGame = game.getGame();
        if (game.getWhiteUsername().equals(username) || game.getBlackUsername().equals(username)){
            state = PlayerState.PLAYING;
        }
        if (state != PlayerState.PLAYING){
            sendMessage(session, new ErrorMessage("Cannot make a move. User is observing"));
            return;
        }
        try {
            chessGame.makeMove(move);
        } catch (InvalidMoveException e){
            sendMessage(session, new ErrorMessage("Invalid move: " + e.getMessage()));
            return;
        }
        connections.broadcast(gameID, session, new LoadGameMessage(LoadGameMessage.Type.LOAD_GAME, username, chessGame));
    }

    private void sendMessage(Session session, ErrorMessage errorMessage) throws IOException {
        String msg = new Gson().toJson(errorMessage);
        if (session.isOpen()) {
            session.getRemote().sendString(msg);
        }
    }

    private void resign(Session session, String username, ResignCommand command) throws IOException{
        Integer gameId = command.getGameID();
        if (state != PlayerState.PLAYING){
            sendMessage(session, new ErrorMessage("Cannot resign. User is observing"));
            return;
        }
        try {
            dao.deleteGame(gameId);
        } catch (DataAccessException e) {
            sendMessage(session, new ErrorMessage("Could not resign the game: " + e.getMessage()));
        }
        connections.broadcast(gameId, session, new NotificationMessage(NotificationMessage.Type.RESIGN, String.format("%s has resigned the game. Congratulations!", username)));
        connections.remove(session);
    }

    private void saveSession(int gameId, Session session) {
        connections.add(gameId,session);
    }

    private String getUsername(String authToken) throws DataAccessException {
        return dao.getAuth(authToken).username();
    }

    private void leaveGame(Session session, String username, LeaveGameCommand command) throws IOException{
        String teamColor = command.getTeamColor();
        Integer gameID = command.getGameID();
        if (state == PlayerState.PLAYING){
            try {
                dao.updateGame(gameID, teamColor, null);
            } catch (DataAccessException e) {
                sendMessage(session, new ErrorMessage("Could not leave game: " + e.getMessage()));
            }
        }
        connections.broadcast(gameID, session, new NotificationMessage(NotificationMessage.Type.LEAVE, String.format("%s left the game", username)));
        connections.remove(session);
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        connections.remove(ctx.session);
        System.out.println("Websocket closed");
    }

    private void connect(Session session, String username, ConnectCommand command) throws IOException {
        Integer gameID = command.getGameID();
        String teamColor = command.getTeamColor();
        connections.add(gameID, session);
        var message = "";
        if (teamColor == null){
            state = PlayerState.OBSERVING;
            message = String.format("%s joined the game as an observer", username);
        } else {
            state = PlayerState.PLAYING;
            message = String.format("%s joined the game as %s", username, teamColor);
        }
        var notification = new NotificationMessage(NotificationMessage.Type.CONNECT, message);
        connections.broadcast(gameID, session, notification);
    }
}