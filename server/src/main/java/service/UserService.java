package service;

import Model.*;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class UserService {
    private final DataAccess dataAccess;
    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (dataAccess.getUser(registerRequest.username())!= null) {
            throw new DataAccessException("Error: Username is already in use");
        }
        UserD user = new UserD(registerRequest.username(), registerRequest.password(), registerRequest.email());
        dataAccess.addUser(user);
        AuthD auth = dataAccess.createAuth(registerRequest.username());
        return new RegisterResult(registerRequest.username(), auth.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserD user = dataAccess.getUser(loginRequest.username());
        if (user == null || !user.password().equals(loginRequest.password())) {
            throw new DataAccessException("Error: unauthorized");
        }
        AuthD auth = dataAccess.createAuth(loginRequest.username());
        return new LoginResult(loginRequest.username(), auth.authToken());
    }
    public void deleteAuth(String authToken) throws DataAccessException{
        if (authToken == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }
    public void logout(String authToken) throws DataAccessException{

        AuthD auth = dataAccess.getAuth(authToken);

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

