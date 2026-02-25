package service;

import Model.*;
import dataaccess.DataAccess;
import passoff.exception.ResponseParseException;

import static service.GenerateToken.generateToken;

public class UserService {
    private final DataAccess dataAccess;
    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }
    public UserD getUser(String username) throws ResponseParseException {
        return dataAccess.getUser(username);
    }
    public UserD createUser(UserD user)throws ResponseParseException{
        return dataAccess.addUser(user);
    }
    public AuthD createAuth(String username)throws ResponseParseException{
        return dataAccess.createAuth(username);
    }

    public RegisterResult register(RegisterRequest registerRequest)throws ResponseParseException{
        getUser(registerRequest.username());
        UserD user = new UserD(registerRequest.username(), registerRequest.password(), registerRequest.email());
        createUser(user);
        createAuth(registerRequest.username());
        return null;
    }

    private void checkPassword(String username, String password) throws ResponseParseException{
        if (!getUser(username).password().equals(password)){
            throw new ResponseParseException("Error: invalid password", Error);
        }
    }
    public LoginResult login(LoginRequest loginRequest){
        getUser(loginRequest.username());
        checkPassword(loginRequest.password());
        AuthD authToken = createAuth(loginRequest.username());
        return authToken;
    }
    public AuthD getAuthData(String token){
        return dataAccess.getAuth(token);
    }
    public void deleteAuth(String token){
        dataAccess.deleteAuth(token);
    }
    public void logout(LogoutRequest logoutRequest){
        getAuthData(logoutRequest.authToken());
        deleteAuth(logoutRequest.authToken());
    }
}

