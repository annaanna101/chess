package Handler;
import Model.registerRequest;
import Model.registerResult;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import server.Server;
import service.UserService;

public class registrationHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public registrationHandler(UserService userService){
        this.userService = userService;
    }
    public void register(Context ctx) throws DataAccessException {
        try {
            registerRequest request =
                    gson.fromJson(ctx.body(), registerRequest.class);

            registerResult result = userService.register(request);

            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (DataAccessException e) {
            String message = e.getMessage();
            if (message.contains("bad request")){
                ctx.status(400);
            } else if (message.contains("unauthorized")) {
                ctx.status(401);
            } else if (message.contains("already taken")) {
                ctx.status(403);
            }else {
                ctx.status(500);
            }
            ctx.result(gson.toJson(new Server.ErrorResponse(message)));
        }

    }

}
