package handler;

import com.google.gson.JsonSyntaxException;
import model.JoinRequest;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import server.Server;
import service.GameService;

public class JoinHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public JoinHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isBlank()) {
                ctx.status(401).json(gson.toJson(new Server.ErrorResponse("Error: missing authToken")));
                return;
            }
            JoinRequest request = gson.fromJson(ctx.body(), JoinRequest.class);
            String color = request.playerColor();
            if (color == null || (!color.equals("WHITE") && !color.equals("BLACK"))){
                ctx.status(400);
                ctx.json(gson.toJson(new Server.ErrorResponse("Error: invalid color")));
                return;
            }
            gameService.joinGame(request, authToken);
            ctx.status(200);
        } catch (DataAccessException e) {
            String message = e.getMessage();
            if (message.contains("unauthorized")) {
                ctx.status(401);
            } else if (message.contains("bad request")) {
                ctx.status(400);
            } else if (message.contains("already taken")) {
                ctx.status(403);
            } else {
                ctx.status(500);
            }
            ctx.result(gson.toJson(new Server.ErrorResponse(message)));
        } catch (JsonSyntaxException | NullPointerException e){
            ctx.status(400);
            ctx.json(gson.toJson(new Server.ErrorResponse("Error: invalid request body")));
        }
    }
}