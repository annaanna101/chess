package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameD;
import model.GameSummary;
import model.JoinRequest;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import server.ServerFacade;
import webSocketMessages.Action;
import webSocketMessages.Notification;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private MySqlDataAccess dao;
    private Gson gson = new Gson();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
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
            sendMessage(session, gameId, new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session, gameId, new ErrorMessage("Error: " + ex.getMessage()));
        }

    }

    private void makeMove(Session session, String username, MakeMoveCommand command) throws InvalidMoveException, IOException, DataAccessException {
        ChessMove move = command.getMove();
        Integer gameID = command.getGameID();
        GameD game = dao.getGame(gameID);
        ChessGame chessGame = game.getGame();
        try {
            chessGame.makeMove(move);
        } catch (InvalidMoveException e){
            sendMessage(session, gameID, new ErrorMessage("Invalid move: " + e.getMessage()));
            throw new InvalidMoveException("Invalid move");
            return;
        }
        //figure out
        connections.broadcast(session, new LoadGameMessage(LoadGameMessage.Type.LOAD_GAME, username, chessGame));
//        connections.broadcast(session, new NotificationMessage(NotificationMessage.Type.MAKE_MOVE, String.format("Move made by %s", username)));
    }

    private void sendMessage(Session session, int gameId, ErrorMessage errorMessage) {

    }

    private void resign(Session session, String username, ResignCommand command) {
    }

    private void saveSession(int gameId, Session session) {
    }

    private String getUsername(String authToken) {
    }

    private void leaveGame(Session session, String username, LeaveGameCommand command) throws DataAccessException, IOException {
        String teamColor = command.getTeamColor();
        Integer gameID = command.getGameID();
        try {
            dao.updateGame(gameID, teamColor, null);
        } catch (DataAccessException e) {
            sendMessage(session, gameID, new ErrorMessage("Could not leave game: " + e.getMessage()));
            throw new DataAccessException(e.getMessage());
        }
        connections.broadcast(session, new NotificationMessage(NotificationMessage.Type.LEAVE, String.format("%s left the game", username)));
        connections.remove(session);
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Session session, String visitorName, ConnectCommand command) throws IOException {
        Integer gameID = command.getGameID();
        String teamColor = command.getTeamColor();
        connections.add(gameID, session);
        var message = String.format("%s joined the game as %s", visitorName, teamColor);
        var notification = new NotificationMessage(NotificationMessage.Type.CONNECT, message);
        connections.broadcast(session, notification);
    }

}