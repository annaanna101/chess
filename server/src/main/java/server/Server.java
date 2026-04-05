package server;

import com.google.gson.Gson;
import dataaccess.MySqlDataAccess;
import handler.*;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import io.javalin.*;
import server.websocket.WebSocketHandler;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        DataAccess dataAccess;
        try {
            dataAccess = new MySqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        webSocketHandler = new WebSocketHandler(dataAccess);
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
        javalin.delete("/db",clearHandler::clear);
        javalin.post("/game", createGameHandler::createGame);
        javalin.get("/game", listHandler::listGames);
        javalin.put("/game", joinHandler::joinGame);

        javalin.exception(DataAccessException.class, (ex, ctx) -> {
            ctx.status(500);
            ctx.json(new Gson().toJson(new ErrorResponse(ex.getMessage())));
        });
        javalin.ws("/ws", ws -> {
            ws.onConnect(webSocketHandler);
            ws.onMessage(webSocketHandler);
            ws.onClose(webSocketHandler);
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
