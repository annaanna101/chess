package dataaccess;

import Model.authD;
import Model.GameD;
import Model.gameSummary;
import Model.userD;
import chess.ChessGame;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess{
    private int nextGameId = 1;
    final private HashMap<String, userD> users = new HashMap<>();
    final private HashMap<Integer, GameD> games = new HashMap<>();
    final private HashMap<String, authD> authTokens = new HashMap<>();

    public userD addUser(userD user){
        if (users.containsKey(user.username())){
            throw new RuntimeException("Username is already in use");
        }

        users.put(user.username(), user);
        return user;
    }

    public userD getUser(String username){
        return users.get(username);
    }

    public GameD getGame(int gameID) {
        return games.get(gameID);
    }

    public Collection<gameSummary> listGames() {
        return games.values().stream()
                .map(game -> new gameSummary(
                        game.getGameID(),
                        game.getWhiteUsername(),
                        game.getBlackUsername(),
                        game.getGameName()
                ))
                .toList();
    }
    public int createGame(String gameName){
        ChessGame chessGame = new ChessGame();
        GameD newGame = new GameD(nextGameId, null, null, gameName, chessGame);
        games.put(nextGameId, newGame);
        nextGameId ++;
        return newGame.getGameID();
    }
    public authD createAuth(String username){
        if (!users.containsKey(username)){
            throw new RuntimeException("User does not exist!");
        }
        String token = UUID.randomUUID().toString();
        authD auth = new authD(token, username);
        authTokens.put(token, auth);
        return auth;
    }
    public void deleteAuth(String token){
        authTokens.remove(token);
    }

    public authD getAuth(String token){
        return authTokens.get(token);
    }

    public void clearAuths(){
        authTokens.clear();
    }
    public void clearUsers() {
        users.clear();
    }
    public void clearGames()  {
        games.clear();
    }
    public void updateGame(Integer gameID, String playerColor, String username) throws DataAccessException{
        if (gameID == null){
            throw new DataAccessException("Error: bad request");
        }
        GameD game = getGame(gameID);
        if (game == null){
            throw new DataAccessException("Error: bad request");
        }
        if ("WHITE".equals(playerColor) || "BLACK".equals(playerColor)) {
            if ("WHITE".equals(playerColor)) {
                if (game.getWhiteUsername() != null) {
                    throw new DataAccessException("Error: already taken");
                } else {
                    game.setWhiteUser(username);
                }
            }
            if ("BLACK".equals(playerColor)) {
                if (game.getBlackUsername() != null) {
                    throw new DataAccessException("Error: already taken");
                } else {
                    game.setBlackUser(username);
                }
            }
        } else {
            throw new DataAccessException("Error: bad request");
        }
    }
}
