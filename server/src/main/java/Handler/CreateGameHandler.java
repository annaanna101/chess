package Handler;

import Model.CreateRequest;
import Model.RegisterRequest;
import Model.RegisterResult;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import server.Server;
import service.GameService;
import service.UserService;

public class CreateGameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(GameService gameService) {this.gameService = gameService;}
    public void createGame(Context ctx) {
        try {
            CreateRequest request =
                    gson.fromJson(ctx.body(), CreateRequest.class);

            int result = gameService.createGame(request);
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
