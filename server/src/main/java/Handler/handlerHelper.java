package Handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import server.Server;

public class handlerHelper {

    void handlerErrorResponse(Context ctx, DataAccessException e){
        final Gson gson = new Gson();
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
