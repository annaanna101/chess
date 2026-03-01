package Handler;

import Model.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import server.Server;
import service.GameService;

import java.util.Collection;

public class listHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();
    private final handlerHelper helper;


    public listHandler(GameService gameService, handlerHelper handlerError) {this.gameService = gameService;
        this.helper = handlerError;
    }
    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isBlank()){
                ctx.status(401).json(new Server.ErrorResponse("Error: missing authToken"));
                return;
            }
            Collection<gameSummary> result = gameService.listGame(authToken);
            listGameResult response = new listGameResult(result);
            ctx.status(200);
            ctx.result(gson.toJson(response));
        } catch (DataAccessException e) {
            helper.handlerErrorResponse(ctx,e);
        }
    }
}
