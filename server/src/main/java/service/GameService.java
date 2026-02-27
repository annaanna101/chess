package service;

import Model.*;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;

import java.util.Collection;

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
        dataAccess.getAuth(authToken);
        int gameID = dataAccess.createGame(createRequest.gameName());
        return new CreateResult(gameID);
    }
//    public void joinGame(JoinRequest joinRequest) throws DataAccessException{}
//    public Collection<GameD> listGame(ListRequest listRequest) throws DataAccessException{}
}
