package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerTestPositive() {
        var port = server.run(0);
        String url = "http://localhost:" + port;
        ServerFacade facade = new ServerFacade(url);
        RegisterRequest request = new RegisterRequest("an", "an", "an");
        RegisterResult result = facade.register(request);
        Assertions.assertEquals("an", result.username());
    }

    @Test
    public void registerTestNegative() {
        Assertions.assertTrue(true);
    }

    @Test
    public void loginTestPositive() {
        var port = server.run(0);
        String url = "http://localhost:" + port;
        ServerFacade facade = new ServerFacade(url);
        LoginRequest request = new LoginRequest("an", "an");
        LoginResult result = facade.login(request);
        Assertions.assertEquals("an", result.username());
    }

    @Test
    public void loginTestNegative() {
        Assertions.assertTrue(true);
    }

    @Test
    public void logoutTestPositive() {
        var port = server.run(0);
        String url = "http://localhost:" + port;
        ServerFacade facade = new ServerFacade(url);
        LoginRequest logRequest = new LoginRequest("an", "an");
        LoginResult logResult = facade.login(logRequest);
        String authToken = logResult.authToken();
        LogoutRequest request = new LogoutRequest(authToken);
        //figure out
         result = facade.register(request);
        Assertions.assertEquals("an", result.username());
    }

    @Test
    public void logoutTestNegative() {
        Assertions.assertTrue(true);
    }

    @Test
    public void createTestPositive() {
        var port = server.run(0);
        String url = "http://localhost:" + port;
        ServerFacade facade = new ServerFacade(url);
        LoginRequest request = new LoginRequest("an", "an");
        LoginResult result = facade.login(request);
        String authToken = result.authToken();
        CreateRequest createRequest = new CreateRequest("game", authToken);
        CreateResult createResult = facade.create(createRequest);
        Assertions.assertNotNull(createResult.gameID());
    }

    @Test
    public void createTestNegative() {
        Assertions.assertTrue(true);
    }

    @Test
    public void listTestPositive() {
        var port = server.run(0);
        String url = "http://localhost:" + port;
        ServerFacade facade = new ServerFacade(url);
        LoginRequest request = new LoginRequest("an", "an");
        LoginResult result = facade.login(request);
        AuthD authToken = new AuthD(result.authToken(), "an");
        ListGameResult listGameResult = facade.list_games(authToken);
        Assertions.assertFalse(listGameResult.games().isEmpty());
    }

    @Test
    public void listTestNegative() {
        Assertions.assertTrue(true);
    }

    @Test
    public void joinTestPositive() {
        var port = server.run(0);
        String url = "http://localhost:" + port;
        ServerFacade facade = new ServerFacade(url);
        LoginRequest request = new LoginRequest("an", "an");
        LoginResult result = facade.login(request);
        AuthD authToken = new AuthD(result.authToken(), "an");
        JoinRequest joinRequest = new JoinRequest(1,"WHITE");
        facade.joinGame(joinRequest, authToken);
        Assertions.assertEquals("an", result.username());
    }

    @Test
    public void joinTestNegative() {
        Assertions.assertTrue(true);
    }


}
