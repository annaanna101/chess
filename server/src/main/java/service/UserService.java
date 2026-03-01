package service;

import Model.*;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class UserService {
    private final DataAccess dataAccess;
    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public registerResult register(registerRequest registerRequest) throws DataAccessException {
        if (registerRequest.username() == null || registerRequest.password() == null){
            throw new DataAccessException("Error: bad request");
        }
        if (dataAccess.getUser(registerRequest.username())!= null) {
            throw new DataAccessException("Error: Username is already taken");
        }
        userD user = new userD(registerRequest.username(), registerRequest.password(), registerRequest.email());
        dataAccess.addUser(user);
        authD auth = dataAccess.createAuth(registerRequest.username());
        return new registerResult(registerRequest.username(), auth.authToken());
    }

    public loginResult login(loginRequest loginRequest) throws DataAccessException {
        userD user = dataAccess.getUser(loginRequest.username());
        if (loginRequest.username() == null || loginRequest.password() == null){
            throw new DataAccessException("Error: bad request");
        }
        if (user == null || !user.password().equals(loginRequest.password())) {
            throw new DataAccessException("Error: unauthorized");
        }
        authD auth = dataAccess.createAuth(loginRequest.username());
        return new loginResult(loginRequest.username(), auth.authToken());
    }
    public void deleteAuth(String authToken) throws DataAccessException{
        if (authToken == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }
    public void logout(String authToken) throws DataAccessException{

        authD auth = dataAccess.getAuth(authToken);

        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        deleteAuth(authToken);
    }
    public void clearUsers() throws DataAccessException{
        dataAccess.clearUsers();
    }
    public void clearAuth() throws DataAccessException{
        dataAccess.clearAuths();
    }

}

