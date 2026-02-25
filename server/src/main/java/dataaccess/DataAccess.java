package dataaccess;

import Model.AuthD;
import Model.GameD;
import Model.UserD;
import passoff.exception.ResponseParseException;

import java.util.Collection;

public interface DataAccess {
    UserD addUser(UserD user) throws ResponseParseException;
    UserD getUser(String username) throws ResponseParseException;
    GameD getGame(int gameID) throws ResponseParseException;
    Collection<GameD> listGames() throws ResponseParseException;
    AuthD createAuth(String username) throws ResponseParseException;
    AuthD getAuth(String token) throws ResponseParseException;
    void clear() throws ResponseParseException;
}
