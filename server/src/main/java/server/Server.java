package server;

import Handler.*;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);
        GameService gameService = new GameService(dataAccess);
        handlerHelper helper = new handlerHelper();
        registrationHandler registrationHandler = new registrationHandler(userService);
        loginHandler loginHandler = new loginHandler(userService, helper);
        logoutHandler logoutHandler = new logoutHandler(userService);
        clearHandler clearHandler = new clearHandler(userService, gameService);
        createGameHandler createGameHandler = new createGameHandler(gameService, helper);
        listHandler listHandler = new listHandler(gameService,helper);
        joinHandler joinHandler = new joinHandler(gameService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.
        javalin.post("/user", registrationHandler::register);
        javalin.post("/session", loginHandler::login);
        javalin.delete("/session", logoutHandler::logout);
        javalin.delete("/db",clearHandler::clearAll);
        javalin.post("/game", createGameHandler::createGame);
        javalin.get("/game", listHandler::listGames);
        javalin.put("/game", joinHandler::joinGame);

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
