package handler;

import model.LoginRequest;
import model.LoginResult;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.UserService;

public class LoginHandler {
    private final UserService userService;
    private final Gson gson = new Gson();
    private final HandlerHelper helper;

    public LoginHandler(UserService userService, HandlerHelper helper) {this.userService = userService;
        this.helper = helper;
    }
    public void login(Context ctx){
        try {
            LoginRequest request =
                    gson.fromJson(ctx.body(), LoginRequest.class);

            LoginResult result = userService.login(request);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (DataAccessException e) {
            helper.handlerErrorResponse(ctx,e);
        }
    }
}
