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

    public CreateResult createGame(CreateRequest createRequest, String authToken) throws DataAccessException {
        if (authToken == null || createRequest.gameName() == null){
            throw new DataAccessException("Error: bad request");
        }
        if (dataAccess.getAuth(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        int gameID = dataAccess.createGame(createRequest.gameName());
        return new CreateResult(gameID);
    }
    public void joinGame(JoinRequest joinRequest, String authToken) throws DataAccessException{
        if (authToken == null || joinRequest.gameID() ==  null) {
            throw new DataAccessException("Error: bad request");
        }
        if (dataAccess.getAuth(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        if (dataAccess.getGame(joinRequest.gameID()) == null){
            throw new DataAccessException("Error: bad request");
        }
        AuthD auth = dataAccess.getAuth(authToken);
        String username = auth.username();
        dataAccess.updateGame(joinRequest.gameID(), joinRequest.playerColor(), username);
    }
    public Collection<GameSummary> listGame(String authToken) throws DataAccessException{
        if (authToken == null){
            throw new DataAccessException("Error: bad request");
        }
        if (dataAccess.getAuth(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        Collection<GameSummary> games = dataAccess.listGames();
        if (games == null){
            return Collections.emptyList();
        }
        return games;
    }
    public void clearGames() throws DataAccessException{
        dataAccess.clearGames();
    }
}
