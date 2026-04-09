package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTest {
    private GameService gameService;
    private UserService userService;
    private DataAccess dataAccess;
    private static final String AUTH = "token";

    @BeforeEach
    void setup() throws DataAccessException {
        dataAccess = new DataAccess() {
            Map<Integer, GameD> games = new HashMap<>();
            Map<String, AuthD> auths = new HashMap<>();
            Map<String, UserD> users = new HashMap<>();
            int nextGameID = 1;
            @Override
            public UserD addUser(UserD user)  {
                users.put(user.username(), user);
                return user;
            }

            @Override
            public UserD getUser(String username) {
                return users.get(username);
            }

            @Override
            public GameD getGame(int gameID){
                return games.get(gameID);
            }

            @Override
            public Collection<GameSummary> listGames() {
                return games.values().stream()
                        .map(g -> new GameSummary(
                                g.getGameID(),
                                g.getGameName(),
                                g.getWhiteUsername(),
                                g.getBlackUsername(),
                                g.getGameStatus()))
                                .toList();
            }

            @Override
            public AuthD createAuth(String username) {
                AuthD auth = new AuthD(AUTH, username);
                auths.put(AUTH, auth);
                return auth;
            }

            @Override
            public AuthD getAuth(String token) {
                return auths.get(token);
            }

            @Override
            public void deleteAuth(String token) {
                auths.remove(token);
            }

            @Override
            public void clearUsers() {
                users.clear();
            }

            @Override
            public void clearAuths() {
                auths.clear();
            }

            @Override
            public void clearGames()  {
                games.clear();
            }

            @Override
            public int createGame(String gameName) {
                int id = nextGameID++;
                games.put(id, new GameD(id, null, null, gameName, new ChessGame(), "In-Play"));
                return id;
            }

            @Override
            public void updateGame(Integer gameID, String playerColor, String username) {
                GameD game = games.get(gameID);
                if (playerColor.equals("WHITE")){
                    game.setWhiteUser(username);
                } else if (playerColor.equals("BLACK")){
                    game.setBlackUser(username);
                }
            }

            @Override
            public boolean verifyUser(String username, String providedClearTextPassword) {
                return getUser(username).password().equals(providedClearTextPassword);
            }
        };
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);
        dataAccess.createAuth("player1");
    }
    @Test
    void testCreateGameNull(){
        CreateRequest request = new CreateRequest(null, AUTH);
        Exception ex = assertThrows(DataAccessException.class, () -> gameService.createGame(request, AUTH));
        assertTrue(ex.getMessage().contains("bad request"));
    }

    @Test
    void testCreateGamePositive() throws DataAccessException{
        CreateRequest request = new CreateRequest("Game1",AUTH);
        CreateResult result = gameService.createGame(request, AUTH);
        assertTrue(result.gameID() > 0);
    }

    @Test
    void testListGamesPositive() throws DataAccessException {
        gameService.createGame(new CreateRequest("Game1", AUTH), AUTH);
        gameService.createGame(new CreateRequest("Game2", AUTH), AUTH);
        gameService.createGame(new CreateRequest("Game3", AUTH), AUTH);
        Collection<GameSummary> games = gameService.listGame(AUTH);
        assertEquals(3, games.size());
    }

    @Test
    void testListGamesListNull() throws DataAccessException {
        gameService.createGame(new CreateRequest("Game1", AUTH), AUTH);
        gameService.createGame(new CreateRequest("Game2", AUTH), AUTH);
        Exception ex = assertThrows(DataAccessException.class, () -> gameService.listGame(null));
        assertTrue(ex.getMessage().contains("bad request"));
    }

    @Test
    void testClearGamesPositive() throws DataAccessException {
        gameService.createGame(new CreateRequest("Game1", AUTH), AUTH);
        gameService.createGame(new CreateRequest("Game2", AUTH), AUTH);
        gameService.clearGames();
        Collection<GameSummary> games = gameService.listGame(AUTH);
        assertEquals(0, games.size());
    }

    @Test
    void testJoinGamePositive() throws DataAccessException{
        gameService.createGame(new CreateRequest("Game1", AUTH), AUTH);
        JoinRequest joinRequest = new JoinRequest(1,"WHITE");
        gameService.joinGame(joinRequest,AUTH);
        GameD game = dataAccess.getGame(1);
        assertNotNull(game);
        assertEquals(game.getWhiteUsername(), dataAccess.getAuth(AUTH).username());
    }

    @Test
    void testJoinGameNullGameID(){
        Exception ex = assertThrows(DataAccessException.class, () -> {
            JoinRequest joinRequest = new JoinRequest(null, "WHITE");
            gameService.joinGame(joinRequest, AUTH);
        });
        assertTrue(ex.getMessage().contains("bad request"));
    }
    @Test
    void testRegister() throws DataAccessException {
        userService.register(new RegisterRequest("username", "password", "email"));
        assertNotNull(dataAccess.getUser("username"));
    }

    @Test
    void testRegisterUsernameNull(){
        Exception ex = assertThrows(DataAccessException.class, () -> userService.register(new RegisterRequest(null, "p", "e")));
        assertTrue(ex.getMessage().contains("bad request"));
    }

    @Test
    void testLogin() throws DataAccessException{
        userService.register(new RegisterRequest("username", "password", "email"));
        assertNotNull(userService.login(new LoginRequest("username", "password")));
    }

    @Test
    void testLoginUnauthorized(){
        Exception ex = assertThrows(DataAccessException.class, () -> {
            userService.register(new RegisterRequest("username", "password", "email"));
            userService.login(new LoginRequest("user", "password"));
        });
        assertTrue(ex.getMessage().contains("unauthorized"));
    }

    @Test
    void testLogout() throws DataAccessException {
        userService.logout(AUTH);
        assertNull(dataAccess.getAuth(AUTH));
    }

    @Test
    void testDeleteAuth() throws DataAccessException {
        userService.deleteAuth(AUTH);
        assertNull(dataAccess.getAuth(AUTH));
    }

    @Test
    void testDeleteAuthUnauthorized() {
        Exception ex = assertThrows(DataAccessException.class, () -> userService.deleteAuth(null));
        assertTrue(ex.getMessage().contains("unauthorized"));
    }

    @Test
    void testLogoutUnauthorized() {
        Exception ex = assertThrows(DataAccessException.class, () -> userService.logout("falseAuth"));
        assertTrue(ex.getMessage().contains("unauthorized"));
    }
    @Test
    void testClearAuths() throws DataAccessException{
        dataAccess.addUser(new UserD("user1", "p1", "e1"));
        dataAccess.addUser(new UserD("user2", "p2", "e2"));
        AuthD auth1 = dataAccess.createAuth("user1");
        AuthD auth2 = dataAccess.createAuth("user2");
        dataAccess.clearAuths();
        assertNull(dataAccess.getAuth(auth1.authToken()));
        assertNull(dataAccess.getAuth(auth2.authToken()));
    }

    @Test
    void testClearUsers() throws DataAccessException{
        dataAccess.addUser(new UserD("user1", "pa1", "email1"));
        dataAccess.addUser(new UserD("user2", "pa2", "email2"));
        dataAccess.clearUsers();
        assertNull(dataAccess.getUser("user1"));
        assertNull(dataAccess.getUser("user2"));
    }

    @Test
    void testClearAll() throws DataAccessException{
        dataAccess.addUser(new UserD("user1", "pass1", "email1"));
        dataAccess.addUser(new UserD("user2", "pass2", "email2"));
        AuthD auth1 = dataAccess.createAuth("user1");
        AuthD auth2 = dataAccess.createAuth("user2");

        int game1 = dataAccess.createGame("Game1");
        int game2 = dataAccess.createGame("Game2");

        dataAccess.clearUsers();
        dataAccess.clearAuths();
        dataAccess.clearGames();

        assertNull(dataAccess.getUser("user1"));
        assertNull(dataAccess.getUser("user2"));

        assertNull(dataAccess.getAuth(auth1.authToken()));
        assertNull(dataAccess.getAuth(auth2.authToken()));

        assertNull(dataAccess.getGame(game1));
        assertNull(dataAccess.getGame(game2));
        assertEquals(0, dataAccess.listGames().size());
    }
}
