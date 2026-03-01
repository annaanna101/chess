package dataaccess;

import Model.authD;
import Model.GameD;
import Model.gameSummary;
import Model.userD;

import java.util.Collection;

public interface DataAccess {
    userD addUser(userD user) throws DataAccessException;
    userD getUser(String username) throws DataAccessException;
    GameD getGame(int gameID) throws DataAccessException;
    Collection<gameSummary> listGames() throws DataAccessException;
    authD createAuth(String username) throws DataAccessException;
    authD getAuth(String token) throws DataAccessException;
    void deleteAuth(String token) throws DataAccessException;
    void clearUsers() throws DataAccessException;
    void clearAuths() throws DataAccessException;
    void clearGames() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    void updateGame(Integer gameID, String playerColor, String username) throws DataAccessException;
}
