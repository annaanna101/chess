package Handler;

import Model.LogoutRequest;
import com.google.gson.Gson;
import io.javalin.http.Context;
import passoff.exception.ResponseParseException;
import server.Server;
import service.UserService;

public class LogoutHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LogoutHandler(UserService userService) {this.userService = userService;}
    public void logout(Context ctx) throws ResponseParseException {
        String authToken = ctx.header("authorization");
        if (authToken == null || authToken.isBlank()){
            ctx.status(401).json(new Server.ErrorResponse("Error: missing authToken"));
            return;
        }
        userService.logout(authToken);
        ctx.status(200).json(new Object());
    }
}
