package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import server.Server;
import service.GameService;
import service.UserService;

public class ClearHandler {
    private final UserService userService;
    private final GameService gameService;
    private final Gson gson = new Gson();

    public ClearHandler(UserService userService, GameService gameService) {
        this.userService = userService; this.gameService = gameService;
    }
    public void clear(Context ctx) throws DataAccessException {
        try {
            userService.clearAuth();
            gameService.clearGames();
            userService.clearUsers();
            ctx.status(200);
        } catch (DataAccessException e) {
            String message = e.getMessage();
            ctx.status(500);
            ctx.result(gson.toJson(new Server.ErrorResponse(message)));
        }
    }
}
