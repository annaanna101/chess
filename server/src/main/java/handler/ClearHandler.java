package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.UserService;

public class ClearHandler {
    private final UserService userService;
    private final GameService gameService;
    private final Gson gson = new Gson();

    public ClearHandler(UserService userService, GameService gameService) {
        this.userService = userService; this.gameService = gameService;
    }
    public void clearAll(Context ctx) throws DataAccessException {
        userService.clearUsers();
        userService.clearAuth();
        gameService.clearGames();
        ctx.status(200);
    }
}
