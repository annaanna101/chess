package server;

import Model.*;

import static server.GenerateToken.generateToken;

public class UserService {
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

