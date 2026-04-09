package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
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
import java.util.HashMap;
import java.util.Map;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private MySqlDataAccess dao;
    private Gson gson = new Gson();
    private Map<Integer, String> listOfCompletedGames = new HashMap<>();

    public WebSocketHandler(DataAccess dao) {
        this.dao = (MySqlDataAccess) dao;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception{
//        int gameId = -1;
        Session session = wsMessageContext.session;

        try {
            UserGameCommand command = gson.fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
//            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());

            switch (command.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand cc = gson.fromJson(wsMessageContext.message(), ConnectCommand.class);
                    connect(session, username, cc);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand mmc = gson.fromJson(wsMessageContext.message(), MakeMoveCommand.class);
                    makeMove(session, username, mmc);
                }
                case LEAVE -> {
                    LeaveGameCommand lgc = gson.fromJson(wsMessageContext.message(), LeaveGameCommand.class);
                    leaveGame(session, username, lgc);
                }
                case RESIGN -> {
                    ResignCommand rc = gson.fromJson(wsMessageContext.message(), ResignCommand.class);
                    resign(session, username, rc);
                }
            }
        } catch (UnauthorizedException ex) {
            sendMessage(session, new ErrorMessage("ERROR: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session, new ErrorMessage("ERROR: " + ex.getMessage()));
        }
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) throws IOException, DataAccessException {
        ChessMove move = command.getMove();
        Integer gameID = command.getGameID();
        GameD game = dao.getGame(gameID);
        if (game == null){
        }
        ChessGame chessGame = game.getGame();

        ChessGame.TeamColor teamColor;
        ChessGame.TeamColor opponent;
        if (game.getWhiteUsername() != null && game.getWhiteUsername().equals(username)){
            teamColor = ChessGame.TeamColor.WHITE;
            opponent = ChessGame.TeamColor.BLACK;
        }else if (game.getBlackUsername() != null && game.getBlackUsername().equals(username)){
            teamColor = ChessGame.TeamColor.BLACK;
            opponent = ChessGame.TeamColor.WHITE;
        } else {
            sendMessage(session, new ErrorMessage("Cannot make a move. User is observing"));
            return;
        }
        if (listOfCompletedGames.get(gameID) != null){
            sendMessage(session, new ErrorMessage("Error: This game has already been resigned."));
            return;
        }
        if (teamColor != chessGame.getTeamTurn()){
            sendMessage(session, new ErrorMessage("It is not your turn."));
            return;
        }
        try {
            chessGame.makeMove(move);
            dao.updateChessBoard(gameID, game, chessGame);
            if (chessGame.isInCheck(opponent)){
                connections.broadcast(gameID, null, new NotificationMessage(NotificationMessage.Type.MAKE_MOVE, String.format("%s is in check.", username)));
            } else if (chessGame.isInCheckmate(opponent)){
                connections.broadcast(gameID, null, new NotificationMessage(NotificationMessage.Type.MAKE_MOVE, String.format("%s is in checkmate.", username)));
                listOfCompletedGames.put(gameID, username);
            } else if (chessGame.isInStalemate(teamColor)){
                listOfCompletedGames.put(gameID,username);
            }
        } catch (InvalidMoveException e){
            sendMessage(session, new ErrorMessage("Invalid move: " + e.getMessage()));
            return;
        }
        connections.broadcast(gameID, null, new LoadGameMessage(username, chessGame));
        var notification = new NotificationMessage(NotificationMessage.Type.MAKE_MOVE, String.format("%s made a move", username));
        connections.broadcast(gameID, session, notification);
    }

    private void sendMessage(Session session, Object message) throws IOException {
        String msg = gson.toJson(message);
        if (session.isOpen()) {
            session.getRemote().sendString(msg);
        }else {
            System.out.println("Session is closed, message not sent");
        }
    }

    private synchronized void resign(Session session, String username, ResignCommand command) throws IOException, DataAccessException {
        Integer gameId = command.getGameID();
        GameD game = dao.getGame(gameId);
        if (listOfCompletedGames.get(gameId) != null){
            sendMessage(session, new ErrorMessage("Error: This game has already been resigned."));
            return;
        }
        if (!game.getBlackUsername().contains(username) && !game.getWhiteUsername().contains(username)){
            sendMessage(session, new ErrorMessage(String.format("Error: %s is not playing, so game cannot be resigned", username)));
            return;
        }
        listOfCompletedGames.put(gameId, username);
        sendMessage(session, new NotificationMessage(NotificationMessage.Type.RESIGN, String.format("%s has resigned the game. Congratulations!", username)));
        connections.broadcast(gameId, session, new NotificationMessage(NotificationMessage.Type.RESIGN, String.format("%s has resigned the game. Congratulations!", username)));
        connections.remove(gameId, session);
    }

    private void saveSession(int gameId, Session session) {
        connections.add(gameId,session);
    }

    private String getUsername(String authToken) throws DataAccessException {
        return dao.getAuth(authToken).username();
    }

    private void leaveGame(Session session, String username, LeaveGameCommand command) throws IOException, DataAccessException {
        Integer gameID = command.getGameID();
        GameD game = dao.getGame(gameID);
        String playerColor = "";
        boolean isPlayer = false;
        if (game == null){
            return;
        }
        if (game.getWhiteUsername() != null){
            if (game.getWhiteUsername().equals(username)) {
                playerColor = "WHITE";
                isPlayer = true;
            }
        }
        if (game.getBlackUsername() != null){
            if (game.getBlackUsername().equals(username)) {
                playerColor = "BLACK";
                isPlayer = true;
            }
        }
        if (isPlayer){
            dao.leaveGame(gameID, playerColor, null);
        }
        boolean isRemoved = connections.remove(gameID, session);
        if(isRemoved){
            connections.broadcast(gameID, session, new NotificationMessage(NotificationMessage.Type.LEAVE, String.format("%s left the game", username)));
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        connections.remove(null, ctx.session);
        System.out.println("Websocket closed");
    }

    private void connect(Session session, String username, ConnectCommand command) throws IOException, DataAccessException {
        Integer gameID = command.getGameID();
        var message = "";
        if (username == null){
            throw new DataAccessException("Username is null");
        } else {
            message = String.format("%s joined the game", username);
        }
        GameD game = dao.getGame(gameID);
        if (game == null){
            throw new DataAccessException("This game does not exist");
        }
        saveSession(gameID, session);
        sendMessage(session, new LoadGameMessage(username, game.getGame()));
        var notification = new NotificationMessage(NotificationMessage.Type.CONNECT, message);
        connections.broadcast(gameID, session, notification);
    }
}