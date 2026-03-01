package Handler;

import Model.createRequest;
import Model.createResult;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import server.Server;
import service.GameService;

public class createGameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();
    private final handlerHelper helper;

    public createGameHandler(GameService gameService, handlerHelper helper) {this.gameService = gameService;
        this.helper = helper;
    }
    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isBlank()){
                ctx.status(401).json(new Server.ErrorResponse("Error: missing authToken"));
                return;
            }
            createRequest request =
                    gson.fromJson(ctx.body(), createRequest.class);
            createResult result = gameService.createGame(request, authToken);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (DataAccessException e) {
            helper.handlerErrorResponse(ctx, e);
        }
    }
}
