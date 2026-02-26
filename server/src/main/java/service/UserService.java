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
//    public UserD getUser(String username) throws ResponseParseException {
//        return dataAccess.getUser(username);
//    }
//    public UserD createUser(UserD user)throws ResponseParseException{
//        return dataAccess.addUser(user);
//    }
//    public AuthD createAuth(String username)throws ResponseParseException{
//        return dataAccess.createAuth(username);
//    }

    public RegisterResult register(RegisterRequest registerRequest)throws ResponseParseException{
        if (dataAccess.getUser(registerRequest.username())!= null) {
            throw new ResponseParseException("Error: Username is already in use", null);
        }
//        getUser(registerRequest.username());
        UserD user = new UserD(registerRequest.username(), registerRequest.password(), registerRequest.email());
//        createUser(user);
        dataAccess.addUser(user);
        AuthD auth = dataAccess.createAuth(registerRequest.username());
//        createAuth(registerRequest.username());
        return new RegisterResult(registerRequest.username(), auth.authToken());
    }

//    private void checkPassword(String username, String password) throws ResponseParseException{
//        if (!getUser(username).password().equals(password)){
//            throw new ResponseParseException("Error: invalid password", Error);
//        }
//    }
    public LoginResult login(LoginRequest loginRequest) throws ResponseParseException{
        UserD user = dataAccess.getUser(loginRequest.username());
        if (user == null || !user.password().equals(loginRequest.password())){
            throw new ResponseParseException("Error: unauthorized", null);
        }
        AuthD auth = dataAccess.createAuth(loginRequest.username());
        return new LoginResult(loginRequest.username(), auth.authToken());
//        getUser(loginRequest.username());
//        checkPassword(loginRequest.username(),loginRequest.password());
//        AuthD authToken = createAuth(loginRequest.username());
//        return authToken;
    }
//    public AuthD getAuthData(String token){
//        return dataAccess.getAuth(token);
//    }
//    public void deleteAuth(String token){
//        dataAccess.deleteAuth(token);
//    }
    public void logout(LogoutRequest logoutRequest) throws ResponseParseException{
//        getAuthData(logoutRequest.authToken());
//        deleteAuth(logoutRequest.authToken());
        AuthD auth = dataAccess.getAuth(logoutRequest.authToken());

        if (auth == null) {
            throw new ResponseParseException("Error: unauthorized", null);
        }
        dataAccess.deleteAuth(logoutRequest.authToken());
    }
}

