package service;

import Model.*;
import dataaccess.DataAccess;
import passoff.exception.ResponseParseException;

public class UserService {
    private final DataAccess dataAccess;
    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseParseException{
        if (dataAccess.getUser(registerRequest.username())!= null) {
            throw new ResponseParseException("Error: Username is already in use", null);
        }
        UserD user = new UserD(registerRequest.username(), registerRequest.password(), registerRequest.email());
        dataAccess.addUser(user);
        AuthD auth = dataAccess.createAuth(registerRequest.username());
        return new RegisterResult(registerRequest.username(), auth.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseParseException {
        UserD user = dataAccess.getUser(loginRequest.username());
        if (user == null || !user.password().equals(loginRequest.password())) {
            throw new ResponseParseException("Error: unauthorized", null);
        }
        AuthD auth = dataAccess.createAuth(loginRequest.username());
        return new LoginResult(loginRequest.username(), auth.authToken());
    }
    public void logout(String authToken) throws ResponseParseException{

        AuthD auth = dataAccess.getAuth(authToken);

        if (auth == null) {
            throw new ResponseParseException("Error: unauthorized", null);
        }
        dataAccess.deleteAuth(authToken);
    }
}

