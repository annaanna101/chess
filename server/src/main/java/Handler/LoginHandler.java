package Handler;

import Model.LoginRequest;
import Model.LoginResult;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import passoff.exception.ResponseParseException;
import service.UserService;

public class LoginHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LoginHandler(UserService userService) {this.userService = userService;}
    public void login(Context ctx) throws DataAccessException {
        LoginRequest request =
                gson.fromJson(ctx.body(), LoginRequest.class);

        LoginResult result = userService.login(request);

        ctx.status(200);
        ctx.result(gson.toJson(result));
    }
}
