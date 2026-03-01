package server;

import handler.*;
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
        HandlerHelper helper = new HandlerHelper();
        RegistrationHandler registrationHandler = new RegistrationHandler(userService);
        LoginHandler loginHandler = new LoginHandler(userService, helper);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        ClearHandler clearHandler = new ClearHandler(userService, gameService);
        CreateGameHandler createGameHandler = new CreateGameHandler(gameService, helper);
        ListHandler listHandler = new ListHandler(gameService,helper);
        JoinHandler joinHandler = new JoinHandler(gameService);

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
