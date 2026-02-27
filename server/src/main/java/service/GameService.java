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

    public int createGame(CreateRequest createRequest) throws DataAccessException {
        if (createRequest.authToken() == null){
            throw new DataAccessException("Error: bad request");
        }
        if (dataAccess.getAuth(createRequest.authToken()) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        dataAccess.getAuth(createRequest.authToken());
        dataAccess.createGame(createRequest.gameName());
        return createGame(createRequest);
    }
//    public void joinGame(JoinRequest joinRequest) throws DataAccessException{}
//    public Collection<GameD> listGame(ListRequest listRequest) throws DataAccessException{}
}
