package dataaccess;

import Model.AuthD;
import Model.GameD;
import Model.UserD;

import java.util.Collection;

public interface DataAccess {
    UserD addUser(UserD user) throws DataAccessException;
    UserD getUser(String username) throws DataAccessException;
    GameD getGame(int gameID) throws DataAccessException;
    Collection<GameD> listGames() throws DataAccessException;
    AuthD createAuth(String username) throws DataAccessException;
    AuthD getAuth(String token) throws DataAccessException;
    void deleteAuth(String token) throws DataAccessException;
    void clearUsers() throws DataAccessException;
    void clearAuths() throws DataAccessException;
    void clearGames() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
}
