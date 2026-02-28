package Handler;

import Model.CreateRequest;
import Model.CreateResult;
import Model.GameD;
import Model.ListRequest;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import server.Server;
import service.GameService;

import java.util.Collection;

public class ListHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public ListHandler(GameService gameService) {this.gameService = gameService;}
    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isBlank()){
                ctx.status(401).json(new Server.ErrorResponse("Error: missing authToken"));
                return;
            }
            ListRequest request =
                    gson.fromJson(ctx.body(), ListRequest.class);
            Collection<GameD> result = gameService.listGame(authToken);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (DataAccessException e) {
            String message = e.getMessage();
            if (message.contains("bad request")){
                ctx.status(400);
            } else if (message.contains("unauthorized")) {
                ctx.status(401);
            }else {
                ctx.status(500);
            }
            ctx.result(gson.toJson(new Server.ErrorResponse(message)));
        }
    }
}
