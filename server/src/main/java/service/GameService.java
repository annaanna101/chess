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
        int gameID = dataAccess.createGame(createRequest.gameName());
        return new CreateResult(gameID);
    }
//    public void joinGame(JoinRequest joinRequest, String authToken) throws DataAccessException{
//        if (authToken == null || joinRequest.gameID() ==  null) {
//            throw new DataAccessException("Error: bad request");
//        }
//        if (dataAccess.getAuth(authToken) == null){
//            throw new DataAccessException("Error: unauthorized");
//        }
//        if (dataAccess.getGame(joinRequest.gameID()) == null){
//            throw new DataAccessException("Error: bad request");
//        }
//        GameD game = dataAccess.getGame(joinRequest.gameID());
//        if (!joinRequest.playerColor().contains("BLACK") || !joinRequest.playerColor().contains("WHITE")){
//            throw new DataAccessException("Error: bad request");
//        }
//        if (joinRequest.playerColor().contains("BLACK")){
//            throw new DataAccessException("Error: bad request");
//        }
//        dataAccess.updateGame(
//        /*
//        get auth (authToken)
//        get game(gameID)
//        ValidatePlayer(playerColor)
//        UpdateGame (gameID, playerColor)
//         */
//        String authToken;
//        int gameID;
//        GameD game;
//    }
    public Collection<GameD> listGame(String authToken) throws DataAccessException{
        if (authToken == null){
            throw new DataAccessException("Error: bad request");
        }
        if (dataAccess.getAuth(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        return dataAccess.listGames();

    }
}
