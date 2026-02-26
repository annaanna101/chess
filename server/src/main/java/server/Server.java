package server;

import Handler.ClearHandler;
import Handler.LoginHandler;
import Handler.LogoutHandler;
import Handler.RegistrationHandler;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import passoff.exception.ResponseParseException;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);
        RegistrationHandler registrationHandler = new RegistrationHandler(userService);
        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        ClearHandler clearHandler = new ClearHandler(userService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.
        javalin.post("/user", registrationHandler::register);
        javalin.post("/session", loginHandler::login);
        javalin.delete("/session", logoutHandler::logout);
        javalin.delete("/db",clearHandler::clearAll);

        javalin.exception(DataAccessException.class, (ex, ctx) -> {
            ctx.status(500);
            ctx.json(new ErrorResponse(ex.getMessage()));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
    public static class ErrorResponse {
        public final String message;
        public ErrorResponse(String message){
            this.message = message;
        }
    }
}
