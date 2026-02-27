package Handler;

import Model.LogoutRequest;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import passoff.exception.ResponseParseException;
import server.Server;
import service.UserService;

public class LogoutHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LogoutHandler(UserService userService) {this.userService = userService;}
    public void logout(Context ctx) throws DataAccessException {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isBlank()){
                ctx.status(401).json(new Server.ErrorResponse("Error: missing authToken"));
                return;
            }
            userService.logout(authToken);
            ctx.status(200);
        } catch (DataAccessException e) {
            String message = e.getMessage();
            if (message.contains("unauthorized")){
                ctx.status(401);
            } else {
                ctx.status(500);
            }
            ctx.result(gson.toJson(new Server.ErrorResponse(message)));
        }
    }
}
