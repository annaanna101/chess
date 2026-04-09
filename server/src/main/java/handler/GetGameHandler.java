package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.GameRequest;
import model.GetGameResult;
import server.Server;
import service.GameService;

public class GetGameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();


    public GetGameHandler(GameService gameService) {this.gameService = gameService;
    }
    public void getGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isBlank()){
                ctx.status(401).json(gson.toJson(new Server.ErrorResponse("Error: missing authToken")));
                return;
            }
//            GameRequest request = gson.fromJson(ctx.body(), GameRequest.class);
//            Integer gameID = request.gameID();
            String gameIDParam = ctx.pathParam("id");
            if (gameIDParam == null){
                ctx.status(400);
                ctx.json(gson.toJson(new Server.ErrorResponse("Error: invalid gameID")));
                return;
            }
            Integer gameID = Integer.parseInt(gameIDParam);
            GameRequest request = new GameRequest(gameID);
            ChessGame result= gameService.getGame(request, authToken);
            GetGameResult response = new GetGameResult(result);
            ctx.status(200);
            ctx.result(gson.toJson(response));
        } catch (DataAccessException e) {
            String message = e.getMessage();
            if (message.contains("unauthorized")) {
                ctx.status(401);
            } else if (message.contains("bad request")) {
                ctx.status(400);
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
