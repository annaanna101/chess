package service;

import Model.*;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;

import java.util.Collection;
import java.util.Collections;

public class GameService {
    private final DataAccess dataAccess;
    public GameService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public createResult createGame(createRequest createRequest, String authToken) throws DataAccessException {
        if (authToken == null || createRequest.gameName() == null){
            throw new DataAccessException("Error: bad request");
        }
        if (dataAccess.getAuth(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        int gameID = dataAccess.createGame(createRequest.gameName());
        return new createResult(gameID);
    }
    public void joinGame(joinRequest joinRequest, String authToken) throws DataAccessException{
        if (authToken == null || joinRequest.gameID() ==  null) {
            throw new DataAccessException("Error: bad request");
        }
        if (dataAccess.getAuth(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        if (dataAccess.getGame(joinRequest.gameID()) == null){
            throw new DataAccessException("Error: bad request");
        }
        authD auth = dataAccess.getAuth(authToken);
        String username = auth.username();
        dataAccess.updateGame(joinRequest.gameID(), joinRequest.playerColor(), username);
    }
    public Collection<gameSummary> listGame(String authToken) throws DataAccessException{
        if (authToken == null){
            throw new DataAccessException("Error: bad request");
        }
        if (dataAccess.getAuth(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        Collection<gameSummary> games = dataAccess.listGames();
        if (games == null){
            return Collections.emptyList();
        }
        return games;
    }
    public void clearGames() throws DataAccessException{
        dataAccess.clearGames();
    }
}
