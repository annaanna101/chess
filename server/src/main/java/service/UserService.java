package service;

import Model.*;
import dataaccess.DataAccess;

import static service.GenerateToken.generateToken;

public class UserService {
    private final DataAccess dataAccess;
    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }
    public RegisterResult register(RegisterRequest registerRequest){
        getUser(registerRequest.username());
        createUser(registerRequest.username(),registerRequest.password(), registerRequest.email());
        createAuth(generateToken());
        return null;
    }
    public LoginResult login(LoginRequest loginRequest){
        getUser(loginRequest.username());
        checkPassword(loginRequest.password());
        createAuth(loginRequest.username());
        return authToken;
    }
    public void logout(LogoutRequest logoutRequest){
        getAuthData(logoutRequest.authToken());
        deleteAuth();
    }
}

