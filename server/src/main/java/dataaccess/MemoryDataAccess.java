package dataaccess;

import Model.AuthD;
import Model.GameD;
import Model.UserD;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess{
    private int nextGameId = 1;
    final private HashMap<String, UserD> users = new HashMap<>();
    final private HashMap<Integer, GameD> games = new HashMap<>();
    final private HashMap<String, AuthD> authTokens = new HashMap<>();

    public UserD addUser(UserD user){
        if (users.containsKey(user.username())){
            throw new RuntimeException("Username is already in use");
        }

        users.put(user.username(), user);
        return user;
    }

    public UserD getUser(String username){
        return users.get(username);
    }

    public GameD getGame(int gameID) {
        return games.get(gameID);
    }

    public Collection<GameD> listGames() {
        return games.values();
    }

    public AuthD createAuth(String username){
        if (!users.containsKey(username)){
            throw new RuntimeException("User does not exist!");
        }
        String token = UUID.randomUUID().toString();
        AuthD auth = new AuthD(token, username);
        authTokens.put(token, auth);
        return auth;
    }
    public void deleteAuth(String token){
        authTokens.remove(token);
    }

    public AuthD getAuth(String token){
        return authTokens.get(token);
    }

    public void clearAuths() throws DataAccessException {
        authTokens.clear();
    }
    public void clearUsers() throws DataAccessException {
        users.clear();
    }
    public void clearGames() throws DataAccessException {
        games.clear();
    }
}
