package Handler;

import Model.joinRequest;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import server.Server;
import service.GameService;

public class joinHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public joinHandler(GameService gameService) {this.gameService = gameService;}
    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isBlank()){
                ctx.status(401).json(new Server.ErrorResponse("Error: missing authToken"));
                return;
            }
            joinRequest request =
                    gson.fromJson(ctx.body(), joinRequest.class);
            gameService.joinGame(request, authToken);
            ctx.status(200);
        } catch (DataAccessException e) {
            String message = e.getMessage();
            if (message.contains("bad request")){
                ctx.status(400);
            } else if (message.contains("unauthorized")) {
                ctx.status(401);
            }else if (message.contains("already taken")){
                ctx.status(403);
            }else {
                ctx.status(500);
            }
            ctx.result(gson.toJson(new Server.ErrorResponse(message)));
        }
    }
}
