package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static String url;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        url = "http://localhost:" + port;
        ServerFacade facade = new ServerFacade(url);
        facade.clear();
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerTestPositive() {
        ServerFacade facade = new ServerFacade(url);
        RegisterRequest request = new RegisterRequest("an", "an", "an");
        RegisterResult result = facade.register(request);
        Assertions.assertEquals("an", result.username());
    }

    @Test
    public void registerTestNegative() {
        ServerFacade facade = new ServerFacade(url);
        RegisterRequest request = new RegisterRequest("u", "p", "email");
        facade.register(request);

        Assertions.assertThrows(RuntimeException.class, () ->{
            facade.register(request);
        });
    }

    @Test
    public void loginTestPositive() {
        ServerFacade facade = new ServerFacade(url);
        LoginRequest request = new LoginRequest("an", "an");
        LoginResult result = facade.login(request);
        Assertions.assertEquals("an", result.username());
    }

    @Test
    public void loginTestNegative() {
        ServerFacade facade = new ServerFacade(url);
        LoginRequest badRequest = new LoginRequest("an", "wrong");
        Assertions.assertThrows(RuntimeException.class, () -> {
            facade.login(badRequest);
        });

    }

    @Test
    public void logoutTestPositive() {
        ServerFacade facade = new ServerFacade(url);
        LoginRequest logRequest = new LoginRequest("an", "an");
        LoginResult logResult = facade.login(logRequest);
        String authToken = logResult.authToken();
        LogoutRequest request = new LogoutRequest(authToken);
        facade.logout(request);
        Assertions.assertTrue(true);
    }

    @Test
    public void logoutTestNegative() {
        ServerFacade facade = new ServerFacade(url);
        LoginRequest logRequest = new LoginRequest("an", "an");
        facade.login(logRequest);
        String authToken = "badToken";
        LogoutRequest request = new LogoutRequest(authToken);
        Assertions.assertThrows(RuntimeException.class, () -> {
            facade.logout(request);
        });
    }

    @Test
    public void createTestPositive() {
        ServerFacade facade = new ServerFacade(url);
        RegisterRequest request1 = new RegisterRequest("an", "an", "an");
        facade.register(request1);
        LoginRequest request = new LoginRequest("an", "an");
        LoginResult result = facade.login(request);
        String authToken = result.authToken();
        CreateRequest createRequest = new CreateRequest("game", authToken);
        CreateResult createResult = facade.create(createRequest);
        Assertions.assertNotNull(createResult.gameID());
    }

    @Test
    public void createTestNegative() {
        ServerFacade facade = new ServerFacade(url);
        RegisterRequest request1 = new RegisterRequest("an", "an", "an");
        facade.register(request1);
        LoginRequest request = new LoginRequest("an", "an");
        facade.login(request);
        CreateRequest createRequest = new CreateRequest("game", null);
        Assertions.assertThrows(RuntimeException.class, () -> {
            facade.create(createRequest);
        });
    }

    @Test
    public void listTestPositive() {
        ServerFacade facade = new ServerFacade(url);
        RegisterRequest request1 = new RegisterRequest("an", "an", "an");
        facade.register(request1);
        LoginRequest request = new LoginRequest("an", "an");
        LoginResult result = facade.login(request);
        AuthD authToken = new AuthD(result.authToken(), "an");
        ListGameResult listGameResult = facade.list_games(authToken);
        Assertions.assertFalse(listGameResult.games().isEmpty());
    }

    @Test
    public void listTestNegative() {
        ServerFacade facade = new ServerFacade(url);
        RegisterRequest request1 = new RegisterRequest("an", "an", "an");
        facade.register(request1);
        LoginRequest request = new LoginRequest("an", "an");
        facade.login(request);
        AuthD authToken = new AuthD("badToken", "an");
        Assertions.assertThrows(RuntimeException.class, () -> {
            facade.list_games(authToken);
        });
    }

    @Test
    public void joinTestPositive() {
        ServerFacade facade = new ServerFacade(url);
        RegisterRequest request1 = new RegisterRequest("an", "an", "an");
        facade.register(request1);
        LoginRequest request = new LoginRequest("an", "an");
        LoginResult result = facade.login(request);
        AuthD authToken = new AuthD(result.authToken(), result.username());
        CreateRequest createRequest = new CreateRequest("anna_game", authToken.authToken());
        CreateResult createResult = facade.create(createRequest);
        JoinRequest joinRequest = new JoinRequest(createResult.gameID(),"BLACK");
        facade.joinGame(joinRequest, authToken);
        Assertions.assertTrue(true);
    }

    @Test
    public void joinTestNegative() {
        ServerFacade facade = new ServerFacade(url);
        RegisterRequest request1 = new RegisterRequest("an", "an", "an");
        facade.register(request1);
        LoginRequest request = new LoginRequest("an", "an");
        LoginResult result = facade.login(request);
        AuthD authToken = new AuthD(result.authToken(), result.username());
        CreateRequest createRequest = new CreateRequest("anna_game", authToken.authToken());
        facade.create(createRequest);
        JoinRequest joinRequest = new JoinRequest(1234,"BLACK");
        Assertions.assertThrows(RuntimeException.class, () -> {
            facade.joinGame(joinRequest, authToken);
        });
    }


}
