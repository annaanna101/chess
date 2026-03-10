package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.UserService;

public class ClearHandler {
    private final UserService userService;
    private final GameService gameService;

    public ClearHandler(UserService userService, GameService gameService) {
        this.userService = userService; this.gameService = gameService;
    }
    public void clear(Context ctx) throws DataAccessException {
        userService.clearAuth();
        gameService.clearGames();
        userService.clearUsers();
        ctx.status(200);
    }
}
