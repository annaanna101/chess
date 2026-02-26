package Handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import server.Server;
import service.UserService;

public class ClearHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public ClearHandler(UserService userService) {this.userService = userService;}
    public void clearAll(Context ctx) throws DataAccessException {
        userService.clearUsers();
        userService.clearAuth();
        ctx.status(200);
    }
}
