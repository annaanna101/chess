package Handler;

import Model.loginRequest;
import Model.loginResult;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import server.Server;
import service.UserService;

public class loginHandler {
    private final UserService userService;
    private final Gson gson = new Gson();
    private final handlerHelper helper;

    public loginHandler(UserService userService, handlerHelper helper) {this.userService = userService;
        this.helper = helper;
    }
    public void login(Context ctx){
        try {
            loginRequest request =
                    gson.fromJson(ctx.body(), loginRequest.class);

            loginResult result = userService.login(request);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (DataAccessException e) {
            helper.handlerErrorResponse(ctx,e);
        }
    }
}
