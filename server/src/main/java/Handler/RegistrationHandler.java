package Handler;
import Model.RegisterRequest;
import Model.RegisterResult;
import com.google.gson.Gson;
import io.javalin.http.Context;
import passoff.exception.ResponseParseException;
import service.UserService;

public class RegistrationHandler{
    private final UserService userService;
    private final Gson gson = new Gson();

    public RegistrationHandler(UserService userService){
        this.userService = userService;
    }
    public void register(Context ctx) throws ResponseParseException {
        RegisterRequest request =
                gson.fromJson(ctx.body(), RegisterRequest.class);

        RegisterResult result = userService.register(request);

        ctx.status(200);
        ctx.result(gson.toJson(result));
    }

}
